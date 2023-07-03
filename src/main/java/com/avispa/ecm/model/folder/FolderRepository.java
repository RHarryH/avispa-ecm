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

    List<Folder> findByFolderIsNull();

    @Query("select o from EcmObject o join Folder f on o.folder.id = f.id where ?1 member f.ancestors")
    List<EcmObject> findAllFoldersAndDocumentsDescend(Folder folder);

    @Query("select o from EcmObject o where o.folder = ?1")
    List<EcmObject> findAllFoldersAndDocuments(Folder folder);

    boolean existsEcmObjectByFolder(Folder folder);
}
