package org.ficha01;

public class Triangle {

    public enum TriangleType {
        EQUILATERAL, ISOSCELES, SCALENE, NOTTRIANGLE
    }

    public static TriangleType idTriangle(int a, int b, int c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("All sides must be positive integers");
        }

        if (a + b <= c || a + c <= b || b + c <= a) {
            return TriangleType.NOTTRIANGLE;
        }

        if (a == b && b == c) {
            return TriangleType.EQUILATERAL;
        }

        if (a == b || b == c || a == c) {
            return TriangleType.ISOSCELES;
        }

        return TriangleType.SCALENE;
    }
}
