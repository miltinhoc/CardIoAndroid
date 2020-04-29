//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Debug;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static final boolean TORCH_BLACK_LISTED;
    private static Boolean sHardwareSupported;

    Util() {
    }

    public static boolean deviceSupportsTorch(Context context) {
        return !TORCH_BLACK_LISTED && context.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }

    public static String manifestHasConfigChange(ResolveInfo resolveInfo, Class activityClass) {
        String error = null;
        if (resolveInfo == null) {
            error = String.format("Didn't find %s in the AndroidManifest.xml", activityClass.getName());
        } else if (!hasConfigFlag(resolveInfo.activityInfo.configChanges, 128)) {
            error = activityClass.getName() + " requires attribute android:configChanges=\"orientation\"";
        }

        if (error != null) {
            Log.e("card.io", error);
        }

        return error;
    }

    public static boolean hasConfigFlag(int config, int configFlag) {
        return (config & configFlag) == configFlag;
    }

    public static boolean hardwareSupported() {
        if (sHardwareSupported == null) {
            sHardwareSupported = hardwareSupportCheck();
        }

        return sHardwareSupported;
    }

    private static boolean hardwareSupportCheck() {
        if (!CardScanner.processorSupported()) {
            Log.w("card.io", "- Processor type is not supported");
            return false;
        } else {
            Camera c = null;

            try {
                c = Camera.open();
            } catch (RuntimeException var5) {
                if (VERSION.SDK_INT >= 23) {
                    return true;
                }

                Log.w("card.io", "- Error opening camera: " + var5);
                throw new CameraUnavailableException();
            }

            if (c == null) {
                Log.w("card.io", "- No camera found");
                return false;
            } else {
                List<Size> list = c.getParameters().getSupportedPreviewSizes();
                c.release();
                boolean supportsVGA = false;
                Iterator var3 = list.iterator();

                while(var3.hasNext()) {
                    Size s = (Size)var3.next();
                    if (s.width == 640 && s.height == 480) {
                        supportsVGA = true;
                        break;
                    }
                }

                if (!supportsVGA) {
                    Log.w("card.io", "- Camera resolution is insufficient");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public static String getNativeMemoryStats() {
        return "(free/alloc'd/total)" + Debug.getNativeHeapFreeSize() + "/" + Debug.getNativeHeapAllocatedSize() + "/" + Debug.getNativeHeapSize();
    }

    public static void logNativeMemoryStats() {
        Log.d("MEMORY", "Native memory stats: " + getNativeMemoryStats());
    }

    public static Rect rectGivenCenter(Point center, int width, int height) {
        return new Rect(center.x - width / 2, center.y - height / 2, center.x + width / 2, center.y + height / 2);
    }

    public static void setupTextPaintStyle(Paint paint) {
        paint.setColor(-1);
        paint.setStyle(Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 1));
        paint.setAntiAlias(true);
        float[] black = new float[]{0.0F, 0.0F, 0.0F};
        paint.setShadowLayer(1.5F, 0.5F, 0.0F, Color.HSVToColor(200, black));
    }

    static void writeCapturedCardImageIfNecessary(Intent origIntent, Intent dataIntent, OverlayView mOverlay) {
        if (origIntent.getBooleanExtra("io.card.payment.returnCardImage", false) && mOverlay != null && mOverlay.getBitmap() != null) {
            ByteArrayOutputStream scaledCardBytes = new ByteArrayOutputStream();
            mOverlay.getBitmap().compress(CompressFormat.JPEG, 80, scaledCardBytes);
            dataIntent.putExtra("io.card.payment.capturedCardImage", scaledCardBytes.toByteArray());
        }

    }

    static {
        TORCH_BLACK_LISTED = Build.MODEL.equals("DROID2");
    }
}
