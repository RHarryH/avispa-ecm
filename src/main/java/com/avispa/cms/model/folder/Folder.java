package com.avispa.cms.model.folder;

import com.avispa.cms.model.CmsObject;
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
public final class Folder extends CmsObject {

    @OneToOne(fetch = FetchType.LAZY)
    private Folder ancestor;

    @Column(nullable = false)
    private String path;
}
