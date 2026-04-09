package com.innowise.orderservice.service.interfaces;

import com.innowise.orderservice.exceptions.ItemNotFoundException;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Defines service operations for managing orders, extending standard CRUD
 * operations.
 * This interface supports administrative creation and updates, user-specific
 * order retrieval, and paginated querying across multiple criteria.
 *
 * @param <S> the type of the response DTO for orders
 * @param <C> the type of the creation request DTO (for standard user-initiated
 * order creation)
 * @param <U> the type of the update request DTO (for standard user-initiated
 * order updates)
 * @param <I> the type of the order identifier
 */
public interface OrderService<S, C, U, I> extends BaseService<S, C, U, I> {

    /**
     * Creates a new order using administrative privileges, bypassing standard
     * user restrictions.
     * This method persists the order entity, and returns the created order as
     * a response DTO.
     *
     * @param request the administrative request DTO containing order details;
     * must contain valid user ID, status, total price, and item list
     * @return the response DTO representing the newly created order
     */
    S adminCreate(OrderAdminCreateRequestDto request);

    /**
     * Updates an existing order using administrative privileges, bypassing standard
     * user restrictions.
     * This method modifies the order entity with the provided administrative update
     * request, persists the updated entity, and returns the updated order as a
     * response DTO.
     *
     * @param id the identifier of the order to update; must correspond to an
     * existing order
     * @param request the administrative request DTO containing updated order data;
     * may include valid user ID, status, total price, and item list
     * @return the response DTO representing the updated order
     * @throws ItemNotFoundException if no order exists with the specified
     * identifier
     */
    S adminUpdate(Long id, OrderAdminUpdateRequestDto request);

    /**
     * Retrieves a list of order response DTOs associated with the specified
     * user ID.
     *
     * @param userId the ID of the user whose orders are to be retrieved;
     * must be non-null
     * @return a list of order response DTOs belonging to the user;
     * may be empty but never null
     */
    List<S> readByUserId(Long userId);

    /**
     * Retrieves a paginated list of order response DTOs, filtered by the given
     * search criteria.
     *
     * @param criteria a map of field names to filter values for querying orders;
     * keys represent field names, values represent the corresponding filter values
     * @param pageable the pagination information, including page number, size, and
     * sorting options
     * @return a paginated list of order response DTOs matching the criteria; may
     * be empty but never null
     */
    Page<S> readAll(Map<String, Object> criteria, Pageable pageable);
}
