package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.document.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@SpringBootTest
class FolderServiceTest {
    private FolderService folderService;
    private DocumentRepository documentRepository;

    @Autowired
    public FolderServiceTest(FolderService folderService, DocumentRepository documentRepository) {
        this.folderService = folderService;
        this.documentRepository = documentRepository;
    }

    @Test
    void getDocumentsInFolder() {
        Folder folder = folderService.createNewFolder("Folder1", null);

        Document document = new Document();
        document.setObjectName("Document1");
        document.setFolder(folder);
        documentRepository.save(document);

        Document document2 = new Document();
        document2.setObjectName("Document2");
        document2.setFolder(folder);
        documentRepository.save(document2);

        List<EcmObject> documents = folderService.getObjectsInFolder(folder);
        assertEquals(2, documents.size());
        assertEquals("Document1", documents.get(0).getObjectName());
        assertEquals("Document2", documents.get(1).getObjectName());
    }
}