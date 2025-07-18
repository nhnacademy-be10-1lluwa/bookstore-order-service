package com.nhnacademy.illuwa.domain.order.batch.writer;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class MemberGradeWriter implements ItemWriter<MemberGradeUpdateRequest> {

    private final UserApiClient userApiClient;

    @Override
    public void write(Chunk<? extends MemberGradeUpdateRequest> chunk) {
        if (!chunk.isEmpty()) {
            userApiClient.sendNetOrderAmount(new ArrayList<>(chunk.getItems()));
        }
    }
}
