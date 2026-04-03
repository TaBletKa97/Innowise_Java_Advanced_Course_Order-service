package com.innowise.orderservice.service.mappers;

import com.innowise.orderservice.external.UserHttpClient;
import com.innowise.orderservice.repository.entity.Order;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.dto.userserviceresponce.UserResponseDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, OrderItemMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrderMapper {

    protected UserHttpClient userHttpClient;

    @Autowired
    public void setUserHttpClient(UserHttpClient userHttpClient) {
        this.userHttpClient = userHttpClient;
    }

    @Mapping(target = "user",
            expression = "java(userHttpClient.requestUserById(order.getUserId()))")
    @Mapping(target = "itemList", source = "orderItems")
    public abstract OrderResponseDto entityToDto(Order order);

    public List<OrderResponseDto> entityListToDto(List<Order> all) {
        if (all.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserResponseDto> externalUsers = userHttpClient.requestAllUsers();
        if (externalUsers.isEmpty()) {
            UserResponseDto plug = new UserResponseDto(null, "User service is not responding.",
                    "Please, try again later.", null, null, false, null, null, null);
            return all.stream().map(o -> forInnerUseMapper(o, plug)).toList();
        }

        Set<Long> userIds = all.stream().map(Order::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserResponseDto> users = externalUsers
                .stream()
                .filter(u -> userIds.contains(u.id()))
                .collect(Collectors.toMap(UserResponseDto::id, Function.identity()));

        return all.stream()
                .map(o -> this.forInnerUseMapper(o, users.get(o.getUserId())))
                .toList();

    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", source = "itemList")
    public abstract Order requestToEntity(OrderAdminCreateRequestDto request);

    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "itemList", source = "orderItems")
    protected abstract OrderResponseDto forInnerUseMapper(
            Order order, @Context UserResponseDto user
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", source = "itemList")
    public abstract void updateOrder(@MappingTarget Order order,
                                     OrderAdminUpdateRequestDto request);
}
