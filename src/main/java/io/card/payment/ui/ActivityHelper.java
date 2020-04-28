package io.card.payment.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.widget.TextView;

public class ActivityHelper {
    @TargetApi(11)
    public static void addActionBarIfSupported(Activity activity) {
        if (actionBarSupported()) {
            activity.requestWindowFeature(8);
        }

    }

    public static void setupActionBarIfSupported(Activity activity, TextView titleTextView, String title, String titleTextViewPrefix, Drawable icon) {
        if (titleTextViewPrefix == null) {
            titleTextViewPrefix = "";
        }

        activity.setTitle(titleTextViewPrefix + title);
        if (actionBarSupported() && actionBarNonNull(activity)) {
            setupActionBar(activity, title, icon);
            if (titleTextView != null) {
                titleTextView.setVisibility(8);
            }
        } else if (titleTextView != null) {
            titleTextView.setText(title);
        }

    }

    @TargetApi(11)
    private static boolean actionBarNonNull(Activity activity) {
        return activity.getActionBar() != null;
    }

    @TargetApi(11)
    private static void setupActionBar(Activity activity, String title, Drawable icon) {
        ActionBar bar = activity.getActionBar();
        bar.setBackgroundDrawable(Appearance.ACTIONBAR_BACKGROUND);
        bar.setTitle(title);
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextView = (TextView)activity.findViewById(actionBarTitleId);
        if (actionBarTextView != null) {
            actionBarTextView.setTextColor(-1);
        }

        bar.setDisplayHomeAsUpEnabled(false);
        if (icon != null && VERSION.SDK_INT >= 14) {
            setActionBarHomeIcon(bar, icon);
        } else {
            bar.setDisplayShowHomeEnabled(false);
        }

    }

    private static boolean actionBarSupported() {
        return VERSION.SDK_INT >= 11;
    }

    @TargetApi(14)
    private static void setActionBarHomeIcon(ActionBar bar, Drawable icon) {
        bar.setIcon(icon);
    }

    public static boolean holoSupported() {
        return VERSION.SDK_INT >= 11;
    }

    @TargetApi(11)
    public static void setActivityTheme(Activity activity, boolean useApplicationTheme) {
        if (useApplicationTheme && 0 != activity.getApplicationInfo().theme) {
            activity.setTheme(activity.getApplicationInfo().theme);
        } else if (holoSupported()) {
            activity.setTheme(16973934);
        } else {
            activity.setTheme(16973836);
        }

    }

    @TargetApi(11)
    public static void setFlagSecure(Activity activity) {
        if (VERSION.SDK_INT >= 11) {
            activity.getWindow().addFlags(8192);
        }

    }
}
