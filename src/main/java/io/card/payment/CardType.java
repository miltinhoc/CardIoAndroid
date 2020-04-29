//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Pair;
import io.card.payment.R.drawable;
import io.card.payment.i18n.LocalizedStrings;
import io.card.payment.i18n.StringKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public enum CardType {
    AMEX("AmEx"),
    DINERSCLUB("DinersClub"),
    DISCOVER("Discover"),
    JCB("JCB"),
    MASTERCARD("MasterCard"),
    VISA("Visa"),
    MAESTRO("Maestro"),
    UNKNOWN("Unknown"),
    INSUFFICIENT_DIGITS("More digits required");

    public final String name;
    private static int minDigits = 1;
    private static HashMap<Pair<String, String>, CardType> intervalLookup = new HashMap();

    private CardType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getDisplayName(String languageOrLocale) {
        switch(this) {
            case AMEX:
                return LocalizedStrings.getString(StringKey.CARDTYPE_AMERICANEXPRESS, languageOrLocale);
            case DINERSCLUB:
            case DISCOVER:
                return LocalizedStrings.getString(StringKey.CARDTYPE_DISCOVER, languageOrLocale);
            case JCB:
                return LocalizedStrings.getString(StringKey.CARDTYPE_JCB, languageOrLocale);
            case MASTERCARD:
                return LocalizedStrings.getString(StringKey.CARDTYPE_MASTERCARD, languageOrLocale);
            case MAESTRO:
                return LocalizedStrings.getString(StringKey.CARDTYPE_MAESTRO, languageOrLocale);
            case VISA:
                return LocalizedStrings.getString(StringKey.CARDTYPE_VISA, languageOrLocale);
            default:
                return null;
        }
    }

    public int numberLength() {
        int result;
        switch(this) {
            case AMEX:
                result = 15;
                break;
            case DINERSCLUB:
                result = 14;
                break;
            case DISCOVER:
            case JCB:
            case MASTERCARD:
            case MAESTRO:
            case VISA:
                result = 16;
                break;
            case INSUFFICIENT_DIGITS:
                result = minDigits;
                break;
            case UNKNOWN:
            default:
                result = -1;
        }

        return result;
    }

    public int cvvLength() {
        byte result;
        switch(this) {
            case AMEX:
                result = 4;
                break;
            case DINERSCLUB:
            case DISCOVER:
            case JCB:
            case MASTERCARD:
            case MAESTRO:
            case VISA:
                result = 3;
                break;
            case INSUFFICIENT_DIGITS:
            case UNKNOWN:
            default:
                result = -1;
        }

        return result;
    }

    public Bitmap imageBitmap(Context context) {
        int cardImageResource = -1;
        switch(this) {
            case AMEX:
                cardImageResource = drawable.cio_ic_amex;
                break;
            case DINERSCLUB:
            case DISCOVER:
                cardImageResource = drawable.cio_ic_discover;
                break;
            case JCB:
                cardImageResource = drawable.cio_ic_jcb;
                break;
            case MASTERCARD:
                cardImageResource = drawable.cio_ic_mastercard;
            case MAESTRO:
            default:
                break;
            case VISA:
                cardImageResource = drawable.cio_ic_visa;
        }

        return cardImageResource != -1 ? BitmapFactory.decodeResource(context.getResources(), cardImageResource) : null;
    }

    private static boolean isNumberInInterval(String number, String intervalStart, String intervalEnd) {
        int numCompareStart = Math.min(number.length(), intervalStart.length());
        int numCompareEnd = Math.min(number.length(), intervalEnd.length());
        if (Integer.parseInt(number.substring(0, numCompareStart)) < Integer.parseInt(intervalStart.substring(0, numCompareStart))) {
            return false;
        } else {
            return Integer.parseInt(number.substring(0, numCompareEnd)) <= Integer.parseInt(intervalEnd.substring(0, numCompareEnd));
        }
    }

    private static HashMap<Pair<String, String>, CardType> getIntervalLookup() {
        return intervalLookup;
    }

    private static Pair<String, String> getNewPair(String intervalStart, String intervalEnd) {
        if (intervalEnd == null) {
            intervalEnd = intervalStart;
        }

        return new Pair(intervalStart, intervalEnd);
    }

    public static CardType fromString(String typeStr) {
        if (typeStr == null) {
            return UNKNOWN;
        } else {
            CardType[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CardType type = var1[var3];
                if (type != UNKNOWN && type != INSUFFICIENT_DIGITS && typeStr.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }

            return UNKNOWN;
        }
    }

    public static CardType fromCardNumber(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return UNKNOWN;
        } else {
            HashSet<CardType> possibleCardTypes = new HashSet();
            Iterator var2 = getIntervalLookup().entrySet().iterator();

            while(var2.hasNext()) {
                Entry<Pair<String, String>, CardType> entry = (Entry)var2.next();
                boolean isPossibleCard = isNumberInInterval(numStr, (String)((Pair)entry.getKey()).first, (String)((Pair)entry.getKey()).second);
                if (isPossibleCard) {
                    possibleCardTypes.add(entry.getValue());
                }
            }

            if (possibleCardTypes.size() > 1) {
                return INSUFFICIENT_DIGITS;
            } else if (possibleCardTypes.size() == 1) {
                return (CardType)possibleCardTypes.iterator().next();
            } else {
                return UNKNOWN;
            }
        }
    }

    static {
        intervalLookup.put(getNewPair("2221", "2720"), MASTERCARD);
        intervalLookup.put(getNewPair("300", "305"), DINERSCLUB);
        intervalLookup.put(getNewPair("309", (String)null), DINERSCLUB);
        intervalLookup.put(getNewPair("34", (String)null), AMEX);
        intervalLookup.put(getNewPair("3528", "3589"), JCB);
        intervalLookup.put(getNewPair("36", (String)null), DINERSCLUB);
        intervalLookup.put(getNewPair("37", (String)null), AMEX);
        intervalLookup.put(getNewPair("38", "39"), DINERSCLUB);
        intervalLookup.put(getNewPair("4", (String)null), VISA);
        intervalLookup.put(getNewPair("50", (String)null), MAESTRO);
        intervalLookup.put(getNewPair("51", "55"), MASTERCARD);
        intervalLookup.put(getNewPair("56", "59"), MAESTRO);
        intervalLookup.put(getNewPair("6011", (String)null), DISCOVER);
        intervalLookup.put(getNewPair("61", (String)null), MAESTRO);
        intervalLookup.put(getNewPair("62", (String)null), DISCOVER);
        intervalLookup.put(getNewPair("63", (String)null), MAESTRO);
        intervalLookup.put(getNewPair("644", "649"), DISCOVER);
        intervalLookup.put(getNewPair("65", (String)null), DISCOVER);
        intervalLookup.put(getNewPair("66", "69"), MAESTRO);
        intervalLookup.put(getNewPair("88", (String)null), DISCOVER);
        Iterator var0 = getIntervalLookup().entrySet().iterator();

        while(var0.hasNext()) {
            Entry<Pair<String, String>, CardType> entry = (Entry)var0.next();
            minDigits = Math.max(minDigits, ((String)((Pair)entry.getKey()).first).length());
            if (((Pair)entry.getKey()).second != null) {
                minDigits = Math.max(minDigits, ((String)((Pair)entry.getKey()).second).length());
            }
        }

    }
}