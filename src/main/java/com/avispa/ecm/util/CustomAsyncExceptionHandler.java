package com.avispa.ecm.util;

import com.avispa.ecm.util.exception.EcmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * This method will be called when any uncaught exception will be thrown in async void
     * method.
     * @param throwable
     * @param method
     * @param obj
     */
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        if(log.isDebugEnabled()) {
            log.debug("Exception message - " + throwable.getMessage());
            log.debug("Method name - " + method.getName());
            for (Object param : obj) {
                log.debug("Parameter value - " + param);
            }
        }

        String message = String.format("Async method %s has failed", method.getName());
        throw new EcmException(message, throwable);
    }
}