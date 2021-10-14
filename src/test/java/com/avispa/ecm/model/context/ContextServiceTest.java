package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.configuration.autoname.Autoname;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.util.expression.SuperDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
@Import({ContextService.class})
@DataJpaTest
class ContextServiceTest {
    @Autowired
    private EcmObjectRepository ecmObjectRepository;

    @Autowired
    private ContextService contextService;

    @Test
    void findConfigurationsForContextAcceptingDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setEcmConfigObjects(List.of(autoname));
        context.setType((Type) ecmObjectRepository.findByObjectName("Document"));
        context.setMatchRule("{ \"objectName\": \"Its me\" }");
        ecmObjectRepository.save(context);

        List<EcmConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingDocumentWhenInputJsonContainsNonExitingField() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setEcmConfigObjects(List.of(autoname));
        context.setType((Type) ecmObjectRepository.findByObjectName("Document"));
        context.setMatchRule("{ \"objectName\": \"Its me\", \"extraField\": \"Extra field\"}");
        ecmObjectRepository.save(context);

        List<EcmConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingSuperDocumentWhenInputIsDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setEcmConfigObjects(List.of(autoname));
        context.setType((Type) ecmObjectRepository.findByObjectName("Document"));
        context.setMatchRule("{ \"objectName\": \"Its me\"}");
        ecmObjectRepository.save(context);

        List<EcmConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingDocumentWhenInputIsSuperDocument() {
        SuperDocument document = createSuperDocument();
        Autoname autoname = createAutoname();

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setEcmConfigObjects(List.of(autoname));
        context.setType((Type) ecmObjectRepository.findByObjectName("Document"));
        context.setMatchRule("{ \"objectName\": \"Its me\"}");
        ecmObjectRepository.save(context);

        List<EcmConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertTrue(configurations.isEmpty());
    }

    @Test
    void findConfigurationsForContextAcceptingAllDocumentOfCertainType() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setEcmConfigObjects(List.of(autoname));
        context.setType((Type) ecmObjectRepository.findByObjectName("Document"));
        context.setMatchRule("{}");
        ecmObjectRepository.save(context);

        List<EcmConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertFalse(configurations.isEmpty());
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    private Document createDocument() {
        Document document = new Document();
        document.setObjectName("Its me");
        ecmObjectRepository.save(document);
        return document;
    }

    private SuperDocument createSuperDocument() {
        SuperDocument document = new SuperDocument();
        document.setObjectName("Its me");
        document.setExtraField("Extra field");
        ecmObjectRepository.save(document);
        return document;
    }

    private Autoname createAutoname() {
        Autoname autoname = new Autoname();
        autoname.setObjectName("Sample autoname");
        autoname.setRule("F/$datevalue(creationDate)");
        autoname.setPropertyName("objectName");
        ecmObjectRepository.save(autoname);
        return autoname;
    }
}