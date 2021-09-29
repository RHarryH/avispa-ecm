package com.avispa.cms;

import com.avispa.cms.model.filestore.FileStore;
import com.avispa.cms.model.filestore.FileStoreRepository;
import com.avispa.cms.util.CustomAsyncExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Rafał Hiszpański
 */
@Configuration
@EnableAsync
@Slf4j
public class CmsConfiguration implements AsyncConfigurer {
    private final FileStoreRepository fileStoreRepository;

    @Lazy // prevents issues with order of beans initialization, allows to use data-{platform}.sql
    public CmsConfiguration(FileStoreRepository fileStoreRepository) {
        this.fileStoreRepository = fileStoreRepository;
    }

    @Bean
    public FileStore getFileStore(@Value("${avispa.cms.fileStoreName:defaultFileStore}") String fileStoreName,
                                  @Value("${avispa.cms.defaultFileStorePath}") String defaultFileStorePath) {
        FileStore fileStore = fileStoreRepository.findByObjectName(fileStoreName);
        if(null == fileStore) {
            fileStore = fileStoreRepository.findByObjectName(fileStoreName);
            if(null == fileStore) {
                fileStore = createDefaultFileStore(fileStoreName, defaultFileStorePath);
            }
        }

        createFileStorePath(fileStore);

        return fileStore;
    }

    /**
     * If defaultFileStorePath is relative path then it will be located in folder defined in user.home
     * @param defaultFileStoreName
     * @param defaultFileStorePath
     * @return
     */
    private FileStore createDefaultFileStore(String defaultFileStoreName, String defaultFileStorePath) {
        FileStore fileStore;

        Path p = Paths.get(defaultFileStorePath);
        if (!p.isAbsolute()) {
            defaultFileStorePath = Path.of(System.getProperty("user.home"), defaultFileStorePath).toString();
        }

        fileStore = new FileStore();
        fileStore.setObjectName(defaultFileStoreName);
        fileStore.setRootPath(defaultFileStorePath);
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

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
