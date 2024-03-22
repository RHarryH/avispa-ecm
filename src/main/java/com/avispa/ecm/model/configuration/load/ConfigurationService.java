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

package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.content.ContentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ListIterator;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {
    private final ContentRepository contentRepository;
    private final EcmConfigRepository<Dictionary> dictionaryRepository;

    private final ConfigurationRegistry configurationRegistry;
    private final EntityManager entityManager;

    /**
     * Clear whole configuration for fresh load
     */
    @Transactional
    public void clear() {
        int n = contentRepository.deleteAllContentsOfEcmEntities();
        log.debug("Removed {} contents", n);

        ListIterator<ConfigurationType> listIterator = configurationRegistry.listIterator(configurationRegistry.size());
        while (listIterator.hasPrevious()) {
            var config = listIterator.previous();
            log.debug("Removing {} configuration", config.getConfigClass());
            delete(config.getConfigClass());
        }
    }

    private void delete(Class<? extends EcmConfig> config) {
        if (config.equals(Dictionary.class)) { // hack for removal of dictionary with bidirectional relationship
            dictionaryRepository.deleteAll();
            return;
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaDelete<? extends EcmConfig> deleteStmt = criteriaBuilder.createCriteriaDelete(config);
        entityManager.createQuery(deleteStmt).executeUpdate();
    }
}
