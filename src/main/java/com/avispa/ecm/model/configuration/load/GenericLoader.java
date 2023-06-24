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

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import com.avispa.ecm.model.configuration.load.mapper.EcmConfigMapper;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.util.exception.EcmConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class GenericLoader<C extends EcmConfig, D extends EcmConfigDto, M extends EcmConfigMapper<C, D>> implements Loader<D> {
    private final EcmConfigRepository<C> ecmConfigRepository;
    private final M ecmConfigMapper;
    private final ContentService contentService;

    protected GenericLoader(EcmConfigRepository<C> ecmConfigRepository, M ecmConfigMapper, ContentService contentService) {
        this.ecmConfigRepository = ecmConfigRepository;
        this.ecmConfigMapper = ecmConfigMapper;
        this.contentService = contentService;
    }

    @Override
    @Transactional
    public void load(D configDto, Path contentPath, boolean overwrite) {
        upsertConfig(configDto, overwrite).ifPresent(config -> {
            if(isValidContent(contentPath)) {
                loadContent(config, contentPath, overwrite);
            } else {
                throw new EcmConfigurationException("Content is invalid and cannot be loaded");
            }
        });
    }

    /**
     * Optional validation for the content. Some configurations might require the content to be in specific format.
     * @param contentPath path to the content file
     * @return
     */
    protected boolean isValidContent(Path contentPath) {
        return true;
    }

    private void loadContent(C entity, Path contentPath, boolean overwrite) {
        contentService.loadContentOf(entity, contentPath, overwrite);
    }

    @Override
    public void load(D configDto, boolean overwrite) {
        upsertConfig(configDto, overwrite);
    }

    /**
     * Inserts configuration to the database. When the configuration already exists and it is allowed to be overwritten
     * then update will be performed. Otherwise, nothing will be done.
     * @param configDto DTO representing configuration
     * @param overwrite when true then update will be possible
     * @return optional of loaded configuration object
     */
    private Optional<C> upsertConfig(D configDto, boolean overwrite) {
        C config;

        var entityOptional = ecmConfigRepository.findByObjectName(configDto.getName());
        if(entityOptional.isPresent()) {
            config = entityOptional.get();
            if(overwrite) {
                ecmConfigMapper.updateEntityFromDto(configDto, config);
            } else {
                log.info("Already found configuration with '{}' name. Skipping", configDto.getName());
                return Optional.empty();
            }
        } else {
            config = ecmConfigMapper.convertToEntity(configDto);
        }

        ecmConfigRepository.save(config);

        return Optional.of(config);
    }
}
