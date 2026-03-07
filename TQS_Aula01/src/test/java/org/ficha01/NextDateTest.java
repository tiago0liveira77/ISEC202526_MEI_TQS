package org.ficha01;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NextDateTest {

    // --- ECT: 31-day months (M1) ---

    @Test
    void m1_midMonth_incrementsDay() {
        // M1, D1, Y1 — typical day in a 31-day month
        assertEquals(LocalDate.of(2023, 1, 16), NextDate.nextDate(1, 15, 2023));
    }

    @Test
    void m1_lastDay_rollsOverToNextMonth() {
        // M1, D5, Y1 — Jan 31 → Feb 1
        assertEquals(LocalDate.of(2023, 2, 1), NextDate.nextDate(1, 31, 2023));
    }

    // --- ECT: 30-day months (M2) ---

    @Test
    void m2_lastDay_rollsOverToNextMonth() {
        // M2, D4, Y1 — Apr 30 → May 1
        assertEquals(LocalDate.of(2023, 5, 1), NextDate.nextDate(4, 30, 2023));
    }

    @Test
    void m2_day31_isInvalid() {
        // D6 — day 31 in a 30-day month
        assertNull(NextDate.nextDate(4, 31, 2023));
    }

    // --- ECT: February, non-leap year (M3 + Y1) ---

    @Test
    void feb_midMonth_nonLeap() {
        // M3, D1, Y1
        assertEquals(LocalDate.of(2023, 2, 16), NextDate.nextDate(2, 15, 2023));
    }

    @Test
    void feb28_nonLeap_rollsOverToMarch() {
        // M3, D2, Y1 — Feb 28 non-leap → Mar 1
        assertEquals(LocalDate.of(2023, 3, 1), NextDate.nextDate(2, 28, 2023));
    }

    @Test
    void feb29_nonLeap_isInvalid() {
        // M3, D3, Y1 — Feb 29 doesn't exist in non-leap year
        assertNull(NextDate.nextDate(2, 29, 2023));
    }

    // --- ECT: February, leap year (M3 + Y2) ---

    @Test
    void feb28_leapYear_returnsFeb29() {
        // M3, D2, Y2 — Feb 28 leap → Feb 29
        assertEquals(LocalDate.of(2024, 2, 29), NextDate.nextDate(2, 28, 2024));
    }

    @Test
    void feb29_leapYear_rollsOverToMarch() {
        // M3, D3, Y2 — Feb 29 leap → Mar 1
        assertEquals(LocalDate.of(2024, 3, 1), NextDate.nextDate(2, 29, 2024));
    }

    // --- ECT: Year rollover ---

    @Test
    void dec31_rollsOverToNextYear() {
        // Dec 31 → Jan 1 next year
        assertEquals(LocalDate.of(2025, 1, 1), NextDate.nextDate(12, 31, 2024));
    }

    @Test
    void dec31_lastValidYear_isInvalid() {
        // Dec 31 2025 → Jan 1 2026, which is out of range
        assertNull(NextDate.nextDate(12, 31, 2025));
    }

    // --- ECT: Invalid inputs (robustness) ---

    @Test
    void invalidMonth_tooLow() {
        assertNull(NextDate.nextDate(0, 15, 2023));
    }

    @Test
    void invalidMonth_tooHigh() {
        assertNull(NextDate.nextDate(13, 15, 2023));
    }

    @Test
    void invalidDay_tooLow() {
        assertNull(NextDate.nextDate(1, 0, 2023));
    }

    @Test
    void invalidDay_tooHigh() {
        assertNull(NextDate.nextDate(1, 32, 2023));
    }

    @Test
    void invalidYear_tooLow() {
        assertNull(NextDate.nextDate(1, 15, 1899));
    }

    @Test
    void invalidYear_tooHigh() {
        assertNull(NextDate.nextDate(1, 15, 2026));
    }

    // --- BVA: year boundaries ---

    @Test
    void bva_year1900_isValid() {
        assertEquals(LocalDate.of(1900, 1, 16), NextDate.nextDate(1, 15, 1900));
    }

    @Test
    void bva_year1899_isInvalid() {
        assertNull(NextDate.nextDate(1, 15, 1899));
    }

    @Test
    void bva_year2025_isValid() {
        assertEquals(LocalDate.of(2025, 1, 16), NextDate.nextDate(1, 15, 2025));
    }

    @Test
    void bva_year2026_isInvalid() {
        assertNull(NextDate.nextDate(1, 15, 2026));
    }

    // --- BVA: month boundaries ---

    @Test
    void bva_month1_isValid() {
        assertEquals(LocalDate.of(2023, 1, 2), NextDate.nextDate(1, 1, 2023));
    }

    @Test
    void bva_month12_isValid() {
        assertEquals(LocalDate.of(2023, 12, 16), NextDate.nextDate(12, 15, 2023));
    }

    // --- BVA: day boundaries ---

    @Test
    void bva_day1_isValid() {
        assertEquals(LocalDate.of(2023, 1, 2), NextDate.nextDate(1, 1, 2023));
    }

    @Test
    void bva_lastDayOfMonth_isValid() {
        // Jan 31 → Feb 1
        assertEquals(LocalDate.of(2023, 2, 1), NextDate.nextDate(1, 31, 2023));
    }

    @Test
    void bva_dayAfterLastDay_isInvalid() {
        // Jan 32 — invalid
        assertNull(NextDate.nextDate(1, 32, 2023));
    }

    // --- BVA: 1900 is NOT a leap year (century rule) ---

    @Test
    void bva_1900_notLeapYear_feb28RollsToMarch() {
        assertEquals(LocalDate.of(1900, 3, 1), NextDate.nextDate(2, 28, 1900));
    }

    @Test
    void bva_1900_notLeapYear_feb29Invalid() {
        assertNull(NextDate.nextDate(2, 29, 1900));
    }

    // =========================================================
    // Lab 3 — Decision Table approach
    //
    // Conditions:
    //   C1: date is valid (year∈[1900,2025], month∈[1,12], day∈[1,lastDay])
    //   C2: day == last day of month
    //   C3: month == 12
    //   C4: year == 2025
    //
    // Actions:
    //   A1: return null        (R1: invalid input; R5: Dec 31 of max year)
    //   A2: day++              (R2: valid, not last day)
    //   A3: day=1, month++     (R3: valid, last day, not December)
    //   A4: day=1, month=1, year++ (R4: valid, Dec 31, year < 2025)
    //
    // Rules:        R1  R2  R3  R4  R5
    //   C1 valid     F   T   T   T   T
    //   C2 last day  -   F   T   T   T
    //   C3 month=12  -   -   F   T   T
    //   C4 year=2025 -   -   -   F   T
    //   Action      A1  A2  A3  A4  A1
    // =========================================================

    // R1: invalid date → null (C1 = false)
    @Test
    void dt_r1_invalidDate_returnsNull() {
        assertNull(NextDate.nextDate(0, 15, 2023));  // invalid month
    }

    // R2: valid date, not last day of month → day++ (C1=T, C2=F)
    @Test
    void dt_r2_notLastDay_incrementsDay() {
        assertEquals(LocalDate.of(2023, 3, 16), NextDate.nextDate(3, 15, 2023));
    }

    // R3: valid date, last day of month, not December → day=1, month++ (C1=T, C2=T, C3=F)
    @Test
    void dt_r3_lastDayNotDecember_incrementsMonth() {
        assertEquals(LocalDate.of(2023, 4, 1), NextDate.nextDate(3, 31, 2023));
    }

    // R3 variant: February last day (leap year logic absorbed into C2)
    @Test
    void dt_r3_feb28NonLeap_rollsToMarch() {
        assertEquals(LocalDate.of(2023, 3, 1), NextDate.nextDate(2, 28, 2023));
    }

    @Test
    void dt_r2_feb28LeapYear_incrementsDay() {
        // Feb 28 in leap year is NOT the last day → R2
        assertEquals(LocalDate.of(2024, 2, 29), NextDate.nextDate(2, 28, 2024));
    }

    @Test
    void dt_r3_feb29LeapYear_rollsToMarch() {
        // Feb 29 in leap year IS the last day, month != 12 → R3
        assertEquals(LocalDate.of(2024, 3, 1), NextDate.nextDate(2, 29, 2024));
    }

    // R4: Dec 31, year < 2025 → day=1, month=1, year++ (C1=T, C2=T, C3=T, C4=F)
    @Test
    void dt_r4_dec31NotMaxYear_incrementsYear() {
        assertEquals(LocalDate.of(2025, 1, 1), NextDate.nextDate(12, 31, 2024));
    }

    // R5: Dec 31, year == 2025 → null (C1=T, C2=T, C3=T, C4=T)
    @Test
    void dt_r5_dec31MaxYear_returnsNull() {
        assertNull(NextDate.nextDate(12, 31, 2025));
    }
}
