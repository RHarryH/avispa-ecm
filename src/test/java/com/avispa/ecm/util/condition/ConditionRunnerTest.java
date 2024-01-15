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

package com.avispa.ecm.util.condition;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.util.NestedObject;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.ConditionGroup;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import({ConditionRunner.class})
class ConditionRunnerTest {
    @Autowired
    private ConditionRunner conditionRunner;

    private TestDocument testDocument;

    @BeforeEach
    void init(@Autowired EcmObjectRepository<TestDocument> repository) {
        testDocument = new TestDocument();
        testDocument.setObjectName("Object Name");
        testDocument.setTestString("TEST");
        testDocument.setTestInt(12);
        testDocument.setNestedObject(new NestedObject("Nested Object", "TEST"));
        repository.save(testDocument);

        TestDocument testDocument2 = new TestDocument();
        testDocument2.setObjectName("Object Name 2");
        testDocument2.setTestString("TEST%2");
        testDocument2.setTestInt(12);
        repository.save(testDocument2);
    }

    @AfterEach
    void cleanup(@Autowired EcmObjectRepository<TestDocument> repository) {
        repository.deleteAll();
    }

    @Test
    void givenSimpleEquals_whenCount_thenReturnOne() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertEquals(1, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenSimpleEquals_whenFetch_thenReturnResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertEquals(Collections.singletonList(testDocument), conditionRunner.fetch(TestDocument.class, conditions));
    }

    @ParameterizedTest
    @CsvSource(value = {"TEST%,2", "TEST\\%2,1", "TEST\\%_,1"})
    void givenLikeCondition_whenCount_thenReturnTwo(String testedValue, int expectedCount) {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.like("testString", ConditionValue.text(testedValue)));

        assertEquals(expectedCount, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenNotLikeCondition_whenFetch_thenReturnRightDocument() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.notLike("testString", ConditionValue.text("TEST__")));

        var results = conditionRunner.fetch(TestDocument.class, conditions);
        assertEquals(1, results.size());
        assertEquals("TEST", results.get(0).getTestString());
    }

    @Test
    void givenSimpleEqualsWithLimit_whenFetch_thenReturnSingleResult() {
        Conditions conditions = new Conditions();
        conditions.setLimit(1);
        conditions.addElement(Condition.equal("testInt", ConditionValue.number(12)));

        assertEquals(1, conditionRunner.fetch(TestDocument.class, conditions).size());
    }

    @Test
    void givenCombinedSimpleEquals_whenCount_thenReturnOne() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));
        conditions.addElement(Condition.equal("testInt", ConditionValue.number(12)));

        assertEquals(1, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenGreaterThan_whenCount_thenReturnTwo() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertEquals(2, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenOrGroup_whenCount_thenReturnOne() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.equal("testInt", ConditionValue.number(11))));

        assertEquals(1, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenMixedGroupsInRoot_whenConversion_thenDefaultAndGroupUsedAndOrGroupAddedAsItsElement() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));
        conditions.addElement(Condition.lessThan("testInt", ConditionValue.number(15)));

        assertEquals(2, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenOrGroupMixedWithDefaultGroupConditions_whenConversion_thenOrGroupAndOtherConditionsAreInDefaultGroup() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertEquals(2, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenNestedGroups_whenCount_thenReturnOne() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(ConditionGroup.and()
                        .addElement(Condition.greaterThan("testInt", ConditionValue.number(11)))
                        .addElement(Condition.lessThan("testInt", ConditionValue.number(15)))));

        assertEquals(2, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenNestedGroups_whenCount_thenReturnNothing() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.and()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST2")))
                .addElement(ConditionGroup.or()
                        .addElement(Condition.greaterThan("testInt", ConditionValue.number(11)))
                        .addElement(Condition.lessThan("testInt", ConditionValue.number(15)))));

        assertEquals(0, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenNestedProperty_whenCount_thenReturnOne() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("nestedObject.nestedField", ConditionValue.text("TEST")));

        assertEquals(1, conditionRunner.count(TestDocument.class, conditions));
    }

    @Test
    void givenOrderByAsc_whenFetch_thenRecordsInOrder() {
        Conditions conditions = new Conditions();
        conditions.setOrderBy(Map.of("objectName", Conditions.OrderDirection.ASC));

        var results = conditionRunner.fetch(TestDocument.class, conditions);
        assertEquals(2, results.size());
        assertEquals("Object Name", results.get(0).getObjectName());
        assertEquals("Object Name 2", results.get(1).getObjectName());
    }

    @Test
    void givenOrderByDesc_whenFetch_thenRecordsInOrder() {
        Conditions conditions = new Conditions();
        conditions.setOrderBy(Map.of("objectName", Conditions.OrderDirection.DESC));

        var results = conditionRunner.fetch(TestDocument.class, conditions);
        assertEquals(2, results.size());
        assertEquals("Object Name 2", results.get(0).getObjectName());
        assertEquals("Object Name", results.get(1).getObjectName());
    }
}