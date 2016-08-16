package com.example.yanbraslavski.bitcoingraph.views;

import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Collections;
import java.util.List;


/**
 * TODO: document your custom view class.
 */
public class GraphView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private Paint mLinePaint;
    private float mTextWidth;
    private float mTextHeight;
    private List<GraphPoint> mDataList;

    private long mMinUnixTimestampVal;
    private long mMaxUnixTimeStampVal;

    private double mMinValueCurrecncyVal;
    private double mMaxValueCurrecncyVal;

    /**
     * Used to describe a data that will be rendered by the graph
     */
    public static class GraphPoint {
        private long mUnixTimestamp;
        private double mCurrencyValue;

        public GraphPoint(long unixTimestamp, double currencyValue) {
            this.mUnixTimestamp = unixTimestamp;
            this.mCurrencyValue = currencyValue;
        }

        public long getUnixTimestamp() {
            return mUnixTimestamp;
        }

        public double getCurrencyValue() {
            return mCurrencyValue;
        }

        @Override
        public String toString() {
            return "{" +
                    "mCurrencyValue:" + mCurrencyValue +
                    ", mUnixTimestamp:" + mUnixTimestamp +
                    '}';
        }
    }

    public GraphView(Context context) {
        super(context);
        init(null, 0);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        //initialize list so it will not be null
        mDataList = Collections.emptyList();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GraphView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.GraphView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.GraphView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.GraphView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.GraphView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.GraphView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataList.isEmpty())
            return;

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

//        long timespanWidth = mDataList.get(mDataList.size() - 1).getUnixTimestamp() - mDataList.get(0).getUnixTimestamp();
//        double timespanHeight = mDataList.get(mDataList.size() - 1).getCurrencyValue() - mDataList.get(0).getCurrencyValue();

        final double unixTimestampSpan = ((double) mMaxUnixTimeStampVal - mMinUnixTimestampVal);
        final double currencyValueSpan = mMaxValueCurrecncyVal - mMinValueCurrecncyVal;

        float startX;
        float startY;
        float stopX;
        float stopY;

        //amount of items we should iterate through
        //as we always look at the item ahead , it is enough
        //to iterate until one item before the last in collection
        final int iterationCount = mDataList.size() - 1;
        for (int i = 0; i < iterationCount; i++) {
            GraphPoint currItem = mDataList.get(i);
            GraphPoint nextItem = mDataList.get(i + 1);

            //TODO : extract to func
            float relativeStartX = (float) toRelative(unixTimestampSpan, (double) currItem.getUnixTimestamp(), (double) mMinUnixTimestampVal);
            float relativeStopX = (float) toRelative(unixTimestampSpan, (double) nextItem.getUnixTimestamp(), (double) mMinUnixTimestampVal);

            //invert y axis
            float relativeStartY = 1f - (float)toRelative(currencyValueSpan, currItem.getCurrencyValue(), mMinValueCurrecncyVal);

            //invert y axis
            float relativeStopY = 1f - (float)toRelative(currencyValueSpan, nextItem.getCurrencyValue(), mMinValueCurrecncyVal);

            startX = contentWidth * relativeStartX;
            startY = contentHeight * relativeStartY;
            stopX = contentWidth * relativeStopX;
            stopY = contentHeight * relativeStopY;


            canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);
            Log.d("yan", startX + ":" + startY + "||" + stopX + ":" + stopY);
        }

//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }

    private double toRelative(double totalSpan, double currentValue, double minValue) {
        return ((currentValue - minValue) / totalSpan);
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public void setDataList(List<GraphPoint> dataList) {
        mDataList = dataList;
        onDataSetChanged();
    }

    private void onDataSetChanged() {
        calculateEdgeValues();
        invalidate();
    }

    private void calculateEdgeValues() {
        //find min and max values of currency
        mMinValueCurrecncyVal = Stream.of(mDataList).map(GraphPoint::getCurrencyValue).min(Double::compareTo).orElse(0.0);
        mMaxValueCurrecncyVal = Stream.of(mDataList).map(GraphPoint::getCurrencyValue).max(Double::compareTo).orElse(0.0);

        //find min and max values of timestamp
        mMinUnixTimestampVal = Stream.of(mDataList).map(GraphPoint::getUnixTimestamp).min(Long::compareTo).orElse(0l);
        mMaxUnixTimeStampVal = Stream.of(mDataList).map(GraphPoint::getUnixTimestamp).max(Long::compareTo).orElse(0l);
    }
}
