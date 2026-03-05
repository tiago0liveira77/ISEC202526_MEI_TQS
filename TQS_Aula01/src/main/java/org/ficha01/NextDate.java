package org.ficha01;

import java.time.LocalDate;

public class NextDate {

    public static LocalDate nextDate(int month, int day, int year) {
        if (year < 1900 || year > 2025) return null;
        if (month < 1 || month > 12) return null;
        if (day < 1) return null;

        int lastDay = lastDayOfMonth(month, year);
        if (day > lastDay) return null;

        if (day < lastDay) {
            return LocalDate.of(year, month, day + 1);
        } else if (month < 12) {
            return LocalDate.of(year, month + 1, 1);
        } else {
            // Dec 31 — next year would be out of range
            if (year == 2025) return null;
            return LocalDate.of(year + 1, 1, 1);
        }
    }

    private static int lastDayOfMonth(int month, int year) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeapYear(year) ? 29 : 28;
            default -> -1;
        };
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
    }
}
