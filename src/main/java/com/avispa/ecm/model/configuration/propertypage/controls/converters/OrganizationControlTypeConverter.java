package com.avispa.ecm.model.configuration.propertypage.controls.converters;

import com.avispa.ecm.model.configuration.propertypage.controls.type.OrganizationControlType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@Converter
public class OrganizationControlTypeConverter implements AttributeConverter<OrganizationControlType, String> {

    @Override
    public String convertToDatabaseColumn(OrganizationControlType value) {
        if (value == null) {
            return null;
        } else {
            return value.getName();
        }
    }

    @Override
    public OrganizationControlType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        } else {
            return Stream.of(OrganizationControlType.values())
                    .filter(c -> c.getName().equals(value))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
