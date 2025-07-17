package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.ApiErrorHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiErrorHistoryRepository extends JpaRepository<ApiErrorHistory, Long> {

}
