package com.avispa.ecm.model;

import com.avispa.ecm.model.folder.Folder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class EcmObject extends EcmEntity {

    @Getter
    @CreatedDate
    private LocalDateTime creationDate;

    @Getter
    @LastModifiedDate
    private LocalDateTime modificationDate;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Folder folder;
}
