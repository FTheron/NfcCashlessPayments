package za.co.rsadevelopers.android;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

class Helper {
     static BigDecimal cleanCurrency(String amount){
        String cleanString = amount.replaceAll("[R,.]", "");
        return new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
    }

    static String createCurrency(BigDecimal amount){
        Locale locale = new Locale("en","ZA");
        return NumberFormat.getCurrencyInstance(locale).format(amount);
    }

    static String getPaddedStringAmount(BigDecimal amount){
        return String.format("%013d", amount.movePointRight(2).intValueExact());
    }

    static Integer ToCents(BigDecimal amount){
        return amount.movePointRight(2).intValueExact();
    }
}
