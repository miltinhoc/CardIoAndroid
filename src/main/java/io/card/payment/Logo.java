//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
//import io.card.payment.R.drawable;

class Logo {
    private final Paint mPaint = new Paint();
    private Bitmap mLogo;
    private boolean mUseCardIOLogo;
    private final Context mContext;

    public Logo(Context context) {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setAlpha(100);
        this.mLogo = null;
        this.mContext = context;
    }

    void loadLogo(boolean useCardIOLogo) {
        if (this.mLogo == null || useCardIOLogo != this.mUseCardIOLogo) {
            this.mUseCardIOLogo = useCardIOLogo;
            if (useCardIOLogo) {
                //this.mLogo = BitmapFactory.decodeResource(this.mContext.getResources(), drawable.cio_card_io_logo);
            } else {
                //this.mLogo = BitmapFactory.decodeResource(this.mContext.getResources(), drawable.cio_paypal_logo);
            }

        }
    }

    public void draw(Canvas canvas, float maxWidth, float maxHeight) {
        if (this.mLogo == null) {
            this.loadLogo(false);
        }

        canvas.save();
        float targetAspectRatio = (float)this.mLogo.getHeight() / (float)this.mLogo.getWidth();
        float drawWidth;
        float drawHeight;
        if (maxHeight / maxWidth < targetAspectRatio) {
            drawHeight = maxHeight;
            drawWidth = maxHeight / targetAspectRatio;
        } else {
            drawWidth = maxWidth;
            drawHeight = maxWidth * targetAspectRatio;
        }

        float halfWidth = drawWidth / 2.0F;
        float halfHeight = drawHeight / 2.0F;
        canvas.drawBitmap(this.mLogo, new Rect(0, 0, this.mLogo.getWidth(), this.mLogo.getHeight()), new RectF(-halfWidth, -halfHeight, halfWidth, halfHeight), this.mPaint);
        canvas.restore();
    }
}
