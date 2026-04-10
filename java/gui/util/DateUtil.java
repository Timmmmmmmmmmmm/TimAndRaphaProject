package gui.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDate parseLocalDate(String s) {
        for (String f : new String[]{"yyyy-MM-dd","dd.MM.yyyy","yyyy/MM/dd"})
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern(f));
            } catch (Exception ignored) {}
        return null;
    }

    public static LocalDateTime parseLocalDateTime(String s) {
        for (String f : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss"})
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f));
            } catch (Exception ignored) {}
        return null;
    }
}
