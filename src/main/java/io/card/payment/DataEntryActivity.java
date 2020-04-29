//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DateKeyListener;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import io.card.payment.R.drawable;
import io.card.payment.i18n.LocalizedStrings;
import io.card.payment.i18n.StringKey;
import io.card.payment.ui.ActivityHelper;
import io.card.payment.ui.Appearance;
import io.card.payment.ui.ViewUtil;

public final class DataEntryActivity extends Activity implements TextWatcher {
    private int viewIdCounter = 1;
    private int editTextIdCounter = 100;
    private TextView activityTitleTextView;
    private EditText numberEdit;
    private Validator numberValidator;
    private EditText expiryEdit;
    private Validator expiryValidator;
    private EditText cvvEdit;
    private Validator cvvValidator;
    private EditText postalCodeEdit;
    private Validator postalCodeValidator;
    private EditText cardholderNameEdit;
    private Validator cardholderNameValidator;
    private ImageView cardView;
    private Button doneBtn;
    private Button cancelBtn;
    private CreditCard capture;
    private boolean autoAcceptDone;
    private String labelLeftPadding;
    private boolean useApplicationTheme;
    private int defaultTextColor;
    private static final String TAG = DataEntryActivity.class.getSimpleName();

    public DataEntryActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == this.getIntent().getExtras()) {
            this.onBackPressed();
        } else {
            this.useApplicationTheme = this.getIntent().getBooleanExtra("io.card.payment.keepApplicationTheme", false);
            ActivityHelper.setActivityTheme(this, this.useApplicationTheme);
            this.defaultTextColor = (new TextView(this)).getTextColors().getDefaultColor();
            this.labelLeftPadding = ActivityHelper.holoSupported() ? "12dip" : "2dip";
            LocalizedStrings.setLanguage(this.getIntent());
            int paddingPx = ViewUtil.typedDimensionValueToPixelsInt("4dip", this);
            RelativeLayout container = new RelativeLayout(this);
            if (!this.useApplicationTheme) {
                container.setBackgroundColor(Appearance.DEFAULT_BACKGROUND_COLOR);
            }

            ScrollView scrollView = new ScrollView(this);
            scrollView.setId(this.viewIdCounter++);
            LayoutParams scrollParams = new LayoutParams(-1, -2);
            scrollParams.addRule(10);
            container.addView(scrollView, scrollParams);
            LinearLayout wrapperLayout = new LinearLayout(this);
            wrapperLayout.setOrientation(1);
            scrollView.addView(wrapperLayout, -1, -1);
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(1);
            android.widget.LinearLayout.LayoutParams mainParams = new android.widget.LinearLayout.LayoutParams(-1, -1);
            this.capture = (CreditCard)this.getIntent().getParcelableExtra("io.card.payment.scanResult");
            this.autoAcceptDone = this.getIntent().getBooleanExtra("debug_autoAcceptResult", false);
            LinearLayout optionLayout;
            if (this.capture != null) {
                this.numberValidator = new CardNumberValidator(this.capture.cardNumber);
                this.cardView = new ImageView(this);
                android.widget.LinearLayout.LayoutParams cardParams = new android.widget.LinearLayout.LayoutParams(-1, -2);
                this.cardView.setPadding(0, 0, 0, paddingPx);
                cardParams.weight = 1.0F;
                this.cardView.setImageBitmap(CardIOActivity.markedCardImage);
                mainLayout.addView(this.cardView, cardParams);
                ViewUtil.setMargins(this.cardView, (String)null, (String)null, (String)null, "8dip");
            } else {
                this.activityTitleTextView = new TextView(this);
                this.activityTitleTextView.setTextSize(24.0F);
                if (!this.useApplicationTheme) {
                    this.activityTitleTextView.setTextColor(Appearance.PAY_BLUE_COLOR);
                }

                mainLayout.addView(this.activityTitleTextView);
                ViewUtil.setPadding(this.activityTitleTextView, (String)null, (String)null, (String)null, "8dip");
                ViewUtil.setDimensions(this.activityTitleTextView, -2, -2);
                optionLayout = new LinearLayout(this);
                optionLayout.setOrientation(1);
                ViewUtil.setPadding(optionLayout, (String)null, "4dip", (String)null, "4dip");
                TextView numberLabel = new TextView(this);
                ViewUtil.setPadding(numberLabel, this.labelLeftPadding, (String)null, (String)null, (String)null);
                numberLabel.setText(LocalizedStrings.getString(StringKey.ENTRY_CARD_NUMBER));
                if (!this.useApplicationTheme) {
                    numberLabel.setTextColor(Appearance.TEXT_COLOR_LABEL);
                }

                optionLayout.addView(numberLabel, -2, -2);
                this.numberEdit = new EditText(this);
                this.numberEdit.setId(this.editTextIdCounter++);
                this.numberEdit.setMaxLines(1);
                this.numberEdit.setImeOptions(6);
                this.numberEdit.setTextAppearance(this.getApplicationContext(), 16842816);
                this.numberEdit.setInputType(3);
                this.numberEdit.setHint("1234 5678 1234 5678");
                if (!this.useApplicationTheme) {
                    this.numberEdit.setHintTextColor(-3355444);
                }

                this.numberValidator = new CardNumberValidator();
                this.numberEdit.addTextChangedListener(this.numberValidator);
                this.numberEdit.addTextChangedListener(this);
                this.numberEdit.setFilters(new InputFilter[]{new DigitsKeyListener(), this.numberValidator});
                optionLayout.addView(this.numberEdit, -1, -2);
                mainLayout.addView(optionLayout, -1, -1);
            }

            optionLayout = new LinearLayout(this);
            android.widget.LinearLayout.LayoutParams optionLayoutParam = new android.widget.LinearLayout.LayoutParams(-1, -2);
            ViewUtil.setPadding(optionLayout, (String)null, "4dip", (String)null, "4dip");
            optionLayout.setOrientation(0);
            boolean requireExpiry = this.getIntent().getBooleanExtra("io.card.payment.requireExpiry", false);
            boolean requireCVV = this.getIntent().getBooleanExtra("io.card.payment.requireCVV", false);
            boolean requirePostalCode = this.getIntent().getBooleanExtra("io.card.payment.requirePostalCode", false);
            LinearLayout postalCodeLayout;
            android.widget.LinearLayout.LayoutParams postalCodeLayoutParam;
            TextView zipLabel;
            if (requireExpiry) {
                postalCodeLayout = new LinearLayout(this);
                postalCodeLayoutParam = new android.widget.LinearLayout.LayoutParams(0, -1, 1.0F);
                postalCodeLayout.setOrientation(1);
                zipLabel = new TextView(this);
                if (!this.useApplicationTheme) {
                    zipLabel.setTextColor(Appearance.TEXT_COLOR_LABEL);
                }

                zipLabel.setText(LocalizedStrings.getString(StringKey.ENTRY_EXPIRES));
                ViewUtil.setPadding(zipLabel, this.labelLeftPadding, (String)null, (String)null, (String)null);
                postalCodeLayout.addView(zipLabel, -2, -2);
                this.expiryEdit = new EditText(this);
                this.expiryEdit.setId(this.editTextIdCounter++);
                this.expiryEdit.setMaxLines(1);
                this.expiryEdit.setImeOptions(6);
                this.expiryEdit.setTextAppearance(this.getApplicationContext(), 16842816);
                this.expiryEdit.setInputType(3);
                this.expiryEdit.setHint(LocalizedStrings.getString(StringKey.EXPIRES_PLACEHOLDER));
                if (!this.useApplicationTheme) {
                    this.expiryEdit.setHintTextColor(-3355444);
                }

                if (this.capture != null) {
                    this.expiryValidator = new ExpiryValidator(this.capture.expiryMonth, this.capture.expiryYear);
                } else {
                    this.expiryValidator = new ExpiryValidator();
                }

                if (this.expiryValidator.hasFullLength()) {
                    this.expiryEdit.setText(this.expiryValidator.getValue());
                }

                this.expiryEdit.addTextChangedListener(this.expiryValidator);
                this.expiryEdit.addTextChangedListener(this);
                this.expiryEdit.setFilters(new InputFilter[]{new DateKeyListener(), this.expiryValidator});
                postalCodeLayout.addView(this.expiryEdit, -1, -2);
                optionLayout.addView(postalCodeLayout, postalCodeLayoutParam);
                ViewUtil.setMargins(postalCodeLayout, (String)null, (String)null, !requireCVV && !requirePostalCode ? null : "4dip", (String)null);
            } else {
                this.expiryValidator = new AlwaysValid();
            }

            if (requireCVV) {
                postalCodeLayout = new LinearLayout(this);
                postalCodeLayoutParam = new android.widget.LinearLayout.LayoutParams(0, -1, 1.0F);
                postalCodeLayout.setOrientation(1);
                zipLabel = new TextView(this);
                if (!this.useApplicationTheme) {
                    zipLabel.setTextColor(Appearance.TEXT_COLOR_LABEL);
                }

                ViewUtil.setPadding(zipLabel, this.labelLeftPadding, (String)null, (String)null, (String)null);
                zipLabel.setText(LocalizedStrings.getString(StringKey.ENTRY_CVV));
                postalCodeLayout.addView(zipLabel, -2, -2);
                this.cvvEdit = new EditText(this);
                this.cvvEdit.setId(this.editTextIdCounter++);
                this.cvvEdit.setMaxLines(1);
                this.cvvEdit.setImeOptions(6);
                this.cvvEdit.setTextAppearance(this.getApplicationContext(), 16842816);
                this.cvvEdit.setInputType(3);
                this.cvvEdit.setHint("123");
                if (!this.useApplicationTheme) {
                    this.cvvEdit.setHintTextColor(-3355444);
                }

                int length = 4;
                if (this.capture != null) {
                    CardType type = CardType.fromCardNumber(this.numberValidator.getValue());
                    length = type.cvvLength();
                }

                this.cvvValidator = new FixedLengthValidator(length);
                this.cvvEdit.setFilters(new InputFilter[]{new DigitsKeyListener(), this.cvvValidator});
                this.cvvEdit.addTextChangedListener(this.cvvValidator);
                this.cvvEdit.addTextChangedListener(this);
                postalCodeLayout.addView(this.cvvEdit, -1, -2);
                optionLayout.addView(postalCodeLayout, postalCodeLayoutParam);
                ViewUtil.setMargins(postalCodeLayout, requireExpiry ? "4dip" : null, (String)null, requirePostalCode ? "4dip" : null, (String)null);
            } else {
                this.cvvValidator = new AlwaysValid();
            }

            if (requirePostalCode) {
                postalCodeLayout = new LinearLayout(this);
                postalCodeLayoutParam = new android.widget.LinearLayout.LayoutParams(0, -1, 1.0F);
                postalCodeLayout.setOrientation(1);
                zipLabel = new TextView(this);
                if (!this.useApplicationTheme) {
                    zipLabel.setTextColor(Appearance.TEXT_COLOR_LABEL);
                }

                ViewUtil.setPadding(zipLabel, this.labelLeftPadding, (String)null, (String)null, (String)null);
                zipLabel.setText(LocalizedStrings.getString(StringKey.ENTRY_POSTAL_CODE));
                postalCodeLayout.addView(zipLabel, -2, -2);
                boolean postalCodeNumericOnly = this.getIntent().getBooleanExtra("io.card.payment.restrictPostalCodeToNumericOnly", false);
                this.postalCodeEdit = new EditText(this);
                this.postalCodeEdit.setId(this.editTextIdCounter++);
                this.postalCodeEdit.setMaxLines(1);
                this.postalCodeEdit.setImeOptions(6);
                this.postalCodeEdit.setTextAppearance(this.getApplicationContext(), 16842816);
                if (postalCodeNumericOnly) {
                    this.postalCodeEdit.setInputType(3);
                } else {
                    this.postalCodeEdit.setInputType(1);
                }

                if (!this.useApplicationTheme) {
                    this.postalCodeEdit.setHintTextColor(-3355444);
                }

                this.postalCodeValidator = new MaxLengthValidator(20);
                this.postalCodeEdit.addTextChangedListener(this.postalCodeValidator);
                this.postalCodeEdit.addTextChangedListener(this);
                postalCodeLayout.addView(this.postalCodeEdit, -1, -2);
                optionLayout.addView(postalCodeLayout, postalCodeLayoutParam);
                ViewUtil.setMargins(postalCodeLayout, !requireExpiry && !requireCVV ? null : "4dip", (String)null, (String)null, (String)null);
            } else {
                this.postalCodeValidator = new AlwaysValid();
            }

            mainLayout.addView(optionLayout, optionLayoutParam);
            this.addCardholderNameIfNeeded(mainLayout);
            wrapperLayout.addView(mainLayout, mainParams);
            ViewUtil.setMargins(mainLayout, "16dip", "20dip", "16dip", "20dip");
            postalCodeLayout = new LinearLayout(this);
            postalCodeLayout.setId(this.viewIdCounter++);
            LayoutParams buttonLayoutParam = new LayoutParams(-1, -2);
            buttonLayoutParam.addRule(12);
            postalCodeLayout.setPadding(0, paddingPx, 0, 0);
            postalCodeLayout.setBackgroundColor(0);
            scrollParams.addRule(2, postalCodeLayout.getId());
            this.doneBtn = new Button(this);
            android.widget.LinearLayout.LayoutParams doneParam = new android.widget.LinearLayout.LayoutParams(-1, -2, 1.0F);
            this.doneBtn.setText(LocalizedStrings.getString(StringKey.DONE));
            this.doneBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DataEntryActivity.this.completed();
                }
            });
            this.doneBtn.setEnabled(false);
            postalCodeLayout.addView(this.doneBtn, doneParam);
            ViewUtil.styleAsButton(this.doneBtn, true, this, this.useApplicationTheme);
            ViewUtil.setPadding(this.doneBtn, "5dip", (String)null, "5dip", (String)null);
            ViewUtil.setMargins(this.doneBtn, "8dip", "8dip", "8dip", "8dip");
            if (!this.useApplicationTheme) {
                this.doneBtn.setTextSize(16.0F);
            }

            this.cancelBtn = new Button(this);
            android.widget.LinearLayout.LayoutParams cancelParam = new android.widget.LinearLayout.LayoutParams(-1, -2, 1.0F);
            this.cancelBtn.setText(LocalizedStrings.getString(StringKey.CANCEL));
            this.cancelBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DataEntryActivity.this.onBackPressed();
                }
            });
            postalCodeLayout.addView(this.cancelBtn, cancelParam);
            ViewUtil.styleAsButton(this.cancelBtn, false, this, this.useApplicationTheme);
            ViewUtil.setPadding(this.cancelBtn, "5dip", (String)null, "5dip", (String)null);
            ViewUtil.setMargins(this.cancelBtn, "4dip", "8dip", "8dip", "8dip");
            if (!this.useApplicationTheme) {
                this.cancelBtn.setTextSize(16.0F);
            }

            container.addView(postalCodeLayout, buttonLayoutParam);
            ActivityHelper.addActionBarIfSupported(this);
            this.setContentView(container);
            Drawable icon = null;
            boolean usePayPalActionBarIcon = this.getIntent().getBooleanExtra("io.card.payment.intentSenderIsPayPal", true);
            if (usePayPalActionBarIcon) {
                icon = this.getResources().getDrawable(drawable.cio_ic_paypal_monogram);
            }

            if (requireExpiry && this.expiryValidator.isValid()) {
                this.afterTextChanged(this.expiryEdit.getEditableText());
            }

            ActivityHelper.setupActionBarIfSupported(this, this.activityTitleTextView, LocalizedStrings.getString(StringKey.MANUAL_ENTRY_TITLE), "card.io - ", icon);
        }
    }

    private void completed() {
        if (this.capture == null) {
            this.capture = new CreditCard();
        }

        if (this.expiryEdit != null) {
            this.capture.expiryMonth = ((ExpiryValidator)this.expiryValidator).month;
            this.capture.expiryYear = ((ExpiryValidator)this.expiryValidator).year;
        }

        CreditCard result = new CreditCard(this.numberValidator.getValue(), this.capture.expiryMonth, this.capture.expiryYear, this.cvvValidator.getValue(), this.postalCodeValidator.getValue(), this.cardholderNameValidator.getValue());
        Intent dataIntent = new Intent();
        dataIntent.putExtra("io.card.payment.scanResult", result);
        if (this.getIntent().hasExtra("io.card.payment.capturedCardImage")) {
            dataIntent.putExtra("io.card.payment.capturedCardImage", this.getIntent().getByteArrayExtra("io.card.payment.capturedCardImage"));
        }

        this.setResult(CardIOActivity.RESULT_CARD_INFO, dataIntent);
        this.finish();
    }

    public void onBackPressed() {
        this.setResult(CardIOActivity.RESULT_ENTRY_CANCELED);
        this.finish();
    }

    protected void onResume() {
        super.onResume();
        this.getWindow().setFlags(0, 1024);
        ActivityHelper.setFlagSecure(this);
        this.validateAndEnableDoneButtonIfValid();
        if (this.numberEdit == null && this.expiryEdit != null && !this.expiryValidator.isValid()) {
            this.expiryEdit.requestFocus();
        } else {
            this.advanceToNextEmptyField();
        }

        if (this.numberEdit != null || this.expiryEdit != null || this.cvvEdit != null || this.postalCodeEdit != null || this.cardholderNameEdit != null) {
            this.getWindow().setSoftInputMode(5);
        }

    }

    private EditText advanceToNextEmptyField() {
        int var1 = 100;

        EditText et;
        do {
            if ((et = (EditText)this.findViewById(var1++)) == null) {
                return null;
            }
        } while(et.getText().length() != 0 || !et.requestFocus());

        return et;
    }

    private void validateAndEnableDoneButtonIfValid() {
        this.doneBtn.setEnabled(this.numberValidator.isValid() && this.expiryValidator.isValid() && this.cvvValidator.isValid() && this.postalCodeValidator.isValid() && this.cardholderNameValidator.isValid());
        if (this.autoAcceptDone && this.numberValidator.isValid() && this.expiryValidator.isValid() && this.cvvValidator.isValid() && this.postalCodeValidator.isValid() && this.cardholderNameValidator.isValid()) {
            this.completed();
        }

    }

    public void afterTextChanged(Editable et) {
        if (this.numberEdit != null && et == this.numberEdit.getText()) {
            if (this.numberValidator.hasFullLength()) {
                if (!this.numberValidator.isValid()) {
                    this.numberEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                } else {
                    this.setDefaultColor(this.numberEdit);
                    this.advanceToNextEmptyField();
                }
            } else {
                this.setDefaultColor(this.numberEdit);
            }

            if (this.cvvEdit != null) {
                CardType type = CardType.fromCardNumber(this.numberValidator.getValue().toString());
                FixedLengthValidator v = (FixedLengthValidator)this.cvvValidator;
                int length = type.cvvLength();
                v.requiredLength = length;
                this.cvvEdit.setHint(length == 4 ? "1234" : "123");
            }
        } else if (this.expiryEdit != null && et == this.expiryEdit.getText()) {
            if (this.expiryValidator.hasFullLength()) {
                if (!this.expiryValidator.isValid()) {
                    this.expiryEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                } else {
                    this.setDefaultColor(this.expiryEdit);
                    this.advanceToNextEmptyField();
                }
            } else {
                this.setDefaultColor(this.expiryEdit);
            }
        } else if (this.cvvEdit != null && et == this.cvvEdit.getText()) {
            if (this.cvvValidator.hasFullLength()) {
                if (!this.cvvValidator.isValid()) {
                    this.cvvEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                } else {
                    this.setDefaultColor(this.cvvEdit);
                    this.advanceToNextEmptyField();
                }
            } else {
                this.setDefaultColor(this.cvvEdit);
            }
        } else if (this.postalCodeEdit != null && et == this.postalCodeEdit.getText()) {
            if (this.postalCodeValidator.hasFullLength()) {
                if (!this.postalCodeValidator.isValid()) {
                    this.postalCodeEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                } else {
                    this.setDefaultColor(this.postalCodeEdit);
                }
            } else {
                this.setDefaultColor(this.postalCodeEdit);
            }
        } else if (this.cardholderNameEdit != null && et == this.cardholderNameEdit.getText()) {
            if (this.cardholderNameValidator.hasFullLength()) {
                if (!this.cardholderNameValidator.isValid()) {
                    this.cardholderNameEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                } else {
                    this.setDefaultColor(this.cardholderNameEdit);
                }
            } else {
                this.setDefaultColor(this.cardholderNameEdit);
            }
        }

        this.validateAndEnableDoneButtonIfValid();
    }

    private void setDefaultColor(EditText editText) {
        if (this.useApplicationTheme) {
            editText.setTextColor(this.defaultTextColor);
        } else {
            editText.setTextColor(-12303292);
        }

    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    private void addCardholderNameIfNeeded(ViewGroup mainLayout) {
        boolean requireCardholderName = this.getIntent().getBooleanExtra("io.card.payment.requireCardholderName", false);
        if (requireCardholderName) {
            LinearLayout cardholderNameLayout = new LinearLayout(this);
            ViewUtil.setPadding(cardholderNameLayout, (String)null, "4dip", (String)null, (String)null);
            cardholderNameLayout.setOrientation(1);
            TextView cardholderNameLabel = new TextView(this);
            if (!this.useApplicationTheme) {
                cardholderNameLabel.setTextColor(Appearance.TEXT_COLOR_LABEL);
            }

            ViewUtil.setPadding(cardholderNameLabel, this.labelLeftPadding, (String)null, (String)null, (String)null);
            cardholderNameLabel.setText(LocalizedStrings.getString(StringKey.ENTRY_CARDHOLDER_NAME));
            cardholderNameLayout.addView(cardholderNameLabel, -2, -2);
            this.cardholderNameEdit = new EditText(this);
            this.cardholderNameEdit.setId(this.editTextIdCounter++);
            this.cardholderNameEdit.setMaxLines(1);
            this.cardholderNameEdit.setImeOptions(6);
            this.cardholderNameEdit.setTextAppearance(this.getApplicationContext(), 16842816);
            this.cardholderNameEdit.setInputType(1);
            if (!this.useApplicationTheme) {
                this.cardholderNameEdit.setHintTextColor(-3355444);
            }

            this.cardholderNameValidator = new MaxLengthValidator(175);
            this.cardholderNameEdit.addTextChangedListener(this.cardholderNameValidator);
            this.cardholderNameEdit.addTextChangedListener(this);
            cardholderNameLayout.addView(this.cardholderNameEdit, -1, -2);
            mainLayout.addView(cardholderNameLayout, -1, -2);
        } else {
            this.cardholderNameValidator = new AlwaysValid();
        }

    }
}
