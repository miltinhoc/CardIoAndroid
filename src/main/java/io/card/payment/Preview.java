//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

class Preview extends ViewGroup {
    private static final String TAG = Preview.class.getSimpleName();
    private int mPreviewWidth;
    private int mPreviewHeight;
    SurfaceView mSurfaceView;

    public Preview(Context context, AttributeSet attributeSet, int previewWidth, int previewHeight) {
        super(context, attributeSet);
        this.mPreviewWidth = previewHeight;
        this.mPreviewHeight = previewWidth;
        this.mSurfaceView = new SurfaceView(context);
        this.addView(this.mSurfaceView);
    }

    public SurfaceView getSurfaceView() {
        assert this.mSurfaceView != null;

        return this.mSurfaceView;
    }

    SurfaceHolder getSurfaceHolder() {
        SurfaceHolder holder = this.getSurfaceView().getHolder();

        assert holder != null;

        return holder;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 0, 0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(this.getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(this.getSuggestedMinimumHeight(), heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && this.getChildCount() > 0) {
            assert this.mSurfaceView != null;

            int width = r - l;
            int height = b - t;
            int scaledChildWidth;
            if (width * this.mPreviewHeight > height * this.mPreviewWidth) {
                scaledChildWidth = this.mPreviewWidth * height / this.mPreviewHeight;
                this.mSurfaceView.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                scaledChildWidth = this.mPreviewHeight * width / this.mPreviewWidth;
                this.mSurfaceView.layout(0, (height - scaledChildWidth) / 2, width, (height + scaledChildWidth) / 2);
            }
        }

    }
}
