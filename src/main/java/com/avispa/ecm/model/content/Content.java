package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.format.Format;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostRemove;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
    @JoinColumn(name="related_object_id", nullable=false)
    @Setter
    private EcmObject relatedObject;

    @Override
    protected void prePersist() {
        super.prePersist();
        updateSize();
    }

    @Override
    protected void preUpdate() {
        super.preUpdate();
        updateSize();
    }

    /**
     * Automatically update content size
     */
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
        } catch (NoSuchFileException e) {
            log.error("'{}' content file does not exist", fileStorePath);
        } catch (IOException e) {
            log.error("Can't delete '{}' content file", fileStorePath, e);
        }
    }

    public boolean isPdf() {
        return format.isPdf();
    }
}
