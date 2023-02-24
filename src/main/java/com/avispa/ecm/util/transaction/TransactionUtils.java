package com.avispa.ecm.util.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class TransactionUtils {
    private TransactionUtils() {}

    public static void registerFileRollback(Path filePath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if(status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.error("File located in '" + filePath + "' path can't be deleted");
                    }
                }
            }
        });
    }
}
