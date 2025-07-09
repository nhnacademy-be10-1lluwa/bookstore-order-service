package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.repository.custom.PackagingQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface PackagingRepository extends JpaRepository<Packaging, Long>, PackagingQuerydslRepository {

    // findAll 전체 포장 옵션 조회

    // 활성화 된 포장 옵션 조회
    List<Packaging> findByActive(boolean active);

    // 해당 포장 옵션 조회
    Optional<Packaging> findByPackagingId(Long packagingId);



    /** 포장 옵션 삭제 (active 컬럼값 false 로 변경)
    * @param packagingId 변경할 포장 옵션 ID
    * @param active 활성화 여부
    * @return 수정정 행 수
    **/
    @Modifying
    @Transactional
    @Query("update Packaging p set p.active = :active where p.packagingId = :packagingId")
    int updateActiveByPackagingId(@Param("packagingId") Long packagingId, @Param("active") Boolean active);

}
