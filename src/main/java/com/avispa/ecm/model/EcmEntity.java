package com.avispa.ecm.model;

import com.avispa.ecm.model.content.Content;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public abstract class EcmEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char") // do not store as binary type
    // TODO: hibernate annotation, consider use of columnDefinition
    @ColumnDefault("random_uuid()") // this function will be used when running manual inserts
    private UUID id;

    private String objectName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "relatedEntity", cascade = CascadeType.ALL)
    private Set<Content> contents;

    @Version
    private Integer version;

    /**
     * Returns true if document has PDF rendition or is already a pdf document
     * @return
     */
    @JsonIgnore
    public boolean isPdfRenditionAvailable() {
        return null != contents && contents.stream().anyMatch(Content::isPdf);
    }

    /**
     * Assign content to the contents set
     * @param content
     */
    public void addContent(Content content) {
        if(null == this.contents) {
            this.contents = new HashSet<>();
        }
        this.contents.add(content);
    }

    /**
     * Returns primary content. Always PDF rendition has precedence over other formats. If not present then first
     * content file will be considered as primary. The first content file means the one with the earliest
     * creation date.
     * @return
     */
    public Content getPrimaryContent() {
        return null == this.contents ? null :
                this.contents.stream().filter(Content::isPdf).findFirst()
                        .orElse(contents.stream().min(Comparator.comparing(EcmObject::getCreationDate)).orElse(null));
    }

    @Override
    public final boolean equals(Object that) {
        return this == that || that instanceof EcmEntity
                && Objects.equals(id, ((EcmEntity) that).id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}
