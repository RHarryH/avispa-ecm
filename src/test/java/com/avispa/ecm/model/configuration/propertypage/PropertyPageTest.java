package com.avispa.ecm.model.configuration.propertypage;

import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.configuration.EcmConfigObjectRepository;
import com.avispa.ecm.model.configuration.propertypage.control.ControlRepository;
import com.avispa.ecm.model.configuration.propertypage.controls.Control;
import com.avispa.ecm.model.configuration.propertypage.controls.OrganizationControl;
import com.avispa.ecm.model.configuration.propertypage.controls.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.controls.type.OrganizationControlType;
import com.avispa.ecm.model.configuration.propertypage.controls.type.PropertyControlType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class PropertyPageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EcmConfigObjectRepository<EcmConfigObject> repository;

    @Autowired
    private ControlRepository<Control> controlRepository;

    @Test
    void whenGetOne_thenReturnPropertyPage() {
        PropertyPage propertyPage = createPropertyPage();

        PropertyPage foundPropertyPage = (PropertyPage) repository.getOne(propertyPage.getId());

        assertEquals(2, foundPropertyPage.getControls().size());

        List<Control> controls = foundPropertyPage.getControls();

        assertTrue(controls.get(0) instanceof PropertyControl);
        assertTrue(controls.get(1) instanceof OrganizationControl);

        PropertyControl objectName = (PropertyControl) controls.get(0);
        assertEquals("objectName", objectName.getName());
        assertEquals("Object name", objectName.getLabel());
        assertEquals(PropertyControlType.TEXT, objectName.getType());

        OrganizationControl label = (OrganizationControl) controls.get(1);
        assertEquals("Label", label.getLabel());
        assertEquals(OrganizationControlType.LABEL, label.getType());
    }

    @Test
    void whenMerge_thenReturnPropertyPage() {
        PropertyPage propertyPage = createPropertyPage();

        List<Control> controls = propertyPage.getControls();
        controls.get(0).setLabel("Updated label");
        controls.get(0).getAttributes().put("newAttr", "newValue");
        entityManager.merge(propertyPage);
        entityManager.flush();

        PropertyPage foundPropertyPage = (PropertyPage) repository.getOne(propertyPage.getId());

        assertEquals("Updated label", foundPropertyPage.getControls().get(0).getLabel());
        assertTrue(foundPropertyPage.getControls().get(0).getAttributes().containsKey("newAttr"));
    }

    @Test
    void whenRemove_thenDoesNotReturnControls() {
        PropertyPage propertyPage = createPropertyPage();

        UUID firstControlUUID = propertyPage.getControls().get(0).getId();
        UUID secondControlUUID = propertyPage.getControls().get(1).getId();

        entityManager.remove(propertyPage);
        entityManager.flush();

        assertFalse(controlRepository.findById(firstControlUUID).isPresent());
        assertFalse(controlRepository.findById(secondControlUUID).isPresent());
    }

    private PropertyPage createPropertyPage() {
        PropertyPage propertyPage = new PropertyPage();

        List<Control> controls = new ArrayList<>(2);
        PropertyControl objectName = new PropertyControl();
        objectName.setType(PropertyControlType.TEXT);
        objectName.setName("objectName");
        objectName.setLabel("Object name");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attr", "val");
        attributes.put("attr2", "val2");
        objectName.setAttributes(attributes);
        controls.add(objectName);

        OrganizationControl label = new OrganizationControl();
        label.setType(OrganizationControlType.LABEL);
        label.setLabel("Label");
        controls.add(label);

        propertyPage.setControls(controls);

        entityManager.persist(propertyPage);
        entityManager.flush();

        return propertyPage;
    }
}