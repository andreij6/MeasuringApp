package com.creativejones.andre.measureapp;

import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;

public class Tick {

    private float mPixels;
    private boolean mIsInch;
    private int mLength;
    private int mFormatString;
    private int mDenominator;
    private Paint mPaint;

    public float getPixels() {
        return mPixels;
    }

    public boolean isInch() {
        return mIsInch;
    }

    public void setPixels(float pixels) {
        mPixels = pixels;
    }

    public void setIsInch(boolean isInch) {
        mIsInch = isInch;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getLength() {
        return mLength;
    }

    public void setFormatString(int formatString) {
        mFormatString = formatString;
    }

    public boolean shouldDrawText(int counter) {
        if(mIsInch){
            return counter != 0;
        }

        if (!canBeReduced(counter)) {
            return true;
        }

        return false;
    }


    boolean canBeReduced(int counter) {
        int numerator = counter + 1;

        return (numerator % 2 == 0);
    }

    public int getFormatString() {
        return mFormatString;
    }

    public static float convertDpToPixel(float dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float convertPxFromDp(final float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public void setDenominator(int denominator) {
        mDenominator = denominator;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public int getDenominator() {
        return mDenominator;
    }

    public int getNumerator(int counter) {
        if(mIsInch){
            return counter;
        }

        int numerator = counter + 1;

        if(numerator > mDenominator){
            return numerator % mDenominator;
        } else {
            return numerator;
        }
    }


    public static class Builder {

        private float mDP;
        private int mLength;
        private int mFormatString;
        private int mDenominator;
        private Paint mPaint;

        public Tick build() {
            Tick result = new Tick();
            result.setIsInch(mDP == 160f);
            result.setPixels(convertDpToPixel(mDP));
            result.setLength(mLength);
            result.setFormatString(mFormatString);
            result.setDenominator(mDenominator);
            result.setPaint(mPaint);
            return result;
        }

        public Builder dps(float DP) {
            mDP = DP;
            return this;
        }

        public Builder length(int length) {
            mLength = length;
            return this;
        }

        public Builder string(int string_format) {
            mFormatString = string_format;
            return this;
        }

        public Builder denominator(int denominator) {
            mDenominator = denominator;
            return this;
        }

        public Builder textPaint(Paint textPaint) {
            mPaint = textPaint;
            return this;
        }
    }
}
