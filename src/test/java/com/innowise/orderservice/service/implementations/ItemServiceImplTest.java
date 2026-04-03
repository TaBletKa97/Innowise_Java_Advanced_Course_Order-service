package com.innowise.orderservice.service.implementations;

import com.innowise.orderservice.exceptions.AlreadyDeletedException;
import com.innowise.orderservice.exceptions.ItemNotFoundException;
import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.repository.interfaces.ItemRepository;
import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemResponseDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import com.innowise.orderservice.service.mappers.ItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void readAll_ShouldReturnMappedDtoList() {
        // Arrange
        Item item = mock(Item.class);
        ItemResponseDto dto = mock(ItemResponseDto.class);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemMapper.listItemsToDto(List.of(item))).thenReturn(List.of(dto));

        // Act
        List<ItemResponseDto> result = itemService.readAll();

        // Assert
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
        verify(itemRepository).findAll();
        verify(itemMapper).listItemsToDto(List.of(item));
    }

    @Test
    void readById_ShouldReturnDto_WhenItemExists() {
        // Arrange
        Long id = 1L;
        Item item = mock(Item.class);
        ItemResponseDto dto = mock(ItemResponseDto.class);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemMapper.itemToDto(item)).thenReturn(dto);

        // Act
        ItemResponseDto result = itemService.readById(id);

        // Assert
        assertSame(dto, result);
        verify(itemRepository).findById(id);
        verify(itemMapper).itemToDto(item);
    }

    @Test
    void readById_ShouldThrowItemNotFoundException_WhenItemMissing() {
        // Arrange
        Long id = 2L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> itemService.readById(id));
        verify(itemRepository).findById(id);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void create_ShouldPersistAndReturnDto() {
        // Arrange
        ItemCreateRequestDto createDto = mock(ItemCreateRequestDto.class);
        Item entity = mock(Item.class);
        ItemResponseDto responseDto = mock(ItemResponseDto.class);
        when(itemMapper.dtoToEntity(createDto)).thenReturn(entity);
        when(itemRepository.saveAndFlush(entity)).thenReturn(entity);
        when(itemMapper.itemToDto(entity)).thenReturn(responseDto);

        // Act
        ItemResponseDto result = itemService.create(createDto);

        // Assert
        assertSame(responseDto, result);
        verify(itemMapper).dtoToEntity(createDto);
        verify(itemRepository).saveAndFlush(entity);
        verify(itemMapper).itemToDto(entity);
    }

    @Test
    void update_ShouldModifyExistingItemAndReturnDto() {
        // Arrange
        Long id = 3L;
        ItemUpdateRequestDto updateDto = mock(ItemUpdateRequestDto.class);
        Item existingItem = mock(Item.class);
        ItemResponseDto responseDto = mock(ItemResponseDto.class);
        when(itemRepository.findById(id)).thenReturn(Optional.of(existingItem));
        when(itemRepository.saveAndFlush(existingItem)).thenReturn(existingItem);
        when(itemMapper.itemToDto(existingItem)).thenReturn(responseDto);

        // Act
        ItemResponseDto result = itemService.update(id, updateDto);

        // Assert
        assertSame(responseDto, result);
        verify(itemRepository).findById(id);
        verify(itemMapper).updateFromDto(updateDto, existingItem);
        verify(itemRepository).saveAndFlush(existingItem);
        verify(itemMapper).itemToDto(existingItem);
    }

    @Test
    void update_ShouldThrowItemNotFoundException_WhenItemMissing() {
        // Arrange
        Long id = 4L;
        ItemUpdateRequestDto updateDto = mock(ItemUpdateRequestDto.class);
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> itemService.update(id, updateDto));
        verify(itemRepository).findById(id);
        verifyNoMoreInteractions(itemMapper, itemRepository);
    }

    @Test
    void deleteById_ShouldMarkItemAsDeleted_WhenNotAlreadyDeleted() {
        // Arrange
        Long id = 5L;
        Item item = mock(Item.class);
        when(item.isDeleted()).thenReturn(false);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        // Act
        itemService.deleteById(id);

        // Assert
        verify(item).setDeleted(true);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteById_ShouldThrowItemAlreadyDeletedException_WhenAlreadyDeleted() {
        // Arrange
        Long id = 6L;
        Item item = mock(Item.class);
        when(item.isDeleted()).thenReturn(true);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(AlreadyDeletedException.class, () -> itemService.deleteById(id));
        verify(item, never()).setDeleted(true);
        verify(itemRepository, never()).save(item);
    }

    @Test
    void deleteById_ShouldThrowItemNotFoundException_WhenItemMissing() {
        // Arrange
        Long id = 7L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteById(id));
        verify(itemRepository).findById(id);
    }
}