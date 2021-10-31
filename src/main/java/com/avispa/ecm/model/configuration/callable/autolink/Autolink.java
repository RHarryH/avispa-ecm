package com.avispa.ecm.model.configuration.callable.autolink;

import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class Autolink extends EcmConfigObject implements CallableConfigObject {

    @ElementCollection
    @OrderColumn
    private List<String> rules = new ArrayList<>();

    private String defaultValue;

    public void addRule(String rule) {
        this.rules.add(rule);
    }
}
