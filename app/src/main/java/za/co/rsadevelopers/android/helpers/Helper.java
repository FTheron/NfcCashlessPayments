package za.co.rsadevelopers.android.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Helper {
    public static BigDecimal cleanCurrency(String amount){
        String cleanString = amount.replaceAll("[R,.]", "");
        return new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
    }

    public static String createCurrency(BigDecimal amount){
        Locale locale = new Locale("en","ZA");
        return NumberFormat.getCurrencyInstance(locale).format(amount);
    }

    public static String getPaddedStringAmount(BigDecimal amount){
        return String.format("%013d", amount.movePointRight(2).intValueExact());
    }

    public static Integer ToCents(BigDecimal amount){
        return amount.movePointRight(2).intValueExact();
    }
}
