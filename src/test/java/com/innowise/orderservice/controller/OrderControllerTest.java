package com.innowise.orderservice.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import com.innowise.orderservice.service.dto.userserviceresponce.UserResponseDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@WireMockTest
class OrderControllerTest extends BaseTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().dynamicPort())
            .build();

    @DynamicPropertySource
    public static void setUpMockBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("user-service.url", wireMock::baseUrl);
    }

    private static final String SURNAME = "Stark";

    private static UserResponseDto user1;
    private static UserResponseDto user2;
    private static UserResponseDto user3;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    static void beforeAll() {
        user1 = new UserResponseDto(1L, "Arya", SURNAME,
                LocalDate.of(1799, 3, 15), "arya@gmail.com", true,
                LocalDateTime.now(), LocalDateTime.now(), null);
        user2 = new UserResponseDto(2L, "Ned", SURNAME,
                LocalDate.of(1779, 12, 16), "arya@gmail.com", true,
                LocalDateTime.now(), LocalDateTime.now(), null);
        user3 = new UserResponseDto(3L, "Catelyn", SURNAME,
                LocalDate.of(1781, 8, 30), "arya@gmail.com", true,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllOrders_Admin_ShouldReturnOrders() throws Exception {

        wireMock.stubFor(WireMock.get("/users")
                .withHeader("role", containing("ADMIN"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(List.of(user1, user2, user3))))
        );

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.id").value(Matchers.notNullValue()));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getOrder_ExistingId_Admin_ShouldReturnOrder() throws Exception {
        wireMock.stubFor(WireMock.get("/users/1")
                .withHeader("role", containing("ADMIN"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(user1)))
        );

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.surname").value(SURNAME));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getOrder_NonExistingId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_ValidRequest_ShouldCreateOrder() throws Exception {
        OrderCreateRequestDto request = new OrderCreateRequestDto(
                List.of(new OrderItemRequestDto(1L, 2))
        );

        mockMvc.perform(post("/orders")
                        .header("user_id", 1)
                        .header("role", "USER")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.itemList.length()").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateOrder_ExistingId_Admin_ShouldUpdateOrder() throws Exception {
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(
                List.of(new OrderItemRequestDto(2L, 3))
        );

        mockMvc.perform(put("/orders/1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemList.length()").value(1));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateOrder_NonEditableStatus_ShouldReturnConflict() throws Exception {
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(
                List.of(new OrderItemRequestDto(1L, 1))
        );

        mockMvc.perform(put("/orders/2")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteOrder_ExistingId_Admin_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getOrdersWithCriteria_Admin_ShouldReturnFilteredOrders() throws Exception {
        final String dates = LocalDate.now().toString()
                .concat(":")
                .concat(LocalDate.now().plusDays(1).toString());
        mockMvc.perform(get("/orders")
                        .param("dates", dates)
                        .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/orders")
                        .param("dates", dates)
                        .param("status", "created")
                        .param("status", "approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }
}