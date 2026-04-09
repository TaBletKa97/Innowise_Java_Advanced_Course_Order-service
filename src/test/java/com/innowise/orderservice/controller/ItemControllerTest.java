package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/init.sql", executionPhase =  Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@WithMockUser(authorities = "ADMIN")
class ItemControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void getAllItems_ShouldReturnTenItem() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void getItem_ExistingId_ShouldReturnItem() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItem_NonExistingId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_ValidRequest_ShouldCreateItem() throws Exception {
        ItemCreateRequestDto request =
                new ItemCreateRequestDto("Test Item", BigDecimal.valueOf(10.99));
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(10.99)))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateItem_ExistingId_ValidRequest_ShouldUpdateItem() throws Exception {
        ItemUpdateRequestDto req =
                new ItemUpdateRequestDto("New", null);

        mockMvc.perform(put("/items/1")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.price").value(9.99));
    }

    @Test
    void deleteItem_ExistingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isNoContent());
    }
}