package com.campingfun.vacancyhunter.campsitehunter.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wayliu on 8/30/2014.
 */
public class ToastHelper {

    public static void showToast(Context context, String text) {
        if (context != null) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }
}
