//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

public class CardIONativeLibsConfig {
    private static String alternativeLibsPath;

    public CardIONativeLibsConfig() {
    }

    public static void init(String path) {
        alternativeLibsPath = path;
    }

    static String getAlternativeLibsPath() {
        return alternativeLibsPath;
    }
}