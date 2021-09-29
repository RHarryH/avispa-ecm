package com.avispa.cms.model.context;

import com.avispa.cms.model.CmsObjectRepository;
import com.avispa.cms.model.configuration.CmsConfigObject;
import com.avispa.cms.model.configuration.autoname.Autoname;
import com.avispa.cms.model.document.Document;
import com.avispa.cms.model.type.Type;
import com.avispa.cms.util.expression.SuperDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
@Import({ContextService.class})
class ContextServiceTest {
    @MockBean
    private CmsObjectRepository cmsObjectRepository;

    @MockBean
    private ContextRepository contextRepository;

    @Autowired
    private ContextService contextService;

    @Test
    void findConfigurationsForContextAcceptingDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Type type = new Type();
        type.setClazz(Document.class);

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setCmsConfigObjects(List.of(autoname));
        context.setType(type);
        context.setMatchRule("{ \"objectName\": \"Its me\" }");

        when(contextRepository.findAll()).thenReturn(List.of(context));
        when(cmsObjectRepository.findOne(any())).thenReturn(Optional.of(document));

        List<CmsConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertTrue(configurations.size() > 0);
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingDocumentWhenInputJsonContainsNonExitingField() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Type type = new Type();
        type.setClazz(Document.class);

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setCmsConfigObjects(List.of(autoname));
        context.setType(type);
        context.setMatchRule("{ \"objectName\": \"Its me\", \"extraField\": \"Extra field\"}");

        when(contextRepository.findAll()).thenReturn(List.of(context));
        when(cmsObjectRepository.findOne(any())).thenReturn(Optional.of(document));

        List<CmsConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertTrue(configurations.size() > 0);
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingSuperDocumentWhenInputIsDocument() {
        Document document = createDocument();
        Autoname autoname = createAutoname();

        Type type = new Type();
        type.setClazz(SuperDocument.class);

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setCmsConfigObjects(List.of(autoname));
        context.setType(type);
        context.setMatchRule("{ \"objectName\": \"Its me\"}");

        when(contextRepository.findAll()).thenReturn(List.of(context));
        when(cmsObjectRepository.findOne(any())).thenReturn(Optional.of(document));

        List<CmsConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertTrue(configurations.size() > 0);
        assertEquals(autoname.getId(), configurations.get(0).getId());
    }

    @Test
    void findConfigurationsForContextAcceptingDocumentWhenInputIsSuperDocument() {
        SuperDocument document = createSuperDocument();
        Autoname autoname = createAutoname();

        Type type = new Type();
        type.setClazz(Document.class);

        Context context = new Context();
        context.setObjectName("Sample context");
        context.setCmsConfigObjects(List.of(autoname));
        context.setType(type);
        context.setMatchRule("{ \"objectName\": \"Its me\"}");

        when(contextRepository.findAll()).thenReturn(List.of(context));
        when(cmsObjectRepository.findOne(any())).thenReturn(Optional.of(document));

        List<CmsConfigObject> configurations = contextService.getMatchingConfigurations(document);

        assertTrue(configurations.isEmpty());
    }

    private Document createDocument() {
        Document document = new Document();
        document.setObjectName("Its me");
        return document;
    }

    private SuperDocument createSuperDocument() {
        SuperDocument document = new SuperDocument();
        document.setObjectName("Its me");
        document.setExtraField("Extra field");
        return document;
    }

    private Autoname createAutoname() {
        Autoname autoname = new Autoname();
        autoname.setObjectName("Sample autoname");
        autoname.setRule("F/$datevalue(creationDate)");
        autoname.setPropertyName("objectName");
        return autoname;
    }
}