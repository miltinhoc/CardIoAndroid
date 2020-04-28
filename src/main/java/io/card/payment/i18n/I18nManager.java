//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment.i18n;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class I18nManager<E extends Enum<?>> {
    private static final String TAG = I18nManager.class.getSimpleName();
    private static final Map<String, String> SPECIAL_LOCALE_MAP = new HashMap();
    private static final Set<String> RIGHT_TO_LEFT_LOCALE_SET = new HashSet();
    private Map<String, SupportedLocale<E>> supportedLocales = new LinkedHashMap();
    private SupportedLocale<E> currentLocale;
    private Class<E> enumClazz;

    public I18nManager(Class<E> enumClazz, List<SupportedLocale<E>> locales) {
        this.enumClazz = enumClazz;
        Iterator var3 = locales.iterator();

        while(var3.hasNext()) {
            SupportedLocale<E> locale = (SupportedLocale)var3.next();
            this.addLocale(locale);
        }

        this.setLanguage((String)null);
    }

    private void logMissingLocalizations(String localeName) {
        List<String> errorMessages = this.getMissingLocaleMessages(localeName);
        Iterator var3 = errorMessages.iterator();

        while(var3.hasNext()) {
            String errorMessage = (String)var3.next();
            Log.i(TAG, errorMessage);
        }

    }

    private List<String> getMissingLocaleMessages(String localeName) {
        SupportedLocale<E> locale = (SupportedLocale)this.supportedLocales.get(localeName);
        List<String> errorMessages = new ArrayList();
        Enum[] var4 = (Enum[])this.enumClazz.getEnumConstants();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            E key = (E) var4[var6];
            String prettyKeyValue = "[" + localeName + "," + key + "]";
            if (null == locale.getAdaptedDisplay(key, (String)null)) {
                errorMessages.add("Missing " + prettyKeyValue);
            }
        }

        return errorMessages;
    }

    public void setLanguage(String localeSpecifier) {
        this.currentLocale = null;
        this.currentLocale = this.getLocaleFromSpecifier(localeSpecifier);

        assert this.currentLocale != null;

        Log.d(TAG, "setting locale to:" + this.currentLocale.getName());
    }

    public SupportedLocale<E> getLocaleFromSpecifier(String localeSpecifier) {
        SupportedLocale<E> foundLocale = null;
        if (null != localeSpecifier) {
            foundLocale = this.lookupSupportedLocale(localeSpecifier);
        }

        if (null == foundLocale) {
            String phoneLanguage = Locale.getDefault().toString();
            Log.d(TAG, localeSpecifier + " not found.  Attempting to look for " + phoneLanguage);
            foundLocale = this.lookupSupportedLocale(phoneLanguage);
        }

        if (null == foundLocale) {
            Log.d(TAG, "defaulting to english");
            foundLocale = (SupportedLocale)this.supportedLocales.get("en");
        }

        assert foundLocale != null;

        return foundLocale;
    }

    private SupportedLocale<E> lookupSupportedLocale(String localeSpecifier) {
        if (null != localeSpecifier && localeSpecifier.length() >= 2) {
            SupportedLocale<E> supportedLocale = null;
            String languageCode;
            if (SPECIAL_LOCALE_MAP.containsKey(localeSpecifier)) {
                languageCode = (String)SPECIAL_LOCALE_MAP.get(localeSpecifier);
                supportedLocale = (SupportedLocale)this.supportedLocales.get(languageCode);
                Log.d(TAG, "Overriding locale specifier " + localeSpecifier + " with " + languageCode);
            }

            if (null == supportedLocale) {
                if (localeSpecifier.contains("_")) {
                    languageCode = localeSpecifier;
                } else {
                    languageCode = localeSpecifier + "_" + Locale.getDefault().getCountry();
                }

                supportedLocale = (SupportedLocale)this.supportedLocales.get(languageCode);
            }

            if (null == supportedLocale) {
                supportedLocale = (SupportedLocale)this.supportedLocales.get(localeSpecifier);
            }

            if (null == supportedLocale) {
                languageCode = localeSpecifier.substring(0, 2);
                supportedLocale = (SupportedLocale)this.supportedLocales.get(languageCode);
            }

            return supportedLocale;
        } else {
            return null;
        }
    }

    public String getString(E key) {
        return this.getString(key, this.currentLocale);
    }

    public String getString(E key, SupportedLocale<E> localeToTranslate) {
        String countryCode = Locale.getDefault().getCountry().toUpperCase(Locale.US);
        String s = localeToTranslate.getAdaptedDisplay(key, countryCode);
        if (s == null) {
            String errorMessage = "Missing localized string for [" + this.currentLocale.getName() + ",Key." + key.toString() + "]";
            Log.i(TAG, errorMessage);
            s = ((SupportedLocale)this.supportedLocales.get("en")).getAdaptedDisplay(key, countryCode);
        }

        if (s == null) {
            Log.i(TAG, "Missing localized string for [en,Key." + key.toString() + "], so defaulting to keyname");
            s = key.toString();
        }

        return s;
    }

    private void addLocale(SupportedLocale<E> supportedLocale) {
        String localeName = supportedLocale.getName();
        if (null == localeName) {
            throw new RuntimeException("Null localeName");
        } else if (this.supportedLocales.containsKey(localeName)) {
            throw new RuntimeException("Locale " + localeName + " already added");
        } else {
            this.supportedLocales.put(localeName, supportedLocale);
            this.logMissingLocalizations(localeName);
        }
    }

    static {
        SPECIAL_LOCALE_MAP.put("zh_CN", "zh-Hans");
        SPECIAL_LOCALE_MAP.put("zh_TW", "zh-Hant_TW");
        SPECIAL_LOCALE_MAP.put("zh_HK", "zh-Hant");
        SPECIAL_LOCALE_MAP.put("en_UK", "en_GB");
        SPECIAL_LOCALE_MAP.put("en_IE", "en_GB");
        SPECIAL_LOCALE_MAP.put("iw_IL", "he");
        SPECIAL_LOCALE_MAP.put("no", "nb");
        RIGHT_TO_LEFT_LOCALE_SET.add("he");
        RIGHT_TO_LEFT_LOCALE_SET.add("ar");
    }
}
