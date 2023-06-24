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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonValidator {

    private final ObjectMapper objectMapper;

    /**
     * Read, parse and validate JSON file. JSON schema file must be located in resources/json-schemas
     *
     * @param jsonContentInputStream
     * @param jsonSchemaPath
     * @return
     */
    public boolean validate(InputStream jsonContentInputStream, String jsonSchemaPath) {
        try {
            // make your JSON to JsonNode
            JsonNode jsonToValidate = objectMapper.readTree(jsonContentInputStream);

            return validate(jsonSchemaPath, jsonToValidate);
        } catch (IOException e) {
            log.error("Cannot verify input json", e);
        }

        return false;
    }

    public boolean validate(byte[] jsonContent, String jsonSchemaPath) {
        try {
            // make your JSON to JsonNode
            JsonNode jsonToValidate = objectMapper.readTree(jsonContent);

            return validate(jsonSchemaPath, jsonToValidate);
        } catch (IOException e) {
            log.error("Cannot verify input json", e);
        }

        return false;
    }

    private boolean validate(String jsonSchemaPath, JsonNode jsonToValidate) throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        Resource resourceSchema = new ClassPathResource(jsonSchemaPath);
        URI resourceSchemaURI = resourceSchema.getURI();

        log.debug("URI to JSON Schema: " + resourceSchemaURI);

        JsonSchema jsonSchema = factory.getSchema(resourceSchemaURI);

        jsonSchema.initializeValidators();

        // validate it against the schema
        Set<ValidationMessage> errors = jsonSchema.validate(jsonToValidate);

        log(errors);

        return errors.isEmpty();
    }

    private void log(Set<ValidationMessage> errors) {
        for (ValidationMessage message : errors) {
            log.error(message.toString());
        }
    }
}
