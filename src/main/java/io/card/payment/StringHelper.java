//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

class StringHelper {
    public static String getDigitsOnlyString(String numString) {
        StringBuilder sb = new StringBuilder();
        char[] var2 = numString.toCharArray();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char c = var2[var4];
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
