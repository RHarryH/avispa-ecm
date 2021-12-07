package com.avispa.ecm.util.json;

import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class JsonValidator {

    private JsonValidator() {

    }

    /**
     * Read, parse and validate JSON file. JSON schema file must be located in resources/jsonSchemas
     *
     * @param jsonContentFile
     * @param jsonSchemaPath
     * @return
     */
    public static boolean validateJson(File jsonContentFile, String jsonSchemaPath) {
        try {
            Resource resource = new ClassPathResource(jsonSchemaPath);

            // this line will generate JSON schema from your class
            JsonNode schemaNode = JsonLoader.fromFile(resource.getFile());

            // make your JSON to JsonNode
            JsonNode jsonToValidate = JsonLoader.fromFile(jsonContentFile);

            // validate it against the schema
            ProcessingReport validate = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode).validate(jsonToValidate);

            return validate.isSuccess();
        } catch (IOException | ProcessingException e) {
            log.error("Cannot verify input json", e);
        }

        return false;
    }
}
