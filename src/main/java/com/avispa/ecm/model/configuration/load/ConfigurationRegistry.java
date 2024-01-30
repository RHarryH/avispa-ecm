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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Rafał Hiszpański
 */
public class ConfigurationRegistry implements Iterable<ConfigurationType> {
    public static final String ECM_DICTIONARY = "ecm_dictionary";
    public static final String ECM_PROPERTY_PAGE = "ecm_property_page";
    public static final String ECM_AUTOLINK = "ecm_autolink";
    public static final String ECM_AUTONAME = "ecm_autoname";
    public static final String ECM_UPSERT = "ecm_upsert";
    public static final String ECM_TEMPLATE = "ecm_template";
    public static final String ECM_CONTEXT = "ecm_context";

    /**
     * List of supported configurations. Order of insertion matters as it defines the priority of configurations.
     * For example dictionary has to be loaded before property page as it is used in property pages
     */
    private final List<ConfigurationType> configTypes = new ArrayList<>();

    /**
     * Register basic configuration
     */
    public ConfigurationRegistry() {
        register(ConfigurationType.of(ECM_DICTIONARY, DictionaryDto.class, false));
        register(ConfigurationType.of(ECM_PROPERTY_PAGE, PropertyPageDto.class, true));
        register(ConfigurationType.of(ECM_AUTOLINK, AutolinkDto.class, false));
        register(ConfigurationType.of(ECM_AUTONAME, AutonameDto.class, false));
        register(ConfigurationType.of(ECM_UPSERT, UpsertDto.class, false));
        register(ConfigurationType.of(ECM_TEMPLATE, TemplateDto.class, true));
        register(ConfigurationType.of(ECM_CONTEXT, ContextDto.class, false));
    }

    /**
     * Register new configuration type and locate it before specified configuration type. This method is useful when
     * registering custom configurations, which are supposed to be used by existing configurations for example by the
     * context.
     * This method is needed due to the behavior of configuration loader, which scans configuration in the order of
     * registration.
     *
     * @param type configuration type to register
     * @param tail name of the existing configuration type before which the new type should be registered
     */
    public void register(ConfigurationType type, String tail) {
        var it = configTypes.listIterator();

        while (it.hasNext()) {
            var element = it.next();
            if (element.getName().equals(tail)) {
                it.previous();
                it.add(type);
                return;
            }
        }

        // if tail configuration was not found, place new type at the end
        register(type);
    }

    /**
     * Register new configuration type and locate it at the end of the list of registered types.
     *
     * @param type configuration type to register
     */
    public void register(ConfigurationType type) {
        configTypes.add(type);
    }

    public int size() {
        return configTypes.size();
    }

    @Override
    public Iterator<ConfigurationType> iterator() {
        return Collections.unmodifiableList(configTypes).iterator();
    }

    public ListIterator<ConfigurationType> listIterator() {
        return Collections.unmodifiableList(configTypes).listIterator();
    }

    public ListIterator<ConfigurationType> listIterator(int index) {
        return Collections.unmodifiableList(configTypes).listIterator(index);
    }
}
