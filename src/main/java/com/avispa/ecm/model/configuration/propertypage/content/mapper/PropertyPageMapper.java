package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyPageMapper {
    private final ExpressionResolver expressionResolver;
    private final TypeRepository typeRepository;
    private final ObjectMapper objectMapper;
    private final DictionaryService dictionaryService;

    @PersistenceContext
    private EntityManager entityManager;

    public PropertyPageContent convertToContent(PropertyPage propertyPage, Object context, boolean readonly) {
        PropertyPageContent propertyPageContent = getPropertyPageContent(propertyPage.getPrimaryContent()).orElseThrow();
        propertyPageContent.setReadonly(readonly);

        processControls(propertyPageContent.getControls(), context);

        return propertyPageContent;
    }

    /**
     * Loads JSON content file and converts it to PropertyPageContent instance
     * @param content property page JSON content
     * @return
     */
    private Optional<PropertyPageContent> getPropertyPageContent(Content content) {
        try {
            byte[] resource = Files.readAllBytes(Path.of(content.getFileStorePath()));
            return Optional.of(objectMapper.readerFor(PropertyPageContent.class).withRootName("propertyPage").readValue(resource));
        } catch (IOException e) {
            log.error("Can't parse property page content from '{}'", content.getFileStorePath(), e);
        }

        return Optional.empty();
    }

    private void processControls(List<Control> controls, Object context) {
        for(Control control : controls) {
            if(control instanceof Columns) {
                Columns columns = (Columns)control;
                processControls(columns.getControls(), context);
            } else if(control instanceof Tabs) {
                Tabs tabs = (Tabs)control;
                for(Tab tab : tabs.getTabs()) {
                    processControls(tab.getControls(), context);
                }
            } else if(control instanceof Table) {
                Table table = (Table)control;
                processTableControls(table.getControls(), table.getPropertyType());
            } else {
                processControl(control, context);
            }
        }
    }

    private void processTableControls(List<Control> controls, String type) {
        controls.stream()
                .filter(ComboRadio.class::isInstance)
                .map(ComboRadio.class::cast)
                .forEach(comboRadio -> {
                    if(StringUtils.isNotEmpty(comboRadio.getTypeName())) {
                        log.info("Type name for tables is ignored. Use dictionaries only.");
                        comboRadio.setTypeName("");
                    }
                    loadDictionary(comboRadio, typeRepository.findByTypeName(type).getClazz());
                });
    }

    private void processControl(Control control, Object context) {
        if(control instanceof Label) {
            Label label = (Label)control;
            label.setExpression(expressionResolver.resolve(context, label.getExpression()));
        } else if(control instanceof ComboRadio) {
            ComboRadio comboRadio = (ComboRadio)control;
            comboRadio.setTypeName(expressionResolver.resolve(context, comboRadio.getTypeName()));
            loadDictionary(comboRadio, context.getClass());
        }
    }

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio
     * @param contextClass
     */
    private void loadDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        if(StringUtils.isNotEmpty(comboRadio.getTypeName())) {
            loadValuesFromObject(comboRadio);
        } else {
            Dictionary dictionary = getDictionary(comboRadio, contextClass);
            loadValuesFromDictionary(comboRadio, dictionary);
        }
    }

    private void loadValuesFromObject(ComboRadio comboRadio) {
        Type type = typeRepository.findByTypeName(comboRadio.getTypeName());
        if (null != type) {
            List<? extends EcmObject> ecmObjects = getEcmObjects(type);

            Map<String, String> values = ecmObjects.stream()
                    .filter(ecmObject -> StringUtils.isNotEmpty(ecmObject.getObjectName())) // filter out incorrect values with empty object name
                    .sorted(Comparator.comparing(EcmObject::getObjectName))
                    .collect(Collectors.toMap(ecmObject -> ecmObject.getId().toString(), EcmObject::getObjectName, (x, y) -> x, LinkedHashMap::new));

            comboRadio.setValues(values);
        } else {
            log.error("Type '{}' was not found", comboRadio.getTypeName());
        }
    }

    private List<? extends EcmObject> getEcmObjects(Type type) {
        SimpleJpaRepository<? extends EcmObject, Long> jpaRepository = getSimpleRepository(type);
        return jpaRepository.findAll();
    }

    /**
     * Creates simple repository for specific type. This will create repositories even for the
     * abstract types allowing to get all concrete subtypes objects.
     * @param type type for which we want to get repository
     * @return
     */
    private SimpleJpaRepository<? extends EcmObject, Long> getSimpleRepository(Type type) {
        SimpleJpaRepository<? extends EcmObject, Long> jpaRepository;
        jpaRepository = new SimpleJpaRepository<>(type.getClazz(), entityManager);
        return jpaRepository;
    }

    private Dictionary getDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        String dictionaryName = comboRadio.getDictionary();

        // if dictionary was not retrieved from property page, try with annotation
        if(StringUtils.isEmpty(dictionaryName)) {
            dictionaryName = dictionaryService.getDictionaryNameFromAnnotation(contextClass, comboRadio.getProperty());
        }

        // if dictionary name is still not resolved throw an exception
        if(StringUtils.isEmpty(dictionaryName)) {
            throw new IllegalStateException(
                    String.format("Dictionary is not specified in property page configuration or using annotation in entity definition. Related property: '%s'", comboRadio.getProperty())
            );
        }

        return dictionaryService.getDictionary(dictionaryName);
    }

    private void loadValuesFromDictionary(ComboRadio comboRadio, Dictionary dictionary) {
        if(log.isDebugEnabled()) {
            log.debug("Loading values from {} dictionary", dictionary.getObjectName());
        }

        Map<String, String> values = dictionary.getValues().stream()
                .filter(value -> StringUtils.isNotEmpty(value.getLabel())) // filter out incorrect values with empty object name
                .sorted(Comparator.comparing(EcmObject::getObjectName))
                .collect(Collectors.toMap(DictionaryValue::getKey, DictionaryValue::getLabel, (x, y) -> x, LinkedHashMap::new));

        comboRadio.setValues(values);
    }
}
