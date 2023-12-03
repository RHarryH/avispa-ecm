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

package com.avispa.ecm.model.configuration.context;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.type.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Contexts importance (for the time being) is based on the insertion order. The context algorithm will search for all
 * contexts till it will find first matching one.
 *
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Context extends EcmConfig {
    @ManyToMany(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<EcmConfig> ecmConfigs;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Type type;

    @Column(nullable = false, columnDefinition = "varchar(255) default '{}'")
    private String matchRule = "{}";

    @Column(nullable = false)
    @PositiveOrZero
    private int importance; // higher = more important

    public void setEcmConfigs(List<EcmConfig> ecmConfigs) {
        this.ecmConfigs = ecmConfigs.stream().filter(config -> !(config instanceof Context)).toList();
    }
}
