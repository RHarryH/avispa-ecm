package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Service
public class FolderService {
    private FolderRepository folderRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public Folder findFolderByObjectNameAndPath(String name, String path) {
        return folderRepository.findFolderByObjectNameAndPath(name, path);
    }

    public Folder createNewFolder(String name, Folder ancestor) {
        Folder folder = new Folder();
        folder.setObjectName(name);

        folder.setPath(getFolderPath(ancestor));
        folder.setAncestor(ancestor);
        return folderRepository.save(folder);
    }

    /**
     * If there is no ancestor insert root path ("/").
     * If there is ancestor but it is a root folder then just append ancestor name
     * If there is ancestor and it is not a root folder then append slash and ancestor name
     * @param ancestor
     * @return
     */
    private String getFolderPath(Folder ancestor) {
        if(null != ancestor) {
            String ancestorPath = ancestor.getPath();

            if(ancestorPath.equals("/")) {
                return ancestorPath + ancestor.getObjectName();
            } else {
                return ancestorPath + "/" + ancestor.getObjectName();
            }
        } else {
            return "/";
        }
    }

    public List<Document> getDocumentsInFolder(Folder folder) {
        return folderRepository.findDocumentsByFolderId(folder.getId());
    }

    public List<Folder> getFoldersInFolder(Folder folder) {
        return folderRepository.findNestedFoldersByFolderId(folder.getId());
    }
}
