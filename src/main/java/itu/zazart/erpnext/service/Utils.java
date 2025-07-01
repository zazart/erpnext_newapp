package itu.zazart.erpnext.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);


    public static Date parseDate(Object date) {
        if (date == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static LocalDate toDate(Object date) {
        if (date == null) return null;

        String value = date.toString().trim();
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            logger.error("Failed to parse date '{}'. Expected format: yyyy-MM-dd", value);
            return null;
        }
    }


    public static LocalTime toTime(Object time) {
        if (time == null) return null;

        String value = time.toString().trim();

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                logger.warn("Unable to parse time: '{}' with format: {}", value, formatter);
            }
        }

        logger.error("No format allowed to parse the time: '{}'", value);
        return null;
    }


    public static LocalDateTime toDateTime(Object datetime) {
        if (datetime == null) return null;

        String value = datetime.toString().trim();

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                logger.warn("Failed to parse date: {}", value);
                return null;
            }
        }
        return null;
    }

    public static int toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return 0;
    }

    public static LocalTime parseTime(Object obj) {
        if (obj == null) return null;
        try {
            return LocalTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static boolean toBoolean(Object obj) {
        return obj != null && Boolean.parseBoolean(obj.toString());
    }


    public static String snakeCaseToWords(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        String[] parts = snakeCase.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            result.append(part.substring(0, 1).toUpperCase());
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
            result.append(" ");
        }

        return result.toString().trim();
    }


    public static String WordsToSnakeCase(String words) {
        if (words == null || words.isEmpty()) {
            return words;
        }
        String[] parts = words.split(" ");
        StringBuilder result = new StringBuilder();
        int test = 0;
        for (String part : parts) {
            test ++;
            if (part.isEmpty()) continue;
            result.append(part.substring(0, 1).toUpperCase());
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }

            if (test == parts.length) {
                break;
            }
            result.append("_");
        }

        return result.toString().trim().toLowerCase();
    }

    public static LocalDate getLastDateOfMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atEndOfMonth();
    }

    public static String formatNumberWithSeparators(Double number) {
        if (number == null) {
            return "0.00";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');  // séparateur de milliers
        symbols.setDecimalSeparator('.');   // séparateur décimal

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(number);
    }
}
