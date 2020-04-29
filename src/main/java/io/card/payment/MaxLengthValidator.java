//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

class MaxLengthValidator extends NonEmptyValidator implements Validator {
    private int maxLength;

    MaxLengthValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isValid() {
        return super.isValid() && this.getValue().length() <= this.maxLength;
    }
}
