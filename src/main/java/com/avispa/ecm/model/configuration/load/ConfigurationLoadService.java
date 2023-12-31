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

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Loads configuration in form of zip file. Each configuration object is represented by JSON object. Each configuration
 * type has its own dedicated folder and may contain subfolder with content file. If a content is not required by a
 * configuration it will be ignored.
 * @author Rafał Hiszpański
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationLoadService {
    private final ConfigurationReader reader;
    private final Map<String, Loader<? extends EcmConfigDto>> loaders;

    public void load(InputStream inputStream) {
        load(inputStream, false);
    }

    public void load(InputStream inputStream, boolean overwrite) {
        Configuration config = reader.read(inputStream);
        load(config, overwrite);
    }

    public void load(Path zipConfigPath) {
        load(zipConfigPath, false);
    }

    public void load(Path zipConfigPath, boolean overwrite) {
        Configuration config = reader.read(zipConfigPath);
        load(config, overwrite);
    }

    @SuppressWarnings("unchecked")
    private void load(Configuration config, boolean overwrite) {
        var configs = config.getConfigDtos();
        for(EcmConfigDto configDto : configs) {
            String loaderName = getLoaderName(configDto);

            Loader<EcmConfigDto> loader = (Loader<EcmConfigDto>) loaders.get(loaderName);
            if(null != loader) {
                var contentPath = findContentPath(configDto.getName(), config.getContents());
                if (contentPath.isPresent()) {
                    loader.load(configDto, contentPath.get(), overwrite);
                } else {
                    loader.load(configDto, overwrite);
                }
            } else {
                log.warn("Loader for configuration '{}' not found. Skipping.", configDto.getName());
            }
        }
    }

    private static String getLoaderName(EcmConfigDto configDto) {
        String dtoClassName = configDto.getClass().getSimpleName();

        String loaderName = dtoClassName.replace("Dto", "Loader");
        loaderName = WordUtils.uncapitalize(loaderName);
        return loaderName;
    }

    private Optional<Path> findContentPath(String configName, Map<String, Path> contents) {
        return Optional.ofNullable(contents.get(configName));
    }
}