//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

class CardNumberValidator implements Validator {
    private String numberString;
    static final int[] AMEX_SPACER = new int[]{4, 11};
    static final int[] NORMAL_SPACER = new int[]{4, 9, 14};
    private int spacerToDelete;

    public CardNumberValidator() {
    }

    public CardNumberValidator(String number) {
        this.numberString = number;
    }

    public void afterTextChanged(Editable source) {
        this.numberString = StringHelper.getDigitsOnlyString(source.toString());
        CardType type = CardType.fromCardNumber(this.numberString);
        int i;
        if (this.spacerToDelete > 1) {
            i = this.spacerToDelete;
            int s = this.spacerToDelete - 1;
            this.spacerToDelete = 0;
            if (i > s) {
                source.delete(s, i);
            }
        }

        for(i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (type.numberLength() == 15 && (i == 4 || i == 11) || (type.numberLength() == 16 || type.numberLength() == 14) && (i == 4 || i == 9 || i == 14)) {
                if (c != ' ') {
                    source.insert(i, " ");
                }
            } else if (c == ' ') {
                source.delete(i, i + 1);
                --i;
            }
        }

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public boolean hasFullLength() {
        if (TextUtils.isEmpty(this.numberString)) {
            return false;
        } else {
            CardType type = CardType.fromCardNumber(this.numberString);
            return this.numberString.length() == type.numberLength();
        }
    }

    public boolean isValid() {
        if (!this.hasFullLength()) {
            return false;
        } else {
            return CreditCardNumber.passesLuhnChecksum(this.numberString);
        }
    }

    public String getValue() {
        return this.numberString;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String updated = (new SpannableStringBuilder(dest)).replace(dstart, dend, source, start, end).toString();
        String updatedDigits = StringHelper.getDigitsOnlyString(updated);
        CardType type = CardType.fromCardNumber(updatedDigits);
        int maxLength = type.numberLength();
        if (updatedDigits.length() > maxLength) {
            return "";
        } else {
            SpannableStringBuilder result = new SpannableStringBuilder(source);
            int[] spacers;
            if (maxLength == 15) {
                spacers = AMEX_SPACER;
            } else {
                spacers = NORMAL_SPACER;
            }

            int replen = dend - dstart;

            for(int i = 0; i < spacers.length; ++i) {
                if (source.length() == 0 && dstart == spacers[i] && dest.charAt(dstart) == ' ') {
                    this.spacerToDelete = spacers[i];
                }

                if (dstart - replen <= spacers[i] && dstart + end - replen >= spacers[i]) {
                    int loc = spacers[i] - dstart;
                    if (loc == end || 0 <= loc && loc < end && result.charAt(loc) != ' ') {
                        result.insert(loc, " ");
                        ++end;
                    }
                }
            }

            return result;
        }
    }
}