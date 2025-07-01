package com.nhnacademy.illuwa.domain.order.external.member;

import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberPointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberSavePointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberUsedPointDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@FeignClient(
        name = "UserService",
        url = "${user-api.url}"
)
public interface MemberPointApiClient {

//    // 사용된 포인트 전송
//    @PostMapping(value = "/points/use")
//    MemberUsedPointDto sendUsedPointByMemberId(Long memberId, BigDecimal usedPoint);
//
//    // 순수 금액 전송 (상품 구매시, 적립금 적용에 사용)
//    @PostMapping(value = "/points/earn")
//    MemberSavePointDto sendTotalPrice(Long memberId, BigDecimal totalPrice);
//
//    // 해당 멤버의 포인트 조회
//    @GetMapping(value = "/points")
//    Optional<MemberPointDto> getPointByMemberId(Long memberId);
//
//    // 멤버들의 3개월간 순수 주문 금액 전송 (주문 상태: confirmed )
//    @PostMapping(value = "/members/grades/update")
//    List<MemberGradeUpdateRequest> sendNetOrderAmount();

    /**
     * FeignClient 인터페이스 메서드 중에 @PostMapping 메서드들이 모두 파라미터가 여러 개인데
     * 각각 @RequestBody 등 HTTP 요청 본문 바인딩 어노테이션이 명시 X -> 에러
     */

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
    List<MemberGradeUpdateRequest> sendNetOrderAmount();
}
