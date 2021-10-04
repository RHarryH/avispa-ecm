package com.avispa.ecm;

import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.filestore.FileStoreRepository;
import com.avispa.ecm.util.CustomAsyncExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeManager;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class EcmConfiguration implements AsyncConfigurer {
    private final FileStoreRepository fileStoreRepository;

    @Lazy // prevents issues with order of beans initialization, allows to use data-{platform}.sql
    public EcmConfiguration(FileStoreRepository fileStoreRepository) {
        this.fileStoreRepository = fileStoreRepository;
    }

    @Bean
    public FileStore getFileStore(@Value("${avispa.ecm.fileStoreName:defaultFileStore}") String fileStoreName,
                                  @Value("${avispa.ecm.defaultFileStorePath}") String defaultFileStorePath) {
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

    @Bean(name = "localOfficeManager", initMethod="start", destroyMethod = "stop")
    @ConditionalOnMissingBean(name = "localOfficeManager")
    public OfficeManager gerLocalOfficeManager(@Value("${office.home:D:\\LibreOffice}") String officePath) {
        return LocalOfficeManager.builder()
                .officeHome(officePath)
                .processManager("org.jodconverter.process.PureJavaProcessManager")
                .build();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
