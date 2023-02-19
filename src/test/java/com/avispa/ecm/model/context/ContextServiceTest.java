package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.callable.autolink.Autolink;
import com.avispa.ecm.model.configuration.callable.autoname.Autoname;
import com.avispa.ecm.model.configuration.callable.autoname.AutonameService;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.condition.ConditionParser;
import com.avispa.ecm.util.condition.ConditionResolver;
import com.avispa.ecm.util.condition.ConditionService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.json.JsonValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
@AutoConfigureJson
@Sql("/basic-configuration.sql")
@Import({ContextService.class,
        // required to add service to list of available services
        AutonameService.class,
        ExpressionResolver.class,
        ConditionService.class,
        ConditionResolver.class,
        ConditionParser.class,
        JsonValidator.class})
class ContextServiceTest {
    @Autowired
    private EcmObjectRepository<EcmObject> ecmObjectRepository;

    @Autowired
    private EcmConfigRepository<EcmConfig> ecmConfigRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private ContextService contextService;

    private Type documentType;
    private Type testDocumentType;

    @BeforeEach
    void init() {
        documentType = typeRepository.findByTypeName("Document");

        // this is not a default type so create it
        testDocumentType = typeRepository.findByObjectName("Test Document").orElseGet(() -> {
                testDocumentType = new Type();
                testDocumentType.setObjectName("Test Document");
                testDocumentType.setEntityClass(TestDocument.class);

                return typeRepository.save(testDocumentType);
        });
    }

    @Test
    void findConfigurationsForContextAcceptingDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void throwExceptionWhenInputJsonContainsNonExitingField() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\", \"testString\": \"Test string\"}", autoname);

        assertThrows(IllegalArgumentException.class, () -> contextService.getConfigurations(document));
    }

    @Test
    void findConfigurationsForContextAcceptingTestDocumentWhenInputIsDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        createContext(testDocumentType, "{ \"objectName\": \"It's me\"}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertTrue(configurations.isEmpty());
    }

    /**
     * In this test we're trying to match TestDocument for context accepting
     * only Documents or above. This test should return true because TestDocument
     * is a subtype of Document.
     */
    @Test
    void givenTestDocumentAndContextAcceptingDocuments_whenGetMatchinConfigurations_thenAutonameIsPresent() {
        TestDocument document = createTestDocument();
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\"}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingAllDocumentOfCertainType() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        createContext(documentType, "{}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void givenDocumentContextWithEmptyRuleAndNewDocument_whenGetConfigurations_thenReturnDocumentContext() {
        Document document = new Document();
        Autoname autoname = createAutoname();

        createContext(documentType, "{}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void givenTestDocumentContextWithEmptyRuleAndNewDocument_whenGetConfigurations_thenReturnNothing() {
        Document document = new Document();
        Autoname autoname = createAutoname();

        createContext(testDocumentType, "{}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertTrue(configurations.isEmpty());
    }

    /**
     *  We're matching for document with 'It's another me' name while context requires 'It's me' document. This document
     *  exists in the repository.
     */
    @Test
    void dontFindConfigurationsForDocumentWhileOtherDocumentMatchingConditionsIsInRepository() {
        createDocument();
        Document document = createDocument("It's another me");
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\"}", autoname);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertTrue(configurations.isEmpty());
    }

    @Test
    void runConfigurationAutomatically() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);

        contextService.applyMatchingConfigurations(document, Autoname.class);

        assertEquals("F/Test string does not exist", document.getObjectName());
    }

    @Test
    void givenContextWithTwoAutonameConfigurationAndAutolink_whenGetConfigurations_thenReturnOnlyFirstAutonameAndAutolink() {
        Document document = createDocument();
        Autoname autoname = createAutoname();
        Autoname autoname2 = createAutoname("Second sample autoname");
        Autolink autolink = createAutolink();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname, autolink, autoname2);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertEquals(2, configurations.size());
    }

    @Test
    void givenContextsWithSameConfigType_whenGetConfigurations_thenReturnConfigFromHigherImportanceContext() {
        Document document = createDocument();
        Autoname autoname = createAutoname();
        Autoname autoname2 = createAutoname("Second sample autoname");

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);
        createContext(documentType, "{ \"objectName\": \"It's me\" }", 1, autoname2);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertEquals(1, configurations.size());
        assertTrue(configurations.contains(autoname2));
    }

    @Test
    void givenContextWithMultipleConfigurations_whenGetConfiguration_thenReturnAutonaming() {
        Document document = createDocument();
        Autoname autoname = createAutoname();
        Autolink autolink = createAutolink();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname, autolink);

        Optional<Autolink> autolinkConfig = contextService.getConfiguration(document, Autolink.class);

        assertTrue(autolinkConfig.isPresent());
        assertEquals(autolink,autolinkConfig.get());
    }

    @Test
    void givenContextWithMultipleConfigurations_whenGetConfiguration_thenReturnEmptyOptional() {
        Document document = createDocument();
        Autoname autoname = createAutoname();
        Autolink autolink = createAutolink();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname, autolink);

        Optional<PropertyPage> propertyPageConfig = contextService.getConfiguration(document, PropertyPage.class);

        assertFalse(propertyPageConfig.isPresent());
    }

    @Autowired
    private ContextRepository contextRepository;

    @Test
    void manyContexts() {
        Autoname autoname = createAutoname();
        Autolink autolink = createAutolink();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);
        createContext(documentType, "{ \"objectName\": \"It's me\" }", 1, autolink);

        assertEquals(2, contextRepository.findAll().size());
    }

    @Test
    void configurationSharing() {
        Autoname autoname = createAutoname();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);
        createContext(documentType, "{ \"objectName\": \"It's me\" }", 1, autoname);

        assertEquals(2, contextRepository.findAll().size());
    }

    @Test
    void givenTwoMatchingContexts_whenGetMatchingConfigurations_thenReturnAllConfigurationsFromBothContexts() {
        Document document = createDocument();
        Autoname autoname = createAutoname();
        Autolink autolink = createAutolink();

        createContext(documentType, "{ \"objectName\": \"It's me\" }", autoname);
        createContext(documentType, "{ \"objectName\": \"It's me\" }", 1, autolink);

        List<EcmConfig> configurations = contextService.getConfigurations(document);

        assertEquals(2, configurations.size());
    }

    @Test
    void givenGeneralAndSpecificContexts_whenGetMatchingConfigurations_thenReturnOnlyGeneralConfig() {
        Autoname autoname = createAutoname();
        Autoname autoname2 = createAutoname("Another autoname");

        createContext(documentType, "{}", autoname);
        createContext(documentType, "{ \"objectName\": \"It's me\" }", 1, autoname2);

        Optional<Autoname> configuration = contextService.getConfiguration(Document.class, Autoname.class);

        assertTrue(configuration.isPresent());
        assertEquals(autoname, configuration.get());
    }

    private Document createDocument() {
        return createDocument("It's me");
    }

    private Document createDocument(String objectName) {
        Document document = new Document();
        document.setObjectName(objectName);
        ecmObjectRepository.save(document);
        return document;
    }

    private TestDocument createTestDocument() {
        TestDocument document = new TestDocument();
        document.setObjectName("It's me");
        document.setTestString("Test String");
        ecmObjectRepository.save(document);
        return document;
    }

    private Autoname createAutoname() {
        return createAutoname("Sample autoname");
    }

    private Autoname createAutoname(String objectName) {
        Autoname autoname = new Autoname();
        autoname.setObjectName(objectName);
        autoname.setRule("'F/' + $default($value('testString'), 'Test string does not exist')");
        autoname.setPropertyName("objectName");
        ecmConfigRepository.save(autoname);
        return autoname;
    }

    private Autolink createAutolink() {
        Autolink autolink = new Autolink();
        autolink.setObjectName("Sample autolink");
        autolink.addRule("ABC");
        autolink.addRule("DEF");
        autolink.addRule("GHI");
        ecmConfigRepository.save(autolink);
        return autolink;
    }

    private void createContext(Type type, String rule, EcmConfig... ecmConfigs) {
        createContext(type, rule, 0, ecmConfigs);
    }

    private void createContext(Type type, String rule, int importance, EcmConfig... ecmConfigs) {
        Context context = new Context();
        context.setObjectName(RandomStringUtils.randomAlphanumeric(10));
        context.setEcmConfigs(List.of(ecmConfigs));
        context.setType(type);
        context.setMatchRule(rule);
        context.setImportance(importance);
        ecmObjectRepository.save(context);
    }
}