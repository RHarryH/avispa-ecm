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

package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.util.exception.EcmException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Slf4j
public final class Content extends EcmObject {

    @ManyToOne
    @JoinColumn(nullable = false)
    @Setter
    private Format format;

    private long size;
    @Setter
    private String fileStorePath; // path in the file store (physical path)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_entity_id", nullable = false)
    @Setter
    private EcmEntity relatedEntity;

    @PrePersist
    @PreUpdate
    private void prePersistAndUpdate() {
        updateSize();
    }

    /**
     * Automatically update content size
     */
    private void updateSize() {
        try {
            Path path = Path.of(fileStorePath);
            if(Files.exists(path)) {
                size = Files.size(path);
            }
        } catch (IOException e) {
            log.error("Can't determine the size of the content file", e);
            size = -1;
        }
    }

    @PostRemove
    private void removeFile() {
        try {
            Path path = Path.of(fileStorePath);
            if(Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            log.error("Can't delete '{}' content file", fileStorePath, e);
            throw new EcmException("Can't delete content file because it is in use by a different process");
        }
    }

    public boolean isPdf() {
        return format.isPdf();
    }
}
