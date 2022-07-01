package com.avispa.ecm.util.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.walk.JsonSchemaWalkListener;
import com.networknt.schema.walk.WalkEvent;
import com.networknt.schema.walk.WalkFlow;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class JsonValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "content/columns.json",
            "content/combo.json",
            "content/date.json",
            "content/group.json",
            "content/money.json",
            "content/number.json",
            "content/radio.json",
            "content/table.json",
            "content/tabs.json",
            "content/textarea.json"
    })
    void validatePropertyPageSchema(String jsonFilePath) {
        assertTrue(validate(jsonFilePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "content/columnsNoItems.json",
            "content/comboInvalidProperty.json",
            "content/groupNoItems.json",
            "content/groupNoVisibilityConditions.json",
            "content/numberForbiddenProperties.json",
            "content/tableForbiddenProperties.json",
            "content/tableNoItems.json",
            "content/tabsGroupNesting.json",
            "content/tabsNoItems.json",
            "content/tabsNoControlItems.json",
    })
    void validatePropertyPageSchemaNegative(String jsonFilePath) {
        assertFalse(validate(jsonFilePath));
    }

    private boolean validate(String jsonFilePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
        assertTrue(JsonValidator.validateJson(inputStream, "/json-schemas/property-page-content.json"));
    }

}