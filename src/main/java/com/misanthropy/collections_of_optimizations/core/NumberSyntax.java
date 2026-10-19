package com.misanthropy.collections_of_optimizations.core;

public final class NumberSyntax {

    public static final CoOStacklessNumberFormatException NOT_A_NUMBER =
            new CoOStacklessNumberFormatException("Not a floating point number");

    private NumberSyntax() {
    }

    public static boolean cannotParse(String text) {
        if (text == null) {
            return false;
        }
        int length = text.length();
        int i = 0;
        while (i < length && text.charAt(i) <= ' ') {
            i++;
        }
        if (i == length) {
            return true;
        }
        char c = text.charAt(i);
        if (c == '+' || c == '-') {
            i++;
            if (i == length) {
                return true;
            }
            c = text.charAt(i);
        }
        return !(c >= '0' && c <= '9' || c == '.' || c == 'N' || c == 'I');
    }

    public static float parseFloat(String text) {
        if (cannotParse(text)) {
            throw NOT_A_NUMBER;
        }
        return Float.parseFloat(text);
    }

    public static double parseDouble(String text) {
        if (cannotParse(text)) {
            throw NOT_A_NUMBER;
        }
        return Double.parseDouble(text);
    }
}
