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