package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PackagingControllerTest {

    @InjectMocks
    private PackagingController controller;

    @Mock
    private PackagingService packagingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PackagingCreateRequestDto requestDto;
    private PackagingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        requestDto = new PackagingCreateRequestDto("선물포장", new BigDecimal("1500"));
        responseDto = new PackagingResponseDto(1L, "선물포장", new BigDecimal("1500"));
    }

    @Test
    @DisplayName("POST /api/packaging - 포장 옵션 생성 (201)")
    void createPackaging_success() throws Exception {
        Mockito.when(packagingService.addPackaging(any(PackagingCreateRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/packaging")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.packagingId", is(1)))
                .andExpect(jsonPath("$.packagingName", is("선물포장")))
                .andExpect(jsonPath("$.packagingPrice", is(1500)));
    }

    @Test
    @DisplayName("GET /api/packaging/{id} - 포장 옵션 단일 조회 (200)")
    void getPackaging_success() throws Exception {
        Mockito.when(packagingService.getPackaging(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/packaging/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packagingId", is(1)))
                .andExpect(jsonPath("$.packagingName", is("선물포장")))
                .andExpect(jsonPath("$.packagingPrice", is(1500)));
    }

    @Test
    @DisplayName("GET /api/packaging - 활성 포장 옵션 리스트 조회 (200)")
    void getAllPackaging_success() throws Exception {
        Mockito.when(packagingService.getPackagingByActive(true))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/packaging"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packagingId", is(1)))
                .andExpect(jsonPath("$[0].packagingName", is("선물포장")))
                .andExpect(jsonPath("$[0].packagingPrice", is(1500)));
    }

    @Test
    @DisplayName("PUT /api/packaging/{id} - 포장 옵션 수정 (200)")
    void updatePackaging_success() throws Exception {
        PackagingCreateRequestDto updateDto = new PackagingCreateRequestDto("새포장", new BigDecimal("2000"));
        PackagingResponseDto updatedResponse = new PackagingResponseDto(1L, "새포장", new BigDecimal("2000"));

        Mockito.when(packagingService.updatePackaging(eq(1L), any(PackagingCreateRequestDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/packaging/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packagingName", is("새포장")))
                .andExpect(jsonPath("$.packagingPrice", is(2000)));
    }

    @Test
    @DisplayName("DELETE /api/packaging/{id} - 포장 옵션 비활성화 (204)")
    void deletePackaging_success() throws Exception {
        Mockito.when(packagingService.removePackaging(1L)).thenReturn(1);

        mockMvc.perform(delete("/api/packaging/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
