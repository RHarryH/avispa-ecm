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
import com.avispa.ecm.util.reflect.EcmPropertyUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public
class ConditionRunner {
    private static final String QUERY_GENERATED_MESSAGE = "Query generated from conditions: {}";

    private final EntityManager entityManager;

    public <T extends EcmObject> List<T> fetch(Class<T> objectClass, Conditions conditions) {
        TypedQuery<T> query = getQuery(objectClass, conditions);

        // limit
        if (null != conditions.getLimit()) {
            query = query.setMaxResults(conditions.getLimit());
        }

        if (log.isDebugEnabled()) {
            log.debug(QUERY_GENERATED_MESSAGE, getQueryString(query));
        }

        return query.getResultList();
    }

    public long count(Class<? extends EcmObject> objectClass, Conditions conditions) {
        TypedQuery<Long> query = getCountQuery(objectClass, conditions);

        // limit
        if (null != conditions.getLimit()) {
            query = query.setMaxResults(conditions.getLimit());
        }

        if (log.isDebugEnabled()) {
            log.debug(QUERY_GENERATED_MESSAGE, getQueryString(query));
        }

        return executeCount(query);
    }

    private long executeCount(TypedQuery<Long> query) {
        long result = query.getSingleResult();

        log.debug("Found: {}", result);

        return result;
    }

    private <T extends EcmObject> TypedQuery<T> getQuery(Class<T> objectClass, Conditions conditions) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(objectClass);
        Root<T> queryRoot = criteriaQuery.from(objectClass);

        criteriaQuery = criteriaQuery.select(queryRoot);

        // where
        var predicates = getPredicates(conditions, criteriaBuilder, queryRoot);
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
        }

        // order by
        var orders = conditions.getOrderBy().entrySet().stream()
                .map(order -> order.getValue() == Conditions.OrderDirection.ASC ? criteriaBuilder.asc(queryRoot.get(order.getKey())) : criteriaBuilder.desc(queryRoot.get(order.getKey())))
                .toList();
        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }

        return entityManager.createQuery(criteriaQuery);
    }

    private TypedQuery<Long> getCountQuery(Class<? extends EcmObject> objectClass, Conditions conditions) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<? extends EcmObject> queryRoot = criteriaQuery.from(objectClass);

        criteriaQuery = criteriaQuery.select(criteriaBuilder.count(queryRoot));

        // where
        List<Predicate> predicates = getPredicates(conditions, criteriaBuilder, queryRoot);
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
        }

        return entityManager.createQuery(criteriaQuery);
    }

    private List<Predicate> getPredicates(Conditions conditions, CriteriaBuilder criteriaBuilder, Root<? extends EcmObject> queryRoot) {
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
            case LIKE -> criteriaBuilder.like(getPath(key, queryRoot), (String) value.getValue(), '\\');
            case NOT_LIKE -> criteriaBuilder.notLike(getPath(key, queryRoot), (String) value.getValue(), '\\');
        };
    }

    private <Y> Path<Y> getPath(String key, Root<? extends EcmObject> queryRoot) {
        String[] nestedProperties = EcmPropertyUtils.splitProperty(key);

        Path<Y> path = queryRoot.get(nestedProperties[0]);
        for (int i = 1; i < nestedProperties.length; i++) {
            path = path.get(nestedProperties[i]);
        }

        return path;
    }

    public String getQueryString(TypedQuery<?> query) {
        return query.unwrap(QuerySqmImpl.class).getSqmStatement().toHqlString();
    }
}
