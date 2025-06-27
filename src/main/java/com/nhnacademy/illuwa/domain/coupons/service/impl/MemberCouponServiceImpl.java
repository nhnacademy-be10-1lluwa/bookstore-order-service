package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponInfoResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponseTest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType.BIRTHDAY;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;

    // welcome 쿠폰 발급
    @Override
    public MemberCouponResponse issueWelcomeCoupon(String email) {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow
                (() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));

        boolean bool = memberCouponRepository.existsByMemberIdAndCoupon_CouponType(member.getId(), CouponType.WELCOME);
        if (bool) {
            throw new IllegalArgumentException("해당 회원은 이미 웰컴 쿠폰을 지급받으셨습니다.");
        }

        Coupon coupon = couponRepository.findByCouponType(CouponType.WELCOME).orElseThrow
                (() -> new IllegalArgumentException("WELCOME 쿠폰이 존재하지 않습니다."));

        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .issuedAt(LocalDate.now())
                .build();

        coupon.setIssueCount(coupon.getIssueCount().subtract(BigDecimal.ONE));

        MemberCoupon save = memberCouponRepository.save(memberCoupon);
        return MemberCouponResponse.fromEntity(save);
    }

    @Override
    public void issueBirthDayCoupon() {
        LocalDate today = LocalDate.now();
        int monthValue = today.getMonthValue();

        List<Member> birthDayMembers = memberRepository.findMemberByBirthMonth(monthValue);

        if (birthDayMembers.isEmpty()) {
            throw new IllegalArgumentException("당월 생일자에게만 발급되는 쿠폰입니다.");
        }

        Coupon birthDayCoupon = couponRepository.findCouponByCouponName("생일쿠폰")
                .orElseThrow(() -> new IllegalArgumentException("생일 쿠폰이 존재하지 않습니다."));

        if (birthDayCoupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        for (Member birthDayMember : birthDayMembers) {
            boolean bool = memberCouponRepository.existsByMemberIdAndCouponId(birthDayMember.getId(), birthDayCoupon.getId());

            if (!bool) {
                MemberCoupon updateMember = MemberCoupon.builder()
                        .member(birthDayMember)
                        .coupon(birthDayCoupon)
                        .issuedAt(today)
                        .build();

                birthDayCoupon.setIssueCount(birthDayCoupon.getIssueCount().subtract(BigDecimal.ONE));
                memberCouponRepository.save(updateMember);
            }
        }
    }

    // 쿠폰 발급
    @Override
    public MemberCouponResponse issueCoupon(MemberCouponCreateRequest request) {
        Member member = memberRepository.findMemberByEmail(request.getMemberEmail()).orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
        Coupon coupon = couponRepository.findCouponByCouponName(request.getCouponName()).orElseThrow(() -> new IllegalArgumentException("해당 쿠폰은 존재하지 않습니다."));

        // 고려사항 1 -> 쿠폰의 수량이 없을 시.
        if (coupon.getIssueCount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다.");
        }

        // Quantity finish
        // CouponPolicyInactiveException
        // isUsed
        // Expiration

        // 고려사항 2 -> 이미 발급받은 쿠폰일때
        if (memberCouponRepository.existsByMemberIdAndCouponId(member.getId(), coupon.getId())) {
            throw new MemberCouponInactiveException("이미 쿠폰을 발급받으셨습니다. -> " + coupon.getCouponName());
        }

        // 고려사항 3 -> 만약 정책쪽에서 (=비활성화) or 쿠폰쪽에서 (삭제)라면?
        // 정책쪽의 repo로 상태 비교후 생성? -> 이러면 회원 쿠폰이라는 엔티티가 정책쪽으로 직접 접근하게됌
        // 쿠폰쪽의 상태로 비교후 생성?
        // 그럼 쿠폰쪽의 상태를 무엇으로 관리할꺼냐 ? -> 정책(=비활성화) = 쿠폰(사용불가) || 정책(활성화) = 쿠폰(사용가능)

        // 고려사항 4 -> 생일 쿠폰
        if (coupon.getCouponType() == BIRTHDAY) {
            issueBirthDayCoupon();
            return null;
        }

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


    // 회원 소유 쿠폰 확인 (email)
    @Override
    public List<MemberCouponResponse> getAllMemberCoupons(String email) {
        return memberCouponRepository.findMemberCouponByMemberEmail(email)
                .stream()
                .map(MemberCouponResponse::fromEntity)
                .toList();
    }

    // 회원 소유 쿠폰 확인 (ID)
    @Override
    public List<MemberCouponResponse> getAllMemberCoupons(Long memberId) {
        return memberCouponRepository.findMemberCouponsByMemberId(memberId)
                .stream()
                .map(MemberCouponResponse::fromEntity)
                .toList();
    }

    @Override
    public CouponInfoResponse getCouponInfoFromMemberCoupon(Long memberCouponId) {
        MemberCoupon membercoupon = memberCouponRepository.findById(memberCouponId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));
        Coupon coupon = membercoupon.getCoupon();
        return CouponInfoResponse.fromEntity(coupon);
    }


    @Override
    public MemberCouponResponse getMemberCouponId(Long id) {
        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponById(id)
                .orElseThrow(() -> new MemberCouponNotFoundException("해당 쿠폰은 존재하지 않습니다."));
        return MemberCouponResponse.fromEntity(memberCoupon);
    }

    @Override
    public List<MemberCouponResponseTest> getAllMemberCouponsTest(Long id) {
        return memberCouponRepository.findMemberById(id)
                .stream()
                .map(MemberCouponResponseTest::fromEntity)
                .toList();
    }

    // 특정 도서에 적용 -> 주문 + ()

    // 회원 소유 쿠폰 사용 ( 우선 이메일과 사용하고자하는 쿠폰이름을 매개변수로 가져옴 )
    // 그러면 우선적으로 회원이 있는지 확인부터 - > 아니지 회원자체는 발급쪽에서 검사를했으니
    // 회원이 발급받은 쿠폰이 있는지부터 확인.
    @Override
    public MemberCouponUseResponse useCoupon(String email, Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findByMember_EmailAndId(email, memberCouponId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));
//        memberCouponRepository.findMemberCouponByMemberEmail(email);
//        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponByMemberEmail(id).orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        // 쿠폰의 사용 시 해당 쿠폰을 삭제하는게 좋지 않나?
        // -> 그러면 DB내의 이미 발급한 쿠폰에 대해서 중복발급이 가능할지도
        // -> 삭제를 안시키고 boolean값으로 사용여부를 체크하면 쿠폰 발급내역 or 쿠폰 사용내역에 좀 도움이 될 듯
        // 아니면 MemberCoupon(회원쿠폰)쪽에 필드로 상태(enum)를 추가해서
        // status("사용가능","사용완료","사용불가","기간만료")도 좋을것 같긴한데 == 나중에 사용가능한 쿠폰만 볼수있도록 처리도 가능


        // 여러 상품 ->
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
