package com.avispa.ecm.model.folder;

import com.avispa.ecm.model.EcmObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public final class Folder extends EcmObject {

    @Column(nullable = false)
    private String path;

    @ManyToMany
    @OrderColumn(name = "distance")
    private List<Folder> ancestors;
}
