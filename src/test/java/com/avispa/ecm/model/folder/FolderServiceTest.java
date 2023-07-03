/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.document.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@SpringBootTest
@Slf4j
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

    @Test
    void ancestorsTest() {
        Folder root = folderService.createNewFolder("root", null);
        Folder folder1_1 = folderService.createNewFolder("folder1.1", root);
        Folder folder2_1 = folderService.createNewFolder("folder2.1", root);
        Folder folder2_2 = folderService.createNewFolder("folder2.2", folder2_1);

        assertEquals(Set.of("root"), getAncestorsNames(root));
        assertEquals(Set.of("root", "folder1.1"), getAncestorsNames(folder1_1));
        assertEquals(Set.of("root", "folder2.1"), getAncestorsNames(folder2_1));
        assertEquals(Set.of("root", "folder2.1", "folder2.2"), getAncestorsNames(folder2_2));
    }

    private Set<String> getAncestorsNames(Folder folder) {
        return folder.getAncestors().stream().map(EcmObject::getObjectName).collect(Collectors.toSet());
    }

    @Test
    void getRootFolders() {
        Folder root = folderService.createNewFolder("root", null);
        Folder anotherRoot = folderService.createNewFolder("another root", null);

        folderService.createNewFolder("folder", root);

        List<Folder> folders = folderService.getRootFolders();

        assertEquals(List.of(root, anotherRoot), folders);
    }

    @Test
    void getAllFolderAndDocumentsTest() {
        Folder root = folderService.createNewFolder("root", null);
        Folder folder1_1 = folderService.createNewFolder("folder1.1", root);
        Folder folder2_1 = folderService.createNewFolder("folder2.1", root);
        Folder folder2_2 = folderService.createNewFolder("folder2.2", folder2_1);

        Document document = new Document();
        document.setObjectName("Document1");
        document.setFolder(folder1_1);
        documentRepository.save(document);

        Document document2 = new Document();
        document2.setObjectName("Document2");
        document2.setFolder(folder2_2);
        documentRepository.save(document2);

        List<EcmObject> objects = folderService.getAllFoldersAndLinkedObjects(root, true);
        Set<String> objectNames = objects.stream().map(EcmObject::getObjectName).collect(Collectors.toSet());
        assertEquals(Set.of("folder1.1", "folder2.1", "folder2.2", "Document1", "Document2"), objectNames);
    }

    @Test
    void getDirectFolderAndDocumentsTest() {
        Folder root = folderService.createNewFolder("root", null);
        Folder folder1_1 = folderService.createNewFolder("folder1.1", root);
        Folder folder2_1 = folderService.createNewFolder("folder2.1", root);
        Folder folder2_2 = folderService.createNewFolder("folder2.2", folder2_1);

        Document document = new Document();
        document.setObjectName("Document1");
        document.setFolder(folder1_1);
        documentRepository.save(document);

        Document document2 = new Document();
        document2.setObjectName("Document2");
        document2.setFolder(folder2_2);
        documentRepository.save(document2);

        List<EcmObject> objects = folderService.getAllFoldersAndLinkedObjects(folder2_1, true);
        Set<String> objectNames = objects.stream().map(EcmObject::getObjectName).collect(Collectors.toSet());
        assertEquals(Set.of("folder2.2", "Document2"), objectNames);
    }

    @Test
    void checkIfFolderIsEmpty() {
        Folder root = folderService.createNewFolder("root", null);
        Folder folder1_1 = folderService.createNewFolder("folder1.1", root);

        assertFalse(folderService.isEmpty(root));
        assertTrue(folderService.isEmpty(folder1_1));
    }
}