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

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * This class implements Spring event allowing for automated loading of ECM configuration on Spring context startup.
 * Useful for test purposes.
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class EcmConfigurationAutoLoader {

    private final ConfigurationLoadService configurationLoadService;

    @Value("${avispa.ecm.configuration.paths:}")
    private String[] configurationPaths;

    @Value("${avispa.ecm.configuration.overwrite:false}")
    private boolean overwrite;

    @EventListener(ApplicationReadyEvent.class) // after bean creation but before the server starts
    public void loadConfigurations() {
        for(String configurationPath : configurationPaths) {
            configurationLoadService.load(Path.of(configurationPath), overwrite);
        }
    }
}
