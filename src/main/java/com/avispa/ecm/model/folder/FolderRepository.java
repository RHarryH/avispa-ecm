package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.document.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface FolderRepository extends EcmObjectRepository<Folder> {
    Folder findFolderByObjectNameAndPath(String folderName, String path);

    @Query("select d from Document d join Folder f on d.folder.id = f.id where f.id = ?1")
    List<Document> findDocumentsByFolderId(UUID folderId);

    @Query("select f from Folder f where f.ancestor.id = ?1")
    List<Folder> findNestedFoldersByFolderId(UUID folderId);
}
