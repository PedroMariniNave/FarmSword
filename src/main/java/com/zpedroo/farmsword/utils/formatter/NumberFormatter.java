package com.zpedroo.farmsword.utils.formatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    public static String formatDecimal(double number) {
        DecimalFormat formatter = new DecimalFormat("##.##");
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }

    public static String formatThousand(double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }
}