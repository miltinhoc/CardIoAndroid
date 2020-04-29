//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.text.InputFilter;
import android.text.TextWatcher;

interface Validator extends InputFilter, TextWatcher {
    String getValue();

    boolean isValid();

    boolean hasFullLength();
}
