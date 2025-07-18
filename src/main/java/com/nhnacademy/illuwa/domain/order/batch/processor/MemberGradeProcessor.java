package com.nhnacademy.illuwa.domain.order.batch.processor;

import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.batch.domain.MemberNetOrderAgg;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class MemberGradeProcessor implements ItemProcessor<MemberNetOrderAgg, MemberGradeUpdateRequest> {

    @Override
    public MemberGradeUpdateRequest process(MemberNetOrderAgg item) throws Exception {
        return new MemberGradeUpdateRequest(item.memberId(), item.totalPrice());
    }
}
