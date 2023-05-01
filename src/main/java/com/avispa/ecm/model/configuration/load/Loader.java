package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

/**
 * Loader is responsible for loading single type of configuration. It should convert DTO object to specific entity and
 * upsert it to the database. If overwriting is enabled then existing configuration with the same name, values will be
 * just updated (there is no reloading of whole configuration). When content is provided it is assigned to the newly
 * loaded configuration.
 *
 * @author Rafał Hiszpański
 */
@Transactional
interface Loader<D extends EcmConfigDto> {
    /**
     * Loads configuration represented by DTO to the database.
     * @param configDto DTO representing the configuration
     * @param overwrite if there is existing configuration with the same object name then it will be replaced when this
     *                  parameter is set to true
     */
    void load(D configDto, boolean overwrite);

    /**
     * Loads configuration represented by DTO to the database. Additionally, attaches new content represented by a path.
     * It is also copied to the configured file store.
     * @param configDto DTO representing the configuration
     * @param contentPath path to the content file.
     * @param overwrite if there is existing configuration with the same object name then it will be replaced when this
     *                  parameter is set to true
     */
    void load(D configDto, Path contentPath, boolean overwrite);
}
