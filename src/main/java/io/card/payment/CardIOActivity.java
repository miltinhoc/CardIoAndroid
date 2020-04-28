//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import io.card.payment.i18n.LocalizedStrings;
import io.card.payment.i18n.StringKey;
import io.card.payment.ui.ActivityHelper;
import io.card.payment.ui.ViewUtil;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.Date;

public final class CardIOActivity extends Activity {
    public static final String EXTRA_NO_CAMERA = "io.card.payment.noCamera";
    public static final String EXTRA_REQUIRE_EXPIRY = "io.card.payment.requireExpiry";
    public static final String EXTRA_SCAN_EXPIRY = "io.card.payment.scanExpiry";
    public static final String EXTRA_UNBLUR_DIGITS = "io.card.payment.unblurDigits";
    public static final String EXTRA_REQUIRE_CVV = "io.card.payment.requireCVV";
    public static final String EXTRA_REQUIRE_POSTAL_CODE = "io.card.payment.requirePostalCode";
    public static final String EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY = "io.card.payment.restrictPostalCodeToNumericOnly";
    public static final String EXTRA_REQUIRE_CARDHOLDER_NAME = "io.card.payment.requireCardholderName";
    public static final String EXTRA_USE_CARDIO_LOGO = "io.card.payment.useCardIOLogo";
    public static final String EXTRA_SCAN_RESULT = "io.card.payment.scanResult";
    public static final String EXTRA_SUPPRESS_MANUAL_ENTRY = "io.card.payment.suppressManual";
    public static final String EXTRA_LANGUAGE_OR_LOCALE = "io.card.payment.languageOrLocale";
    public static final String EXTRA_GUIDE_COLOR = "io.card.payment.guideColor";
    public static final String EXTRA_SUPPRESS_CONFIRMATION = "io.card.payment.suppressConfirmation";
    public static final String EXTRA_HIDE_CARDIO_LOGO = "io.card.payment.hideLogo";
    public static final String EXTRA_SCAN_INSTRUCTIONS = "io.card.payment.scanInstructions";
    public static final String EXTRA_SUPPRESS_SCAN = "io.card.payment.suppressScan";
    public static final String EXTRA_CAPTURED_CARD_IMAGE = "io.card.payment.capturedCardImage";
    public static final String EXTRA_RETURN_CARD_IMAGE = "io.card.payment.returnCardImage";
    public static final String EXTRA_SCAN_OVERLAY_LAYOUT_ID = "io.card.payment.scanOverlayLayoutId";
    public static final String EXTRA_USE_PAYPAL_ACTIONBAR_ICON = "io.card.payment.intentSenderIsPayPal";
    public static final String EXTRA_KEEP_APPLICATION_THEME = "io.card.payment.keepApplicationTheme";
    private static int lastResult = 13274384;
    public static final int RESULT_CARD_INFO;
    public static final int RESULT_ENTRY_CANCELED;
    public static final int RESULT_SCAN_NOT_AVAILABLE;
    public static final int RESULT_SCAN_SUPPRESSED;
    public static final int RESULT_CONFIRMATION_SUPPRESSED;
    private static final String TAG;
    private static final long[] VIBRATE_PATTERN;
    private OverlayView mOverlay;
    private OrientationEventListener orientationListener;
    Preview mPreview;
    private CreditCard mDetectedCard;
    private Rect mGuideFrame;
    private int mLastDegrees;
    private int mFrameOrientation;
    private boolean suppressManualEntry;
    private boolean mDetectOnly;
    private LinearLayout customOverlayLayout;
    private boolean waitingForPermission;
    private RelativeLayout mUIBar;
    private FrameLayout mMainLayout;
    private boolean useApplicationTheme;
    private static int numActivityAllocations;
    private CardScanner mCardScanner;
    private boolean manualEntryFallbackOrForced = false;
    static Bitmap markedCardImage;

    public CardIOActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ++numActivityAllocations;
        if (numActivityAllocations != 1) {
            Log.i(TAG, String.format("INTERNAL WARNING: There are %d (not 1) CardIOActivity allocations!", numActivityAllocations));
        }

        Intent clientData = this.getIntent();
        this.useApplicationTheme = this.getIntent().getBooleanExtra("io.card.payment.keepApplicationTheme", false);
        ActivityHelper.setActivityTheme(this, this.useApplicationTheme);
        LocalizedStrings.setLanguage(clientData);
        this.mDetectOnly = clientData.getBooleanExtra("io.card.payment.suppressScan", false);
        ResolveInfo resolveInfo = this.getPackageManager().resolveActivity(clientData, 65536);
        String errorMsg = Util.manifestHasConfigChange(resolveInfo, CardIOActivity.class);
        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        } else {
            this.suppressManualEntry = clientData.getBooleanExtra("io.card.payment.suppressManual", false);
            if (savedInstanceState != null) {
                this.waitingForPermission = savedInstanceState.getBoolean("io.card.payment.waitingForPermission");
            }

            if (clientData.getBooleanExtra("io.card.payment.noCamera", false)) {
                Log.i("card.io", "EXTRA_NO_CAMERA set to true. Skipping camera.");
                this.manualEntryFallbackOrForced = true;
            } else if (!CardScanner.processorSupported()) {
                Log.i("card.io", "Processor not Supported. Skipping camera.");
                this.manualEntryFallbackOrForced = true;
            } else {
                try {
                    if (VERSION.SDK_INT >= 23) {
                        if (!this.waitingForPermission) {
                            if (this.checkSelfPermission("android.permission.CAMERA") == -1) {
                                Log.d(TAG, "permission denied to camera - requesting it");
                                String[] permissions = new String[]{"android.permission.CAMERA"};
                                this.waitingForPermission = true;
                                this.requestPermissions(permissions, 11);
                            } else {
                                this.checkCamera();
                                this.android23AndAboveHandleCamera();
                            }
                        }
                    } else {
                        this.checkCamera();
                        this.android22AndBelowHandleCamera();
                    }
                } catch (Exception var6) {
                    this.handleGeneralExceptionError(var6);
                }
            }

        }
    }

    private void android23AndAboveHandleCamera() {
        if (this.manualEntryFallbackOrForced) {
            this.finishIfSuppressManualEntry();
        } else {
            this.showCameraScannerOverlay();
        }

    }

    private void android22AndBelowHandleCamera() {
        if (this.manualEntryFallbackOrForced) {
            this.finishIfSuppressManualEntry();
        } else {
            this.requestWindowFeature(1);
            this.showCameraScannerOverlay();
        }

    }

    private void finishIfSuppressManualEntry() {
        if (this.suppressManualEntry) {
            Log.i("card.io", "Camera not available and manual entry suppressed.");
            this.setResultAndFinish(RESULT_SCAN_NOT_AVAILABLE, (Intent)null);
        }

    }

    private void checkCamera() {
        try {
            if (!Util.hardwareSupported()) {
                StringKey errorKey = StringKey.ERROR_NO_DEVICE_SUPPORT;
                String localizedError = LocalizedStrings.getString(errorKey);
                Log.w("card.io", errorKey + ": " + localizedError);
                this.manualEntryFallbackOrForced = true;
            }
        } catch (CameraUnavailableException var5) {
            StringKey errorKey = StringKey.ERROR_CAMERA_CONNECT_FAIL;
            String localizedError = LocalizedStrings.getString(errorKey);
            Log.e("card.io", errorKey + ": " + localizedError);
            Toast toast = Toast.makeText(this, localizedError, 1);
            toast.setGravity(17, 0, -75);
            toast.show();
            this.manualEntryFallbackOrForced = true;
        }

    }

    private void showCameraScannerOverlay() {
        if (VERSION.SDK_INT >= 16) {
            View decorView = this.getWindow().getDecorView();
            int uiOptions = 4;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = this.getActionBar();
            if (null != actionBar) {
                actionBar.hide();
            }
        }

        try {
            this.mGuideFrame = new Rect();
            this.mFrameOrientation = 1;
            if (this.getIntent().getBooleanExtra("io.card.payment.cameraBypassTestMode", false)) {
                if (!this.getPackageName().contentEquals("io.card.development")) {
                    throw new IllegalStateException("Illegal access of private extra");
                }

                Class<?> testScannerClass = Class.forName("io.card.payment.CardScannerTester");
                Constructor<?> cons = testScannerClass.getConstructor(this.getClass(), Integer.TYPE);
                this.mCardScanner = (CardScanner)cons.newInstance(this, this.mFrameOrientation);
            } else {
                this.mCardScanner = new CardScanner(this, this.mFrameOrientation);
            }

            this.mCardScanner.prepareScanner();
            this.setPreviewLayout();
            this.orientationListener = new OrientationEventListener(this, 2) {
                public void onOrientationChanged(int orientation) {
                    CardIOActivity.this.doOrientationChange(orientation);
                }
            };
        } catch (Exception var4) {
            this.handleGeneralExceptionError(var4);
        }

    }

    private void handleGeneralExceptionError(Exception e) {
        StringKey errorKey = StringKey.ERROR_CAMERA_UNEXPECTED_FAIL;
        String localizedError = LocalizedStrings.getString(errorKey);
        Log.e("card.io", "Unknown exception, please post the stack trace as a GitHub issue", e);
        Toast toast = Toast.makeText(this, localizedError, 1);
        toast.setGravity(17, 0, -75);
        toast.show();
        this.manualEntryFallbackOrForced = true;
    }

    private void doOrientationChange(int orientation) {
        if (orientation >= 0 && this.mCardScanner != null) {
            orientation += this.mCardScanner.getRotationalOffset();
            if (orientation > 360) {
                orientation -= 360;
            }

            int degrees = -1;
            if (orientation >= 15 && orientation <= 345) {
                if (orientation > 75 && orientation < 105) {
                    degrees = 90;
                    this.mFrameOrientation = 4;
                } else if (orientation > 165 && orientation < 195) {
                    degrees = 180;
                    this.mFrameOrientation = 2;
                } else if (orientation > 255 && orientation < 285) {
                    degrees = 270;
                    this.mFrameOrientation = 3;
                }
            } else {
                degrees = 0;
                this.mFrameOrientation = 1;
            }

            if (degrees >= 0 && degrees != this.mLastDegrees) {
                this.mCardScanner.setDeviceOrientation(this.mFrameOrientation);
                this.setDeviceDegrees(degrees);
                if (degrees == 90) {
                    this.rotateCustomOverlay(270.0F);
                } else if (degrees == 270) {
                    this.rotateCustomOverlay(90.0F);
                } else {
                    this.rotateCustomOverlay((float)degrees);
                }
            }

        }
    }

    protected void onResume() {
        super.onResume();
        if (!this.waitingForPermission) {
            if (this.manualEntryFallbackOrForced) {
                this.nextActivity();
                return;
            }

            Util.logNativeMemoryStats();
            this.getWindow().addFlags(1024);
            this.getWindow().addFlags(128);
            ActivityHelper.setFlagSecure(this);
            this.setRequestedOrientation(1);
            this.orientationListener.enable();
            if (!this.restartPreview()) {
                Log.e(TAG, "Could not connect to camera.");
                StringKey error = StringKey.ERROR_CAMERA_UNEXPECTED_FAIL;
                this.showErrorMessage(LocalizedStrings.getString(error));
                this.nextActivity();
            } else {
                this.setFlashOn(false);
            }

            this.doOrientationChange(this.mLastDegrees);
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("io.card.payment.waitingForPermission", this.waitingForPermission);
    }

    protected void onPause() {
        super.onPause();
        if (this.orientationListener != null) {
            this.orientationListener.disable();
        }

        this.setFlashOn(false);
        if (this.mCardScanner != null) {
            this.mCardScanner.pauseScanning();
        }

    }

    protected void onDestroy() {
        this.mOverlay = null;
        --numActivityAllocations;
        if (this.orientationListener != null) {
            this.orientationListener.disable();
        }

        this.setFlashOn(false);
        if (this.mCardScanner != null) {
            this.mCardScanner.endScanning();
            this.mCardScanner = null;
        }

        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 11) {
            this.waitingForPermission = false;
            if (grantResults.length > 0 && grantResults[0] == 0) {
                this.showCameraScannerOverlay();
            } else {
                this.manualEntryFallbackOrForced = true;
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 10:
                if (resultCode == 0) {
                    Log.d(TAG, "ignoring onActivityResult(RESULT_CANCELED) caused only when Camera Permissions are Denied in Android 23");
                } else if (resultCode != RESULT_CARD_INFO && resultCode != RESULT_ENTRY_CANCELED && !this.manualEntryFallbackOrForced) {
                    if (this.mUIBar != null) {
                        this.mUIBar.setVisibility(0);
                    }
                } else {
                    if (data != null && data.hasExtra("io.card.payment.scanResult")) {
                        Log.v(TAG, "EXTRA_SCAN_RESULT: " + data.getParcelableExtra("io.card.payment.scanResult"));
                    } else {
                        Log.d(TAG, "no data in EXTRA_SCAN_RESULT");
                    }

                    this.setResultAndFinish(resultCode, data);
                }
            default:
        }
    }

    public void onBackPressed() {
        if (!this.manualEntryFallbackOrForced && this.mOverlay.isAnimating()) {
            try {
                this.restartPreview();
            } catch (RuntimeException var2) {
                Log.w(TAG, "*** could not return to preview: " + var2);
            }
        } else if (this.mCardScanner != null) {
            super.onBackPressed();
        }

    }

    public static boolean canReadCardWithCamera() {
        try {
            return Util.hardwareSupported();
        } catch (CameraUnavailableException var1) {
            return false;
        } catch (RuntimeException var2) {
            Log.w(TAG, "RuntimeException accessing Util.hardwareSupported()");
            return false;
        }
    }

    public static String sdkVersion() {
        return "5.5.1";
    }

    public static Date sdkBuildDate() {
        return new Date("03/17/2017 14:23:12 -0400");
    }

    public static Bitmap getCapturedCardImage(Intent intent) {
        if (intent != null && intent.hasExtra("io.card.payment.capturedCardImage")) {
            byte[] imageData = intent.getByteArrayExtra("io.card.payment.capturedCardImage");
            ByteArrayInputStream inStream = new ByteArrayInputStream(imageData);
            Bitmap result = BitmapFactory.decodeStream(inStream, (Rect)null, new Options());
            return result;
        } else {
            return null;
        }
    }

    void onFirstFrame(int orientation) {
        SurfaceView sv = this.mPreview.getSurfaceView();
        if (this.mOverlay != null) {
            this.mOverlay.setCameraPreviewRect(new Rect(sv.getLeft(), sv.getTop(), sv.getRight(), sv.getBottom()));
        }

        this.mFrameOrientation = 1;
        this.setDeviceDegrees(0);
        if (orientation != this.mFrameOrientation) {
            Log.wtf("card.io", "the orientation of the scanner doesn't match the orientation of the activity");
        }

        this.onEdgeUpdate(new DetectionInfo());
    }

    void onEdgeUpdate(DetectionInfo dInfo) {
        this.mOverlay.setDetectionInfo(dInfo);
    }

    void onCardDetected(Bitmap detectedBitmap, DetectionInfo dInfo) {
        try {
            Vibrator vibrator = (Vibrator)this.getSystemService("vibrator");
            vibrator.vibrate(VIBRATE_PATTERN, -1);
        } catch (SecurityException var7) {
            Log.e("card.io", "Could not activate vibration feedback. Please add <uses-permission android:name=\"android.permission.VIBRATE\" /> to your application's manifest.");
        } catch (Exception var8) {
            Log.w("card.io", "Exception while attempting to vibrate: ", var8);
        }

        this.mCardScanner.pauseScanning();
        this.mUIBar.setVisibility(4);
        if (dInfo.predicted()) {
            this.mDetectedCard = dInfo.creditCard();
            this.mOverlay.setDetectedCard(this.mDetectedCard);
        }

        float sf;
        if (this.mFrameOrientation != 1 && this.mFrameOrientation != 2) {
            sf = (float)this.mGuideFrame.right / 428.0F * 1.15F;
        } else {
            sf = (float)this.mGuideFrame.right / 428.0F * 0.95F;
        }

        Matrix m = new Matrix();
        m.postScale(sf, sf);
        Bitmap scaledCard = Bitmap.createBitmap(detectedBitmap, 0, 0, detectedBitmap.getWidth(), detectedBitmap.getHeight(), m, false);
        this.mOverlay.setBitmap(scaledCard);
        if (this.mDetectOnly) {
            Intent dataIntent = new Intent();
            Util.writeCapturedCardImageIfNecessary(this.getIntent(), dataIntent, this.mOverlay);
            this.setResultAndFinish(RESULT_SCAN_SUPPRESSED, dataIntent);
        } else {
            this.nextActivity();
        }

    }

    private void nextActivity() {
        final Intent origIntent = this.getIntent();
        if (origIntent != null && origIntent.getBooleanExtra("io.card.payment.suppressConfirmation", false)) {
            Intent dataIntent = new Intent(this, DataEntryActivity.class);
            if (this.mDetectedCard != null) {
                dataIntent.putExtra("io.card.payment.scanResult", this.mDetectedCard);
                this.mDetectedCard = null;
            }

            Util.writeCapturedCardImageIfNecessary(origIntent, dataIntent, this.mOverlay);
            this.setResultAndFinish(RESULT_CONFIRMATION_SUPPRESSED, dataIntent);
        } else {
            (new Handler()).post(new Runnable() {
                public void run() {
                    CardIOActivity.this.getWindow().clearFlags(1024);
                    CardIOActivity.this.getWindow().addFlags(512);
                    Intent dataIntent = new Intent(CardIOActivity.this, DataEntryActivity.class);
                    Util.writeCapturedCardImageIfNecessary(origIntent, dataIntent, CardIOActivity.this.mOverlay);
                    if (CardIOActivity.this.mOverlay != null) {
                        CardIOActivity.this.mOverlay.markupCard();
                        if (CardIOActivity.markedCardImage != null && !CardIOActivity.markedCardImage.isRecycled()) {
                            CardIOActivity.markedCardImage.recycle();
                        }

                        CardIOActivity.markedCardImage = CardIOActivity.this.mOverlay.getCardImage();
                    }

                    if (CardIOActivity.this.mDetectedCard != null) {
                        dataIntent.putExtra("io.card.payment.scanResult", CardIOActivity.this.mDetectedCard);
                        CardIOActivity.this.mDetectedCard = null;
                    } else {
                        dataIntent.putExtra("io.card.payment.manualEntryScanResult", true);
                    }

                    dataIntent.putExtras(CardIOActivity.this.getIntent());
                    dataIntent.addFlags(1082195968);
                    CardIOActivity.this.startActivityForResult(dataIntent, 10);
                }
            });
        }

    }

    private void showErrorMessage(String msgStr) {
        Log.e("card.io", "error display: " + msgStr);
        Toast toast = Toast.makeText(this, msgStr, 1);
        toast.show();
    }

    private boolean restartPreview() {
        this.mDetectedCard = null;

        assert this.mPreview != null;

        boolean success = this.mCardScanner.resumeScanning(this.mPreview.getSurfaceHolder());
        if (success) {
            this.mUIBar.setVisibility(0);
        }

        return success;
    }

    private void setDeviceDegrees(int degrees) {
        View sv = this.mPreview.getSurfaceView();
        if (sv == null) {
            Log.wtf("card.io", "surface view is null.. recovering... rotation might be weird.");
        } else {
            this.mGuideFrame = this.mCardScanner.getGuideFrame(sv.getWidth(), sv.getHeight());
            Rect var10000 = this.mGuideFrame;
            var10000.top += sv.getTop();
            var10000 = this.mGuideFrame;
            var10000.bottom += sv.getTop();
            this.mOverlay.setGuideAndRotation(this.mGuideFrame, degrees);
            this.mLastDegrees = degrees;
        }
    }

    void toggleFlash() {
        this.setFlashOn(!this.mCardScanner.isFlashOn());
    }

    void setFlashOn(boolean b) {
        boolean success = this.mPreview != null && this.mOverlay != null && this.mCardScanner.setFlashOn(b);
        if (success) {
            this.mOverlay.setTorchOn(b);
        }

    }

    void triggerAutoFocus() {
        this.mCardScanner.triggerAutoFocus(true);
    }

    private void setPreviewLayout() {
        this.mMainLayout = new FrameLayout(this);
        this.mMainLayout.setBackgroundColor(-16777216);
        this.mMainLayout.setLayoutParams(new LayoutParams(-1, -1));
        FrameLayout previewFrame = new FrameLayout(this);
        previewFrame.setId(1);
        this.mCardScanner.getClass();
        this.mCardScanner.getClass();
        this.mPreview = new Preview(this, (AttributeSet)null, 640, 480);
        this.mPreview.setLayoutParams(new android.widget.FrameLayout.LayoutParams(-1, -1, 48));
        previewFrame.addView(this.mPreview);
        this.mOverlay = new OverlayView(this, (AttributeSet)null, Util.deviceSupportsTorch(this));
        this.mOverlay.setLayoutParams(new LayoutParams(-1, -1));
        if (this.getIntent() != null) {
            boolean useCardIOLogo = this.getIntent().getBooleanExtra("io.card.payment.useCardIOLogo", false);
            this.mOverlay.setUseCardIOLogo(useCardIOLogo);
            int color = this.getIntent().getIntExtra("io.card.payment.guideColor", 0);
            if (color != 0) {
                int alphaRemovedColor = color | -16777216;
                if (color != alphaRemovedColor) {
                    Log.w("card.io", "Removing transparency from provided guide color.");
                }

                this.mOverlay.setGuideColor(alphaRemovedColor);
            } else {
                this.mOverlay.setGuideColor(-16711936);
            }

            boolean hideCardIOLogo = this.getIntent().getBooleanExtra("io.card.payment.hideLogo", false);
            this.mOverlay.setHideCardIOLogo(hideCardIOLogo);
            String scanInstructions = this.getIntent().getStringExtra("io.card.payment.scanInstructions");
            if (scanInstructions != null) {
                this.mOverlay.setScanInstructions(scanInstructions);
            }
        }

        previewFrame.addView(this.mOverlay);
        android.widget.RelativeLayout.LayoutParams previewParams = new android.widget.RelativeLayout.LayoutParams(-1, -1);
        previewParams.addRule(10);
        previewParams.addRule(2, 2);
        this.mMainLayout.addView(previewFrame, previewParams);
        this.mUIBar = new RelativeLayout(this);
        this.mUIBar.setGravity(80);
        android.widget.RelativeLayout.LayoutParams mUIBarParams = new android.widget.RelativeLayout.LayoutParams(-1, -2);
        previewParams.addRule(12);
        this.mUIBar.setLayoutParams(mUIBarParams);
        this.mUIBar.setId(2);
        this.mUIBar.setGravity(85);
        if (!this.suppressManualEntry) {
            Button keyboardBtn = new Button(this);
            keyboardBtn.setId(3);
            keyboardBtn.setText(LocalizedStrings.getString(StringKey.KEYBOARD));
            keyboardBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CardIOActivity.this.nextActivity();
                }
            });
            this.mUIBar.addView(keyboardBtn);
            ViewUtil.styleAsButton(keyboardBtn, false, this, this.useApplicationTheme);
            if (!this.useApplicationTheme) {
                keyboardBtn.setTextSize(14.0F);
            }

            keyboardBtn.setMinimumHeight(ViewUtil.typedDimensionValueToPixelsInt("42dip", this));
            android.widget.RelativeLayout.LayoutParams keyboardParams = (android.widget.RelativeLayout.LayoutParams)keyboardBtn.getLayoutParams();
            keyboardParams.width = -2;
            keyboardParams.height = -2;
            keyboardParams.addRule(12);
            ViewUtil.setPadding(keyboardBtn, "16dip", (String)null, "16dip", (String)null);
            ViewUtil.setMargins(keyboardBtn, "4dip", "4dip", "4dip", "4dip");
        }

        android.widget.RelativeLayout.LayoutParams uiParams = new android.widget.RelativeLayout.LayoutParams(-1, -2);
        uiParams.addRule(12);
        float scale = this.getResources().getDisplayMetrics().density;
        int uiBarMarginPx = (int)(15.0F * scale + 0.5F);
        uiParams.setMargins(0, uiBarMarginPx, 0, uiBarMarginPx);
        this.mMainLayout.addView(this.mUIBar, uiParams);
        if (this.getIntent() != null) {
            if (this.customOverlayLayout != null) {
                this.mMainLayout.removeView(this.customOverlayLayout);
                this.customOverlayLayout = null;
            }

            int resourceId = this.getIntent().getIntExtra("io.card.payment.scanOverlayLayoutId", -1);
            if (resourceId != -1) {
                this.customOverlayLayout = new LinearLayout(this);
                this.customOverlayLayout.setLayoutParams(new LayoutParams(-1, -1));
                LayoutInflater inflater = this.getLayoutInflater();
                inflater.inflate(resourceId, this.customOverlayLayout);
                this.mMainLayout.addView(this.customOverlayLayout);
            }
        }

        this.setContentView(this.mMainLayout);
    }

    private void rotateCustomOverlay(float degrees) {
        if (this.customOverlayLayout != null) {
            float pivotX = (float)(this.customOverlayLayout.getWidth() / 2);
            float pivotY = (float)(this.customOverlayLayout.getHeight() / 2);
            Animation an = new RotateAnimation(0.0F, degrees, pivotX, pivotY);
            an.setDuration(0L);
            an.setRepeatCount(0);
            an.setFillAfter(true);
            this.customOverlayLayout.setAnimation(an);
        }

    }

    private void setResultAndFinish(int resultCode, Intent data) {
        this.setResult(resultCode, data);
        markedCardImage = null;
        this.finish();
    }

    public Rect getTorchRect() {
        return this.mOverlay == null ? null : this.mOverlay.getTorchRect();
    }

    static {
        RESULT_CARD_INFO = lastResult++;
        RESULT_ENTRY_CANCELED = lastResult++;
        RESULT_SCAN_NOT_AVAILABLE = lastResult++;
        RESULT_SCAN_SUPPRESSED = lastResult++;
        RESULT_CONFIRMATION_SUPPRESSED = lastResult++;
        TAG = CardIOActivity.class.getSimpleName();
        VIBRATE_PATTERN = new long[]{0L, 70L, 10L, 40L};
        markedCardImage = null;
    }
}
