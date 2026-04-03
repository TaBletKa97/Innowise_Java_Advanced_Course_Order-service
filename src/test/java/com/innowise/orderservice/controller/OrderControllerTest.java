package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/init.sql", executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllOrders_Admin_ShouldReturnOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getOrder_ExistingId_Admin_ShouldReturnOrder() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
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
        mockMvc.perform(get("/orders/search")
                        .param("date", LocalDate.now().toString())
                        .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}