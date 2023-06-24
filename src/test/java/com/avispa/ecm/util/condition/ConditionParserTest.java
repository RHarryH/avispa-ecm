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

import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.ConditionGroup;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import com.avispa.ecm.util.json.JsonValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
@JsonTest
@Import({ConditionParser.class, JsonValidator.class})
@Slf4j
class ConditionParserTest {

    @Autowired
    private ConditionParser conditionParser;

    @Test
    void givenTextRule_whenConversion_thenThrowException() {
        assertThrows(IllegalStateException.class, () ->
            conditionParser.parse("\"testString\": \"TEST\"}"));
    }

    @Test
    void givenInvalidRule_whenConversion_thenThrowException() {
        assertThrows(IllegalStateException.class, () ->
                conditionParser.parse("{\"testString\": {\"unknown\": \"TEST\"}}"));
    }

    @Test
    void givenSimpleEquals_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertEquals(conditions,
                conditionParser.parse("{\"testString\": \"TEST\"}"));
    }

    @Test
    void givenCombinedSimpleEquals_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));
        conditions.addElement(Condition.equal("testInt", ConditionValue.number(12)));

        assertEquals(conditions,
                conditionParser.parse("{\"testString\": \"TEST\", \"testInt\": 12}"));
    }

    @Test
    void givenStringEquals_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));

        assertEquals(conditions,
                conditionParser.parse("{\"testString\": { \"$eq\": \"TEST\"}}"));
    }

    @Test
    void givenBooleanEquals_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testBoolean", ConditionValue.bool(true)));

        assertEquals(conditions,
                conditionParser.parse("{\"testBoolean\": true}"));
    }

    @Test
    void givenGreaterThanNumber_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertEquals(conditions,
                conditionParser.parse("{\"testInt\": { \"$gt\": 11}}"));
    }

    @Test
    void givenGreaterThanOrEqualNumber_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.greaterThanOrEqual("testInt", ConditionValue.number(11)));

        assertEquals(conditions,
                conditionParser.parse("{\"testInt\": { \"$gte\": 11}}"));
    }

    @Test
    void givenLessThanNumber_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.lessThan("testInt", ConditionValue.number(15)));

        assertEquals(conditions,
                conditionParser.parse("{\"testInt\": { \"$lt\": 15}}"));
    }

    @Test
    void givenLessThanOrEqualNumber_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.lessThanOrEqual("testInt", ConditionValue.number(15)));

        assertEquals(conditions,
                conditionParser.parse("{\"testInt\": { \"$lte\": 15}}"));
    }

    @Test
    void givenAndGroupInRoot_whenConversion_thenDefaultAndGroupUsed() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("testString", ConditionValue.text("TEST")));
        conditions.addElement(Condition.equal("testInt", ConditionValue.number(12)));

        assertEquals(conditions,
                conditionParser.parse("{\"$and\": [{\"testString\": { \"$eq\": \"TEST\"}}, {\"testInt\": { \"$eq\": 12}}]}"));
    }

    @Test
    void givenMultipleAndGroupsInRoot_whenConversion_thenDefaultAndGroupUsedAndFirstAndGroupIgnored() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(12)));
        conditions.addElement(Condition.lessThan("testInt", ConditionValue.number(15)));

        assertEquals(conditions,
                conditionParser.parse("{\"$and\": [{\"testString\": { \"ne\": \"TEST\"}}, {\"testString\": { \"$ne\": \"TEST2\"}}],\"$and\": [{\"testInt\": { \"$gt\": 12}}, {\"testInt\": { \"$lt\": 15}}]}"));
    }

    @Test
    void givenOrGroup_whenConversion_thenOrGroupIsInDefaultAndGroup() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.equal("testInt", ConditionValue.number(11))));

        assertEquals(conditions,
                conditionParser.parse("{\"$or\": [{\"testString\": { \"$eq\": \"TEST\"}}, {\"testInt\": { \"$eq\": 11}}]}"));
    }

    @Test
    void givenMixedGroupsInRoot_whenConversion_thenDefaultAndGroupUsedAndOrGroupAddedAsItsElement() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));
        conditions.addElement(Condition.lessThan("testInt", ConditionValue.number(15)));

        assertEquals(conditions,
                conditionParser.parse("{\"$or\": [{\"testString\": { \"$ne\": \"TEST2\"}}, {\"testString\": { \"$ne\": \"TEST3\"}}],\"$and\": [{\"testInt\": { \"$gt\": 11}}, {\"testInt\": { \"$lt\": 15}}]}"));
    }

    @Test
    void givenOrGroupMixedWithDefaultGroupConditions_whenConversion_thenOrGroupAndOtherConditionsAreInDefaultGroup() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.equal("testString", ConditionValue.text("TEST")))
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST3"))));
        conditions.addElement(Condition.greaterThan("testInt", ConditionValue.number(11)));

        assertEquals(conditions,
                conditionParser.parse("{\"$or\": [{\"testString\": { \"$eq\": \"TEST\"}}, {\"testString\": { \"$ne\": \"TEST3\"}}],\"testInt\": { \"$gt\": 11}}"));
    }

    @Test
    void givenNestedGroups_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(ConditionGroup.or()
                .addElement(Condition.notEqual("testString", ConditionValue.text("TEST2")))
                .addElement(ConditionGroup.and()
                        .addElement(Condition.greaterThan("testInt", ConditionValue.number(11)))
                        .addElement(Condition.lessThan("testInt", ConditionValue.number(15)))));

        assertEquals(conditions,
                conditionParser.parse("{\"$or\": [{\"testString\": { \"$ne\": \"TEST2\"}}, {\"$and\": [{\"testInt\": { \"$gt\": 11}}, {\"testInt\": { \"$lt\": 15}}]}]}"));
    }

    @Test
    void givenNestedProperty_whenConversion_thenEqualsIntermediateResult() {
        Conditions conditions = new Conditions();
        conditions.addElement(Condition.equal("nestedObject.nestedField", ConditionValue.text("TEST")));

        assertEquals(conditions,
                conditionParser.parse("{\"nestedObject.nestedField\": \"TEST\"}"));
    }
}