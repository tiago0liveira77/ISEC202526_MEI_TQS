package org.ficha01;

import org.junit.jupiter.api.Test;

import static org.ficha01.Triangle.TriangleType.*;
import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {

    // --- ECT: Valid output classes ---

    @Test
    void equilateral() {
        // C1: all sides equal
        assertEquals(EQUILATERAL, Triangle.idTriangle(3, 3, 3));
    }

    @Test
    void isosceles_firstTwoEqual() {
        // C2: a == b
        assertEquals(ISOSCELES, Triangle.idTriangle(3, 3, 4));
    }

    @Test
    void isosceles_lastTwoEqual() {
        // C2: b == c
        assertEquals(ISOSCELES, Triangle.idTriangle(3, 4, 4));
    }

    @Test
    void isosceles_firstAndThirdEqual() {
        // C2: a == c
        assertEquals(ISOSCELES, Triangle.idTriangle(4, 3, 4));
    }

    @Test
    void scalene() {
        // C3: all sides different, triangle inequality holds
        assertEquals(SCALENE, Triangle.idTriangle(3, 4, 5));
    }

    @Test
    void notTriangle_sumLessThan() {
        // C4: a+b < c
        assertEquals(NOTTRIANGLE, Triangle.idTriangle(1, 2, 4));
    }

    // --- ECT: Invalid input class (robustness) ---

    @Test
    void invalidSide_zero() {
        // C5: side = 0
        assertThrows(IllegalArgumentException.class, () -> Triangle.idTriangle(0, 1, 1));
    }

    @Test
    void invalidSide_negative() {
        // C5: side < 0
        assertThrows(IllegalArgumentException.class, () -> Triangle.idTriangle(-1, 1, 1));
    }

    // --- BVA: side boundary (0 / 1) ---

    @Test
    void bva_minValidSide() {
        // side = 1: minimum valid value
        assertEquals(EQUILATERAL, Triangle.idTriangle(1, 1, 1));
    }

    @Test
    void bva_sideZero_boundary() {
        // side = 0: just below minimum valid value
        assertThrows(IllegalArgumentException.class, () -> Triangle.idTriangle(0, 4, 4));
    }

    // --- BVA: triangle inequality boundary ---

    @Test
    void bva_notTriangle_sumExactlyEqual() {
        // a+b = c (= 7): on the boundary, NOT a valid triangle
        assertEquals(NOTTRIANGLE, Triangle.idTriangle(3, 4, 7));
    }

    @Test
    void bva_scalene_sumJustAbove() {
        // a+b = c+1 (7 > 6): one step inside valid territory
        assertEquals(SCALENE, Triangle.idTriangle(3, 4, 6));
    }
}
