package com.avispa.ecm.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
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
//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ecm_object")
public abstract class EcmObject implements EcmEntity {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /*@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @ColumnDefault("random_uuid()") // this function will be used when running manual inserts*/
    @Type(type = "uuid-char") // do not store as binary type
    @Column(name = "uuid", updatable = false, nullable = false)
    @Getter
    private final UUID uuid = UUID.randomUUID();

    @Getter
    @Setter
    private String objectName;

    @Getter
    private LocalDateTime creationDate;

    @Getter
    private LocalDateTime modificationDate;

    @Version
    private int version;

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
                && Objects.equals(uuid, ((EcmObject) that).uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
