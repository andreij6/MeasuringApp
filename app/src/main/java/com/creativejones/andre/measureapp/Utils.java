package com.creativejones.andre.measureapp;

import android.content.res.Resources;

public class Utils {

    public static float convertDpToPixel(float dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
