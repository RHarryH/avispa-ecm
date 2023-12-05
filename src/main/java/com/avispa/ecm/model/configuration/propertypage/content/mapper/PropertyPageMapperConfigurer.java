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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class PropertyPageMapperConfigurer {
    private boolean readonly;
    private List<String> fillBlacklist;

    public static PropertyPageMapperConfigurer readonly() {
        return new PropertyPageMapperConfigurer(true, List.of("id"));
    }

    public static PropertyPageMapperConfigurer insert() {
        return writable(List.of("id"));
    }

    public static PropertyPageMapperConfigurer edit() {
        return writable(List.of());
    }

    private static PropertyPageMapperConfigurer writable(List<String> ignoredFields) {
        return new PropertyPageMapperConfigurer(false, ignoredFields);
    }
}
