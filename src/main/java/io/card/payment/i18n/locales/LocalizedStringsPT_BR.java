//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment.i18n.locales;

import java.util.HashMap;
import java.util.Map;

import io.card.payment.i18n.StringKey;
import io.card.payment.i18n.SupportedLocale;

public class LocalizedStringsPT_BR implements SupportedLocale<StringKey> {
    private static Map<StringKey, String> mDisplay = new HashMap();
    private static Map<String, String> mAdapted = new HashMap();

    public String getName() {
        return "pt_BR";
    }

    public String getAdaptedDisplay(StringKey key, String country) {
        String adaptedKey = key.toString() + "|" + country;
        return mAdapted.containsKey(adaptedKey) ? (String)mAdapted.get(adaptedKey) : (String)mDisplay.get(key);
    }

    public LocalizedStringsPT_BR() {
        mDisplay.put(StringKey.CANCEL, "Cancelar");
        mDisplay.put(StringKey.CARDTYPE_AMERICANEXPRESS, "American Express");
        mDisplay.put(StringKey.CARDTYPE_DISCOVER, "Discover");
        mDisplay.put(StringKey.CARDTYPE_JCB, "JCB");
        mDisplay.put(StringKey.CARDTYPE_MASTERCARD, "MasterCard");
        mDisplay.put(StringKey.CARDTYPE_VISA, "Visa");
        mDisplay.put(StringKey.DONE, "Concluído");
        mDisplay.put(StringKey.ENTRY_CVV, "CVV");
        mDisplay.put(StringKey.ENTRY_POSTAL_CODE, "CEP");
        mDisplay.put(StringKey.ENTRY_CARDHOLDER_NAME, "Nome do titular do cartão");
        mDisplay.put(StringKey.ENTRY_EXPIRES, "Vencimento");
        mDisplay.put(StringKey.EXPIRES_PLACEHOLDER, "MM/AA");
        mDisplay.put(StringKey.SCAN_GUIDE, "Posicionar cartão aqui.\nEle será digitalizado automaticamente.");
        mDisplay.put(StringKey.KEYBOARD, "Teclado…");
        mDisplay.put(StringKey.ENTRY_CARD_NUMBER, "Número do Cartão");
        mDisplay.put(StringKey.MANUAL_ENTRY_TITLE, "Dados do cartão");
        mDisplay.put(StringKey.ERROR_NO_DEVICE_SUPPORT, "Este dispositivo não pode usar a câmera para ler números de cartão.");
        mDisplay.put(StringKey.ERROR_CAMERA_CONNECT_FAIL, "A câmera do dispositivo não está disponível.");
        mDisplay.put(StringKey.ERROR_CAMERA_UNEXPECTED_FAIL, "O dispositivo sofreu um erro inesperado ao abrir a câmera.");
    }
}
