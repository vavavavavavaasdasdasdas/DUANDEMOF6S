package com.example.demodatn.core;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Vietnamese presentation labels only; persisted codes remain unchanged. */
@Component("nhan")
@ControllerAdvice
public class NhanHienThi {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.forLanguageTag("vi"));

    public static String text(String key) {
        return MESSAGES.getString(key);
    }

    public static String label(Object code) {
        String key = "enum." + code;
        return code == null ? "—" : MESSAGES.containsKey(key) ? text(key) : code.toString();
    }

    public static String date(java.sql.Timestamp value) {
        return value.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern(text("date.pattern")));
    }

    public static String day(Object value) {
        return java.time.LocalDate.parse(value.toString()).format(java.time.format.DateTimeFormatter.ofPattern(text("date.only")));
    }

    @ModelAttribute("nhanHienThi")
    public Map<String, String> messages() {
        Map<String, String> result = new LinkedHashMap<>();
        MESSAGES.keySet().stream().sorted().forEach(key -> result.put(key, text(key)));
        return result;
    }
}
