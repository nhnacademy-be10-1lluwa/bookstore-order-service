package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.repository.custom.ShippingPolicyQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long>, ShippingPolicyQuerydslRepository {

    // 전체 조회
    // findAll

    // 활성화 상태로 정책 조회
    List<ShippingPolicy> findByActive(boolean active);

    // id로 정책 조회하기
    Optional<ShippingPolicy> findByShippingPolicyId(long shippingPolicyId);

    /** 정책 옵션 삭제 (active 컬럼값 false 로 변경)
     * @param shippingPolicyId 변경할 정책 ID
     * @param active 활성화 여부
     * @return 수정된 행 수
    * */
    @Modifying
    @Transactional
    @Query("update ShippingPolicy s set s.active = :active where s.shippingPolicyId = :shippingPolicyId")
    int updateActiveByPackagingId(@Param("shippingPolicyId") long shippingPolicyId, @Param("active") Boolean active);
}
