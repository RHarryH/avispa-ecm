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

package com.avispa.ecm.model.configuration.display;

import com.avispa.ecm.model.configuration.annotation.AnnotationService;
import com.avispa.ecm.model.configuration.display.annotation.DisplayName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Service
public class DisplayService extends AnnotationService {
    public String getDisplayValueFromAnnotation(Class<?> objectClass, String propertyName) {
        DisplayName displayName = getFromAnnotation(DisplayName.class, objectClass, propertyName);
        return displayName != null ? displayName.value() : propertyName;
    }
}
