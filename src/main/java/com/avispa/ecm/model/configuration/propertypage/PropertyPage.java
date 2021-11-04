package com.avispa.ecm.model.configuration.propertypage;

import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.configuration.propertypage.controls.Control;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class PropertyPage extends EcmConfigObject {
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @OrderColumn
    private List<Control> controls;
}
