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
    public FileStore getFileStore(@Value("${avispa.ecm.fileStore.name:defaultFileStore}") String fileStoreName,
                                  @Value("${avispa.ecm.fileStore.path:defaultFileStore}") String defaultFileStorePath) {
        FileStore fileStore = fileStoreRepository.findByObjectName(fileStoreName)
                .orElse(createFileStore(fileStoreName, defaultFileStorePath));

        createFileStorePath(fileStore);

        return fileStore;
    }

    /**
     * Creates new file store using provided file store name and file store path.
     * If fileStorePath is relative path then it will be located in folder defined in user.home
     * @param fileStoreName
     * @param fileStorePath
     * @return
     */
    private FileStore createFileStore(String fileStoreName, String fileStorePath) {
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
