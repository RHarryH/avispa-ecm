package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Slf4j
public class Dictionary extends EcmConfig {
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dictionary")
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    private List<DictionaryValue> values;

    public String getLabel(String key) {
        if (isEmpty()) {
            return null;
        }

        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(key))
                .map(DictionaryValue::getLabel)
                .findFirst()
                .orElse("UNKNOWN KEY");
    }

    public String getColumnValue(String key, String columnName) {
        if (isEmpty()) {
            return null;
        }

        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(key))
                .map(dictionaryValue -> dictionaryValue.getColumns().get(columnName))
                .findFirst()
                .orElse("UNKNOWN COLUMN");
    }

    public String getColumnValue(UUID valueId, String columnName) {
        if (isEmpty()) {
            return null;
        }

        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getId().equals(valueId))
                .map(dictionaryValue -> dictionaryValue.getColumns().get(columnName))
                .findFirst()
                .orElse("UNKNOWN COLUMN");
    }

    public DictionaryValue getValue(String key) {
        if (isEmpty()) {
            return null;
        }

        return values.stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        if(CollectionUtils.isEmpty(values)) {
            log.warn("Dictionary {} is empty", getObjectName());
            return true;
        }

        return false;
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

    public void clearValues() {
        if(null != this.values) {
            this.values.clear();
        }
    }
}
