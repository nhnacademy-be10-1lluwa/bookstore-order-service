package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m WHERE MONTH(m.birth) = :month")
    List<Member> findMemberByBirthDay(short birthDay);

    Optional<Member> findMemberById(Long id);
}
