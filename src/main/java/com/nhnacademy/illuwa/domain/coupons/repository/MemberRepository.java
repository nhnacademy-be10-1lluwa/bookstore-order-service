package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmail(String email);

    Optional<Member> findMemberById(Long id);
}
