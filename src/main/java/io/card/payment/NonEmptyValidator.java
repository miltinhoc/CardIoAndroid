//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.text.Editable;
import android.text.Spanned;

class NonEmptyValidator implements Validator {
    private String value;

    NonEmptyValidator() {
    }

    public void afterTextChanged(Editable s) {
        this.value = s.toString().trim();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public String getValue() {
        return this.value;
    }

    public boolean hasFullLength() {
        return this.isValid();
    }

    public boolean isValid() {
        return this.value != null && this.value.length() > 0;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return null;
    }
}
