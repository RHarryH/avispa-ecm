/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.model;

import com.avispa.ecm.model.content.Content;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
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
                        .orElseGet(() -> contents.stream().min(Comparator.comparing(EcmObject::getCreationDate)).orElse(null));
    }

    @Override
    public final boolean equals(Object that) {
        return this == that || that instanceof EcmEntity thatEntity
                && Objects.equals(id, thatEntity.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}
