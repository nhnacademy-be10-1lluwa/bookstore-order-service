package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberDto;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponInfoResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyInactiveException;
import com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.repository.*;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType.BIRTHDAY;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final UserApiClient userApiClient;

    // welcome 쿠폰 발급
//    @Override
//    public MemberCouponResponse issueWelcomeCoupon(String email) {
//        Member member = memberRepository.findMemberByEmail(email).orElseThrow
//                (() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
//
//        boolean bool = memberCouponRepository.existsByMemberIdAndCoupon_CouponType(member.getId(), CouponType.WELCOME);
//        if (bool) {
//            throw new IllegalArgumentException("해당 회원은 이미 웰컴 쿠폰을 지급받으셨습니다.");
//        }
//
//        Coupon coupon = couponRepository.findByCouponType(CouponType.WELCOME).orElseThrow
//                (() -> new IllegalArgumentException("WELCOME 쿠폰이 존재하지 않습니다."));
//
//        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
//        }
//
//        MemberCoupon memberCoupon = MemberCoupon.builder()
//                .member(member)
//                .coupon(coupon)
//                .issuedAt(LocalDate.now())
//                .build();
//
//        coupon.setIssueCount(coupon.getIssueCount().subtract(BigDecimal.ONE));
//
//        MemberCoupon save = memberCouponRepository.save(memberCoupon);
//        return MemberCouponResponse.fromEntity(save);
//    }


    // 회원 생일 쿠폰 발급
    @Override
    public void issueBirthDayCoupon() {
        int monthValue = LocalDate.now().getMonthValue();
        List<MemberDto> members = userApiClient.getBirthDayMember(monthValue);

        if (members.isEmpty()) {
            throw new IllegalArgumentException("당월 생일자에게만 발급되는 쿠폰입니다.");
        }

        Coupon birthDayCoupon = couponRepository.findCouponByCouponName("생일쿠폰")
                .orElseThrow(() -> new IllegalArgumentException("생일 쿠폰이 존재하지 않습니다."));

        if (birthDayCoupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        for (MemberDto birthDayMember : members) {
            boolean bool = memberCouponRepository.existsByMemberIdAndCouponId(birthDayMember.getMemberId(), birthDayCoupon.getId());

            if (!bool) {
                MemberCoupon updateMember = MemberCoupon.builder()
                        .memberId(birthDayMember.getMemberId())
                        .coupon(birthDayCoupon)
                        .issuedAt(LocalDate.now())
                        .build();

                birthDayCoupon.setIssueCount(birthDayCoupon.getIssueCount().subtract(BigDecimal.ONE));
                memberCouponRepository.save(updateMember);
            }
        }
    }

    // 쿠폰 발급
    @Override
    public MemberCouponResponse issueCoupon(Long memberId, MemberCouponCreateRequest request) {
//         MemberDto member = userApiClient.getMember(request.getMemberId()); // 우선적으로 FeignClient를 통해 필요한 정보를 가져옴 || 여기서 못가져오면 존재하지 않는 회원이라 API통신에러라고 표시될꺼임
        Coupon coupon = couponRepository.findCouponByCouponName(request.getCouponName()).orElseThrow(() -> new CouponNotFoundException("해당 쿠폰은 존재하지 않습니다."));
        // 고려사항 + -> 쿠폰정책이 임시적으로 비활성화일시 (쿠폰의 발급 불가)
        if (coupon.getPolicy().getStatus().equals(CouponStatus.INACTIVE)) {
            throw new CouponPolicyInactiveException("해당 쿠폰은 관리자에 의해 임시적으로 발급이 불가능합니다.");
        }

        // 고려사항 1 -> 쿠폰의 수량이 없을 시.
        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }
        // 고려사항 2 -> 이미 발급받은 쿠폰일때
        if (memberCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId())) {
            throw new MemberCouponInactiveException("이미 쿠폰을 발급받으셨습니다. -> " + coupon.getCouponName());
        }

        // 고려사항 3 -> 만약 정책쪽에서 (=비활성화) or 쿠폰쪽에서 (삭제)라면?
        // 정책쪽의 repo로 상태 비교후 생성? -> 이러면 회원 쿠폰이라는 엔티티가 정책쪽으로 직접 접근하게됌
        // 쿠폰쪽의 상태로 비교후 생성?
        // 그럼 쿠폰쪽의 상태를 무엇으로 관리할꺼냐 ? -> 정책(=비활성화) = 쿠폰(사용불가) || 정책(활성화) = 쿠폰(사용가능)


//         고려사항 4 -> 생일 쿠폰
        if (coupon.getCouponType() == BIRTHDAY) {
            issueBirthDayCoupon();
            return null;
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .coupon(coupon)
                .issuedAt(LocalDate.now())
                .build();
        // 쿠폰 수량 차감 (= 따로 save를 호출하지 않아도 @Transactional 어노테이션 덕분에 자동으로 반영)
        coupon.setIssueCount(coupon.getIssueCount().subtract(BigDecimal.ONE));
        // DB저장
        MemberCoupon save = memberCouponRepository.save(memberCoupon);

        return MemberCouponResponse.fromEntity(save);
    }

    @Override
    public List<MemberCouponResponse> getAllMemberCoupons(Long memberId) {
        return memberCouponRepository.findMemberCouponsByMemberId(memberId)
                .stream()
                .map(MemberCouponResponse::fromEntity)
                .toList();
    }

    @Override
    public MemberCouponDiscountDto getDiscountPrice(Long memberCouponId) {
        return memberCouponRepository.findDtoByMemberCouponId(memberCouponId).orElseThrow(()
                -> new NotFoundException("해당 쿠폰을 찾지 못하였습니다.", memberCouponId));
    }

    // 회원 소유 쿠폰 확인 (email)
//    @Override
//    public List<MemberCouponResponse> getAllMemberCoupons(String email) {
//        return memberCouponRepository.findMemberCouponByMemberEmail(email)
//                .stream()
//                .map(MemberCouponResponse::fromEntity)
//                .toList();
//    }


//    @Override
//    public CouponInfoResponse getCouponInfoFromMemberCoupon(Long memberCouponId) {
//        MemberCoupon membercoupon = memberCouponRepository.findById(memberCouponId)
//                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));
//        Coupon coupon = membercoupon.getCoupon();
//        return CouponInfoResponse.fromEntity(coupon);
//    }

//    @Override
//    public MemberCouponResponse getMemberCouponId(Long id) {
//        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponById(id)
//                .orElseThrow(() -> new MemberCouponNotFoundException("해당 쿠폰은 존재하지 않습니다."));
//        return MemberCouponResponse.fromEntity(memberCoupon);
//    }
//
//    @Override
//    public List<MemberCouponResponseTest> getAllMemberCouponsTest(Long id) {
//        return null;
//        return memberCouponRepository.findMemberById(id)
//                .stream()
//                .map(MemberCouponResponseTest::fromEntity)
//                .toList();
    // 특정 도서에 적용 -> 주문 + ()

    // 회원 소유 쿠폰 사용 ( 우선 이메일과 사용하고자하는 쿠폰이름을 매개변수로 가져옴 )
    // 그러면 우선적으로 회원이 있는지 확인부터 - > 아니지 회원자체는 발급쪽에서 검사를했으니
    // 회원이 발급받은 쿠폰이 있는지부터 확인.
    @Override
    public MemberCouponUseResponse useCoupon(Long memberId, Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndId(memberId, memberCouponId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));

        if (memberCoupon.getCoupon().getPolicy().getStatus() == CouponStatus.INACTIVE) {
            throw new CouponPolicyInactiveException("관리자에 의해 정책이 비활성화이므로 사용이 불가능합니다.");
        }

        if (memberCoupon.isUsed()) {
            throw new MemberCouponIsUsed("이미 사용한 쿠폰입니다.");
        }
        if (memberCoupon.getExpiresAt().isBefore(LocalDate.now())) {
            throw new MemberCouponExpiredException("쿠폰의 유효기간이 만료되었습니다.");
        }
        memberCoupon.setUsed(true);
        memberCoupon.setUsedAt(LocalDate.now());

        return MemberCouponUseResponse.fromEntity(memberCoupon);
    }
}


