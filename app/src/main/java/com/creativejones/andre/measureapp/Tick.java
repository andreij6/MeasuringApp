package com.creativejones.andre.measureapp;

import android.graphics.Paint;

public class Tick {

    private float mPixels;
    private int mLength;
    private int mFormatString;
    private int mDenominator;
    private Paint mPaint;
    private double[] mSections;
    private double mSprawlValue;
    private boolean mDrawText;
    private boolean mInchTick;

    //region Getters & Setters
    public float getPixels() {
        return mPixels;
    }

    public void setPixels(float pixels) {
        mPixels = pixels;
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

    public double[] getSections() {
        return mSections;
    }

    public double getSprawlValue() {
        return mSprawlValue;
    }

    public void setSections(double[] sections) {
        mSections = sections;
    }

    public void setSprawl(double sprawl) {
        mSprawlValue = sprawl;
    }

    public boolean canDrawText() {
        return mDrawText;
    }

    public void setDrawText(boolean drawText) {
        mDrawText = drawText;
    }

    public boolean isInchTick() {
        return mInchTick;
    }

    public void setIsInchTick(boolean isInchTick) {
        mInchTick = isInchTick;
    }
    //endregion

    public static class Builder {

        private float mDP;
        private int mLength;
        private int mFormatString;
        private int mDenominator;
        private Paint mPaint;
        private double sprawl;
        double[] sections;
        private boolean mShowLabel = true;

        public Tick build() {
            Tick result = new Tick();
            result.setPixels(Utils.convertDpToPixel(mDP));
            result.setLength(mLength);
            result.setIsInchTick(mDP == 160);
            result.setFormatString(mFormatString);
            result.setDenominator(mDenominator);
            result.setPaint(mPaint);
            result.setSprawl(sprawl);
            result.setSections(sections);
            result.setDrawText(mShowLabel);
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

        public Builder sprawlValue(double sprawlValue) {
            sprawl = sprawlValue;
            return this;
        }

        public Builder sections(double[] array) {
            sections = array;
            return this;
        }

        public Builder hideLabel() {
            mShowLabel = false;
            return this;
        }
    }
}
