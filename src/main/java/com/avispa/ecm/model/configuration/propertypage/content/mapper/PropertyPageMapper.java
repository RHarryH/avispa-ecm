package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private final EcmObjectRepository<EcmObject> ecmObjectRepository;
    private final TypeRepository typeRepository;
    private final ObjectMapper objectMapper;

    public PropertyPageContent convertToContent(PropertyPage propertyPage, Object object, boolean readonly) {
        PropertyPageContent propertyPageContent = getPropertyPageContent(propertyPage.getPrimaryContent()).orElseThrow();
        propertyPageContent.setReadonly(readonly);

        List<Control> aggregatedControls = new ArrayList<>();
        aggregateControls(propertyPageContent.getControls(), aggregatedControls);

        resolveLabelsExpressions(object, aggregatedControls);
        loadDictionaries(aggregatedControls);

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

    /**
     * Aggregates controls from whole property page including nested ones
     * @param controls input controls from current level of property page
     * @param aggregatedControls result list
     */
    private void aggregateControls(List<Control> controls, List<Control> aggregatedControls) {
        for(Control control : controls) {
            if(control instanceof Columns) {
                Columns columns = (Columns)control;
                aggregateControls(columns.getControls(), aggregatedControls);
            } else if(control instanceof Tabs) {
                Tabs tabs = (Tabs)control;
                for(Tab tab : tabs.getTabs()) {
                    aggregateControls(tab.getControls(), aggregatedControls);
                }
            } else {
                aggregatedControls.add(control);
            }
        }
    }

    /**
     * Finds all Label controls and resolves expressions encoded in them
     * @param object
     * @param controls
     */
    private void resolveLabelsExpressions(Object object, List<Control> controls) {
        controls.stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .forEach(label -> label.setExpression(expressionResolver.resolve(object, label.getExpression())));
    }

    /**
     * Finds all controls retrieving the data from the database and tries to retrieve that
     * data
     * @param controls
     */
    private void loadDictionaries(List<Control> controls) {
        controls.stream()
            .filter(ComboRadio.class::isInstance)
            .map(ComboRadio.class::cast)
            .forEach(this::loadDictionary);
    }

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio
     */
    private void loadDictionary(ComboRadio comboRadio) {
        if(StringUtils.isNotEmpty(comboRadio.getObjectType())) {
            Type type = typeRepository.findByTypeName(comboRadio.getObjectType());
            if (null != type) {
                EcmObject sampleObject = createNew(type.getClazz());
                Map<String, String> values = ecmObjectRepository.findAll(Example.of(sampleObject), Sort.by(Sort.Direction.ASC, "objectName")).stream()
                        .filter(ecmObject -> StringUtils.isNotEmpty(ecmObject.getObjectName())) // filter out incorrect customers with empty object name
                        .collect(Collectors.toMap(ecmObject -> ecmObject.getId().toString(), EcmObject::getObjectName));

                comboRadio.setValues(values);
            } else {
                log.error("Type '{}' was not found", comboRadio.getObjectType());
            }
        } else {
            log.info("Object type for combo/radio control is not provided. Values will be used.");
        }
    }

    /**
     * Creates empty instance of EcmObject or its subtype
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends EcmObject> T createNew(Class<T> clazz) {
        T contextDto = null;
        try {
            contextDto = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log.error("Cannot instantiate EcmObject object", e);
        }
        return contextDto;
    }
}
