package itu.zazart.erpnext.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class Utils {


    public static Date parseDate(Object date) {
        if (date == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) date);
        } catch (ParseException e) {
            return null;
        }
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

    public static boolean toBoolean(Object obj) {
        return obj != null && Boolean.parseBoolean(obj.toString());
    }


}
