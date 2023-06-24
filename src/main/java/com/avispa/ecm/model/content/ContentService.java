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
import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.format.FormatNotFoundException;
import com.avispa.ecm.model.format.FormatRepository;
import com.avispa.ecm.util.exception.EcmException;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.avispa.ecm.model.format.Format.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentRepository contentRepository;
    private final FormatRepository formatRepository;
    private final FileStore fileStore;

    /**
     * Loads any content from provided path to the object
     * Works with classpath: and file: pseudo protocols
     * @param relatedObject object to which we want to attach the content
     * @param contentPath location of the file
     */
    @Transactional
    public void loadContentOf(EcmEntity relatedObject, Path contentPath) {
        loadContentOf(relatedObject, contentPath, false);
    }

    @Transactional
    public void loadContentOf(EcmEntity relatedEntity, Path contentPath, boolean overwrite) {
        if(existsByRelatedObjectId(relatedEntity.getId())) {
            if(overwrite) {
                deleteByRelatedEntity(relatedEntity);
            } else {
                log.error("'{}' object already has its content", relatedEntity.getObjectName());
                return;
            }
        }

        String fileName = contentPath.getFileName().toString();
        String extension = FilenameUtils.getExtension(fileName);

        try {
            byte[] bytes = Files.readAllBytes(contentPath);

            Path fileStorePath = saveToFileStore(new ByteArrayInputStream(bytes));
            createNewContent(extension, relatedEntity, fileStorePath);
        } catch (IOException e) {
            throw new EcmException("Can't load '" + contentPath + "' content", e);
        }
    }

    private boolean existsByRelatedObjectId(UUID id) {
        return contentRepository.existsByRelatedEntityId(id);
    }

    private Path saveToFileStore(InputStream resource) {
        Path fullFileStorePath = Paths.get(fileStore.getRootPath(), UUID.randomUUID().toString());

        try {
            Files.copy(resource, fullFileStorePath);
        } catch(IOException e) {
            throw new EcmException("Can't save the content in the file store", e);
        }

        return fullFileStorePath;
    }

    @Transactional
    public Content createNewContent(String extension, EcmEntity relatedObject, Path fileStorePath) {
        Format format = formatRepository.findByExtension(extension);

        Content content = new Content();
        content.setObjectName(relatedObject.getObjectName().replace("/","_") + "." + extension);
        content.setFormat(format);
        content.setRelatedEntity(relatedObject);
        content.setFileStorePath(fileStorePath.toString());

        relatedObject.addContent(content);

        return contentRepository.save(content);
    }

    public Content findPdfRenditionByDocumentId(UUID id) {
        try {
            return contentRepository.findByRelatedEntityIdAndFormat(id, formatRepository.findByExtensionOrThrowException(PDF));
        } catch (FormatNotFoundException e) {
            throw new RepositoryCorruptionError("PDF Format not found in ECM Repository. Probably it is corrupted.");
        }
    }

    public void deleteByRelatedEntity(EcmEntity entity) {
        var deletedContents = contentRepository.deleteByRelatedEntity(entity);
        deletedContents.forEach(entity.getContents()::remove);
    }
}
