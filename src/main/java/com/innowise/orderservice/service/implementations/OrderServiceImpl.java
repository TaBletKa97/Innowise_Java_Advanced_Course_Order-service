package com.innowise.orderservice.service.implementations;

import com.innowise.orderservice.exceptions.AlreadyDeletedException;
import com.innowise.orderservice.exceptions.ImmutableOrderUpdateException;
import com.innowise.orderservice.exceptions.OrderNotFoundException;
import com.innowise.orderservice.repository.entity.Order;
import com.innowise.orderservice.repository.entity.OrderItem;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.repository.interfaces.OrderRepository;
import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.OrderService;
import com.innowise.orderservice.service.mappers.OrderItemMapper;
import com.innowise.orderservice.service.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.innowise.orderservice.service.OrderSpecification.haveDate;
import static com.innowise.orderservice.service.OrderSpecification.haveStatus;
import static com.innowise.orderservice.utils.GlobalConstants.DATE;
import static com.innowise.orderservice.utils.GlobalConstants.STATUS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements
        OrderService<OrderResponseDto, OrderCreateRequestDto,
                OrderUpdateRequestDto, Long> {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderResponseDto> readAll() {
        return orderMapper.entityListToDto(orderRepository.findAll());
    }

    @Override
    public Page<OrderResponseDto> readAll(Map<String, String> criteria, Pageable pageable) {

        Specification<Order> spec = Specification.where((r, q, cb) -> null);
        spec = spec.and(haveDate(criteria.get(DATE)));
        spec = spec.and(haveStatus(criteria.get(STATUS)));

        return orderRepository.findAll(spec, pageable).map(orderMapper::entityToDto);
    }

    @Override
    public OrderResponseDto readById(Long id) {
        return orderMapper.entityToDto(findOrderById(id));
    }

    @Transactional
    public OrderResponseDto create(OrderCreateRequestDto createRequest) {
        Order order = new Order();

        order.setUserId(getCurrentUserId());

        List<OrderItem> list = orderItemMapper.dtoListToEntityList(
                createRequest.itemList());
        list.forEach(i -> i.setOrder(order));

        order.setStatus(OrderStatus.CREATED);
        order.setOrderItems(list);
        order.setTotalPrice(getTotalPrice(list));

        return orderMapper.entityToDto(orderRepository.saveAndFlush(order));
    }

    @Transactional
    @Override
    public OrderResponseDto update(Long id, OrderUpdateRequestDto updateRequest) {
        Order order = findOrderById(id);

        if (!order.getStatus().equals(OrderStatus.CREATED)) {
            throw new ImmutableOrderUpdateException();
        }

        List<OrderItem> orderItems = orderItemMapper.dtoListToEntityList(
                updateRequest.itemList());
        orderItems.forEach(i -> i.setOrder(order));

        order.setOrderItems(orderItems);
        order.setTotalPrice(getTotalPrice(orderItems));

        return orderMapper.entityToDto(orderRepository.saveAndFlush(order));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Order order = findOrderById(id);
        if (order.isDeleted()) {
            throw new AlreadyDeletedException(id);
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public OrderResponseDto adminCreate(OrderAdminCreateRequestDto request) {
        Order order = orderMapper.requestToEntity(request);
        order.getOrderItems().forEach(orderItem ->
                orderItem.setOrder(order));
        return orderMapper.entityToDto(orderRepository.saveAndFlush(order));
    }

    @Override
    public OrderResponseDto adminUpdate(Long id,
                                        OrderAdminUpdateRequestDto request) {
        Order order = findOrderById(id);
        orderMapper.updateOrder(order, request);
        order.getOrderItems().forEach(orderItem ->
                orderItem.setOrder(order));
        return orderMapper.entityToDto(orderRepository.saveAndFlush(order));
    }

    @Override
    public List<OrderResponseDto> readByUserId(Long userId) {
        return orderMapper.entityListToDto(
                orderRepository.findOrderByUserId(userId));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException(id));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private BigDecimal getTotalPrice(List<OrderItem> list) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : list) {
            totalPrice = totalPrice.add(orderItem.getItem().getPrice()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        return totalPrice;
    }
}
