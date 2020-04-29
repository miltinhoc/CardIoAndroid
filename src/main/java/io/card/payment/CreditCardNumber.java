//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;

class CreditCardNumber {
    public static boolean passesLuhnChecksum(String number) {
        int even = 0;
        int sum = 0;
        int[][] sums = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, {0, 2, 4, 6, 8, 1, 3, 5, 7, 9}};
        CharacterIterator iter = new StringCharacterIterator(number);

        for(char c = iter.last(); c != '\uffff'; c = iter.previous()) {
            if (!Character.isDigit(c)) {
                return false;
            }

            int cInt = c - 48;
            sum += sums[even++ & 1][cInt];
        }

        return sum % 10 == 0;
    }

    public static String formatString(String numStr) {
        return formatString(numStr, true, (CardType)null);
    }

    public static String formatString(String numStr, boolean filterDigits, CardType type) {
        String digits;
        if (filterDigits) {
            digits = StringHelper.getDigitsOnlyString(numStr);
        } else {
            digits = numStr;
        }

        if (type == null) {
            type = CardType.fromCardNumber(digits);
        }

        int numLen = type.numberLength();
        if (digits.length() == numLen) {
            if (numLen == 16) {
                return formatSixteenString(digits);
            }

            if (numLen == 15) {
                return formatFifteenString(digits);
            }
        }

        return numStr;
    }

    private static String formatFifteenString(String digits) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 15; ++i) {
            if (i == 4 || i == 10) {
                sb.append(' ');
            }

            sb.append(digits.charAt(i));
        }

        return sb.toString();
    }

    private static String formatSixteenString(String digits) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 16; ++i) {
            if (i != 0 && i % 4 == 0) {
                sb.append(' ');
            }

            sb.append(digits.charAt(i));
        }

        return sb.toString();
    }

    public static boolean isDateValid(int expiryMonth, int expiryYear) {
        if (expiryMonth >= 1 && 12 >= expiryMonth) {
            Calendar now = Calendar.getInstance();
            int thisYear = now.get(1);
            int thisMonth = now.get(2) + 1;
            if (expiryYear < thisYear) {
                return false;
            } else if (expiryYear == thisYear && expiryMonth < thisMonth) {
                return false;
            } else {
                return expiryYear <= thisYear + 15;
            }
        } else {
            return false;
        }
    }

    public static SimpleDateFormat getDateFormatForLength(int len) {
        if (len == 4) {
            return new SimpleDateFormat("MMyy");
        } else {
            return len == 6 ? new SimpleDateFormat("MMyyyy") : null;
        }
    }

    public static Date getDateForString(String dateString) {
        String digitsOnly = StringHelper.getDigitsOnlyString(dateString);
        SimpleDateFormat validDate = getDateFormatForLength(digitsOnly.length());
        if (validDate != null) {
            try {
                validDate.setLenient(false);
                Date date = validDate.parse(digitsOnly);
                return date;
            } catch (ParseException var4) {
                return null;
            }
        } else {
            return null;
        }
    }
}