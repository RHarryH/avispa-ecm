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

package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.format.Format;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends EcmObjectRepository<Content> {
    boolean existsByRelatedEntityId(UUID id);
    Content findByRelatedEntityIdAndFormat(UUID id, Format format);
    List<Content> deleteByRelatedEntity(EcmEntity relatedEntity);

    @Modifying
    @Query("DELETE FROM Content c WHERE c.relatedEntity IN (SELECT config FROM EcmConfig config)")
    int deleteAllContentsOfEcmEntities();
}
