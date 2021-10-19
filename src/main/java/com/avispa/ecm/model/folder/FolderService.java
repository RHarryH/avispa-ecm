package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.document.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;

    public Folder findFolderByNameAndAncestor(String name, Folder ancestor) {
        return folderRepository.findFolderByObjectNameAndPath(name, getFolderPath(name, ancestor));
    }

    public Folder createNewFolder(String name, Folder ancestor) {
        Folder folder = new Folder();
        folder.setObjectName(name);

        folder.setPath(getFolderPath(name, ancestor));
        folder.setAncestor(ancestor);
        return folderRepository.save(folder);
    }

    /**
     * If there is no ancestor insert root path ("/").
     * If there is ancestor but it is a root folder then just append ancestor name
     * If there is ancestor and it is not a root folder then append slash and ancestor name
     * @param name
     * @param ancestor
     * @return
     */
    private String getFolderPath(String name, Folder ancestor) {
        String ancestorPath = "";
        if(null != ancestor) {
            ancestorPath = ancestor.getPath();
        }

        return ancestorPath + "/" + name;
    }

    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    public List<Document> getDocumentsInFolder(Folder folder) {
        return folderRepository.findDocumentsByFolderId(folder.getId());
    }

    public List<Folder> getFoldersInFolder(Folder folder) {
        return folderRepository.findNestedFoldersByFolderId(folder.getId());
    }
}
