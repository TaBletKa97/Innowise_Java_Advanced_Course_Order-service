package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.external.UserHttpClient;
import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.repository.entity.Order;
import com.innowise.orderservice.repository.entity.OrderItem;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.repository.interfaces.ItemRepository;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.userserviceresponce.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private UserHttpClient userHttpClient;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderItemMapper orderItemMapper = Mappers.getMapper(OrderItemMapper.class);

    @InjectMocks
    private OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "orderItemMapper", orderItemMapper);
    }

    @Test
    void entityToDto_ShouldMapOrderToOrderResponseDto() {
        //Arrange
        var order = new Order();
        var orderItem = new OrderItem();
        var item = new Item();
        var yesterday = LocalDateTime.now().minusDays(1);

        item.setId(1L);
        item.setName("item");
        item.setDeleted(false);
        item.setPrice(BigDecimal.valueOf(100.00));
        item.setCreatedAt(yesterday);
        item.setUpdatedAt(yesterday);

        orderItem.setId(1L);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setUpdatedAt(yesterday);
        orderItem.setCreatedAt(yesterday);
        orderItem.setOrder(order);

        order.setId(1L);
        order.setUserId(2L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.valueOf(200.00));
        order.setDeleted(Boolean.FALSE);
        order.setCreatedAt(yesterday);
        order.setUpdatedAt(yesterday);
        order.setOrderItems(List.of(orderItem));

        UserResponseDto user = new UserResponseDto(2L, "name",
                "surname", null, "email", true,
                yesterday, yesterday, null);

        when(userHttpClient.requestUserById(2L)).thenReturn(user);

        //Act
        OrderResponseDto result = mapper.entityToDto(order);

        //Assert
        assertEquals(1L, result.id());
        assertEquals(OrderStatus.CREATED, result.status());
        assertEquals(BigDecimal.valueOf(200.00), result.totalPrice());
        assertFalse(result.deleted());
        assertEquals(yesterday, result.createdAt());
        assertEquals(yesterday, result.updatedAt());
        assertSame(user, result.user());
        assertEquals(1, result.itemList().size());
        assertEquals("item", result.itemList().getFirst().item().name());
    }

    @Test
    void entityListToDto_ShouldMapOrderListToOrderResponseDtoList() {
        //Arrange
        var orders = List.of(
                new Order(1L,
                        2L,
                        OrderStatus.CREATED,
                        BigDecimal.valueOf(200.00),
                        Boolean.FALSE,
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().minusDays(1),
                        null),
                new Order(
                        2L,
                        3L,
                        OrderStatus.APPROVED,
                        BigDecimal.valueOf(150.00),
                        Boolean.FALSE,
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(2),
                        null),
                new Order(3L,
                        2L,
                        OrderStatus.REJECTED,
                        BigDecimal.valueOf(300.00),
                        Boolean.FALSE,
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(3),
                        null)
        );

        var user1 = new UserResponseDto(
                2L,
                "John",
                "Doe",
                null,
                "john@example.com",
                true,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10),
                null);
        var user2 = new UserResponseDto(
                3L,
                "Jane",
                "Smith",
                null,
                "jane@example.com",
                true,
                LocalDateTime.now().minusDays(11),
                LocalDateTime.now().minusDays(11),
                null);

        when(userHttpClient.requestAllUsers()).thenReturn(List.of(user1, user2));

        //Act
        List<OrderResponseDto> result = mapper.entityListToDto(orders);

        //Assert
        assertNotNull(result);

        for (OrderResponseDto order : result) {
            assertNotNull(order.id());
            assertNotNull(order.status());
            assertNotNull(order.deleted());
            assertNotNull(order.createdAt());
            assertNotNull(order.updatedAt());
            assertEquals(order.userId(), order.user().id());
        }
    }

    @Test
    void requestToEntity_ShouldMapOrderRequestDtoToOrder() {
        //Arrange
        var status = OrderStatus.APPROVED;
        var totalPrice = BigDecimal.valueOf(250.00);
        var request = new OrderAdminCreateRequestDto(
                5L, status, totalPrice, List.of(
                new OrderItemRequestDto(1L, 3),
                new OrderItemRequestDto(2L, 2)
        ));
        var item1 = new Item(1L, "item1", BigDecimal.valueOf(15.99),
                false, null, null);
        var item2 = new Item(2L, "item2", BigDecimal.valueOf(10.99),
                false, null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        //Act
        Order result = mapper.requestToEntity(request);

        //Assert
        assertEquals(5L, result.getUserId());
        assertEquals(status, result.getStatus());
        assertEquals(totalPrice, result.getTotalPrice());
        assertNull(result.getId());
        assertEquals(2, result.getOrderItems().size());
        assertEquals(item1.getName(), result.getOrderItems().get(0).getItem().getName());
        assertEquals(2, result.getOrderItems().get(1).getQuantity());
    }

    @Test
    void updateOrder_ShouldUpdateGivenOrderFromRequestDto() {
        //Arrange
        LocalDateTime time = LocalDateTime.now().minusDays(5);
        var order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.TEN);
        order.setDeleted(false);
        order.setCreatedAt(time);
        order.setUpdatedAt(time);

        var item1 = new Item(1L, "item1", BigDecimal.valueOf(15.99),
                false, null, null);
        var item2 = new Item(2L, "item2", BigDecimal.valueOf(10.99),
                false, null, null);

        var status = OrderStatus.APPROVED;
        var totalPrice = BigDecimal.valueOf(150.00);
        var request = new OrderAdminUpdateRequestDto(3L, status,
                totalPrice, List.of(
                new OrderItemRequestDto(1L, 3),
                new OrderItemRequestDto(2L, 2)
        ));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        //Act
        mapper.updateOrder(order, request);

        //Assert
        assertEquals(1L, order.getId());
        assertEquals(3L, order.getUserId());
        assertEquals(status, order.getStatus());
        assertEquals(totalPrice, order.getTotalPrice());
        assertEquals(time, order.getCreatedAt());
        assertEquals(time, order.getUpdatedAt());
        assertEquals(1L, order.getOrderItems().get(0).getItem().getId());
        assertEquals(2L, order.getOrderItems().get(1).getItem().getId());
    }

    @Test
    void updateOrder_updatesOnlyNonNullFields() {
        //Arrange
        var order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.TEN);
        order.setDeleted(false);
        order.setCreatedAt(LocalDateTime.now().minusDays(5));
        order.setUpdatedAt(LocalDateTime.now().minusDays(5));

        var request = new OrderAdminUpdateRequestDto(null, OrderStatus.REJECTED, null, null);

        //Act
        mapper.updateOrder(order, request);

        //Assert
        assertEquals(2L, order.getUserId());
        assertEquals(OrderStatus.REJECTED, order.getStatus());
        assertEquals(BigDecimal.TEN, order.getTotalPrice());
    }
}