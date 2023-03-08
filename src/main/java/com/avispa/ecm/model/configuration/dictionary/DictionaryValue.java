package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public final class DictionaryValue extends EcmConfig {
    @ElementCollection
    private Map<String, String> columns;

    @Column(nullable = false)
    private String label;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Dictionary dictionary;

    //private boolean enabled;

    @JsonIgnore
    public void setKey(String key) {
        setObjectName(key);
    }

    public String getKey() {
        return getObjectName();
    }
}
