package com.creativejones.andre.measureapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class Ruler extends View {

    private static final float BASELINE_WIDTH = 1;
    private static final float BASELINE_POSITION = 10;

    Paint mLinePaint;

    Paint mInchTextPaint;
    float mInchTextHeight;

    Paint mQuarterEightPaint;
    float mQuarterEightTextHeight;

    float spread;

    int mLineColor = Color.BLACK;
    int mTextColor = Color.BLACK;

    List<Tick> mTickList;
    float mBaseLineLength;


    public Ruler(Context context) {
        super(context);
        initialize();
    }

    public Ruler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(BASELINE_WIDTH);

        mInchTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInchTextPaint.setColor(mTextColor);
        mInchTextPaint.setTextSize(35);

        if (mInchTextHeight == 0) {
            mInchTextHeight = mInchTextPaint.getTextSize();
        } else {
            mInchTextPaint.setTextSize(mInchTextHeight);
        }

        mQuarterEightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mQuarterEightPaint.setColor(mTextColor);
        mQuarterEightPaint.setTextSize(15);

        if(mQuarterEightTextHeight == 0){
            mQuarterEightTextHeight = mQuarterEightPaint.getTextSize();
        } else {
            mQuarterEightPaint.setTextSize(mQuarterEightTextHeight);
        }

        mTickList = new ArrayList<>();

        mTickList.add(new Tick.Builder().dps(160f).denominator(1).string(R.string.inch_format).textPaint(mInchTextPaint).length(200).build());
        mTickList.add(new Tick.Builder().dps(80f).denominator(2).string(R.string.half_inch).textPaint(mInchTextPaint).length(125).build());
        mTickList.add(new Tick.Builder().dps(40f).denominator(4).string(R.string.quarter_inch_format).textPaint(mQuarterEightPaint).length(75).build());
        mTickList.add(new Tick.Builder().dps(20f).denominator(8).string(R.string.eight_inch_format).textPaint(mQuarterEightPaint).length(40).build());
        mTickList.add(new Tick.Builder().dps(10f).denominator(16).string(R.string.eight_inch_format).textPaint(mInchTextPaint).length(25).build());


        spread = Utils.convertDpToPixel(160);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        setMeasuredDimension(display.getWidth(), (int)Utils.convertDpToPixel(160 * 11));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBaseLineLength = Utils.convertDpToPixel(160) * 10.5f;
        //draw baseline
        canvas.drawLine(BASELINE_POSITION, 0, BASELINE_POSITION, mBaseLineLength, mLinePaint);

        drawTicks(canvas);

    }

    private void drawTicks(Canvas canvas) {
        Tick inchTick = mTickList.get(0);
        float pxs = inchTick.getPixels();
        Resources res = getResources();
        int counter = 0;

        for (int y = (int) BASELINE_WIDTH; y < Utils.convertDpToPixel(160) * 12; y += pxs) {

            int inchValue = 0;

            for (Tick tick : mTickList) {

                if (tick.getDenominator() == 1) {

                    int yPosition = calculateSprawl(y, counter, 0.125);
                    canvas.drawLine(BASELINE_POSITION, yPosition, tick.getLength(), yPosition, mLinePaint);
                    if(counter != 0) {
                        canvas.drawText(String.format(res.getString(tick.getFormatString()), counter), tick.getLength() + 10, yPosition + 10, tick.getPaint());
                    }

                    inchValue = yPosition;
                }

                if (tick.getDenominator() == 2) {
                    double[] halveArray = { 0.5 };

                    drawTick(canvas, inchValue, halveArray, 0.0625, tick, true);
                }

                if (tick.getDenominator() == 4) {

                    double[] forthArray = { 0.25, 0.75 };

                    drawTick(canvas, inchValue, forthArray, (0.0625 / 2), tick, true);
                }

                if (tick.getDenominator() == 8) {

                    double[] eigthArray = { 0.125, 0.375, 0.625, 0.875 };

                    drawTick(canvas, inchValue, eigthArray, (0.0625 / 4), tick, true);
                }

                if (tick.getDenominator() == 16) {

                    double[] sixteenthArray = { 0.0625, 0.1875, 0.3125, 0.4375, 0.5625, 0.6875, 0.8125, 0.9375};

                    drawTick(canvas, inchValue, sixteenthArray, (0.0625 / 8), tick, false);
                }

            }

            counter++;
        }

        canvas.drawLine(BASELINE_POSITION, mBaseLineLength, inchTick.getLength(), mBaseLineLength, mLinePaint);
        canvas.drawText(String.format(res.getString(inchTick.getFormatString()), counter), inchTick.getLength() + 10, mBaseLineLength + 10, inchTick.getPaint());
    }


    private void drawTick(Canvas canvas, int inchMarker, double[] sections, double sprawlValue, Tick tick, boolean shouldDrawText){

        int oddNumbers = 1;
        for (int i = 0; i < sections.length; i++) {

            int position = calcPosition(inchMarker, sections[i]);
            int yPosition = calculateSprawl(position, oddNumbers, sprawlValue);
            canvas.drawLine(BASELINE_POSITION , yPosition, tick.getLength(), yPosition, mLinePaint);

            if(shouldDrawText){
                drawText(canvas, tick, yPosition, oddNumbers);
            }

            oddNumbers +=2;

        }
    }

    private int calculateSprawl(int y, int counter, double sprawlValue) {
        double mulitplyValue = counter * sprawlValue;
        return (int)(y - (spread * mulitplyValue));
    }


    private int calcPosition(int previous, double decimal) {
        return (int) (previous + (spread * decimal));
    }


    private void drawText(Canvas canvas, Tick tick, int y, int numerator) {
        Resources res = getResources();
        if(tick.getDenominator() == 2){
            canvas.drawText(res.getString(tick.getFormatString()), tick.getLength() + 10, y + 10, tick.getPaint());
        } else {
            canvas.drawText(String.format(res.getString(tick.getFormatString()), numerator), tick.getLength() + 10, y + 5, tick.getPaint());
        }
    }


}
