//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment.i18n;

import android.content.Intent;
import io.card.payment.i18n.locales.LocalizedStringsList;

public final class LocalizedStrings {
    private static final I18nManager<StringKey> i18nManager;

    public static String getString(StringKey key) {
        return i18nManager.getString(key);
    }

    public static String getString(StringKey key, String languageOrLocale) {
        return i18nManager.getString(key, i18nManager.getLocaleFromSpecifier(languageOrLocale));
    }

    public static void setLanguage(Intent intent) {
        i18nManager.setLanguage(intent.getStringExtra("io.card.payment.languageOrLocale"));
    }

    static {
        i18nManager = new I18nManager(StringKey.class, LocalizedStringsList.ALL_LOCALES);
    }
}
