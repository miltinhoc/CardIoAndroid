//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import io.card.payment.i18n.LocalizedStrings;
import io.card.payment.i18n.StringKey;
import java.lang.ref.WeakReference;

class OverlayView extends View {
    private static final String TAG = OverlayView.class.getSimpleName();
    private static final Orientation[] GRADIENT_ORIENTATIONS;
    private final WeakReference<CardIOActivity> mScanActivityRef;
    private DetectionInfo mDInfo;
    private Bitmap mBitmap;
    private Rect mGuide;
    private CreditCard mDetectedCard;
    private int mRotation;
    private int mState;
    private int guideColor;
    private boolean hideCardIOLogo;
    private String scanInstructions;
    private GradientDrawable mGradientDrawable;
    private final Paint mGuidePaint;
    private final Paint mLockedBackgroundPaint;
    private Path mLockedBackgroundPath;
    private Rect mCameraPreviewRect;
    private final Torch mTorch;
    private final Logo mLogo;
    private Rect mTorchRect;
    private Rect mLogoRect;
    private final boolean mShowTorch;
    private int mRotationFlip;
    private float mScale = 1.0F;

    public OverlayView(CardIOActivity captureActivity, AttributeSet attributeSet, boolean showTorch) {
        super(captureActivity, attributeSet);
        this.mShowTorch = showTorch;
        this.mScanActivityRef = new WeakReference(captureActivity);
        this.mRotationFlip = 1;
        this.mScale = this.getResources().getDisplayMetrics().density / 1.5F;
        this.mTorch = new Torch(70.0F * this.mScale, 50.0F * this.mScale);
        this.mLogo = new Logo(captureActivity);
        this.mGuidePaint = new Paint(1);
        this.mLockedBackgroundPaint = new Paint(1);
        this.mLockedBackgroundPaint.clearShadowLayer();
        this.mLockedBackgroundPaint.setStyle(Style.FILL);
        this.mLockedBackgroundPaint.setColor(-1157627904);
        this.scanInstructions = LocalizedStrings.getString(StringKey.SCAN_GUIDE);
    }

    public void setGuideColor(int color) {
        this.guideColor = color;
    }

    public void setHideCardIOLogo(boolean hide) {
        this.hideCardIOLogo = hide;
    }

    public void setScanInstructions(String scanInstructions) {
        this.scanInstructions = scanInstructions;
    }

    public void setGuideAndRotation(Rect rect, int rotation) {
        this.mRotation = rotation;
        this.mGuide = rect;
        this.invalidate();
        Point topEdgeUIOffset;
        if (this.mRotation % 180 != 0) {
            topEdgeUIOffset = new Point((int)(40.0F * this.mScale), (int)(60.0F * this.mScale));
            this.mRotationFlip = -1;
        } else {
            topEdgeUIOffset = new Point((int)(60.0F * this.mScale), (int)(40.0F * this.mScale));
            this.mRotationFlip = 1;
        }

        if (this.mCameraPreviewRect != null) {
            Point torchPoint = new Point(this.mCameraPreviewRect.left + topEdgeUIOffset.x, this.mCameraPreviewRect.top + topEdgeUIOffset.y);
            this.mTorchRect = Util.rectGivenCenter(torchPoint, (int)(70.0F * this.mScale), (int)(50.0F * this.mScale));
            Point logoPoint = new Point(this.mCameraPreviewRect.right - topEdgeUIOffset.x, this.mCameraPreviewRect.top + topEdgeUIOffset.y);
            this.mLogoRect = Util.rectGivenCenter(logoPoint, (int)(100.0F * this.mScale), (int)(50.0F * this.mScale));
            int[] gradientColors = new int[]{-1, -16777216};
            Orientation gradientOrientation = GRADIENT_ORIENTATIONS[this.mRotation / 90 % 4];
            this.mGradientDrawable = new GradientDrawable(gradientOrientation, gradientColors);
            this.mGradientDrawable.setGradientType(0);
            this.mGradientDrawable.setBounds(this.mGuide);
            this.mGradientDrawable.setAlpha(50);
            this.mLockedBackgroundPath = new Path();
            this.mLockedBackgroundPath.addRect(new RectF(this.mCameraPreviewRect), Direction.CW);
            this.mLockedBackgroundPath.addRect(new RectF(this.mGuide), Direction.CCW);
        }

    }

    public void setBitmap(Bitmap bitmap) {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }

        this.mBitmap = bitmap;
        if (this.mBitmap != null) {
            this.decorateBitmap();
        }

    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setDetectionInfo(DetectionInfo dinfo) {
        if (this.mDInfo != null && !this.mDInfo.sameEdgesAs(dinfo)) {
            this.invalidate();
        }

        this.mDInfo = dinfo;
    }

    public Bitmap getCardImage() {
        return this.mBitmap != null && !this.mBitmap.isRecycled() ? Bitmap.createBitmap(this.mBitmap, 0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight()) : null;
    }

    private Rect guideStrokeRect(int x1, int y1, int x2, int y2) {
        int t2 = (int)(8.0F * this.mScale);
        Rect r = new Rect();
        r.left = Math.min(x1, x2) - t2;
        r.right = Math.max(x1, x2) + t2;
        r.top = Math.min(y1, y2) - t2;
        r.bottom = Math.max(y1, y2) + t2;
        return r;
    }

    public void onDraw(Canvas canvas) {
        if (this.mGuide != null && this.mCameraPreviewRect != null) {
            canvas.save();
            this.mGradientDrawable.draw(canvas);
            int tickLength;
            if (this.mRotation != 0 && this.mRotation != 180) {
                tickLength = (this.mGuide.right - this.mGuide.left) / 4;
            } else {
                tickLength = (this.mGuide.bottom - this.mGuide.top) / 4;
            }

            if (this.mDInfo != null && this.mDInfo.numVisibleEdges() == 4) {
                canvas.drawPath(this.mLockedBackgroundPath, this.mLockedBackgroundPaint);
            }

            this.mGuidePaint.clearShadowLayer();
            this.mGuidePaint.setStyle(Style.FILL);
            this.mGuidePaint.setColor(this.guideColor);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.top, this.mGuide.left + tickLength, this.mGuide.top), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.top, this.mGuide.left, this.mGuide.top + tickLength), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.right, this.mGuide.top, this.mGuide.right - tickLength, this.mGuide.top), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.right, this.mGuide.top, this.mGuide.right, this.mGuide.top + tickLength), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.bottom, this.mGuide.left + tickLength, this.mGuide.bottom), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.bottom, this.mGuide.left, this.mGuide.bottom - tickLength), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.right, this.mGuide.bottom, this.mGuide.right - tickLength, this.mGuide.bottom), this.mGuidePaint);
            canvas.drawRect(this.guideStrokeRect(this.mGuide.right, this.mGuide.bottom, this.mGuide.right, this.mGuide.bottom - tickLength), this.mGuidePaint);
            if (this.mDInfo != null) {
                if (this.mDInfo.topEdge) {
                    canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.top, this.mGuide.right, this.mGuide.top), this.mGuidePaint);
                }

                if (this.mDInfo.bottomEdge) {
                    canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.bottom, this.mGuide.right, this.mGuide.bottom), this.mGuidePaint);
                }

                if (this.mDInfo.leftEdge) {
                    canvas.drawRect(this.guideStrokeRect(this.mGuide.left, this.mGuide.top, this.mGuide.left, this.mGuide.bottom), this.mGuidePaint);
                }

                if (this.mDInfo.rightEdge) {
                    canvas.drawRect(this.guideStrokeRect(this.mGuide.right, this.mGuide.top, this.mGuide.right, this.mGuide.bottom), this.mGuidePaint);
                }

                if (this.mDInfo.numVisibleEdges() < 3) {
                    float guideHeight = 34.0F * this.mScale;
                    float guideFontSize = 26.0F * this.mScale;
                    Util.setupTextPaintStyle(this.mGuidePaint);
                    this.mGuidePaint.setTextAlign(Align.CENTER);
                    this.mGuidePaint.setTextSize(guideFontSize);
                    canvas.translate((float)(this.mGuide.left + this.mGuide.width() / 2), (float)(this.mGuide.top + this.mGuide.height() / 2));
                    canvas.rotate((float)(this.mRotationFlip * this.mRotation));
                    if (this.scanInstructions != null && this.scanInstructions != "") {
                        String[] lines = this.scanInstructions.split("\n");
                        float y = -((guideHeight * (float)(lines.length - 1) - guideFontSize) / 2.0F) - 3.0F;

                        for(int i = 0; i < lines.length; ++i) {
                            canvas.drawText(lines[i], 0.0F, y, this.mGuidePaint);
                            y += guideHeight;
                        }
                    }
                }
            }

            canvas.restore();
            if (!this.hideCardIOLogo) {
                canvas.save();
                canvas.translate(this.mLogoRect.exactCenterX(), this.mLogoRect.exactCenterY());
                canvas.rotate((float)(this.mRotationFlip * this.mRotation));
                this.mLogo.draw(canvas, 100.0F * this.mScale, 50.0F * this.mScale);
                canvas.restore();
            }

            if (this.mShowTorch) {
                canvas.save();
                canvas.translate(this.mTorchRect.exactCenterX(), this.mTorchRect.exactCenterY());
                canvas.rotate((float)(this.mRotationFlip * this.mRotation));
                this.mTorch.draw(canvas);
                canvas.restore();
            }

        }
    }

    public void setDetectedCard(CreditCard creditCard) {
        this.mDetectedCard = creditCard;
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            int action = event.getAction() & 255;
            if (action == 0) {
                Point p = new Point((int)event.getX(), (int)event.getY());
                Rect r = Util.rectGivenCenter(p, 20, 20);
                if (this.mShowTorch && this.mTorchRect != null && Rect.intersects(this.mTorchRect, r)) {
                    ((CardIOActivity)this.mScanActivityRef.get()).toggleFlash();
                } else {
                    ((CardIOActivity)this.mScanActivityRef.get()).triggerAutoFocus();
                }
            }
        } catch (NullPointerException var5) {
            Log.d(TAG, "NullPointerException caught in onTouchEvent method");
        }

        return false;
    }

    private void decorateBitmap() {
        RectF roundedRect = new RectF(2.0F, 2.0F, (float)(this.mBitmap.getWidth() - 2), (float)(this.mBitmap.getHeight() - 2));
        float cornerRadius = (float)this.mBitmap.getHeight() * 0.06666667F;
        Bitmap maskBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(maskBitmap);
        maskCanvas.drawColor(0);
        Paint maskPaint = new Paint(1);
        maskPaint.setColor(-16777216);
        maskPaint.setStyle(Style.FILL);
        maskCanvas.drawRoundRect(roundedRect, cornerRadius, cornerRadius, maskPaint);
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        Canvas canvas = new Canvas(this.mBitmap);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawBitmap(maskBitmap, 0.0F, 0.0F, paint);
        paint.setXfermode((Xfermode)null);
        maskBitmap.recycle();
    }

    public void markupCard() {
        if (this.mBitmap != null) {
            if (this.mDetectedCard.flipped) {
                Matrix m = new Matrix();
                m.setRotate(180.0F, (float)(this.mBitmap.getWidth() / 2), (float)(this.mBitmap.getHeight() / 2));
                this.mBitmap = Bitmap.createBitmap(this.mBitmap, 0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight(), m, false);
            }

            Canvas bc = new Canvas(this.mBitmap);
            Paint paint = new Paint();
            Util.setupTextPaintStyle(paint);
            paint.setTextSize(28.0F * this.mScale);
            int len = this.mDetectedCard.cardNumber.length();
            float sf = (float)this.mBitmap.getWidth() / 428.0F;
            int yOffset = (int)((float)this.mDetectedCard.yoff * sf - 6.0F);

            for(int i = 0; i < len; ++i) {
                int xOffset = (int)((float)this.mDetectedCard.xoff[i] * sf);
                bc.drawText("" + this.mDetectedCard.cardNumber.charAt(i), (float)xOffset, (float)yOffset, paint);
            }

        }
    }

    public boolean isAnimating() {
        return this.mState != 0;
    }

    public void setCameraPreviewRect(Rect rect) {
        this.mCameraPreviewRect = rect;
    }

    public void setTorchOn(boolean b) {
        this.mTorch.setOn(b);
        this.invalidate();
    }

    public void setUseCardIOLogo(boolean useCardIOLogo) {
        this.mLogo.loadLogo(useCardIOLogo);
    }

    public Rect getTorchRect() {
        return this.mTorchRect;
    }

    static {
        GRADIENT_ORIENTATIONS = new Orientation[]{Orientation.TOP_BOTTOM, Orientation.LEFT_RIGHT, Orientation.BOTTOM_TOP, Orientation.RIGHT_LEFT};
    }
}
