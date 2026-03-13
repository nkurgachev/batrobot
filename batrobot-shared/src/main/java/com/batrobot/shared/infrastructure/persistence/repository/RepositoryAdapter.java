package com.batrobot.shared.infrastructure.persistence.repository;

import com.batrobot.shared.domain.repository.Repository;

/**
 * Base class for all DDD Repository adapters.
 *
 * @param <T> The domain aggregate type
 * @param <E> The JPA entity type
 * @param <ID> The aggregate identifier type
 */
public abstract class RepositoryAdapter<T, E, ID> implements Repository<T, ID> {

    /**
     * Converts JPA entity to domain aggregate.
     *
     * @param entity JPA entity
     * @return Domain aggregate
     */
    protected abstract T toDomain(E entity);

    /**
     * Converts domain aggregate to JPA entity.
     *
     * @param aggregate Domain aggregate
     * @return JPA entity
     */
    protected abstract E toEntity(T aggregate);

    /**
     * Template method for common save operation.
     * Subclasses can override toEntity() and toDomain() for specific conversion logic.
     *
     * @param aggregate Domain aggregate to save
     * @return Persisted domain aggregate
     */
    public T saveAggregate(T aggregate) {
        E entity = toEntity(aggregate);
        E saved = persistEntity(entity);
        return toDomain(saved);
    }

    /**
     * Subclasses must implement to persist the entity to database.
     *
     * @param entity JPA entity to persist
     * @return Persisted entity
     */
    protected abstract E persistEntity(E entity);

    /**
     * Validates that aggregate exists before deletion.
     * Can be overridden for custom validation logic.
     *
     * @param id Aggregate identifier
     * @throws IllegalStateException if aggregate does not exist
     */
    protected void validateExists(ID id) {
        if (!existsById(id)) {
            throw new IllegalStateException("Cannot delete non-existent aggregate with id: " + id);
        }
    }
}
