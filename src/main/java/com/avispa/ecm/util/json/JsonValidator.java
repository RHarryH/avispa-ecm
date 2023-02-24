package com.avispa.ecm.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JsonValidator {

    private ObjectMapper objectMapper;

    @Autowired
    public JsonValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Read, parse and validate JSON file. JSON schema file must be located in resources/jsonSchemas
     *
     * @param jsonContentInputStream
     * @param jsonSchemaPath
     * @return
     */
    public boolean validate(InputStream jsonContentInputStream, String jsonSchemaPath) {
        try {
            // make your JSON to JsonNode
            JsonNode jsonToValidate = objectMapper.readTree(jsonContentInputStream);

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
        } catch (IOException e) {
            log.error("Cannot verify input json", e);
        }

        return false;
    }

    private void log(Set<ValidationMessage> errors) {
        for (ValidationMessage message : errors) {
            log.error(message.toString());
        }
    }
}
