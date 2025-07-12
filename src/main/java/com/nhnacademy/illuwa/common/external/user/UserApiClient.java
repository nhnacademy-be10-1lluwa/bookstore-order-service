package com.nhnacademy.illuwa.common.external.user;

import com.nhnacademy.illuwa.common.annotation.CurrentUserId;
import com.nhnacademy.illuwa.common.external.user.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@FeignClient(name = "user-service")

public interface UserApiClient {

    // 멤버 주소록 조회
    @GetMapping(value = "/api/members/addresses")
    List<MemberAddressDto> getAddressByMemberId(@RequestHeader("X-USER-ID") Long memberId);


    // 순수 금액 전송 (상품 구매시, 적립금 적용에 사용)
    @PostMapping(value = "/api/members/points/order/earn")
    MemberSavePointDto sendTotalPrice(@RequestBody TotalRequest request);

    // 사용된 포인트 전송
    @PostMapping(value = "/api/members/points/order/use")
    MemberUsedPointDto sendUsedPointByMemberId(@RequestBody PointRequest request);



    // 해당 멤버의 포인트 조회 (RequestHeader 로 memberId 전달)
    @GetMapping(value = "/api/members/points")
    Optional<BigDecimal> getPointByMemberId(@RequestHeader("X-USER-ID") Long memberId);

    // 멤버들의 3개월간 순수 주문 금액 전송 (주문 상태: confirmed )
    @PostMapping(value = "/api/members/grades/update")
    List<MemberGradeUpdateRequest> sendNetOrderAmount(@RequestBody List<MemberGradeUpdateRequest> request);

    @GetMapping("/api/members")
    MemberDto getMember(@RequestHeader("X-USER-ID") long memberId);
    // 이제 USER 쪽에 RabbitMQ로 회원가입이 완료됐다는 신호를 받으면 내쪽에서는
    // 그 신호를 바탕으로 FeignClient를 사용해 해당 멤버 ID를 기준으로 멤버를 가져와서
    // 쿠폰을 발급시킨다.

    @GetMapping(value = "/api/members/birth-month", params = "month")
    List<MemberDto> getBirthDayMember(@RequestParam("month") int month);
}
