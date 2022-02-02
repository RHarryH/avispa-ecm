package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
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
import com.avispa.ecm.util.reflect.PropertyUtils;
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
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    public PropertyPageContent convertToContent(PropertyPage propertyPage, Object object, boolean readonly) {
        PropertyPageContent propertyPageContent = getPropertyPageContent(propertyPage.getPrimaryContent()).orElseThrow();
        propertyPageContent.setReadonly(readonly);

        processControls(propertyPageContent.getControls(), object);

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

    private void processControls(List<Control> controls, Object object) {
        for(Control control : controls) {
            if(control instanceof Columns) {
                Columns columns = (Columns)control;
                processControls(columns.getControls(), object);
            } else if(control instanceof Tabs) {
                Tabs tabs = (Tabs)control;
                for(Tab tab : tabs.getTabs()) {
                    processControls(tab.getControls(), object);
                }
            } else if(control instanceof Table) {
                Table table = (Table)control;
                processControls(table.getControls(), PropertyUtils.getPropertyValue(object, table.getProperty()));
            } else {
                processControl(control, object);
            }
        }
    }

    private void processControl(Control control, Object object) {
        if(control instanceof Label) {
            Label label = (Label)control;
            label.setExpression(expressionResolver.resolve(object, label.getExpression()));
        } else if(control instanceof ComboRadio) {
            ComboRadio comboRadio = (ComboRadio)control;
            loadDictionary(comboRadio);
        }
    }

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio
     */
    private void loadDictionary(ComboRadio comboRadio) {
        if(StringUtils.isNotEmpty(comboRadio.getObjectType())) {
            loadValuesFromObject(comboRadio);
        } else if(StringUtils.isNotEmpty(comboRadio.getDictionary())){
            loadValuesFromDictionary(comboRadio);
        }
    }

    private void loadValuesFromObject(ComboRadio comboRadio) {
        Type type = typeRepository.findByTypeName(comboRadio.getObjectType());
        if (null != type) {
            List<? extends EcmObject> ecmObjects = getEcmObjects(type);

            List<Map.Entry<UUID, String>> values = ecmObjects.stream()
                        .filter(ecmObject -> StringUtils.isNotEmpty(ecmObject.getObjectName())) // filter out incorrect values with empty object name
                        .map(ecmObject -> new AbstractMap.SimpleEntry<>(ecmObject.getId(), ecmObject.getObjectName()))
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toList());

            comboRadio.setValues(values);
        } else {
            log.error("Type '{}' was not found", comboRadio.getObjectType());
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

    private void loadValuesFromDictionary(ComboRadio comboRadio) {
        if(log.isDebugEnabled()) {
            log.debug("Loading values from {} dictionary", comboRadio.getDictionary());
        }
        Dictionary dictionary = dictionaryService.getDictionary(comboRadio.getDictionary());

        List<Map.Entry<UUID, String>> values = dictionary.getValues().stream()
                    .filter(value -> StringUtils.isNotEmpty(value.getLabel())) // filter out incorrect values with label
                    .map(value -> new AbstractMap.SimpleEntry<>(value.getId(), value.getLabel()))
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toList());

        comboRadio.setValues(values);
    }
}
