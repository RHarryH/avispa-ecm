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

package com.avispa.ecm.model.configuration.callable.autolink;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OrderColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
public final class Autolink extends EcmConfig implements CallableConfigObject {

    @ElementCollection
    @OrderColumn(nullable = false)
    private List<String> rules = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "varchar(255) default 'Unknown'")
    private String defaultValue = "Unknown";

    public void addRule(String rule) {
        this.rules.add(rule);
    }
}
