package com.creativejones.andre.measureapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class RulerView extends View {

    private static final float BASELINE_WIDTH = 1;
    private static final float BASELINE_POSITION = 10;
    private static final double INCH_SPRAWL_VALUE = 0.125;
    private final int DENSITY_DPI = getResources().getDisplayMetrics().densityDpi;

    private Paint mLinePaint;
    private Paint mInchTextPaint;
    private float mInchTextHeight;
    private Paint mQuarterEightPaint;
    private float mQuarterEightTextHeight;
    private float mSpread;
    private int mLineColor = Color.BLACK;
    private int mTextColor = Color.BLACK;
    private List<Tick> mTickList;
    private float mBaseLineLength;
    private Resources mResources;
    private int mPreviousInch;
    private int mAdjustmentValue;

    //region Constructors
    public RulerView(Context context) {
        super(context);
        initialize();
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    //endregion

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

        calculateAdjustmentValue();
        
        mBaseLineLength = Utils.convertDpToPixel(160) * 10.56f + ( mAdjustmentValue * 12);

        //draw baseline
        canvas.drawLine(BASELINE_POSITION, 0, BASELINE_POSITION, mBaseLineLength, mLinePaint);

        drawTicks(canvas);
    }

    //region Helpers
    private void initialize() {
        mResources = getResources();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(BASELINE_WIDTH);

        mInchTextPaint = setPaint(35);
        mQuarterEightPaint = setPaint(15);

        mAdjustmentValue = 5;

        setTextSize(mInchTextHeight, mInchTextPaint);
        setTextSize(mQuarterEightTextHeight, mQuarterEightPaint);

        buildTickList();

        mSpread = Utils.convertDpToPixel(160);
    }

    private void drawTicks(Canvas canvas) {
        Tick inchTick = mTickList.get(0);
        float inchSpreadPX = Utils.convertDpToPixel(160f);
        int counter = 0;

        //start baseline--
        int yPosition = calculateSprawl((int) BASELINE_WIDTH, 0, INCH_SPRAWL_VALUE);
        canvas.drawLine(BASELINE_POSITION, yPosition, inchTick.getLength(), yPosition, mLinePaint);
        //--
        mPreviousInch = yPosition;

        float loopMax = mBaseLineLength * 2;

        for (int y = (int) BASELINE_WIDTH; y < loopMax; y += (inchSpreadPX + BASELINE_WIDTH)) {

            int inchValue = 0;

            for (Tick tick : mTickList) {

                if (tick.isInchTick()) {
                    inchValue = drawInchTick(canvas, counter, y, tick);
                } else {
                    if(counter < 12) {
                        drawTick(canvas, inchValue, tick, true);
                    }
                }
            }

            mPreviousInch = inchValue;
            counter++;
        }
    }

    private int drawInchTick(Canvas canvas, int counter, int y, Tick tick) {
        int yPosition = calculateSprawl(y, counter, INCH_SPRAWL_VALUE);

        if(counter > 0) {
            yPosition = adjustByDPI(yPosition, counter);
            canvas.drawLine(BASELINE_POSITION, yPosition, tick.getLength(), yPosition, mLinePaint);
            drawText(canvas, tick, yPosition, counter);
        }

        return yPosition;
    }

    private int adjustByDPI(int yPosition, int counter) {
        //limited by devices I can test on. I have Medium & High.

        if(mAdjustmentValue == 0){
            return yPosition;
        } else {
            return yPosition + mAdjustmentValue * counter;
        }

    }

    private void drawTick(Canvas canvas, int inchMarker, Tick tick, boolean shouldDrawText){
        double[] sections = tick.getSections();
        int oddNumber = 1;
        for (int i = 0; i < sections.length; i++) {

            int position = calcPosition(inchMarker, sections[i]);
            int yPosition = calculateSprawl(position, oddNumber, tick.getSprawlValue());
            //yPosition = adjustByDPI(yPosition, oddNumber);
            canvas.drawLine(BASELINE_POSITION , yPosition, tick.getLength(), yPosition, mLinePaint);

            if(shouldDrawText){
                drawText(canvas, tick, yPosition, oddNumber);
            }

            oddNumber +=2;

        }
    }

    private int calculateSprawl(int y, int counter, double sprawlValue) {
        double mulitplyValue = counter * sprawlValue;
        return (int)(y - (mSpread * mulitplyValue));
    }

    private int calcPosition(int currentInch, double decimal) {
        return (int) (currentInch + ((mSpread + mAdjustmentValue) * decimal));
    }

    private void calculateAdjustmentValue() {
        switch (DENSITY_DPI) {
            case DisplayMetrics.DENSITY_LOW:
                mAdjustmentValue = -10;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                mAdjustmentValue = 0;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                mAdjustmentValue = 10;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                mAdjustmentValue = 20;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                mAdjustmentValue = 30;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                mAdjustmentValue = 40;
                break;
            default:
                mAdjustmentValue = 0;
                break;
        }
    }

    private void buildTickList() {
        mTickList = new ArrayList<>();

        mTickList.add(new Tick.Builder().isInchTick()
                .dps(160f).denominator(1).string(R.string.inch_format)
                .textPaint(mInchTextPaint).length(200).build());

        double[] halveArray = { 0.5 };

        mTickList.add(new Tick.Builder().sprawlValue(0.0625).sections(halveArray)
                .dps(80f).denominator(2).string(R.string.half_inch)
                .textPaint(mInchTextPaint).length(125).build());

        double[] forthArray = { 0.25, 0.75 };

        mTickList.add(new Tick.Builder().sprawlValue((0.0625 / 2)).sections(forthArray)
                .dps(40f).denominator(4).string(R.string.quarter_inch_format)
                .textPaint(mQuarterEightPaint).length(75).build());

        double[] eigthArray = { 0.125, 0.375, 0.625, 0.875 };

        mTickList.add(new Tick.Builder().sprawlValue((0.0625 / 4)).sections(eigthArray)
                .dps(20f).denominator(8).string(R.string.eight_inch_format)
                .textPaint(mQuarterEightPaint).length(40).build());

        double[] sixteenthArray = { 0.0625, 0.1875, 0.3125, 0.4375, 0.5625, 0.6875, 0.8125, 0.9375};

        mTickList.add(new Tick.Builder().sprawlValue((0.0625 / 8)).sections(sixteenthArray).hideLabel()
                .dps(10f).denominator(16).string(R.string.eight_inch_format)
                .textPaint(mInchTextPaint).length(25).build());

    }

    private void drawText(Canvas canvas, Tick tick, int y, int formatNumber) {
        if(tick.canDrawText()) {
            switch (tick.getDenominator()){
                case 2:
                    canvas.drawText(mResources.getString(tick.getFormatString()), tick.getLength() + 10, y + 10, tick.getPaint());
                    break;
                case 1:
                    canvas.drawText(String.format(mResources.getString(tick.getFormatString()), formatNumber), tick.getLength() + 10, y + 10, tick.getPaint());
                    break;
                default:
                    canvas.drawText(String.format(mResources.getString(tick.getFormatString()), formatNumber), tick.getLength() + 10, y + 5, tick.getPaint());
                    break;
            }
        }
    }

    private void setTextSize(float textHeight, Paint paint) {
        if(textHeight == 0){
            textHeight = paint.getTextSize();
        } else {
            paint.setTextSize(textHeight);
        }
    }

    private Paint setPaint(int textSize) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mTextColor);
        paint.setTextSize(textSize);

        return paint;
    }
    //endregion
}
