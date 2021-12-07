package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
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

    @Query("select o from EcmObject o join Folder f on o.folder.id = f.id where f.id = ?1")
    List<EcmObject> findObjectsByFolderId(UUID folderId);

    @Query("select f from Folder f where f.folder.id = ?1")
    List<Folder> findNestedFoldersByFolderId(UUID folderId);
}
