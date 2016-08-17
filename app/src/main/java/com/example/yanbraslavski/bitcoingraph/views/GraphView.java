package com.example.yanbraslavski.bitcoingraph.views;

import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.List;


/**
 * This is a rather simple Graph rendering view
 * that is plotting a graph using x:y positions
 *
 * It has also additional representation of the values and time
 * on x and y axises.
 *
 * TODO : add pinch to zoom functionality
 */
public class GraphView extends View {

    //render data list
    private List<GraphPoint> mDataList;

    //color of the graph
    private int mGraphColor;

    //paints used for drawing
    private TextPaint mTextPaint;
    private Paint mLinePaint;

    //Edge unix timestamp values
    private long mMinUnixTimestampVal;
    private long mMaxUnixTimeStampVal;

    //edge currency values
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

        mGraphColor = a.getColor(
                R.styleable.GraphView_graphColor,
                mGraphColor);

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
        mTextPaint.setTextSize(15);
        mTextPaint.setColor(Color.BLACK);
//        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
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

        //draw the actual graph
        plotGraph(canvas, contentWidth, contentHeight);

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

    /**
     * This function plots the graph on the canvas
     * @param canvas
     * @param contentWidth
     * @param contentHeight
     */
    private void plotGraph(Canvas canvas, int contentWidth, int contentHeight) {

        //we need to calculate the total length of the values that lies between maximum and minimum
        final double unixTimestampSpan = ((double) mMaxUnixTimeStampVal - mMinUnixTimestampVal);
        final double currencyValueSpan = mMaxValueCurrecncyVal - mMinValueCurrecncyVal;

        //those are the values of the view that will be drawn
        float startX,startY,stopX,stopY;

        //amount of items we should iterate through
        //as we always look at the item ahead , it is enough
        //to iterate until one item before the last in collection
        final int iterationCount = mDataList.size() - 1;
        for (int i = 0; i < iterationCount; i++) {

            //get the current data item and the next
            //to draw a line between them
            GraphPoint currItem = mDataList.get(i);
            GraphPoint nextItem = mDataList.get(i + 1);

            //calculate start point in relative units (0 ... 1)
            float relativeStartX = (float) toRelative(unixTimestampSpan, (double) currItem.getUnixTimestamp(), (double) mMinUnixTimestampVal);
            float relativeStopX = (float) toRelative(unixTimestampSpan, (double) nextItem.getUnixTimestamp(), (double) mMinUnixTimestampVal);

            //calculate stop point in relative units (0 ... 1)
            //we also need to invert y axis , because in android view Y axis goes from top to bottom
            float relativeStartY = 1f - (float)toRelative(currencyValueSpan, currItem.getCurrencyValue(), mMinValueCurrecncyVal);
            float relativeStopY = 1f - (float)toRelative(currencyValueSpan, nextItem.getCurrencyValue(), mMinValueCurrecncyVal);

            //to obtain the actual points on the view we need
            //to translate back the relative positions to actual
            //view dimensions
            startX = contentWidth * relativeStartX;
            stopX = contentWidth * relativeStopX;
            startY = contentHeight * relativeStartY;
            stopY = contentHeight * relativeStopY;

            canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);
        }
    }

    private double toRelative(double totalSpan, double currentValue, double minValue) {
        return ((currentValue - minValue) / totalSpan);
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
