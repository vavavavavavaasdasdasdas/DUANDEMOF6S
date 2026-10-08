package com.example.demodatn.thongke;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final JdbcTemplate jdbc;
    private static final String FILTER = " o.created_at>=? AND o.created_at<? AND (?='ALL' OR o.order_type=?)";
    private static final String ELIGIBLE = " o.payment_status='PAID' AND o.status<>'CANCELLED' AND" + FILTER;

    @Transactional(readOnly = true)
    public Map<String,Object> load(LocalDate from, LocalDate to, String channel) {
        Object[] args = {from, to.plusDays(1), channel, channel};
        Map<String,Object> result = new LinkedHashMap<>(jdbc.queryForMap(
                "SELECT COALESCE(SUM(o.final_amount),0) AS revenue,COUNT_BIG(*) AS orders FROM orders o WHERE" + ELIGIBLE, args));
        BigDecimal revenue = (BigDecimal) result.get("revenue");
        long count = ((Number) result.get("orders")).longValue();
        result.put("average",count==0 ? BigDecimal.ZERO : revenue.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP));
        var rows = jdbc.queryForList("SELECT CAST(o.created_at AS date) AS day,SUM(o.final_amount) AS amount,COUNT_BIG(*) AS orders FROM orders o WHERE" + ELIGIBLE + " GROUP BY CAST(o.created_at AS date) ORDER BY day",args);
        Map<LocalDate,Map<String,Object>> days = new HashMap<>();
        rows.forEach(row -> {
            LocalDate day = LocalDate.parse(row.get("day").toString());
            row.put("day",day.toString());
            days.put(day,row);
        });
        List<Map<String,Object>> daily = new ArrayList<>();
        for(LocalDate day=from;!day.isAfter(to);day=day.plusDays(1))
            daily.add(days.getOrDefault(day,Map.of("day",day.toString(),"amount",BigDecimal.ZERO,"orders",0)));
        result.put("daily",daily);
        result.put("statuses",jdbc.queryForList("SELECT o.status,COUNT_BIG(*) AS total FROM orders o WHERE" + FILTER + " GROUP BY o.status ORDER BY o.status",args));
        String products = "SELECT TOP (10) p.id,p.name,p.image_url,SUM(CAST(d.quantity AS bigint)) AS quantity,SUM(d.total_price) AS amount FROM order_details d JOIN orders o ON o.id=d.order_id JOIN product_variants v ON v.id=d.product_variant_id JOIN products p ON p.id=v.product_id WHERE" + ELIGIBLE + " GROUP BY p.id,p.name,p.image_url ORDER BY ";
        result.put("topQuantity",jdbc.queryForList(products+"quantity DESC,amount DESC,p.id",args));
        result.put("topRevenue",jdbc.queryForList(products+"amount DESC,quantity DESC,p.id",args));
        return result;
    }
}
