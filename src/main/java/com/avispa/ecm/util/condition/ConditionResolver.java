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

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.ConditionGroup;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.GroupType;
import com.avispa.ecm.util.condition.intermediate.IConditionElement;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public
class ConditionResolver {
    private final EntityManager entityManager;

    public boolean resolve(Conditions conditions, Class<? extends EcmObject> objectClass) {
        if(conditions.isEmpty()) {
            return true;
        }

        TypedQuery<Long> query = getQuery(conditions, objectClass);

        return hasResult(query);
    }

    public boolean resolve(Conditions conditions, EcmObject object) {
        if(conditions.isEmpty()) {
            return true;
        }

        TypedQuery<Long> query = getQuery(conditions, object);

        return hasResult(query);
    }

    private boolean hasResult(TypedQuery<Long> query) {
        long result = query.getSingleResult();

        log.debug("Found: {}", result);

        return result > 0;
    }

    private TypedQuery<Long> getQuery(Conditions conditions, Class<? extends EcmObject> objectClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<? extends EcmObject> queryRoot = criteriaQuery.from(objectClass);

        List<Predicate> predicates = getPredicates(conditions, criteriaBuilder, criteriaQuery, queryRoot);

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));

        return entityManager.createQuery(criteriaQuery);
    }

    private TypedQuery<Long> getQuery(Conditions conditions, EcmObject object) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<? extends EcmObject> queryRoot = criteriaQuery.from(object.getClass());

        List<Predicate> predicates = getPredicates(conditions, criteriaBuilder, criteriaQuery, queryRoot);
        predicates.add(criteriaBuilder.equal(queryRoot.get("id"), object.getId()));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));

        return entityManager.createQuery(criteriaQuery);
    }

    private List<Predicate> getPredicates(Conditions conditions, CriteriaBuilder criteriaBuilder, CriteriaQuery<Long> criteriaQuery, Root<? extends EcmObject> queryRoot) {
        criteriaQuery.select(criteriaBuilder.count(queryRoot));

        ConditionGroup group = conditions.getConditionGroup();

        return resolveGroup(criteriaBuilder, queryRoot, group);
    }

    private List<Predicate> resolveGroup(CriteriaBuilder criteriaBuilder, Root<? extends EcmObject> queryRoot, ConditionGroup group) {
        List<Predicate> predicates = new ArrayList<>(group.getConditions().size());
        for (IConditionElement element : group.getConditions()) {
            if (element instanceof ConditionGroup nestedGroup) {
                List<Predicate> groupPredicates = resolveGroup(criteriaBuilder, queryRoot, nestedGroup);

                Predicate predicate = getPredicate(nestedGroup.getGroupType(), criteriaBuilder, groupPredicates);
                predicates.add(predicate);

            } else if (element instanceof Condition condition) {
                predicates.add(getPredicate(condition, criteriaBuilder, queryRoot));
            }
        }
        return predicates;
    }

    private Predicate getPredicate(GroupType groupType, CriteriaBuilder criteriaBuilder, List<Predicate> groupPredicates) {
        return switch (groupType) {
            case AND -> criteriaBuilder.and(groupPredicates.toArray(new Predicate[0]));
            case OR -> criteriaBuilder.or(groupPredicates.toArray(new Predicate[0]));
        };
    }

    private Predicate getPredicate(Condition condition, CriteriaBuilder criteriaBuilder, Root<? extends EcmObject> queryRoot) {
        String key = condition.key();
        Operator operator = condition.operator();
        ConditionValue<?> value = condition.value();

        return switch (operator) {
            case EQ -> criteriaBuilder.equal(getPath(key, queryRoot), value.getValue());
            case NE -> criteriaBuilder.notEqual(getPath(key, queryRoot), value.getValue());
            case GT -> criteriaBuilder.gt(getPath(key, queryRoot), (Number) value.getValue());
            case GTE -> criteriaBuilder.ge(getPath(key, queryRoot), (Number) value.getValue());
            case LT -> criteriaBuilder.lt(getPath(key, queryRoot), (Number) value.getValue());
            case LTE -> criteriaBuilder.le(getPath(key, queryRoot), (Number) value.getValue());
        };
    }

    private <Y> Path<Y> getPath(String key, Root<? extends EcmObject> queryRoot) {
        String[] properties = key.split("\\.");

        Path<Y> path = queryRoot.get(properties[0]);
        for(int i = 1; i < properties.length; i++) {
            path = path.get(properties[i]);
        }

        return path;
    }

    public String getQueryString(Conditions conditions, Class<? extends EcmObject> objectClass) {
        TypedQuery<Long> query = getQuery(conditions, objectClass);

        return query.unwrap(org.hibernate.query.Query.class).getQueryString();
    }
}
