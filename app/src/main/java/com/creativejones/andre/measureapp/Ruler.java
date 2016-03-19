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

    Paint mLinePaint;

    Paint mInchTextPaint;
    float mInchTextHeight;

    Paint mQuarterEightPaint;
    float mQuarterEightTextHeight;


    float spread;

    int mLineColor = Color.BLACK;
    int mTextColor = Color.BLACK;

    List<Tick> mTickList;


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


        spread = Tick.convertDpToPixel(160);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        setMeasuredDimension(display.getWidth(), (int)Tick.convertDpToPixel(160 * 11));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw baseline
        canvas.drawLine(10, 0, 10, Tick.convertDpToPixel(160) * 10.5f, mLinePaint);

        drawTicks(canvas);

    }

    private void drawTicks(Canvas canvas) {

        float pxs = mTickList.get(0).getPixels();
        Resources res = getResources();
        int counter = 0;

        for (int y = (int) BASELINE_WIDTH; y < Tick.convertDpToPixel(160) * 12; y += pxs) {

            int inchValue = 0;

            for (Tick tick : mTickList) {

                if (tick.getDenominator() == 1) {

                    int yPosition = calculateSprawl(y, counter, 0.125);
                    canvas.drawLine(10, yPosition, tick.getLength(), yPosition, mLinePaint);
                    if(counter != 0) {
                        canvas.drawText(String.format(res.getString(tick.getFormatString()), counter), tick.getLength() + 10, yPosition + 10, tick.getPaint());
                    }

                    inchValue = yPosition;
                }

                if (tick.getDenominator() == 2) {
                    int position = calcPosition(inchValue, 0.5);
                    int yPosition = calculateSprawl(position, 1, 0.0625);
                    canvas.drawLine(10, yPosition, tick.getLength(), yPosition, mLinePaint);
                    canvas.drawText(res.getString(tick.getFormatString()), tick.getLength() + 10, yPosition + 10, tick.getPaint());
                }

                if (tick.getDenominator() == 4) {
                    int position1 = calcPosition(inchValue, 0.25);
                    int position2 = calcPosition(inchValue, 0.75);

                    int yPosition = calculateSprawl(position1, 1, (0.0625 / 2));
                    int yPosition2 = calculateSprawl(position2, 3, (0.0625 / 2));

                    canvas.drawLine(10, yPosition, tick.getLength(), yPosition, mLinePaint);
                    canvas.drawLine(10, yPosition2, tick.getLength(), yPosition2, mLinePaint);
                    drawText(canvas, tick, yPosition, 1);
                    drawText(canvas, tick, yPosition2, 3);
                }

                if (tick.getDenominator() == 8) {

                    int position = calcPosition(inchValue, 0.125);
                    int position1 = calcPosition(inchValue, 0.375);
                    int position2 = calcPosition(inchValue, 0.625);
                    int position3 = calcPosition(inchValue, 0.875);

                    int yPosition = calculateSprawl(position, 1, (0.0625 / 4));
                    int yPosition1 = calculateSprawl(position1, 3, (0.0625 / 4));
                    int yPosition2 = calculateSprawl(position2, 5, (0.0625 / 4));
                    int yPosition3 = calculateSprawl(position3, 7, (0.0625 / 4));

                    canvas.drawLine(10, yPosition, tick.getLength(), yPosition, mLinePaint);
                    canvas.drawLine(10, yPosition1, tick.getLength(), yPosition1, mLinePaint);
                    canvas.drawLine(10, yPosition2, tick.getLength(), yPosition2, mLinePaint);
                    canvas.drawLine(10, yPosition3, tick.getLength(), yPosition3, mLinePaint);

                    drawText(canvas, tick, yPosition, 1);
                    drawText(canvas, tick, yPosition1, 3);
                    drawText(canvas, tick, yPosition2, 5);
                    drawText(canvas, tick, yPosition3, 7);
                }

                if (tick.getDenominator() == 16) {
                    int position = calcPosition(inchValue, 0.0625);
                    int position2 = calcPosition(inchValue, 0.1875);
                    int position3 = calcPosition(inchValue, 0.3125);
                    int position4 = calcPosition(inchValue, 0.4375);

                    int position5 = calcPosition(inchValue, 0.5625);
                    int position6 = calcPosition(inchValue, 0.6875);
                    int position7 = calcPosition(inchValue, 0.8125);
                    int position8 = calcPosition(inchValue, 0.9375);

                    int yPosition = calculateSprawl(position, 1, (0.0625 / 8));
                    int yPosition2 = calculateSprawl(position2, 3, (0.0625 / 8));
                    int yPosition3 = calculateSprawl(position3, 5, (0.0625 / 8));
                    int yPosition4 = calculateSprawl(position4, 7, (0.0625 / 8));

                    int yPosition5 = calculateSprawl(position5, 9, (0.0625 / 8));
                    int yPosition6 = calculateSprawl(position6, 11, (0.0625 / 8));
                    int yPosition7 = calculateSprawl(position7, 13, (0.0625 / 8));
                    int yPosition8 = calculateSprawl(position8, 15, (0.0625 / 8));

                    canvas.drawLine(10, yPosition, tick.getLength(), yPosition, mLinePaint);
                    canvas.drawLine(10, yPosition2, tick.getLength(), yPosition2, mLinePaint);
                    canvas.drawLine(10, yPosition3, tick.getLength(), yPosition3, mLinePaint);
                    canvas.drawLine(10, yPosition4, tick.getLength(), yPosition4, mLinePaint);

                    canvas.drawLine(10, yPosition5, tick.getLength(), yPosition5, mLinePaint);
                    canvas.drawLine(10, yPosition6, tick.getLength(), yPosition6, mLinePaint);
                    canvas.drawLine(10, yPosition7, tick.getLength(), yPosition7, mLinePaint);
                    canvas.drawLine(10, yPosition8, tick.getLength(), yPosition8, mLinePaint);
                }

            }

            counter++;
        }

        Tick tick = mTickList.get(0);

        float finalPosition = Tick.convertDpToPixel(160) * 10.5f;

        canvas.drawLine(10, finalPosition, tick.getLength(), finalPosition, mLinePaint);
        canvas.drawText(String.format(res.getString(tick.getFormatString()), counter), tick.getLength() + 10, finalPosition + 10, tick.getPaint());
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
        canvas.drawText(String.format(res.getString(tick.getFormatString()), numerator), tick.getLength() + 10, y + 5, tick.getPaint());

    }


}
