package com.avispa.ecm.model.configuration.autolink;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.folder.Folder;
import com.avispa.ecm.model.folder.FolderService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
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

    @BeforeAll
    static void init() {
        document = new Document();
        document.setObjectName("Document");
    }

    @Test
    void simpleTest() {
        // given
        Autolink autolink = new Autolink();
        autolink.addRule("ABC");
        autolink.addRule("DEF");
        autolink.addRule("GHI");

        // when
        autolinkService.apply(autolink, document);

        // then
        Folder folder = document.getFolder();
        assertEquals("GHI", folder.getObjectName());
        assertEquals("/ABC/DEF", folder.getPath());
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
        assertEquals("/", folder.getPath());
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
        assertEquals("/", folder.getPath());
    }
}