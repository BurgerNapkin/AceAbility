package com.somedomain.aceability;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {

    static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static GradientDrawable makeSquare(int width, int height, DisplayMetrics metrics) {
        GradientDrawable gd = new GradientDrawable();
        gd.setSize(width, height);
        gd.setShape(GradientDrawable.RECTANGLE);

        int stroke = (int) (4*metrics.density);
        gd.setStroke(stroke, Color.BLACK);
        gd.setColor(Color.argb(0, 0, 0, 0));
        int radius = (int) (6 * metrics.density);
        gd.setCornerRadii(new float[]{radius,radius,radius,radius,radius,radius,radius,radius});
        return gd;
    }
}
