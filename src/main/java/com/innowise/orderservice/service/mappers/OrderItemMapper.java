package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.repository.entity.OrderItem;
import com.innowise.orderservice.repository.interfaces.ItemRepository;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import com.innowise.orderservice.exceptions.ItemNotFoundException;
import com.innowise.orderservice.service.dto.orderitem.OrderItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        imports = {ItemMapper.class, ItemNotFoundException.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrderItemMapper {

    protected ItemRepository itemRepository;

    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public abstract List<OrderItem> dtoListToEntityList(
            List<OrderItemRequestDto> dtoList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", expression = "java(itemRepository" +
            ".findById(dto.itemId()).orElseThrow(() -> " +
            "new ItemNotFoundException(dto.itemId())))")
    public abstract OrderItem dtoToEntity(OrderItemRequestDto dto);


    public abstract OrderItemResponseDto entityToDto(OrderItem entity);

    public abstract List<OrderItemResponseDto> entityToDtoList(List<OrderItem> entityList);
}