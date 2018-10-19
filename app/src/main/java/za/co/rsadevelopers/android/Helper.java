package za.co.rsadevelopers.android;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Helper {
    public static BigDecimal cleanCurrency(String amount){
        String cleanString = amount.replaceAll("[R,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        return parsed;
    }

    public static String createCurrency(BigDecimal amount){
        Locale locale = new Locale("en","ZA");
        String formatted = NumberFormat.getCurrencyInstance(locale).format(amount);
        return formatted;
    }

    public static String getPaddedStringAmount(BigDecimal amount){
        return String.format("%013d", amount.movePointRight(2).intValueExact());
    }
}
