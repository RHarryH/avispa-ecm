package com.avispa.ecm.model.configuration.propertypage.property;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@Converter
public class PropertyTypeConverter implements AttributeConverter<PropertyType, String> {

    @Override
    public String convertToDatabaseColumn(PropertyType value) {
        if (value == null) {
            return null;
        } else {
            return value.getName();
        }
    }

    @Override
    public PropertyType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        } else {
            return Stream.of(PropertyType.values())
                    .filter(c -> c.getName().equals(value))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
