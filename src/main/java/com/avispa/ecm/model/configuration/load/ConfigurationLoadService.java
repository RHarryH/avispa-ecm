package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import com.avispa.ecm.util.exception.EcmConfigurationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

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