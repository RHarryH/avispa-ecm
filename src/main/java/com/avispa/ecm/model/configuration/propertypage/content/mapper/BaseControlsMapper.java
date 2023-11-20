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

package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@RequiredArgsConstructor
abstract class BaseControlsMapper<C extends Control> implements ControlMapper<C> {
    protected static final String OBJECT_NAME = "objectName";

    protected final DictionaryControlLoader dictionaryControlLoader;
    protected final ObjectMapper objectMapper;
}