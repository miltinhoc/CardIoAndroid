//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.card.payment.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Appearance {
    public static final int[] BUTTON_STATE_PRESSED = new int[]{16842919, 16842910};
    public static final int[] BUTTON_STATE_NORMAL = new int[]{16842910};
    public static final int[] BUTTON_STATE_DISABLED = new int[]{-16842910};
    public static final int[] BUTTON_STATE_FOCUSED = new int[]{16842908};
    public static final int PAY_BLUE_COLOR = Color.parseColor("#003087");
    public static final int PAL_BLUE_COLOR = Color.parseColor("#009CDE");
    public static final int PAL_BLUE_COLOR_OPACITY_66 = Color.parseColor("#aa009CDE");
    public static final Drawable ACTIONBAR_BACKGROUND = new ColorDrawable(Color.parseColor("#717074"));
    public static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#f5f5f5");
    public static final int BUTTON_PRIMARY_NORMAL_COLOR;
    public static final int BUTTON_PRIMARY_FOCUS_COLOR;
    public static final int BUTTON_PRIMARY_PRESSED_COLOR;
    public static final int BUTTON_PRIMARY_DISABLED_COLOR;
    public static final int BUTTON_SECONDARY_NORMAL_COLOR;
    public static final int BUTTON_SECONDARY_FOCUS_COLOR;
    public static final int BUTTON_SECONDARY_PRESSED_COLOR;
    public static final int BUTTON_SECONDARY_DISABLED_COLOR;
    public static final int TEXT_COLOR_LIGHT;
    public static final int TEXT_COLOR_ERROR;
    public static final int TEXT_COLOR_LABEL;
    public static final Typeface TYPEFACE_BUTTON;

    public static Drawable buttonBackgroundPrimary(Context context) {
        StateListDrawable d = new StateListDrawable();
        d.addState(BUTTON_STATE_PRESSED, new ColorDrawable(BUTTON_PRIMARY_PRESSED_COLOR));
        d.addState(BUTTON_STATE_DISABLED, new ColorDrawable(BUTTON_PRIMARY_DISABLED_COLOR));
        d.addState(BUTTON_STATE_FOCUSED, buttonBackgroundPrimaryFocused(context));
        d.addState(BUTTON_STATE_NORMAL, buttonBackgroundPrimaryNormal(context));
        return d;
    }

    private static float getFocusBorderWidthPixels(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        float adjustedwidth = ViewUtil.typedDimensionValueToPixels("4dip", context) / 2.0F * scale;
        return adjustedwidth;
    }

    private static Drawable buttonBackgroundPrimaryNormal(Context context) {
        return buttonNormal(BUTTON_PRIMARY_NORMAL_COLOR, getFocusBorderWidthPixels(context));
    }

    private static Drawable buttonBackgroundPrimaryFocused(Context context) {
        return buttonFocused(BUTTON_PRIMARY_NORMAL_COLOR, BUTTON_PRIMARY_FOCUS_COLOR, getFocusBorderWidthPixels(context));
    }

    public static Drawable buttonBackgroundSecondary(Context context) {
        StateListDrawable d = new StateListDrawable();
        d.addState(BUTTON_STATE_PRESSED, new ColorDrawable(BUTTON_SECONDARY_PRESSED_COLOR));
        d.addState(BUTTON_STATE_DISABLED, new ColorDrawable(BUTTON_SECONDARY_DISABLED_COLOR));
        d.addState(BUTTON_STATE_FOCUSED, buttonBackgroundSecondaryFocused(context));
        d.addState(BUTTON_STATE_NORMAL, buttonBackgroundSecondaryNormal(context));
        return d;
    }

    private static Drawable buttonBackgroundSecondaryNormal(Context context) {
        return buttonNormal(BUTTON_SECONDARY_NORMAL_COLOR, getFocusBorderWidthPixels(context));
    }

    private static Drawable buttonBackgroundSecondaryFocused(Context context) {
        return buttonFocused(BUTTON_SECONDARY_NORMAL_COLOR, BUTTON_SECONDARY_FOCUS_COLOR, getFocusBorderWidthPixels(context));
    }

    private static Drawable buttonNormal(int color, float width) {
        Drawable[] layers = new Drawable[]{new ColorDrawable(color), null};
        ShapeDrawable s = new ShapeDrawable(new RectShape());
        s.getPaint().setStrokeWidth(2.0F * width);
        s.getPaint().setStyle(Style.STROKE);
        s.getPaint().setColor(DEFAULT_BACKGROUND_COLOR);
        layers[1] = s;
        LayerDrawable ld = new LayerDrawable(layers);
        return ld;
    }

    private static Drawable buttonFocused(int backgroundColor, int focusBoxColor, float scaledBorderWidth) {
        Drawable[] layers = new Drawable[]{new ColorDrawable(backgroundColor), null, null};
        ShapeDrawable s = new ShapeDrawable(new RectShape());
        s.getPaint().setStrokeWidth(2.0F * scaledBorderWidth);
        s.getPaint().setStyle(Style.STROKE);
        s.getPaint().setColor(DEFAULT_BACKGROUND_COLOR);
        layers[1] = s;
        ShapeDrawable s2 = new ShapeDrawable(new RectShape());
        s2.getPaint().setStrokeWidth(scaledBorderWidth);
        s2.getPaint().setStyle(Style.STROKE);
        s2.getPaint().setColor(focusBoxColor);
        layers[2] = s2;
        LayerDrawable ld = new LayerDrawable(layers);
        return ld;
    }

    private static Typeface typefaceLight() {
        return Typeface.create("sans-serif-light", 0);
    }

    static {
        BUTTON_PRIMARY_NORMAL_COLOR = PAL_BLUE_COLOR;
        BUTTON_PRIMARY_FOCUS_COLOR = PAL_BLUE_COLOR_OPACITY_66;
        BUTTON_PRIMARY_PRESSED_COLOR = PAY_BLUE_COLOR;
        BUTTON_PRIMARY_DISABLED_COLOR = Color.parseColor("#c5ddeb");
        BUTTON_SECONDARY_NORMAL_COLOR = Color.parseColor("#717074");
        BUTTON_SECONDARY_FOCUS_COLOR = Color.parseColor("#aa717074");
        BUTTON_SECONDARY_PRESSED_COLOR = Color.parseColor("#5a5a5d");
        BUTTON_SECONDARY_DISABLED_COLOR = Color.parseColor("#f5f5f5");
        TEXT_COLOR_LIGHT = Color.parseColor("#515151");
        TEXT_COLOR_ERROR = Color.parseColor("#b32317");
        TEXT_COLOR_LABEL = TEXT_COLOR_LIGHT;
        TYPEFACE_BUTTON = typefaceLight();
    }
}