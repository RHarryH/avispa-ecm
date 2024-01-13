/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2024 Rafał Hiszpański
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
import com.avispa.ecm.util.json.JsonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import({ConditionService.class, ConditionParser.class, ConditionRunner.class, JsonValidator.class, ObjectMapper.class})
class ConditionServiceTest {
    private TestDocument testDocument;

    @Autowired
    private ConditionService conditionService;

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
        testDocument2.setTestString("TEST 2");
        testDocument2.setTestInt(11);
        repository.save(testDocument2);
    }

    @AfterEach
    void cleanup(@Autowired EcmObjectRepository<TestDocument> repository) {
        repository.deleteAll();
    }

    @Test
    void givenEmptyMatchRule_whenContextMatch_thenReturnTrue() {
        assertTrue(conditionService.hasContextMatch("{}", TestDocument.class));
    }

    @Test
    void givenEmptyMatchRuleAndSpecificObject_whenContextMatch_thenReturnTrue() {
        assertTrue(conditionService.hasContextMatch("{}", testDocument));
    }

    @Test
    void givenMatchRule_whenContextMatch_thenReturnTrue() {
        assertTrue(conditionService.hasContextMatch("{\"testString\": \"TEST\"}", TestDocument.class));
    }

    @Test
    void givenMatchRuleAndSpecificObject_whenContextMatch_thenReturnFalse() {
        assertFalse(conditionService.hasContextMatch("{\"testString\": \"TEST 2\"}", testDocument));
    }

    @Test
    void givenMatchRuleMatchingMoreThanOneObject_whenContextMatch_thenReturnTrue() {
        assertTrue(conditionService.hasContextMatch("{\"testInt\": 12}", TestDocument.class));
    }

    @Test
    void givenCondition_whenCount_thenReturnOne() {
        assertEquals(1, conditionService.count("{\"testString\": \"TEST\"}", TestDocument.class));
    }

    @Test
    void givenCondition_whenFetch_thenReturnTestDocument() {
        var result = conditionService.fetch("{\"testString\": \"TEST\"}", TestDocument.class);
        assertEquals(1, result.size());
        assertEquals("TEST", result.get(0).getTestString());
    }
}