package com.nhnacademy.illuwa.domain.order.external.member;



import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberPointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberSavePointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberUsedPointDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MemberPointApiClient {

    // 사용된 포인트 전송
    MemberUsedPointDto sendUsedPointByMemberId(Long memberId, BigDecimal usedPoint);

    // 순수 금액 전송 (상품 구매시, 적립금 적용에 사용)
    MemberSavePointDto sendTotalPrice(Long memberId, BigDecimal totalPrice);

    // 해당 멤버의 포인트 조회
    Optional<MemberPointDto> getPointByMemberId(Long memberId);

    // 멤버들의 3개월간 순수 주문 금액 전송 (주문 상태: confirmed )
    List<MemberGradeUpdateRequest> sendNetOrderAmount();

}
