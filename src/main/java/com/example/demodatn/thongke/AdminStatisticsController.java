package com.example.demodatn.thongke;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final StatisticsService statistics;
    @Value("${shop.time-zone:Asia/Ho_Chi_Minh}") private String timeZone;

    @GetMapping({"/admin/statistics","/admin/statistics/revenue","/admin/statistics/best-sellers",
            "/admin/reports/revenue","/admin/reports/best-sellers"})
    public String report(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,
            @RequestParam(defaultValue="") String period,@RequestParam(defaultValue="ALL") String channel,
            HttpServletRequest request,Model model) {
        LocalDate end=to==null ? LocalDate.now(ZoneId.of(timeZone)) : to;
        LocalDate start=from!=null ? from : switch(period) {
            case "today" -> end;
            case "month" -> end.withDayOfMonth(1);
            default -> end.minusDays(29);
        };
        model.addAttribute("from",start);model.addAttribute("to",end);model.addAttribute("channel",channel);
        boolean products=request.getRequestURI().endsWith("best-sellers");
        model.addAttribute("productsTab",products);model.addAttribute("adminActive",products ? "best-sellers" : "revenue");
        model.addAttribute("adminTitle","Thống kê");model.addAttribute("timeZone",timeZone);
        String error=end.isBefore(start) ? "Ngày kết thúc phải từ ngày bắt đầu." :
                ChronoUnit.DAYS.between(start,end)>3660 ? "Vui lòng chọn khoảng ngày không quá 10 năm." :
                !Set.of("ALL","ONLINE","POS").contains(channel) ? "Kênh bán không hợp lệ." : null;
        model.addAttribute("filterError",error);
        if(error==null) model.addAttribute("stats",statistics.load(start,end,channel));
        return "admin/statistics";
    }
}
