package com.batrobot.shared.domain.specification;

/**
 * Specification interface for defining business rules and validation logic.
 */
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
    
    default void check(T candidate) {
        if (!isSatisfiedBy(candidate)) {
            throw new RuntimeException("Specification not satisfied: " + this.getClass().getSimpleName());
        }
    }
    
    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }
    
    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }
    
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }
}

class AndSpecification<T> implements Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;
    
    public AndSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
    }
}

class OrSpecification<T> implements Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;
    
    public OrSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
    }
}

class NotSpecification<T> implements Specification<T> {
    private final Specification<T> spec;
    
    public NotSpecification(Specification<T> spec) {
        this.spec = spec;
    }
    
    @Override
    public boolean isSatisfiedBy(T candidate) {
        return !spec.isSatisfiedBy(candidate);
    }
}