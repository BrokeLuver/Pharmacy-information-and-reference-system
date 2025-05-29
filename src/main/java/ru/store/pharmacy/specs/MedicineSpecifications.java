package ru.store.pharmacy.specs;

import org.springframework.data.jpa.domain.Specification;
import ru.store.pharmacy.model.Medicines;

public class MedicineSpecifications {

    public static Specification<Medicines> hasId(int id) {
        return (root, query, criteriaBuilder) ->
                id <= 0
                        ? null
                        : criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Medicines> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                (name == null || name.isEmpty())
                        ? null
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Medicines> hasDescription(String description) {
        return (root, query, criteriaBuilder) ->
                (description == null || description.isEmpty())
                        ? null
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description + "%");
    }

    public static Specification<Medicines> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                (category == null || category.isEmpty())
                        ? null
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category + "%");
    }

    public static Specification<Medicines> hasForm(String form) {
        return (root, query, criteriaBuilder) ->
                (form == null || form.isEmpty())
                        ? null
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("form")), "%" + form + "%");
    }

    public static Specification<Medicines> weightGreaterThan(int weight) {
        return (root, query, criteriaBuilder) ->
                weight <= 0
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("weight"), weight);
    }

    public static Specification<Medicines> priceGreaterThan(int price) {
        return (root, query, criteriaBuilder) ->
                price <= 0
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }
}
