package fr.openmc.core.utils;

public class MathUtils {

    /**
     * Linearly interpolates between two colors.
     *
     * @param startColor The starting color in RGB format (0xRRGGBB).
     * @param endColor   The ending color in RGB format (0xRRGGBB).
     * @param t          The interpolation factor (0.0 to 1.0).
     * @return The interpolated color in RGB format (0xRRGGBB).
     */
    public static int lerpColor(int startColor, int endColor, double t) {
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (r << 16) | (g << 8) | b;
    }

}
