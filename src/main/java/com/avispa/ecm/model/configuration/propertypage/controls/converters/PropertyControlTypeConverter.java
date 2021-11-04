package com.avispa.ecm.model.configuration.propertypage.controls.converters;

import com.avispa.ecm.model.configuration.propertypage.controls.type.PropertyControlType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@Converter
public class PropertyControlTypeConverter implements AttributeConverter<PropertyControlType, String> {

    @Override
    public String convertToDatabaseColumn(PropertyControlType value) {
        if (value == null) {
            return null;
        } else {
            return value.getName();
        }
    }

    @Override
    public PropertyControlType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        } else {
            return Stream.of(PropertyControlType.values())
                    .filter(c -> c.getName().equals(value))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
