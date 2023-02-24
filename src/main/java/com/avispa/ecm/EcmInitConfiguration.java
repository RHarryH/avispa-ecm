package com.avispa.ecm;

import com.avispa.ecm.model.configuration.propertypage.PropertyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author Rafał Hiszpański
 */
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
@Profile("!test")
public class EcmInitConfiguration {
    private final PropertyPageService propertyPageService;

    //@EventListener(ApplicationReadyEvent.class)
    @EventListener(ContextRefreshedEvent.class) // after bean creation but before the server starts
    public void loadDefaultConfiguration() {
        propertyPageService.loadContent("Folder property page", "classpath:/content/Folder property page content.json");
        propertyPageService.loadContent("Document property page", "classpath:/content/Document property page content.json");
    }
}
