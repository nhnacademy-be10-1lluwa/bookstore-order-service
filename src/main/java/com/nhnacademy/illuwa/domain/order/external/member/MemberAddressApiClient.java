package com.nhnacademy.illuwa.domain.order.external.member;

import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberAddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "UserService",
        url = "${user-api.url}"
)
public interface MemberAddressApiClient {

    @GetMapping(value = "/members/{memberId}/addresses")
    List<MemberAddressDto> getAddressByMemberId(@PathVariable("memberId") Long memberId);
}
