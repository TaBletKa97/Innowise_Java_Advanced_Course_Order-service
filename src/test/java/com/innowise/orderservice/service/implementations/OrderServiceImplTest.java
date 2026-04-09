package com.innowise.orderservice.service.implementations;

import com.innowise.orderservice.exceptions.AlreadyDeletedException;
import com.innowise.orderservice.exceptions.ImmutableOrderUpdateException;
import com.innowise.orderservice.exceptions.OrderNotFoundException;
import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.repository.entity.Order;
import com.innowise.orderservice.repository.entity.OrderItem;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.repository.interfaces.OrderRepository;
import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import com.innowise.orderservice.service.mappers.OrderItemMapper;
import com.innowise.orderservice.service.mappers.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void readAll_ShouldReturnDtoList() {
        //Arrange
        var orderResponseDto = mock(OrderResponseDto.class);
        var order = mock(Order.class);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.entityListToDto(List.of(order)))
                .thenReturn(List.of(orderResponseDto));

        //Act
        List<OrderResponseDto> response = orderService.readAll();

        //Assert
        verify(orderRepository).findAll();
        verify(orderMapper).entityListToDto(List.of(order));
        assertEquals(1, response.size());
    }

    @Test
    void readById_ShouldReturnDto() {
        //Arrange
        final Long id = 1L;
        var orderResponseDto = mock(OrderResponseDto.class);
        var order = mock(Order.class);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.entityToDto(order)).thenReturn(orderResponseDto);

        //Act
        OrderResponseDto responseDto = orderService.readById(id);

        //Assert
        verify(orderRepository).findById(id);
        verify(orderMapper).entityToDto(order);
        assertEquals(orderResponseDto, responseDto);
    }

    @Test
    void readById_ShouldReturnNotFound() {
        //Arrange
        final Long id = 1L;
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.readById(id));
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void createOrder_ShouldReturnDto() {
        //Arrange
        var request = new OrderCreateRequestDto(List.of(
                new OrderItemRequestDto(1L, 2)
        ));
        var response = mock(OrderResponseDto.class);

        var item = new Item(1L, "item", BigDecimal.TEN, false, null, null);
        var orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(2);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(orderItemMapper.dtoListToEntityList(request.itemList()))
                .thenReturn(List.of(orderItem));
        when(orderMapper.entityToDto(any())).thenReturn(response);

        //Act
        OrderResponseDto responseDto = orderService.create(request);

        //Assert
        assertNotNull(responseDto);
        verify(orderRepository).saveAndFlush(argThat(order -> {
            assertEquals(1L, order.getUserId());
            assertEquals(OrderStatus.CREATED, order.getStatus());
            assertEquals(BigDecimal.valueOf(20), order.getTotalPrice());
            assertEquals(1, order.getOrderItems().size());
            return true;
        }));
        verify(orderItemMapper).dtoListToEntityList(request.itemList());
        verify(orderMapper).entityToDto(any());
    }

    @Test
    void updateOrder_ShouldReturnDto() {
        //Arrange
        final Long id = 1L;
        var updateRequest = new OrderUpdateRequestDto(List.of(
                new OrderItemRequestDto(1L, 3)
        ));
        var response = mock(OrderResponseDto.class);
        var order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setId(id);
        var item = new Item(1L, "item", BigDecimal.TEN, false, null, null);
        var orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(3);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderItemMapper.dtoListToEntityList(updateRequest.itemList()))
                .thenReturn(List.of(orderItem));
        when(orderMapper.entityToDto(order)).thenReturn(response);
        when(orderRepository.saveAndFlush(order)).thenReturn(order);

        //Act
        OrderResponseDto result = orderService.update(id, updateRequest);

        //Assert
        assertEquals(response, result);
        verify(orderRepository).findById(id);
        verify(orderRepository).saveAndFlush(argThat(o -> {
            assertEquals(1L, o.getId());
            assertEquals(BigDecimal.valueOf(30), o.getTotalPrice());
            assertEquals(1, o.getOrderItems().size());
            assertEquals(orderItem, o.getOrderItems().getFirst());
            return true;
        }));
        verify(orderItemMapper).dtoListToEntityList(updateRequest.itemList());
        verify(orderMapper).entityToDto(order);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"APPROVED", "REJECTED"})
    void updateOrder_ShouldThrowImmutableOrderException(OrderStatus status) {
        //Arrange
        final Long id = 1L;
        var updateRequest = new OrderUpdateRequestDto(List.of());
        var order = new Order();
        order.setStatus(status);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        //Act & Assert
        assertThrows(ImmutableOrderUpdateException.class, () ->
                orderService.update(id, updateRequest));
    }

    @Test
    void deleteOrder_ShouldMarkAsDeleted() {
        //Arrange
        final Long id = 1L;
        var order = new Order();
        order.setId(id);
        order.setDeleted(false);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        //Act
        orderService.deleteById(id);

        //Assert
        assertTrue(order.isDeleted());
        verify(orderRepository).findById(id);
        verify(orderRepository).save(order);
    }

    @Test
    void deleteOrder_ShouldThrowAlreadyDeletedException() {
        //Arrange
        final Long id = 1L;
        var order = new Order();
        order.setId(id);
        order.setDeleted(true);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        //Act & Assert
        assertThrows(AlreadyDeletedException.class, () ->  orderService.deleteById(id));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testAdminCreate_ShouldReturnDto() {
        //Arrange
        var request = mock(OrderAdminCreateRequestDto.class);
        var response = mock(OrderResponseDto.class);
        var order = new Order();
        order.setOrderItems(List.of(new OrderItem()));

        when(orderMapper.requestToEntity(request)).thenReturn(order);
        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        when(orderMapper.entityToDto(order)).thenReturn(response);

        //Act
        OrderResponseDto result = orderService.adminCreate(request);

        //Assert
        assertNotNull(result);
        verify(orderMapper).requestToEntity(request);
        verify(orderRepository).saveAndFlush(order);
        verify(orderMapper).entityToDto(order);
    }

    @Test
    void testAdminUpdate_ShouldReturnDto() {
        //Arrange
        final Long id = 1L;
        var updateRequest = mock(OrderAdminUpdateRequestDto.class);
        var response = mock(OrderResponseDto.class);
        var order = new Order();
        order.setOrderItems(List.of(new OrderItem()));

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        when(orderMapper.entityToDto(order)).thenReturn(response);

        //Act
        OrderResponseDto result = orderService.adminUpdate(id, updateRequest);

        //Assert
        assertNotNull(result);
        verify(orderMapper).updateOrder(order, updateRequest);
        verify(orderRepository).saveAndFlush(order);
        verify(orderMapper).entityToDto(order);
    }

    @Test
    void readByUserId_ShouldReturnDtoList() {
        //Arrange
        final Long userId = 1L;
        List<Order> list = List.of(new Order());
        List<OrderResponseDto> dtoList = List.of(mock(OrderResponseDto.class));

        when(orderRepository.findOrderByUserId(userId)).thenReturn(list);
        when(orderMapper.entityListToDto(list)).thenReturn(dtoList);

        //Act
        List<OrderResponseDto> resp = orderService.readByUserId(userId);

        //Arrange
        assertNotNull(resp);
        verify(orderRepository).findOrderByUserId(userId);
        verify(orderMapper).entityListToDto(list);
    }
}