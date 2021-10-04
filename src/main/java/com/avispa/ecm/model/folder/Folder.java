package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public final class Folder extends EcmObject {

    @OneToOne(fetch = FetchType.LAZY)
    private Folder ancestor;

    @Column(nullable = false)
    private String path;
}
