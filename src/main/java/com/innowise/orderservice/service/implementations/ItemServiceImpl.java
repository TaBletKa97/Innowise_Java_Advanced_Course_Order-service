package com.innowise.orderservice.service.implementations;

import com.innowise.orderservice.exceptions.AlreadyDeletedException;
import com.innowise.orderservice.exceptions.ItemNotFoundException;
import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.repository.interfaces.ItemRepository;
import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemResponseDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.ItemService;
import com.innowise.orderservice.service.mappers.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements
        ItemService<ItemResponseDto, ItemCreateRequestDto, ItemUpdateRequestDto, Long> {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponseDto> readAll() {
        return itemMapper.listItemsToDto(itemRepository.findAll());
    }

    @Override
    public ItemResponseDto readById(Long id) {
        Item item = getItemFromRepository(id);
        return itemMapper.itemToDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto create(ItemCreateRequestDto createRequest) {
        Item item = itemMapper.dtoToEntity(createRequest);
        item = itemRepository.saveAndFlush(item);
        return itemMapper.itemToDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long id, ItemUpdateRequestDto updateRequest) {
        Item updatedItem = getItemFromRepository(id);
        itemMapper.updateFromDto(updateRequest, updatedItem);
        updatedItem = itemRepository.saveAndFlush(updatedItem);
        return itemMapper.itemToDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Item item = getItemFromRepository(id);
        if (item.isDeleted()) {
            throw new AlreadyDeletedException(id);
        }
        item.setDeleted(true);
        itemRepository.save(item);
    }

    private Item getItemFromRepository(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new ItemNotFoundException(id));
    }
}
