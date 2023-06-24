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

package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
// it is used only to test persistence layer thus it loads only part of context - repositories and so on
//@DataJpaTest
// adding classes not related to data jpa test but required by tests
// optionally I could use just SpringBootTest but this disables tests slicing
// can be also done using @TestConfiguration
//@Import({AutonameService.class, ExpressionResolver.class})
@Slf4j
class AutonameServiceTest {
    @Autowired
    private AutonameService autonameService;

    private static Document document;

    @TestConfiguration
    static class ContextServiceTestContextConfiguration {
        @Bean
        public AutonameService autonameService() {
            return new AutonameService(new ExpressionResolver());
        }
    }

    @BeforeAll
    static void init() {
        document = new Document();
        document.setObjectName("Document");
    }

    @Test
    void simpleTest() {
        Autoname autoname = new Autoname();
        autoname.setRule("'NewName'");
        autoname.setPropertyName("objectName");
        autonameService.apply(autoname, document);

        assertEquals("NewName", document.getObjectName());
    }
}