package com.avispa.ecm.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ecm_object")
public abstract class EcmObject implements EcmEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Type(type="org.hibernate.type.UUIDCharType")
    @Type(type = "uuid-char") // do not store as binary type
    //@Column(name = "id", updatable = false, nullable = false)
    // TODO: hibernate annotation, consider use of columnDefinition
    @ColumnDefault("random_uuid()") // this function will be used when running manual inserts
    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    private String objectName;

    @Getter
    private LocalDateTime creationDate;

    @Getter
    private LocalDateTime modificationDate;

    @Version
    private Integer version;

    @PrePersist
    private void prePersist() {
        modificationDate = creationDate = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        modificationDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof EcmObject
                && Objects.equals(id, ((EcmObject) that).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
