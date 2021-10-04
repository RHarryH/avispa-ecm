package com.avispa.ecm.model.document;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.folder.Folder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Document extends EcmObject {

    @OneToOne(fetch = FetchType.LAZY)
    private Folder folder;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Content> contents;

    /**
     * Returns true if document has pdf rendition or is already a pdf document
     * @return
     */
    public boolean hasPdfRendition() {
        for(Content content : contents) {
            if(content.isPdf()) {
                return true;
            }
        }

        return false;
    }
}
