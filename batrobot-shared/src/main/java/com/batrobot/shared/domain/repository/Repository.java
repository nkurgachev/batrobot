package com.batrobot.shared.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Base Repository interface defining common CRUD operations for all aggregate roots.
 *
 * @param <T> The aggregate root type
 * @param <ID> The aggregate root identifier type
 */
public interface Repository<T, ID> {

    /**
     * Finds an aggregate by its identification.
     *
     * @param id The aggregate identifier
     * @return Optional containing the aggregate if found, empty otherwise
     */
    Optional<T> findById(ID id);
    
    /**
     * Finds all aggregates by their identifications.
     * Useful for batch fetching to avoid N+1 queries.
     *
     * @param ids Collection of aggregate identifiers
     * @return List of found aggregates (may be empty or partial if some IDs don't exist)
     */
    List<T> findAllById(Collection<ID> ids);

    /**
     * Saves or updates an aggregate.
     *
     * @param aggregate The aggregate to persist
     * @return The persisted aggregate (possibly with generated IDs)
     */
    T save(T aggregate);

    /**
     * Checks if aggregate exists by ID.
     *
     * @param id The aggregate identifier
     * @return true if aggregate exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Deletes an aggregate by ID.
     *
     * @param id The aggregate identifier
     */
    void deleteById(ID id);

    /**
     * Counts total aggregates in repository.
     *
     * @return Total count
     */
    long count();
}
