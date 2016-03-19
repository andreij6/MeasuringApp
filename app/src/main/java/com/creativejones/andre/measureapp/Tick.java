package com.creativejones.andre.measureapp;

import android.graphics.Paint;

public class Tick {

    private float mPixels;
    private boolean mIsInch;
    private int mLength;
    private int mFormatString;
    private int mDenominator;
    private Paint mPaint;

    //region Getters & Setters
    public float getPixels() {
        return mPixels;
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

    public int getFormatString() {
        return mFormatString;
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

    //endregion

    public static class Builder {

        private float mDP;
        private int mLength;
        private int mFormatString;
        private int mDenominator;
        private Paint mPaint;

        public Tick build() {
            Tick result = new Tick();
            result.setIsInch(mDP == 160f);
            result.setPixels(Utils.convertDpToPixel(mDP));
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
