package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfigObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class Dictionary extends EcmConfigObject {
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dictionary")
    @Setter(AccessLevel.NONE)
    private List<DictionaryValue> values;

    public String getLabel(String key) {
        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(key))
                .map(DictionaryValue::getLabel)
                .findFirst()
                .orElse("UNKNOWN KEY");
    }

    public String getColumnValue(String key, String columnName) {
        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(key))
                .map(dictionaryValue -> dictionaryValue.getColumns().get(columnName))
                .findFirst()
                .orElse("UNKNOWN COLUMN");
    }

    public String getColumnValue(UUID valueId, String columnName) {
        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getId().equals(valueId))
                .map(dictionaryValue -> dictionaryValue.getColumns().get(columnName))
                .findFirst()
                .orElse("UNKNOWN COLUMN");
    }

    public void addValue(DictionaryValue value) {
        if(null == this.values) {
            this.values = new ArrayList<>();
        }
        this.values.add(value);
        value.setDictionary(this);
    }

    public void removeValue(DictionaryValue value) {
        if (null != this.values) {
            this.values.remove(value);
            value.setDictionary(null);
        }
    }
}
