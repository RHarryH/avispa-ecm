package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        folder.setFolder(ancestor);

        List<Folder> ancestors = new ArrayList<>();
        ancestors.add(folder); // add self to ancestors list
        if(null != ancestor) {
            ancestors.addAll(ancestor.getAncestors()); // add all previous ancestors
        }
        folder.setAncestors(ancestors);
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

    public List<EcmObject> getObjectsInFolder(Folder folder) {
        return folderRepository.findObjectsByFolderId(folder.getId());
    }

    public List<Folder> getFoldersInFolder(Folder folder) {
        return folderRepository.findNestedFoldersByFolderId(folder.getId());
    }

    /**
     * Returns list of folders, which does not have any ancestors
     * @return
     */
    public List<Folder> getRootFolders() {
        return folderRepository.findByFolderIsNull();
    }

    /**
     * Returns list of all folders and objects linked to them.
     * @param root starting point
     * @param descend if true, returns also indirect results (folders and documents inside root folders). Otherwise,
     *                only direct results are considered
     * @return
     */
    public List<EcmObject> getAllFoldersAndLinkedObjects(Folder root, boolean descend) {
        return descend ? folderRepository.findAllFoldersAndDocumentsDescend(root) :
                         folderRepository.findAllFoldersAndDocuments(root);
    }
}
