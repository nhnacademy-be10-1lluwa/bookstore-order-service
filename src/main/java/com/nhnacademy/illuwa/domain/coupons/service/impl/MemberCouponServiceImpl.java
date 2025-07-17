package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponExpiredException;
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
    private final UserApiClient userApiClient;
    private final ApiErrorHistoryService apiErrorHistoryService;

    // 회원 생일 쿠폰 발급 (=자동 발급에 대해서)
    @Override
    public void issueBirthDayCoupon() {
        int monthValue = LocalDate.now().getMonthValue();
        List<MemberDto> members;

        try {
            members = userApiClient.getBirthDayMember(monthValue);
        } catch (Exception e) {
            log.error("[생일쿠폰] UserAPI 호출 실패 - month: {}, 원인: {}", monthValue, e.getMessage());
            apiErrorHistoryService.save("BIRTHDAY_COUPON", e);
            return;
        }

        if (members.isEmpty()) {
            throw new MemberNotBirthdayMonthException("당월 생일자에게만 발급되는 쿠폰입니다.");
        }

        Coupon birthDayCoupon = couponRepository.findCouponByCouponName("생일쿠폰")
                .orElseThrow(() -> new CouponNotFoundException("생일 쿠폰이 존재하지 않습니다."));

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

    // 생일 쿠폰 발급 ( 수동으로 발급 )
    @Override
    public void issueBirthDayCouponForMember(Long memberId) {
        MemberDto member = userApiClient.getMember(memberId);
        int month = LocalDate.now().getMonthValue();

        if (member.getBirth().getMonthValue() != month) {
            throw new MemberNotBirthdayMonthException("당월 생일자에게만 발급 되는 쿠폰입니다.");
        }

        Coupon coupon = couponRepository.findCouponByCouponName("생일쿠폰")
                .orElseThrow(() -> new CouponNotFoundException("생일 쿠폰이 존재하지 않습니다."));


        boolean exists = memberCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId());

        if (exists) {
            throw new MemberCouponExistsException("이미 쿠폰을 발급받으셨습니다. ->" + coupon.getCouponName());
        }

        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .coupon(coupon)
                .issuedAt(LocalDate.now())
                .build();
        coupon.setIssueCount(coupon.getIssueCount().subtract(BigDecimal.ONE));
        memberCouponRepository.save(memberCoupon);

    }

    // 쿠폰 발급
    @Override
    public MemberCouponResponse issueCoupon(Long memberId, MemberCouponCreateRequest request) {

        // 1차 책임 -> request의 값으로 쿠폰 유효성 검증
        Coupon coupon = couponRepository.findCouponByCouponName(request.getCouponName())
                .orElseThrow(() -> new CouponNotFoundException("해당 쿠폰이 존재하지 않습니다. -> " + request.getCouponName()));

        // 2차 책임 -> 조회한 coupon과 memberId로 "발급 여부"를 검증 (= 비즈니스 로직 )
        validateCoupon(coupon, memberId);


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
    @Transactional(readOnly = true)
    public List<MemberCouponResponse> getAllMemberCoupons(Long memberId) {
        return memberCouponRepository.findMemberCouponsByMemberId(memberId)
                .stream()
                .map(MemberCouponResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberCouponDiscountDto getDiscountPrice(Long memberCouponId) {
        return memberCouponRepository.findDtoByMemberCouponId(memberCouponId).orElseThrow(()
                -> new NotFoundException("해당 쿠폰을 찾지 못하였습니다.", memberCouponId));
    }


    // 회원 소유 쿠폰 사용 ( 우선 이메일과 사용하고자하는 쿠폰이름을 매개변수로 가져옴 )
    // 그러면 우선적으로 회원이 있는지 확인부터 - > 아니지 회원자체는 발급쪽에서 검사를했으니
    // 회원이 발급받은 쿠폰이 있는지부터 확인.
    @Override
    public MemberCouponUseResponse useCoupon(Long memberId, Long memberCouponId) {
        // 1차 책임 -> memberId와 memberCouponId로 회원이 소유한 쿠폰을 조회
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndId(memberId, memberCouponId)
                .orElseThrow(() -> new MemberCouponNotFoundException("회원이 소유한 쿠폰이 존재하지 않습니다."));

        // 2차 책임 -> 조회한 MemberCoupon의 "사용 가능 여부"를 검증 (= 비즈니스 로직)
        validateCouponUsable(memberCoupon);

        memberCoupon.setUsed(true);
        memberCoupon.setUsedAt(LocalDate.now());

        return MemberCouponUseResponse.fromEntity(memberCoupon);
    }

    // 모든 도서에 사용 가능한 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDto> getAvailableCouponsAll(Long memberId) {
        return memberCouponRepository.findAvailableCouponsWelcome(memberId, CouponType.WELCOME);
    }

    // 특정 도서에 사용 가능한 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDto> getAvailableCouponsForBook(Long memberId, Long bookId, CouponType couponType) {
        return memberCouponRepository.findAvailableCouponsForBook(memberId, bookId, couponType);
    }

    // 특정 카테고리에 사용 가능한 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDto> getAvailableCouponsForCategory(Long memberId, Long categoryId, CouponType couponType) {
        return memberCouponRepository.findAvailableCouponsForCategory(memberId, categoryId, couponType);
    }

    // 쿠폰 발급에 대한 유효성 검증 메서드
    private void validateCoupon(Coupon coupon, Long memberId) {

        // 정책 활성화에 따른 발급 여부 검증
        if (coupon.getPolicy().getStatus().equals(CouponStatus.INACTIVE)) {
            throw new CouponPolicyInactiveException("해당 쿠폰은 관리자에 의해 임시적으로 발급이 불가능합니다.");
        }

        // 쿠폰의 수량 여부 검증
        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        // 쿠폰 중복발급 여부 검증
        if (memberCouponRepository.existsByMemberIdAndCouponId(memberId,coupon.getId())) {
            throw new MemberCouponExistsException("이미 쿠폰을 발급받으셨습니다. -> " + coupon.getCouponName());
        }

        // 유효기간에 따른 발급 여부 검증
        LocalDate now = LocalDate.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            throw new CouponExpiredException("쿠폰의 발급 가능 기간이 아닙니다.");
        }
    }

    // 쿠폰 사용에 대한 유효성 메서드
    private void validateCouponUsable(MemberCoupon memberCoupon) {

        if (memberCoupon.getCoupon().getPolicy().getStatus() == CouponStatus.INACTIVE) {
            throw new CouponPolicyInactiveException("관리자에 의해 정책이 비활성화이므로 사용이 불가능합니다.");
        }

        if (memberCoupon.isUsed()) {
            throw new MemberCouponIsUsed("이미 사용한 쿠폰입니다.");
        }

        if (memberCoupon.getExpiresAt().isBefore(LocalDate.now())) {
            throw new MemberCouponExpiredException("쿠폰의 유효기간이 만료되었습니다.");
        }
    }
}


