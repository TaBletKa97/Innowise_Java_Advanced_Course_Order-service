package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.repository.entity.Item;
import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemResponseDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    List<ItemResponseDto> listItemsToDto(List<Item> items);

    ItemResponseDto itemToDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", expression = "java(false)")
    Item dtoToEntity(ItemCreateRequestDto createRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDto(ItemUpdateRequestDto updateRequest, @MappingTarget Item updatedItem);
}
