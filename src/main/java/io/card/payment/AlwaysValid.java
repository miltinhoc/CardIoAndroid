//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.text.Editable;
import android.text.Spanned;

class AlwaysValid implements Validator {
    private String placeholder;

    public AlwaysValid() {
    }

    public boolean isValid() {
        return true;
    }

    public String getValue() {
        return this.placeholder;
    }

    public void afterTextChanged(Editable s) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return null;
    }

    public boolean hasFullLength() {
        return true;
    }
}