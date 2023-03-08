package com.avispa.ecm.util.condition;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.util.NestedObject;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.ConditionGroup;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import({ConditionResolver.class})
@Slf4j
class ConditionResolverTest {
    @Autowired
    private ConditionResolver conditionResolver;

    private TestDocument testDocument;

    @BeforeEach
    void init(@Autowired EcmObjectRepository<TestDocument> repository) {
        testDocument = new TestDocument();
        testDocument.setObjectName("Object Name");
        testDocument.setId(UUID.randomUUID());
        testDocument.setTestString("TEST");
        testDocument.setTestInt(12);
        testDocument.setNestedObject(new NestedObject("Nested Object", "TEST"));
        repository.save(testDocument);
    }

    @Test
    void givenSimpleEquals_whenResolve_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenCombinedSimpleEquals_whenResolve_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));
        conditions.addElement(Condition.equal("testInt", ConditionValue.number(12)));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenGreaterThan_whenResolve_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenOrGroup_whenResolve_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.equal("testInt", ConditionValue.number(11))));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenMixedGroupsInRoot_whenConversion_thenDefaultAndGroupUsedAndOrGroupAddedAsItsElement() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));
        conditions.addElement(Condition.lessThan("testInt", ConditionValue.number(15)));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenOrGroupMixedWithDefaultGroupConditions_whenConversion_thenOrGroupAndOtherConditionsAreInDefaultGroup() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenNestedGroups_whenConversion_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(ConditionGroup.and()
                        .addElement(Condition.greaterThan("testInt", ConditionValue.number(11)))
                        .addElement(Condition.lessThan("testInt", ConditionValue.number(15)))));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenNestedGroups_whenConversion_thenReturnFalse() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.and()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST2")))
                .addElement(ConditionGroup.or()
                        .addElement(Condition.greaterThan("testInt", ConditionValue.number(11)))
                        .addElement(Condition.lessThan("testInt", ConditionValue.number(15)))));

        assertFalse(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenNestedProperty_whenConversion_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("nestedObject.nestedField", ConditionValue.text("TEST")));

        assertTrue(conditionResolver.resolve(conditions, TestDocument.class));
    }

    @Test
    void givenObjectWithId_whenConversion_thenReturnTrue() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertTrue(conditionResolver.resolve(conditions, testDocument));
    }
}