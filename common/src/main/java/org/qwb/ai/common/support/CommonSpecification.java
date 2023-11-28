package org.qwb.ai.common.support;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author demoQ
 * @date 2022/1/13 14:32
 */
public class CommonSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 1900581010229669687L;
    private List<SearchCriteria> list = new ArrayList<>();

    public CommonSpecification() {
    }

    public void add(SearchCriteria criteria) {
        this.list.add(criteria);
    }

    /**
     * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
     * {@link Root} and {@link CriteriaQuery}.
     *
     * @param root  must not be {@literal null}.
     * @param query must not be {@literal null}.
     * @param cb    must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (SearchCriteria criteria : list) {
            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                predicates.add(cb.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(cb.equal(root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(cb.notEqual(root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                predicates.add(cb.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                predicates.add(cb.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                predicates.add(cb.like(cb.lower(root.get(criteria.getKey())), "%" + criteria.getValue() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                predicates.add(cb.like(cb.lower(root.get(criteria.getKey())), criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.TRUE)) {
                predicates.add(cb.isTrue(root.get(criteria.getKey())));
            } else if (criteria.getOperation().equals(SearchOperation.FALSE)) {
                predicates.add(cb.isFalse(root.get(criteria.getKey())));
            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                String key = criteria.getKey();
                Object value = criteria.getValue();
                CriteriaBuilder.In<Object> in = cb.in(root.get(key));

                List<Object> values = castList(value);
                for (Object v : values) {
                    in.value(v);
                }
                Predicate predicate = cb.and(cb.and(in));
                predicates.add(predicate);
            }
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private <t> List<t> castList(Object obj) {
        List<t> result = new ArrayList<t>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                Class<t> tClass = null;
                if (o instanceof String) {
                    tClass = (Class<t>) String.class;
                } else if (o instanceof Long) {
                    tClass = (Class<t>) Long.class;
                } else if (o instanceof Integer) {
                    tClass = (Class<t>) Integer.class;
                } else if (o instanceof Date) {
                    tClass = (Class<t>) Date.class;
                }
                assert tClass != null;
                result.add(tClass.cast(o));
            }
            return result;
        }
        return null;
    }
}
