package com.innowise.orderservice.service.interfaces;

/**
 * Defines service operations for managing items, extending standard CRUD
 * operations.
 * This interface provides methods to read, create, update, and delete Item
 * entities, leveraging DTOs for request and response payloads.
 *
 * @param <S> the type of the response DTO for items
 * @param <C> the type of the creation request DTO
 * @param <U> the type of the update request DTO
 * @param <L> the type of the item identifier
 */
public interface ItemService<S, C, U, L> extends BaseService<S, C, U, L> {

}
