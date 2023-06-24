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
