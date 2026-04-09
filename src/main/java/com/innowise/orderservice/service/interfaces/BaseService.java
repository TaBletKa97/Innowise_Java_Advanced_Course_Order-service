package com.innowise.orderservice.service.interfaces;

import com.innowise.orderservice.exceptions.ItemNotFoundException;

import java.util.List;

/**
 * Defines the CRUD operations for a service layer.
 * This interface serves as a generic contract for service implementations,
 * parameterized by the types representing the response dto (S), creation
 * request dto (C), update request dto (U), and identifier (I).
 */
public interface BaseService<S, C, U, I> {

    /**
     * Retrieves all entities of type {@code S}.
     *
     * @return a list of all entities; may be empty but never null
     */
    List<S> readAll();

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity to retrieve; may not be null
     * @return the response dto corresponding to the given identifier;
     * @throws ItemNotFoundException if no entity exists with the specified
     * identifier.
     */
    S readById(I id);

    /**
     * Creates a new entity using the provided creation request data.
     *
     * @param createRequest the request dto object containing data needed to
     * create the entity; may not be null
     * @return the response DTO representing the created entity
     */
    S create(C createRequest);

    /**
     * Updates an existing entity identified by the given identifier using the
     * provided update request data.
     *
     * @param id the identifier of the entity to update; may not be null
     * @param updateRequest the request DTO object containing the update data;
     * may not be null
     * @return the response DTO representing the updated entity
     * @throws ItemNotFoundException if no entity exists with the specified
     * identifier
     */
    S update(I id, U updateRequest);

    /**
     * Deletes an entity identified by the given identifier.
     *
     * @param id the identifier of the entity to delete; may not be null
     * @throws ItemNotFoundException if no entity exists with the specified
     * identifier
     */
    void deleteById(I id);
}