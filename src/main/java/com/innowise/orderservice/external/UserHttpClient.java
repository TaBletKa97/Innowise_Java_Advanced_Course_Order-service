package com.innowise.orderservice.external;

import com.innowise.orderservice.service.dto.userserviceresponce.UserResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.ArrayList;
import java.util.List;

@HttpExchange(
        accept = "application/json",
        url = "${user-service.url}",
        headers = {"user_id=1", "role=ADMIN"}
)
public interface UserHttpClient {

    Logger log = LoggerFactory.getLogger(UserHttpClient.class);

    @GetExchange("/users/{id}")
    @CircuitBreaker(name = "userservice", fallbackMethod = "getPlugUserBack")
    UserResponseDto requestUserById(@PathVariable Long id);

    @GetExchange("/users")
    @CircuitBreaker(name = "userservice", fallbackMethod = "getPlugUserListBack")
    List<UserResponseDto> requestAllUsers();

    default UserResponseDto getPlugUserBack(Exception e) {
        log.error(e.getMessage(), e);
        return new UserResponseDto(null, "User service is not responding.",
                "Please, try again later.", null, null, false, null, null, null);
    }

    default List<UserResponseDto> getPlugUserListBack(Exception e) {
        log.error(e.getMessage(), e);
        return new ArrayList<>();
    }
}
