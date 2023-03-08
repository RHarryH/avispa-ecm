package com.avispa.ecm.model.configuration.load;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * This class implements Spring event allowing for automated loading of ECM configuration on Spring context startup.
 * Useful for test purposes.
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class EcmConfigurationAutoLoader {

    private final ConfigurationLoadService configurationLoadService;

    @Value("${avispa.ecm.configuration.paths:}")
    private String[] configurationPaths;

    @Value("${avispa.ecm.configuration.overwrite:false}")
    private boolean overwrite;

    @EventListener(ApplicationReadyEvent.class) // after bean creation but before the server starts
    public void loadConfigurations() {
        for(String configurationPath : configurationPaths) {
            configurationLoadService.load(Path.of(configurationPath), overwrite);
        }
    }
}
