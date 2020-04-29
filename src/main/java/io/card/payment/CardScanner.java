//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

class CardScanner implements AutoFocusCallback, PreviewCallback, Callback {
    private static final String TAG = CardScanner.class.getSimpleName();
    private Bitmap detectedBitmap;
    private static boolean manualFallbackForError;
    protected WeakReference<CardIOActivity> mScanActivityRef;
    private boolean mSuppressScan = false;
    private boolean mScanExpiry;
    private int mUnblurDigits = -1;
    final int mPreviewWidth = 640;
    final int mPreviewHeight = 480;
    private int mFrameOrientation = 1;
    private boolean mFirstPreviewFrame = true;
    private long captureStart;
    private long mAutoFocusStartedAt;
    private long mAutoFocusCompletedAt;
    private Camera mCamera;
    private byte[] mPreviewBuffer;
    protected boolean useCamera = true;
    private boolean isSurfaceValid;
    private int numManualRefocus;
    private int numAutoRefocus;
    private int numManualTorchChange;
    private int numFramesSkipped;
    private static boolean processingInProgress;

    public static native boolean nUseNeon();

    public static native boolean nUseTegra();

    public static native boolean nUseX86();

    private native void nSetup(boolean var1, float var2);

    private native void nSetup(boolean var1, float var2, int var3);

    private native void nResetAnalytics();

    private native void nGetGuideFrame(int var1, int var2, int var3, Rect var4);

    private native void nScanFrame(byte[] var1, int var2, int var3, int var4, DetectionInfo var5, Bitmap var6, boolean var7);

    private native int nGetNumFramesScanned();

    private native void nCleanup();

    private static void loadLibrary(String libraryName) throws UnsatisfiedLinkError {
        try {
            System.loadLibrary(libraryName);
        } catch (UnsatisfiedLinkError var4) {
            String altLibsPath = CardIONativeLibsConfig.getAlternativeLibsPath();
            if (altLibsPath == null || altLibsPath.length() == 0) {
                throw var4;
            }

            if (!File.separator.equals(altLibsPath.charAt(altLibsPath.length() - 1))) {
                altLibsPath = altLibsPath + File.separator;
            }

            String fullPath = altLibsPath + Build.CPU_ABI + File.separator + System.mapLibraryName(libraryName);
            Log.d("card.io", "loadLibrary failed for library " + libraryName + ". Trying " + fullPath);
            System.load(fullPath);
        }

    }

    private static boolean usesSupportedProcessorArch() {
        return nUseNeon() || nUseTegra() || nUseX86();
    }

    static boolean processorSupported() {
        return !manualFallbackForError && usesSupportedProcessorArch();
    }

    CardScanner(CardIOActivity scanActivity, int currentFrameOrientation) {
        Intent scanIntent = scanActivity.getIntent();
        if (scanIntent != null) {
            this.mSuppressScan = scanIntent.getBooleanExtra("io.card.payment.suppressScan", false);
            this.mScanExpiry = scanIntent.getBooleanExtra("io.card.payment.requireExpiry", false) && scanIntent.getBooleanExtra("io.card.payment.scanExpiry", true);
            this.mUnblurDigits = scanIntent.getIntExtra("io.card.payment.unblurDigits", -1);
        }

        this.mScanActivityRef = new WeakReference(scanActivity);
        this.mFrameOrientation = currentFrameOrientation;
        this.nSetup(this.mSuppressScan, 6.0F, this.mUnblurDigits);
    }

    private Camera connectToCamera(int checkInterval, int maxTimeout) {
        long start = System.currentTimeMillis();
        if (this.useCamera) {
            do {
                try {
                    return Camera.open();
                } catch (RuntimeException var8) {
                    try {
                        Log.w("card.io", "Wasn't able to connect to camera service. Waiting and trying again...");
                        Thread.sleep((long)checkInterval);
                    } catch (InterruptedException var7) {
                        Log.e("card.io", "Interrupted while waiting for camera", var7);
                    }
                } catch (Exception var9) {
                    Log.e("card.io", "Unexpected exception. Please report it as a GitHub issue", var9);
                    maxTimeout = 0;
                }
            } while(System.currentTimeMillis() - start < (long)maxTimeout);
        }

        Log.w(TAG, "camera connect timeout");
        return null;
    }

    void prepareScanner() {
        this.mFirstPreviewFrame = true;
        this.mAutoFocusStartedAt = 0L;
        this.mAutoFocusCompletedAt = 0L;
        this.numManualRefocus = 0;
        this.numAutoRefocus = 0;
        this.numManualTorchChange = 0;
        this.numFramesSkipped = 0;
        if (this.useCamera && this.mCamera == null) {
            this.mCamera = this.connectToCamera(50, 5000);
            if (this.mCamera == null) {
                Log.e("card.io", "prepare scanner couldn't connect to camera!");
                return;
            }

            this.setCameraDisplayOrientation(this.mCamera);
            Parameters parameters = this.mCamera.getParameters();
            List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            if (supportedPreviewSizes != null) {
                Size previewSize = null;
                Iterator var4 = supportedPreviewSizes.iterator();

                while(var4.hasNext()) {
                    Size s = (Size)var4.next();
                    if (s.width == 640 && s.height == 480) {
                        previewSize = s;
                        break;
                    }
                }

                if (previewSize == null) {
                    Log.w("card.io", "Didn't find a supported 640x480 resolution, so forcing");
                    previewSize = (Size)supportedPreviewSizes.get(0);
                    previewSize.width = 640;
                    previewSize.height = 480;
                }
            }

            parameters.setPreviewSize(640, 480);
            this.mCamera.setParameters(parameters);
        } else if (!this.useCamera) {
            Log.w(TAG, "useCamera is false!");
        } else if (this.mCamera != null) {
            Log.v(TAG, "we already have a camera instance: " + this.mCamera);
        }

        if (this.detectedBitmap == null) {
            this.detectedBitmap = Bitmap.createBitmap(428, 270, Config.ARGB_8888);
        }

    }

    boolean resumeScanning(SurfaceHolder holder) {
        if (this.mCamera == null) {
            this.prepareScanner();
        }

        if (this.useCamera && this.mCamera == null) {
            Log.i(TAG, "null camera. failure");
            return false;
        } else {
            assert holder != null;

            if (this.useCamera && this.mPreviewBuffer == null) {
                Parameters parameters = this.mCamera.getParameters();
                int previewFormat = parameters.getPreviewFormat();
                int bytesPerPixel = ImageFormat.getBitsPerPixel(previewFormat) / 8;
                int bufferSize = 307200 * bytesPerPixel * 3;
                this.mPreviewBuffer = new byte[bufferSize];
                this.mCamera.addCallbackBuffer(this.mPreviewBuffer);
            }

            holder.addCallback(this);
            holder.setType(3);
            if (this.useCamera) {
                this.mCamera.setPreviewCallbackWithBuffer(this);
            }

            if (this.isSurfaceValid) {
                this.makePreviewGo(holder);
            }

            this.setFlashOn(false);
            this.captureStart = System.currentTimeMillis();
            this.nResetAnalytics();
            return true;
        }
    }

    public void pauseScanning() {
        this.setFlashOn(false);
        if (this.mCamera != null) {
            try {
                this.mCamera.stopPreview();
                this.mCamera.setPreviewDisplay((SurfaceHolder)null);
            } catch (IOException var2) {
                Log.w("card.io", "can't stop preview display", var2);
            }

            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.release();
            this.mPreviewBuffer = null;
            this.mCamera = null;
        }

    }

    public void endScanning() {
        if (this.mCamera != null) {
            this.pauseScanning();
        }

        this.nCleanup();
        this.mPreviewBuffer = null;
    }

    private boolean makePreviewGo(SurfaceHolder holder) {
        assert holder != null;

        assert holder.getSurface() != null;

        this.mFirstPreviewFrame = true;
        if (this.useCamera) {
            try {
                this.mCamera.setPreviewDisplay(holder);
            } catch (IOException var4) {
                Log.e("card.io", "can't set preview display", var4);
                return false;
            }

            try {
                this.mCamera.startPreview();
                this.mCamera.autoFocus(this);
            } catch (RuntimeException var3) {
                Log.e("card.io", "startPreview failed on camera. Error: ", var3);
                return false;
            }
        }

        return true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.mCamera == null && this.useCamera) {
            Log.wtf("card.io", "CardScanner.surfaceCreated() - camera is null!");
        } else {
            this.isSurfaceValid = true;
            this.makePreviewGo(holder);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, String.format("Preview.surfaceChanged(holder?:%b, f:%d, w:%d, h:%d )", holder != null, format, width, height));
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mCamera != null) {
            try {
                this.mCamera.stopPreview();
            } catch (Exception var3) {
                Log.e("card.io", "error stopping camera", var3);
            }
        }

        this.isSurfaceValid = false;
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null) {
            Log.w(TAG, "frame is null! skipping");
        } else if (processingInProgress) {
            Log.e(TAG, "processing in progress.... dropping frame");
            ++this.numFramesSkipped;
            if (camera != null) {
                camera.addCallbackBuffer(data);
            }

        } else {
            processingInProgress = true;
            if (this.mFirstPreviewFrame) {
                this.mFirstPreviewFrame = false;
                this.mFrameOrientation = 1;
                ((CardIOActivity)this.mScanActivityRef.get()).onFirstFrame(1);
            }

            DetectionInfo dInfo = new DetectionInfo();
            this.nScanFrame(data, 640, 480, this.mFrameOrientation, dInfo, this.detectedBitmap, this.mScanExpiry);
            boolean sufficientFocus = dInfo.focusScore >= 6.0F;
            if (!sufficientFocus) {
                this.triggerAutoFocus(false);
            } else if (dInfo.predicted() || this.mSuppressScan && dInfo.detected()) {
                ((CardIOActivity)this.mScanActivityRef.get()).onCardDetected(this.detectedBitmap, dInfo);
            }

            if (camera != null) {
                camera.addCallbackBuffer(data);
            }

            processingInProgress = false;
        }
    }

    void onEdgeUpdate(DetectionInfo dInfo) {
        ((CardIOActivity)this.mScanActivityRef.get()).onEdgeUpdate(dInfo);
    }

    Rect getGuideFrame(int orientation, int previewWidth, int previewHeight) {
        Rect r = null;
        if (processorSupported()) {
            r = new Rect();
            this.nGetGuideFrame(orientation, previewWidth, previewHeight, r);
        }

        return r;
    }

    Rect getGuideFrame(int width, int height) {
        return this.getGuideFrame(this.mFrameOrientation, width, height);
    }

    void setDeviceOrientation(int orientation) {
        this.mFrameOrientation = orientation;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        this.mAutoFocusCompletedAt = System.currentTimeMillis();
    }

    boolean isAutoFocusing() {
        return this.mAutoFocusCompletedAt < this.mAutoFocusStartedAt;
    }

    void triggerAutoFocus(boolean isManual) {
        if (this.useCamera && !this.isAutoFocusing()) {
            try {
                this.mAutoFocusStartedAt = System.currentTimeMillis();
                this.mCamera.autoFocus(this);
                if (isManual) {
                    ++this.numManualRefocus;
                } else {
                    ++this.numAutoRefocus;
                }
            } catch (RuntimeException var3) {
                Log.w(TAG, "could not trigger auto focus: " + var3);
            }
        }

    }

    public boolean isFlashOn() {
        if (!this.useCamera) {
            return false;
        } else {
            Parameters params = this.mCamera.getParameters();
            return params.getFlashMode().equals("torch");
        }
    }

    public boolean setFlashOn(boolean b) {
        if (this.mCamera != null) {
            try {
                Parameters params = this.mCamera.getParameters();
                params.setFlashMode(b ? "torch" : "off");
                this.mCamera.setParameters(params);
                ++this.numManualTorchChange;
                return true;
            } catch (RuntimeException var3) {
                Log.w(TAG, "Could not set flash mode: " + var3);
            }
        }

        return false;
    }

    private void setCameraDisplayOrientation(Camera mCamera) {
        int result;
        if (VERSION.SDK_INT >= 21) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(0, info);
            int degrees = this.getRotationalOffset();
            int cameraOrientation = info.orientation;
            result = (cameraOrientation - degrees + 360) % 360;
        } else {
            result = 90;
        }

        mCamera.setDisplayOrientation(result);
    }

    int getRotationalOffset() {
        int naturalOrientation = ((WindowManager)((CardIOActivity)this.mScanActivityRef.get()).getSystemService("window")).getDefaultDisplay().getRotation();
        short rotationOffset;
        if (naturalOrientation == 0) {
            rotationOffset = 0;
        } else if (naturalOrientation == 1) {
            rotationOffset = 90;
        } else if (naturalOrientation == 2) {
            rotationOffset = 180;
        } else if (naturalOrientation == 3) {
            rotationOffset = 270;
        } else {
            rotationOffset = 0;
        }

        return rotationOffset;
    }

    static {
        Log.i("card.io", "card.io 5.5.1 03/17/2017 14:23:12 -0400");

        try {
            loadLibrary("cardioDecider");
            Log.d("card.io", "Loaded card.io decider library.");
            Log.d("card.io", "    nUseNeon(): " + nUseNeon());
            Log.d("card.io", "    nUseTegra():" + nUseTegra());
            Log.d("card.io", "    nUseX86():  " + nUseX86());
            if (usesSupportedProcessorArch()) {
                loadLibrary("opencv_core");
                Log.d("card.io", "Loaded opencv core library");
                loadLibrary("opencv_imgproc");
                Log.d("card.io", "Loaded opencv imgproc library");
            }

            if (nUseNeon()) {
                loadLibrary("cardioRecognizer");
                Log.i("card.io", "Loaded card.io NEON library");
            } else if (nUseX86()) {
                loadLibrary("cardioRecognizer");
                Log.i("card.io", "Loaded card.io x86 library");
            } else if (nUseTegra()) {
                loadLibrary("cardioRecognizer_tegra2");
                Log.i("card.io", "Loaded card.io Tegra2 library");
            } else {
                Log.w("card.io", "unsupported processor - card.io scanning requires ARMv7 or x86 architecture");
                manualFallbackForError = true;
            }
        } catch (UnsatisfiedLinkError var2) {
            String error = "Failed to load native library: " + var2.getMessage();
            Log.e("card.io", error);
            manualFallbackForError = true;
        }

        processingInProgress = false;
    }
}