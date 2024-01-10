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

import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Group;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.content.Content;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyPageMapper {

    private final ObjectMapper objectMapper;

    private final SimpleControlMapper simpleControlMapper;
    private final TableMapper tableMapper;

    public PropertyPageContent convertToContent(PropertyPageMapperConfigurer configurer, PropertyPage propertyPage, Object context) {
        PropertyPageContent propertyPageContent = getPropertyPageContent(propertyPage.getPrimaryContent()).orElseThrow();
        propertyPageContent.setContext(configurer.getContext());
        propertyPageContent.setId(propertyPage.getId());

        processControls(propertyPageContent.getControls(), configurer.getFillBlacklist(), context);

        return propertyPageContent;
    }

    /**
     * Loads JSON content file and converts it to PropertyPageContent instance
     * @param content property page JSON content
     * @return
     */
    private Optional<PropertyPageContent> getPropertyPageContent(Content content) {
        try {
            if(null == content) {
                return Optional.empty();
            }

            byte[] resource = Files.readAllBytes(Path.of(content.getFileStorePath()));
            return Optional.of(objectMapper.readerFor(PropertyPageContent.class).withRootName("propertyPage").readValue(resource));
        } catch (IOException e) {
            log.error("Can't parse property page content from '{}'", content.getFileStorePath(), e);
        }

        return Optional.empty();
    }

    private void processControls(List<Control> controls, List<String> fillBlacklist, Object context) {
        for(Control control : controls) {
            if (control instanceof Columns columns) {
                processControls(columns.getControls(), fillBlacklist, context);
            } else if (control instanceof Group group) {
                processControls(group.getControls(), fillBlacklist, context);
            } else if (control instanceof Tabs tabs) {
                for(Tab tab : tabs.getTabs()) {
                    processControls(tab.getControls(), fillBlacklist, context);
                }
            } else if (control instanceof Table table) {
                tableMapper.processControl(table, fillBlacklist, context);
            } else {
                simpleControlMapper.processControl(control, fillBlacklist, context);
            }
        }
    }
}