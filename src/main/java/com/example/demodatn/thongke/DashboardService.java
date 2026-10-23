package com.example.demodatn.thongke;
import com.example.demodatn.repository.thongke.DashboardRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository repository;
    @Value("${shop.low-stock-threshold:5}") private int threshold;
    @Value("${shop.time-zone:Asia/Ho_Chi_Minh}") private String timeZone;

    @Transactional(readOnly = true)
    public Map<String, Object> load(int days) {
        LocalDate today = LocalDate.now(ZoneId.of(timeZone));
        LocalDate from = today.minusDays(days - 1), until = today.plusDays(1);
        Map<String, Object> data = new LinkedHashMap<>(repository.counts());
        data.put("todayRevenue", repository.revenue(today, until));
        data.put("monthRevenue", repository.revenue(today.withDayOfMonth(1), until));
        Map<LocalDate, Object> amounts = new HashMap<>();
        repository.daily(from, until).forEach(row -> amounts.put(LocalDate.parse(row.get("day").toString()), row.get("amount")));
        List<String> labels = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (LocalDate day = from; day.isBefore(until); day = day.plusDays(1)) {
            labels.add(day.toString());
            values.add(amounts.getOrDefault(day, 0));
        }
        data.put("labels", labels);
        data.put("values", values);
        data.put("statuses", repository.statuses());
        data.put("statusLabels",com.example.demodatn.banhang.OrderStatusService.LABELS);
        data.put("bestSellers", repository.bestSellers(from, until));
        data.put("recent", repository.recent());
        data.put("lowStock", repository.lowStock(Math.max(0, threshold)));
        data.put("threshold", Math.max(0, threshold));
        data.put("days", days);
        data.put("today", today.toString());
        return data;
    }
}
