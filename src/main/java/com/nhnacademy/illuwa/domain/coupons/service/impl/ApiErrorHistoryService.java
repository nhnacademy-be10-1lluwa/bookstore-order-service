package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.entity.ApiErrorHistory;
import com.nhnacademy.illuwa.domain.coupons.repository.ApiErrorHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiErrorHistoryService {
    private final ApiErrorHistoryRepository apiErrorHistoryRepository;

    public void save(String errorType, Exception e) {
        try {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            ApiErrorHistory history = ApiErrorHistory.builder()
                    .errorType(errorType)
                    .stackTrace(stackTrace)
                    .occurredAt(LocalDateTime.now())
                    .build();

            apiErrorHistoryRepository.save(history);
        } catch (Exception ex) {
            log.error("[장애이력 저장실패] - 원인: {}", ex.getMessage());
        }
    }
}
