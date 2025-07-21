package com.nhnacademy.illuwa.domain.order.service.admin;

import java.util.Map;

public interface AdminUtilsService {
    int sendMonthlyNetOrderAmount();
    Map<String, Integer> dbDataScheduler();
}
