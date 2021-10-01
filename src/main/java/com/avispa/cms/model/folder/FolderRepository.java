package com.avispa.cms.model.folder;

import com.avispa.cms.model.CmsObjectRepository;
import com.avispa.cms.model.document.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface FolderRepository extends CmsObjectRepository<Folder> {
    Folder findFolderByObjectNameAndPath(String folderName, String path);

    @Query("select d from Document d join Folder f on d.folder.id = f.id where f.id = ?1")
    List<Document> findDocumentsByFolderId(Long folderId);

    @Query("select f from Folder f where f.ancestor.id = ?1")
    List<Folder> findNestedFoldersByFolderId(Long folderId);
}
