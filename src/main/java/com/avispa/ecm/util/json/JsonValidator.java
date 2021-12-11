package com.avispa.ecm.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

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
     * @param jsonContentInputStream
     * @param jsonSchemaPath
     * @return
     */
    public static boolean validateJson(InputStream jsonContentInputStream, String jsonSchemaPath) {
        try {
            // make your JSON to JsonNode
            JsonNode jsonToValidate = JsonLoader.fromReader(new InputStreamReader(jsonContentInputStream));

            Resource resource = new ClassPathResource(jsonSchemaPath);
            URI uri = resource.getURI();

            if(log.isDebugEnabled()) {
                log.debug("URI to JSON Schema: " + uri);
            }

            // validate it against the schema
            ProcessingReport report = JsonSchemaFactory.byDefault().getJsonSchema(uri.toString()).validate(jsonToValidate);

            log(report);

            return report.isSuccess();
        } catch (IOException | ProcessingException e) {
            log.error("Cannot verify input json", e);
        }

        return false;
    }

    private static void log(ProcessingReport report) {
        for (ProcessingMessage message : report) {
            if (message.getLogLevel().equals(LogLevel.ERROR)) {
                log.error(message.toString());
            }
        }
    }
}
