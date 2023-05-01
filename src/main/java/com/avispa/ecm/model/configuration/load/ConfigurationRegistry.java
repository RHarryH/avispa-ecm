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
