package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
// it is used only to test persistence layer thus it loads only part of context - repositories and so on
//@DataJpaTest
// adding classes not related to data jpa test but required by tests
// optionally I could use just SpringBootTest but this disables tests slicing
// can be also done using @TestConfiguration
//@Import({AutonameService.class, ExpressionResolver.class})
@Slf4j
class AutonameServiceTest {
    @Autowired
    private AutonameService autonameService;

    private static Document document;

    @TestConfiguration
    static class ContextServiceTestContextConfiguration {
        @Bean
        public AutonameService autonameService() {
            return new AutonameService(new ExpressionResolver());
        }
    }

    @BeforeAll
    static void init() {
        document = new Document();
        document.setObjectName("Document");
    }

    @Test
    void simpleTest() {
        Autoname autoname = new Autoname();
        autoname.setRule("NewName");
        autoname.setPropertyName("objectName");
        autonameService.apply(autoname, document);

        assertEquals("NewName", document.getObjectName());
    }
}