/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.walk.JsonSchemaWalkListener;
import com.networknt.schema.walk.WalkEvent;
import com.networknt.schema.walk.WalkFlow;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class JsonValidatorTest {

    private final JsonValidator jsonValidator = new JsonValidator(new ObjectMapper());

    @ParameterizedTest
    @ValueSource(strings = {
            "content/columns.json",
            "content/combo.json",
            "content/constraints.json",
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
            "content/groupNoVisibilityConstraints.json",
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
        return jsonValidator.validate(inputStream, "/json-schemas/property-page-content.json");
    }

    @Test
    @Disabled("Not an actual test")
    void testWalker() throws Exception {
        var svc = new SchemaValidatorsConfig();
        svc.addKeywordWalkListener("$ref", new JsonSchemaWalkListener() {
            @Override
            public WalkFlow onWalkStart(WalkEvent walkEvent) {
                if(!walkEvent.getAt().endsWith("conditions.visibility") &&
                   !walkEvent.getAt().endsWith("conditions.requirement")) {
                    return WalkFlow.CONTINUE;
                } else {
                    return WalkFlow.SKIP;
                }
            }

            @Override
            public void onWalkEnd(WalkEvent we, Set<ValidationMessage> validationMessages) {
                var ref = we.getSchemaNode().get("$ref").asText();
                var parent = we.getParentSchema().getCurrentUri();
                var target = we.getCurrentJsonSchemaFactory().getUriFactory().create(parent, ref);
                var schema = we.getRefSchema(target);
                var sn = (ObjectNode) we.getParentSchema().getSchemaNode();
                var field = fieldName(we.getSchemaPath());
                if(field == null) {
                    field = RandomStringUtils.randomAlphanumeric(10);
                }
                sn.set(field, schema.getSchemaNode());

            }

            private String fieldName(String schemaPath) {
                int idx = schemaPath.lastIndexOf("/");
                if(idx >= 0 && idx < schemaPath.length() - 1) {
                    return schemaPath.substring(idx + 1);
                }
                return null;
            }
        });

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        Resource resourceSchema = new ClassPathResource("/json-schemas/property-page-content.json");

        var schema = factory.getSchema(resourceSchema.getURI(), svc);
        schema.walk(null, false);
        System.out.println(schema.getSchemaNode().toPrettyString());

        assertTrue(true);
    }

}