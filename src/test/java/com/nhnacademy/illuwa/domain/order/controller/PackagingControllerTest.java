package com.nhnacademy.illuwa.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.controller.admin.PackagingController;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = PackagingController.class)
class PackagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PackagingService packagingService;

    private PackagingCreateRequestDto requestDto;
    private PackagingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new PackagingCreateRequestDto("선물포장", new BigDecimal("1500"));
        responseDto = new PackagingResponseDto(1L, "선물포장", new BigDecimal("1500"));
    }

    @Test
    @DisplayName("POST /packaging - 포장 옵션 생성")
    void createPackaging() throws Exception {
        Mockito.when(packagingService.addPackaging(Mockito.any(PackagingCreateRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/packaging")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.packagingId").value(1L))
                .andExpect(jsonPath("$.packagingName").value("선물포장"))
                .andExpect(jsonPath("$.packagingPrice").value(1500));
    }

    @Test
    @DisplayName("GET /packaging/{id} - 포장 옵션 단일 조회")
    void getPackaging() throws Exception {
        Mockito.when(packagingService.getPackaging(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/packaging/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packagingId").value(1L))
                .andExpect(jsonPath("$.packagingName").value("선물포장"))
                .andExpect(jsonPath("$.packagingPrice").value(1500));
    }

    @Test
    @DisplayName("GET /packaging - 활성화된 포장 옵션 전체 조회")
    void getAllPackaging() throws Exception {
        Mockito.when(packagingService.getPackagingByActive(true))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/packaging"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packagingId").value(1L));
    }

    @Test
    @DisplayName("PUT /packaging/{id} - 포장 옵션 수정")
    void updatePackaging() throws Exception {
        PackagingCreateRequestDto updateDto =
                new PackagingCreateRequestDto("새포장", new BigDecimal("2000"));
        PackagingResponseDto updatedResponse =
                new PackagingResponseDto(1L, "새포장", new BigDecimal("2000"));

        Mockito.when(packagingService.updatePackaging(Mockito.eq(1L),
                        Mockito.any(PackagingCreateRequestDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/packaging/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packagingName").value("새포장"))
                .andExpect(jsonPath("$.packagingPrice").value(2000));
    }

    @Test
    @DisplayName("DELETE /packaging/{id} - 포장 옵션 비활성화")
    void deletePackaging() throws Exception {
        Mockito.when(packagingService.removePackaging(1L)).thenReturn(1);

        mockMvc.perform(delete("/api/packaging/{id}", 1L))
                .andExpect(status().isNoContent());

    }
}
