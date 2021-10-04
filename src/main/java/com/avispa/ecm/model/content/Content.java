package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.document.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.avispa.ecm.util.Formats.PDF;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Slf4j
public final class Content extends EcmObject {

    @Setter
    private String extension;
    private long size;
    @Setter
    private String fileStorePath; // path in the file store (physical path)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="document_id", nullable=false)
    @Setter
    private Document document;

    @PrePersist
    @PreUpdate
    private void updateSize() {
        try {
            size = Files.size(Path.of(fileStorePath));
        } catch (IOException e) {
            log.error("Can't determine the size of the content file", e);
            size = -1;
        }
    }

    @PostRemove
    private void removeFile() {
        try {
            Files.delete(Path.of(fileStorePath));
        } catch (IOException e) {
            log.error("Can't delete '{}' content file", fileStorePath, e);
            throw new IllegalStateException();
        }
    }

    public boolean isPdf() {
        return extension.equals(PDF);
    }
}
