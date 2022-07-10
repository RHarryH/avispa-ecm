package com.avispa.ecm;

import com.avispa.ecm.model.configuration.propertypage.PropertyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author Rafał Hiszpański
 */
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class EcmInitConfiguration {
    private final PropertyPageService propertyPageService;

    //@EventListener(ApplicationReadyEvent.class)
    @EventListener(ContextRefreshedEvent.class) // after bean creation but before the server starts
    public void doSomethingAfterStartup() {
        propertyPageService.loadContentTo("Folder property page", "classpath:/content/Folder property page content.json");
        propertyPageService.loadContentTo("Document property page", "classpath:/content/Document property page content.json");
    }
}
