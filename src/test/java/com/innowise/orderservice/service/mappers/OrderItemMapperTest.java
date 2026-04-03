package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.exceptions.ItemNotFoundException;
import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.repository.interfaces.ItemRepository;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemMapperTest {

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);

    @Test
    void testDtoToEntity_MapsFieldsSuccessfully() {
        //Arrange
        Item item = new Item(1L, "Test Item", BigDecimal.valueOf(10.0),
                false, null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        OrderItemRequestDto dto = new OrderItemRequestDto(1L, 5);

        //Act
        var orderItem = mapper.dtoToEntity(dto);

        //Assert
        assertNotNull(orderItem);
        assertEquals(item, orderItem.getItem());
        assertEquals(dto.quantity(), orderItem.getQuantity());
        assertNull(orderItem.getId());
        assertNull(orderItem.getCreatedAt());
        assertNull(orderItem.getUpdatedAt());
    }

    @Test
    void testDtoToEntity_ThrowsWhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        OrderItemRequestDto dto = new OrderItemRequestDto(99L, 2);

        assertThrows(ItemNotFoundException.class, () -> mapper.dtoToEntity(dto));
    }
}