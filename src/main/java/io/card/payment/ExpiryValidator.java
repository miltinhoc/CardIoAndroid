//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import java.util.Date;

class ExpiryValidator implements Validator {
    private static final String TAG = ExpiryValidator.class.getSimpleName();
    public int month;
    public int year;
    private boolean fullLength;

    public ExpiryValidator() {
    }

    public ExpiryValidator(int m, int y) {
        this.month = m;
        this.year = y;
        this.fullLength = this.month > 0 && this.year > 0;
        if (this.year < 2000) {
            this.year += 2000;
        }

    }

    public void afterTextChanged(Editable s) {
        this.fullLength = s.length() >= 5;
        String dateStr = s.toString();
        if (dateStr != null) {
            Date expiry = CreditCardNumber.getDateForString(dateStr);
            if (expiry != null) {
                this.month = expiry.getMonth() + 1;
                this.year = expiry.getYear();
                if (this.year < 1900) {
                    this.year += 1900;
                }

            }
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        this.month = 0;
        this.year = 0;
        this.fullLength = false;
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public String getValue() {
        return String.format("%02d/%02d", this.month, this.year % 100);
    }

    public boolean hasFullLength() {
        return this.fullLength;
    }

    public boolean isValid() {
        if (this.month >= 1 && 12 >= this.month) {
            Date now = new Date();
            if (this.year > 1900 + now.getYear() + 15) {
                return false;
            } else {
                return this.year > 1900 + now.getYear() || this.year == 1900 + now.getYear() && this.month >= now.getMonth() + 1;
            }
        } else {
            return false;
        }
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        SpannableStringBuilder result = new SpannableStringBuilder(source);
        if (dstart == 0 && result.length() > 0 && '1' < result.charAt(0) && result.charAt(0) <= '9') {
            result.insert(0, "0");
            ++end;
        }

        int replen = dend - dstart;
        if (dstart - replen <= 2 && dstart + end - replen >= 2) {
            int loc = 2 - dstart;
            if (loc == end || 0 <= loc && loc < end && result.charAt(loc) != '/') {
                result.insert(loc, "/");
                ++end;
            }
        }

        String updated = (new SpannableStringBuilder(dest)).replace(dstart, dend, result, start, end).toString();
        if (updated.length() < 1 || updated.charAt(0) >= '0' && '1' >= updated.charAt(0)) {
            if (updated.length() >= 2) {
                if (updated.charAt(0) != '0' && updated.charAt(1) > '2') {
                    return "";
                }

                if (updated.charAt(0) == '0' && updated.charAt(1) == '0') {
                    return "";
                }
            }

            return (CharSequence)(updated.length() > 5 ? "" : result);
        } else {
            return "";
        }
    }
}
