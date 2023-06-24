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

package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import com.avispa.ecm.model.configuration.load.dto.AutonameDto;
import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import com.avispa.ecm.model.configuration.load.dto.PropertyPageDto;
import com.avispa.ecm.model.configuration.load.dto.TemplateDto;
import com.avispa.ecm.model.configuration.load.dto.UpsertDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public class ConfigurationRegistry implements Iterable<ConfigurationType>{
    /**
     * List of supported configurations. Order of insertion matters as it defines the priority of configurations.
     * For example dictionary has to be loaded before property page as it is used in property pages
     */
    private final List<ConfigurationType> configTypes = new ArrayList<>();

    /**
     * Register basic configuration
     */
    public ConfigurationRegistry() {
        registerNewConfigurationType(ConfigurationType.of("ecm_dictionary", DictionaryDto.class, false));
        registerNewConfigurationType(ConfigurationType.of("ecm_property_page", PropertyPageDto.class, true));
        registerNewConfigurationType(ConfigurationType.of("ecm_autolink", AutolinkDto.class, false));
        registerNewConfigurationType(ConfigurationType.of("ecm_autoname", AutonameDto.class, false));
        registerNewConfigurationType(ConfigurationType.of("ecm_upsert", UpsertDto.class, false));
        registerNewConfigurationType(ConfigurationType.of("ecm_template", TemplateDto.class, true));
        registerNewConfigurationType(ConfigurationType.of("ecm_context", ContextDto.class, false));
    }

    public void registerNewConfigurationType(ConfigurationType type) {
        configTypes.add(type);
    }

    @Override
    public Iterator<ConfigurationType> iterator() {
        return configTypes.iterator();
    }
}
