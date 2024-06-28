package ch.thoenluk;

public class ColourConverter {
    public static final int R = 0;
    public static final int G = 1;
    public static final int B = 2;
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int L = 0;
    public static final int A = 1;
    private static final double XYZ_VAR_THRESHOLD = 0.04045;
    private static final double XYZ_VAR_THRESHOLD_SUMMAND = 0.055;
    private static final double XYZ_VAR_THRESHOLD_DIVISOR = 1.055;
    private static final double XYZ_VAR_THRESHOLD_EXPONENT = 2.4;
    private static final double XYZ_VAR_BELOW_THRESHOLD_DIVISOR = 12.92;
    private static final double[][] VAR_TO_XYZ_FACTORS = {
            {0.4124, 0.3576, 0.1805},
            {0.2126, 0.7152, 0.0722},
            {0.0193, 0.1192, 0.9505}
    };
    private static final double RGB_VAR_THRESHOLD = 0.0031308;
    private static final double[][] XYZ_TO_RGB_VAR_FACTORS = {
            {3.2406, -1.5372, -0.4986},
            {-0.9689, 1.8758, 0.0415},
            {0.0557, -0.2040, 1.0570}
    };
    private static final double X10 = 94.811;
    private static final double Y10 = 100;
    private static final double Z10 = 107.304;
    private static final double CIELAB_VAR_THRESHOLD = 0.008856;
    private static final double CIELAB_VAR_EXPONENT = 1D / 3;
    private static final double CIELAB_VAR_MULTIPLICAND = 7.787;
    private static final double CIELAB_VAR_SUMMAND = 16D / 116;
    private static final double CIELAB_L_MULTIPLICAND = 116;
    private static final double CIELAB_L_DEDUCTAND = 16;
    private static final double CIELAB_A_MULTIPLICAND = 500;
    private static final double CIELAB_B_MULTIPLICAND = 200;
    private static final double CIELAB_TO_XYZ_EXPONENT = 3;

    public static double[] rgbToCielab(final byte[] rgb) {
        return xyzToCielab(rgbToXyz(rgb));
    }

    public static byte[] cielabToRgb(final double[] cielab) {
        return xyzToRgb(cielabToXyz(cielab));
    }

    public static double[] rgbToXyz(final byte[] rgb) {
        final double[] xyz = new double[3];
        final double varR = rgbToXyzVar(rgb[R]);
        final double varG = rgbToXyzVar(rgb[G]);
        final double varB = rgbToXyzVar(rgb[B]);
        for (int i = X; i <= Z; i++) {
            xyz[i] = varR * VAR_TO_XYZ_FACTORS[i][R];
            xyz[i] += varG * VAR_TO_XYZ_FACTORS[i][G];
            xyz[i] += varB * VAR_TO_XYZ_FACTORS[i][B];
        }
        return xyz;
    }

    private static double rgbToXyzVar(final byte rgb) {
        double xyzVar = ((double) rgb) / 255;
        if (xyzVar > XYZ_VAR_THRESHOLD) {
            xyzVar += XYZ_VAR_THRESHOLD_SUMMAND;
            xyzVar /= XYZ_VAR_THRESHOLD_DIVISOR;
            xyzVar= Math.pow(xyzVar, XYZ_VAR_THRESHOLD_EXPONENT);
        }
        else {
            xyzVar /= XYZ_VAR_BELOW_THRESHOLD_DIVISOR;
        }
        return xyzVar * 100;
    }

    public static byte[] xyzToRgb(final double[] xyz) {
        final byte[] rgb = new byte[3];
        for (int i = R; i <= B; i++) {
            final double prevar = xyz[X] * XYZ_TO_RGB_VAR_FACTORS[i][X] + xyz[Y] * XYZ_TO_RGB_VAR_FACTORS[i][Y] + xyz[Z] * XYZ_TO_RGB_VAR_FACTORS[i][Z];
            rgb[i] = (byte) Math.round(xyzToRgbVar(prevar) * 255);
        }
        return rgb;
    }

    private static double xyzToRgbVar(final double xyz) {
        double rgbVar = xyz / 100;
        if (rgbVar > RGB_VAR_THRESHOLD) {
            rgbVar = Math.pow(rgbVar, 1 / XYZ_VAR_THRESHOLD_EXPONENT);
            rgbVar *= XYZ_VAR_THRESHOLD_DIVISOR;
            rgbVar -= XYZ_VAR_THRESHOLD_SUMMAND;
        }
        else {
            rgbVar *= XYZ_VAR_BELOW_THRESHOLD_DIVISOR;
        }
        return rgbVar;
    }

    public static double[] xyzToCielab(final double[] xyz) {
        final double[] cielab = new double[3];
        final double varX = xyzToCielabVar(xyz[X] / X10);
        final double varY = xyzToCielabVar(xyz[Y] / Y10);
        final double varZ = xyzToCielabVar(xyz[Z] / Z10);
        cielab[L] = CIELAB_L_MULTIPLICAND * varY - CIELAB_L_DEDUCTAND;
        cielab[A] = CIELAB_A_MULTIPLICAND * (varX - varY);
        cielab[B] = CIELAB_B_MULTIPLICAND * (varY - varZ);
        return cielab;
    }

    private static double xyzToCielabVar(final double xyz) {
        double cielabVar = xyz;
        if (cielabVar > CIELAB_VAR_THRESHOLD) {
            cielabVar = Math.pow(cielabVar, CIELAB_VAR_EXPONENT);
        }
        else {
            cielabVar *= CIELAB_VAR_MULTIPLICAND;
            cielabVar += CIELAB_VAR_SUMMAND;
        }
        return cielabVar;
    }

    public static double[] cielabToXyz(final double[] cielab) {
        final double[] xyz = new double[3];
        final double varY = (cielab[L] + CIELAB_L_DEDUCTAND) / CIELAB_L_MULTIPLICAND;
        final double varX = cielab[A] / CIELAB_A_MULTIPLICAND + varY;
        final double varZ = varY - cielab[B] / CIELAB_B_MULTIPLICAND;
        xyz[X] = cielabToXyzVar(varX) * X10;
        xyz[Y] = cielabToXyzVar(varY) * Y10;
        xyz[Z] = cielabToXyzVar(varZ) * Z10;
        return xyz;
    }

    private static double cielabToXyzVar(final double cielab) {
        final double exponentiated = Math.pow(cielab, CIELAB_TO_XYZ_EXPONENT);
        if (exponentiated > CIELAB_VAR_THRESHOLD) {
            return exponentiated;
        }
        else {
            double xyzVar = cielab - CIELAB_VAR_SUMMAND;
            xyzVar /= CIELAB_VAR_MULTIPLICAND;
            return xyzVar;
        }
    }
}
