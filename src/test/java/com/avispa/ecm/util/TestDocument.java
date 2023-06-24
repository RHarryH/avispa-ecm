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

package com.avispa.ecm.util;

import com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary;
import com.avispa.ecm.model.configuration.display.annotation.DisplayName;
import com.avispa.ecm.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class TestDocument extends Document {
    @Dictionary(name = "TestDict")
    private String testString;
    private LocalDateTime testDateTime;
    private LocalDate testDate;
    private boolean testBoolean;

    @DisplayName("Some test integer")
    private int testInt;

    @OneToMany
    private List<Document> table;

    @OneToMany
    private Set<Document> nonTable;

    @OneToOne(cascade = CascadeType.ALL)
    private NestedObject nestedObject;
}
