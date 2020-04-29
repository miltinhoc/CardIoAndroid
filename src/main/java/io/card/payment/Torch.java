//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.shapes.RoundRectShape;
import java.util.Arrays;

class Torch {
    private static final String TAG = Torch.class.getSimpleName();
    private boolean mOn = false;
    private float mWidth;
    private float mHeight;

    public Torch(float width, float height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(-this.mWidth / 2.0F, -this.mHeight / 2.0F);
        float cornerRadius = 5.0F;
        Paint borderPaint = new Paint();
        borderPaint.setColor(-16777216);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(1.5F);
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Style.FILL);
        fillPaint.setColor(-1);
        if (this.mOn) {
            fillPaint.setAlpha(192);
        } else {
            fillPaint.setAlpha(96);
        }

        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, cornerRadius);
        RoundRectShape buttonShape = new RoundRectShape(outerRadii, (RectF)null, (float[])null);
        buttonShape.resize(this.mWidth, this.mHeight);
        buttonShape.draw(canvas, fillPaint);
        buttonShape.draw(canvas, borderPaint);
        Paint boltPaint = new Paint();
        boltPaint.setStyle(Style.FILL_AND_STROKE);
        boltPaint.setAntiAlias(true);
        if (this.mOn) {
            boltPaint.setColor(-1);
        } else {
            boltPaint.setColor(-16777216);
        }

        Path boltPath = createBoltPath();
        Matrix m = new Matrix();
        float boltHeight = 0.8F * this.mHeight;
        m.postScale(boltHeight, boltHeight);
        boltPath.transform(m);
        canvas.translate(this.mWidth / 2.0F, this.mHeight / 2.0F);
        canvas.drawPath(boltPath, boltPaint);
        canvas.restore();
    }

    public void setOn(boolean on) {
        this.mOn = on;
    }

    private static Path createBoltPath() {
        Path p = new Path();
        p.moveTo(10.0F, 0.0F);
        p.lineTo(0.0F, 11.0F);
        p.lineTo(6.0F, 11.0F);
        p.lineTo(2.0F, 20.0F);
        p.lineTo(13.0F, 8.0F);
        p.lineTo(7.0F, 8.0F);
        p.lineTo(10.0F, 0.0F);
        p.setLastPoint(10.0F, 0.0F);
        Matrix m = new Matrix();
        m.postTranslate(-6.5F, -10.0F);
        m.postScale(0.05F, 0.05F);
        p.transform(m);
        return p;
    }
}
