package com.nhnacademy.illuwa.common.external.user;

import com.nhnacademy.illuwa.common.external.user.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "user-service"
)
public interface UserApiClient {

    // 멤버 주소록 조회
    @GetMapping(value = "/members/{memberId}/addresses")
    List<MemberAddressDto> getAddressByMemberId(@PathVariable("memberId") Long memberId);

    // 사용된 포인트 전송
    @PostMapping(value = "/points/use")
    MemberUsedPointDto sendUsedPointByMemberId(@RequestBody PointRequest request);

    // 순수 금액 전송 (상품 구매시, 적립금 적용에 사용)
    @PostMapping(value = "/points/earn")
    MemberSavePointDto sendTotalPrice(@RequestBody TotalRequest request);

    // 해당 멤버의 포인트 조회 (쿼리 파라미터로 memberId 전달)
    @GetMapping(value = "/points")
    Optional<MemberPointDto> getPointByMemberId(@RequestParam("memberId") Long memberId);

    // 멤버들의 3개월간 순수 주문 금액 전송 (주문 상태: confirmed )
    @PostMapping(value = "/members/grades/update")
    List<MemberGradeUpdateRequest> sendNetOrderAmount(@RequestBody List<MemberGradeUpdateRequest> request);
}
