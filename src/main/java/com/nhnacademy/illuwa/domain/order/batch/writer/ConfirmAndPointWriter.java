package com.nhnacademy.illuwa.domain.order.batch.writer;

import com.nhnacademy.illuwa.domain.order.batch.domain.OrderRow;
import com.nhnacademy.illuwa.domain.order.dto.event.PointSavedEvent;
import com.nhnacademy.illuwa.domain.order.service.publisher.PointEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
public class ConfirmAndPointWriter implements ItemWriter<Long> {

    private final JdbcTemplate jdbc;
    private final PointEventPublisher pointEventPublisher;
    //    private final UserApiClient userApi;

    private static final String SELECT_SQL = """
            select order_id, member_id, total_price
            from orders
            where order_id in (%s)
            for update
            """;

    private static final String UPDATE_SQL = """
            update orders set order_status = 'Confirmed'
            where order_id = ?
            """;

    @Override
    public void write(Chunk<? extends Long> orderIds) throws Exception {
        if (orderIds.isEmpty()) {
            return;
        }

        // 주문 상세 조회
        String inClause = String.join(",", Collections.nCopies(orderIds.size(), "?"));
        List<OrderRow> rows = jdbc.query(
                SELECT_SQL.formatted(inClause),
                orderIds.getItems().toArray(),
                (rs, i) -> new OrderRow(
                        rs.getLong("order_id"),
                        rs.getLong("member_id"),
                        rs.getBigDecimal("total_price")
                )
        );

        // 멤버별 금액 합산
        Map<Long, BigDecimal> memberTotals = new HashMap<>();
        rows.forEach(r -> memberTotals.merge(r.memberId(), r.totalPrice(), BigDecimal::add));

        // 주문 상태 업데이트
        jdbc.batchUpdate(
                UPDATE_SQL,
                orderIds.getItems(),
                orderIds.size(),
                (ps, orderId) -> ps.setLong(1, orderId)
        );

        memberTotals.forEach((memberId, total) -> {
//            userApi.sendTotalPrice(new TotalRequest(memberId, total));
            pointEventPublisher.sendPointSavedEvent(new PointSavedEvent(memberId, total));
        });
    }

}
