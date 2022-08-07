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

    public <T extends EcmObject> boolean resolve(Conditions conditions, Class<T> inputObjectClass) {
        TypedQuery<Long> query = getQuery(conditions, inputObjectClass);

        log.info("Found: {}", query.getSingleResult());

        return query.getSingleResult() > 0;
    }

    private <T extends EcmObject> TypedQuery<Long> getQuery(Conditions conditions, Class<T> inputObjectClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> queryRoot = criteriaQuery.from(inputObjectClass);
        criteriaQuery.select(criteriaBuilder.count(queryRoot));

        ConditionGroup group = conditions.getConditionGroup();

        List<Predicate> predicates = resolveGroup(criteriaBuilder, queryRoot, group);
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery);
    }

    private <T extends EcmObject> List<Predicate> resolveGroup(CriteriaBuilder criteriaBuilder, Root<T> queryRoot, ConditionGroup group) {
        List<Predicate> predicates = new ArrayList<>(group.getConditions().size());
        for (IConditionElement element : group.getConditions()) {
            if(element instanceof ConditionGroup) {
                ConditionGroup nestedGroup = (ConditionGroup) element;
                List<Predicate> groupPredicates = resolveGroup(criteriaBuilder, queryRoot, nestedGroup);

                Predicate predicate = getPredicate(nestedGroup.getGroupType(), criteriaBuilder, groupPredicates);
                predicates.add(predicate);

            } else if(element instanceof Condition) {
                Condition condition = (Condition) element;

                predicates.add(getPredicate(condition, criteriaBuilder, queryRoot));
            }
        }
        return predicates;
    }

    private Predicate getPredicate(GroupType groupType, CriteriaBuilder criteriaBuilder, List<Predicate> groupPredicates) {
        switch(groupType) {
            case AND:
                return criteriaBuilder.and(groupPredicates.toArray(new Predicate[0]));
            case OR:
                return criteriaBuilder.or(groupPredicates.toArray(new Predicate[0]));
            default:
                if(log.isWarnEnabled()) {
                    log.warn("Unknown group type: {}", groupType);
                }
                return null;
        }
    }

    private <T extends EcmObject> Predicate getPredicate(Condition condition, CriteriaBuilder criteriaBuilder, Root<T> queryRoot) {
        String key = condition.getKey();
        Operator operator = condition.getOperator();
        ConditionValue<?> value = condition.getValue();

        switch(operator) {
            case EQ:
                return criteriaBuilder.equal(getPath(key, queryRoot), value.getValue());
            case NE:
                return criteriaBuilder.notEqual(getPath(key, queryRoot), value.getValue());
            case GT:
                return criteriaBuilder.gt(getPath(key, queryRoot), (Number) value.getValue());
            case GTE:
                return criteriaBuilder.ge(getPath(key, queryRoot), (Number) value.getValue());
            case LT:
                return criteriaBuilder.lt(getPath(key, queryRoot), (Number) value.getValue());
            case LTE:
                return criteriaBuilder.le(getPath(key, queryRoot), (Number) value.getValue());
            default:
                if(log.isWarnEnabled()) {
                    log.warn("Unknown operator: {}", operator);
                }
                return null;
        }
    }

    private <T extends EcmObject, Y> Path<Y> getPath(String key, Root<T> queryRoot) {
        String[] properties = key.split("\\.");

        Path<Y> path = queryRoot.get(properties[0]);
        for(int i = 1; i < properties.length; i++) {
            path = path.get(properties[i]);
        }

        return path;
    }

    public <T extends EcmObject> String getQueryString(Conditions conditions, Class<T> inputObjectClass) {
        TypedQuery<Long> query = getQuery(conditions, inputObjectClass);

        return query.unwrap(org.hibernate.query.Query.class).getQueryString();
    }
}
