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

package com.avispa.ecm;

import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.filestore.FileStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Rafał Hiszpański
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileStoreConfiguration {
    private final FileStoreRepository fileStoreRepository;

    @Bean
    public FileStore getFileStore(@Value("${avispa.ecm.file-store.name:default-file-store}") String fileStoreName,
                                  @Value("${avispa.ecm.file-store.path:default-file-store}") String defaultFileStorePath) {
        FileStore fileStore = fileStoreRepository.findByObjectName(fileStoreName)
                .orElseGet(() -> createFileStore(fileStoreName, defaultFileStorePath, fileStoreRepository));

        createFileStorePath(fileStore);

        return fileStore;
    }

    /**
     * Creates new file store using provided file store name and file store path.
     * If fileStorePath is relative path then it will be located in folder defined in user.home
     * @param fileStoreName
     * @param fileStorePath
     * @param fileStoreRepository
     * @return
     */
    private FileStore createFileStore(String fileStoreName, String fileStorePath, FileStoreRepository fileStoreRepository) {
        Path p = Path.of(fileStorePath);
        if (!p.isAbsolute()) {
            fileStorePath = Path.of(System.getProperty("user.home"), fileStorePath).toString();
        }

        FileStore fileStore = new FileStore();
        fileStore.setObjectName(fileStoreName);
        fileStore.setRootPath(fileStorePath);
        fileStoreRepository.save(fileStore);

        return fileStore;
    }

    private void createFileStorePath(FileStore fileStore) {
        Path fp = Paths.get(fileStore.getRootPath());
        try {
            Files.createDirectories(fp);
        } catch (IOException e) {
            log.error("Can't create file store folders", e);
        }
    }
}
