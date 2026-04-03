package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void testDtoToEntity_MapsCorrectly() {
        // Arrange
        ItemCreateRequestDto dto = new ItemCreateRequestDto("Product Name",
                BigDecimal.valueOf(99.99));

        // Act
        Item entity = itemMapper.dtoToEntity(dto);

        // Assert
        assertNull(entity.getId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
        assertEquals(dto.name(), entity.getName());
        assertEquals(dto.price(), entity.getPrice());
        assertFalse(entity.isDeleted());
    }

    @Test
    void testUpdateFromDto_UpdatesFieldsSuccessfully() {
        // Arrange
        ItemUpdateRequestDto updateDto = new ItemUpdateRequestDto(
                "Updated Name", BigDecimal.valueOf(149.99));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        Item item = new Item();
        item.setName("Original Name");
        item.setPrice(BigDecimal.valueOf(99.99));
        item.setDeleted(false);
        item.setId(1L);
        item.setCreatedAt(yesterday);
        item.setUpdatedAt(yesterday);

        // Act
        itemMapper.updateFromDto(updateDto, item);

        // Assert
        assertEquals(updateDto.name(), item.getName());
        assertEquals(updateDto.price(), item.getPrice());
        assertFalse(item.isDeleted());
        assertEquals(1L, item.getId());
        assertEquals(yesterday,  item.getCreatedAt());
        assertEquals(yesterday, item.getUpdatedAt());
    }

    @Test
    void testUpdateFromDto_IgnoresNullValues() {
        // Arrange
        final String name = "Original Name";
        ItemUpdateRequestDto updateDto = new ItemUpdateRequestDto(
                null, BigDecimal.valueOf(149.99));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        Item item = new Item();
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(99.99));
        item.setDeleted(false);
        item.setId(1L);
        item.setCreatedAt(yesterday);
        item.setUpdatedAt(yesterday);

        // Act
        itemMapper.updateFromDto(updateDto, item);

        // Assert
        assertEquals(name, item.getName());
        assertEquals(updateDto.price(), item.getPrice());
        assertFalse(item.isDeleted());
        assertEquals(1L, item.getId());
        assertEquals(yesterday,  item.getCreatedAt());
        assertEquals(yesterday, item.getUpdatedAt());
    }

}