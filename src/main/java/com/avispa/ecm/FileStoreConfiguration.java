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
    public FileStore getFileStore(@Value("${avispa.ecm.file-store.name:defaultFileStore}") String fileStoreName,
                                  @Value("${avispa.ecm.file-store.path:defaultFileStore}") String defaultFileStorePath) {
        FileStore fileStore = fileStoreRepository.findByObjectName(fileStoreName)
                .orElse(createFileStore(fileStoreName, defaultFileStorePath, fileStoreRepository));

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
        FileStore fileStore;

        Path p = Paths.get(fileStorePath);
        if (!p.isAbsolute()) {
            fileStorePath = Path.of(System.getProperty("user.home"), fileStorePath).toString();
        }

        fileStore = new FileStore();
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
