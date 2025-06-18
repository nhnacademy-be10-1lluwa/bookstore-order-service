package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;

    // 쿠폰 발급
    @Override
    public MemberCouponResponse issueCoupon(MemberCouponCreateRequest request) {
        Member member = memberRepository.findMemberByEmail(request.getMemberEmail()).orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
        Coupon coupon = couponRepository.findCouponByCouponName(request.getCouponName()).orElseThrow(() -> new IllegalArgumentException("해당 쿠폰은 존재하지 않습니다."));

        // 고려사항 1 -> 쿠폰의 수량이 없을 시.
        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        // 고려사항 2 -> 이미 발급받은 쿠폰일때
        if (memberCouponRepository.existsByMemberIdAndCouponId(member.getId(), coupon.getId())) {
            throw new IllegalArgumentException("이미 쿠폰을 발급받으셨습니다.");
        }

        // 고려사항 3 -> 만약 정책쪽에서 (=비활성화) or 쿠폰쪽에서 (삭제)라면?
        // 정책쪽의 repo로 상태 비교후 생성? -> 이러면 회원 쿠폰이라는 엔티티가 정책쪽으로 직접 접근하게됌
        // 쿠폰쪽의 상태로 비교후 생성?
        // 그럼 쿠폰쪽의 상태를 무엇으로 관리할꺼냐 ? -> 정책(=비활성화) = 쿠폰(사용불가) || 정책(활성화) = 쿠폰(사용가능)

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .issuedAt(LocalDate.now())
                .build();
//        // 만료일 +30 설정
//        memberCoupon.setExpireDate();
        // 쿠폰 수량 차감 (= 따로 save를 호출하지 않아도 @Transactional 어노테이션 덕분에 자동으로 반영)
        coupon.setIssueCount(coupon.getIssueCount().subtract(BigDecimal.ONE));
        // DB저장
        MemberCoupon save = memberCouponRepository.save(memberCoupon);

        return MemberCouponResponse.fromEntity(save);
    }

    // 회원 소유 쿠폰 조회 {ID 기준}
    @Override
    public MemberCouponResponse getMemberCouponId(Long id) {
        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponById(id).orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        return MemberCouponResponse.fromEntity(memberCoupon);
    }

    // 회원 소유 쿠폰 사용
    @Override
    public MemberCouponUseResponse useCoupon(Long id) {
        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponById(id).orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        // 쿠폰의 사용 시 해당 쿠폰을 삭제하는게 좋지 않나?
        // -> 그러면 DB내의 이미 발급한 쿠폰에 대해서 중복발급이 가능할지도
        // -> 삭제를 안시키고 boolean값으로 사용여부를 체크하면 쿠폰 발급내역 or 쿠폰 사용내역에 좀 도움이 될 듯
        // 아니면 MemberCoupon(회원쿠폰)쪽에 필드로 상태(enum)를 추가해서
        // status("사용가능","사용완료","사용불가","기간만료")도 좋을것 같긴한데 == 나중에 사용가능한 쿠폰만 볼수있도록 처리도 가능
        if (memberCoupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용한 쿠폰입니다.");
        }
        if (memberCoupon.getExpiresAt().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("쿠폰의 유효기간이 만료되었습니다.");
        }
        memberCoupon.setUsed(true);
        memberCoupon.setUsedAt(LocalDate.now());

        return MemberCouponUseResponse.fromEntity(memberCoupon);

    }


}
