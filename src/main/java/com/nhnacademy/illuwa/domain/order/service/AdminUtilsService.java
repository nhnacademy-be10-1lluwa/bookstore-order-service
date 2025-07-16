package com.nhnacademy.illuwa.domain.order.service;

import java.util.Map;

public interface AdminUtilsService {
    int sendMonthlyNetOrderAmount();
    Map<String, Integer> dbDataScheduler();
}
