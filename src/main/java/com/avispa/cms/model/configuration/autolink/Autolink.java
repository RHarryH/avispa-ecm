package com.avispa.cms.model.configuration.autolink;

import com.avispa.cms.model.configuration.CmsConfigObject;
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
public class Autolink extends CmsConfigObject {

    @ElementCollection
    @OrderColumn
    private List<String> rules = new ArrayList<>();

    private String defaultValue;

    public void addRule(String rule) {
        this.rules.add(rule);
    }
}
