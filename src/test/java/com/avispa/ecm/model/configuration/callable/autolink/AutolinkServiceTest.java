package com.avispa.ecm.model.configuration.callable.autolink;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.folder.Folder;
import com.avispa.ecm.model.folder.FolderService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
// it is used only to test persistence layer thus it loads only part of context - repositories and so on
@DataJpaTest
// adding classes not related to data jpa test but required by tests
// optionally I could use just SpringBootTest but this disables tests slicing
@Import({AutolinkService.class, FolderService.class, ExpressionResolver.class})
@Slf4j
class AutolinkServiceTest {
    @Autowired
    private AutolinkService autolinkService;

    private static Document document;
    private static Document document2;

    @BeforeAll
    static void init() {
        document = new Document();
        document.setObjectName("Document");

        document2 = new Document();
        document2.setObjectName("Document 2");
    }

    @Test
    void simpleTest() {
        // given
        Autolink autolink = new Autolink();
        autolink.addRule("'ABC'");
        autolink.addRule("'DEF'");
        autolink.addRule("'GHI'");

        // when
        autolinkService.apply(autolink, document);

        // then
        Folder folder = document.getFolder();
        assertEquals("GHI", folder.getObjectName());
        assertEquals("/ABC/DEF/GHI", folder.getPath());
    }

    @Test
    void unresolvableExpressionButNotEmptyFolderName() {
        // given
        Autolink autolink = new Autolink();
        autolink.addRule("'ABC_' + $value('nonExistingValue')");
        autolink.setDefaultValue("Unknown");

        // when
        autolinkService.apply(autolink, document);

        // then
        Folder folder = document.getFolder();
        assertEquals("ABC_", folder.getObjectName());
        assertEquals("/ABC_", folder.getPath());
    }

    @Test
    void unresolvableExpressionAndEmptyFolderName() {
        // given
        Autolink autolink = new Autolink();
        autolink.addRule("$value('nonExistingValue')");
        autolink.setDefaultValue("Unknown");

        // when
        autolinkService.apply(autolink, document);

        // then
        Folder folder = document.getFolder();
        assertEquals("Unknown", folder.getObjectName());
        assertEquals("/Unknown", folder.getPath());
    }

    @Test
    void folderSharing() { // if documents lie in the same folder, existing folders should be used
        // given
        Autolink autolink = new Autolink();
        autolink.addRule("'ABC'");
        autolink.addRule("'DEF'");

        // when
        autolinkService.apply(autolink, document);
        autolinkService.apply(autolink, document2);

        // then
        assertEquals(document.getFolder().getId(), document2.getFolder().getId());
    }
}