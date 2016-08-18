package com.example.yanbraslavski.bitcoingraph.views;

import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
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

    public static final int DEFAULT_GRAPH_COLOR = Color.RED;
    public static final int DEFAULT_VALUE_LINES_COLOR = Color.BLACK;
    public static final int DEFAULT_VALUE_LINES_STOKE_WIDTH = 1;
    public static final int DEFAULT_GRAPH_STOKE_WIDTH = 5;
    public static final int AMOUNT_OF_MEASUERD_CURRENCY_LINES = 10;
    public static final String MEASUREMENT_VALUE_STRING_PATTERN = "***";
    public static final int DEFAULT_TEXT_SIZE = 24;
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
    private double mMinValueCurrencyVal;
    private double mMaxValueCurrencyVal;
    private float mMaxValueTextWidth;
    private float mOffsetFromTheText;

    /**
     * Used to describe a data that will be rendered by the graph
     */
    public static class GraphPoint implements Parcelable {
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

        protected GraphPoint(Parcel in) {
            mUnixTimestamp = in.readLong();
            mCurrencyValue = in.readDouble();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(mUnixTimestamp);
            dest.writeDouble(mCurrencyValue);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<GraphPoint> CREATOR = new Parcelable.Creator<GraphPoint>() {
            @Override
            public GraphPoint createFromParcel(Parcel in) {
                return new GraphPoint(in);
            }

            @Override
            public GraphPoint[] newArray(int size) {
                return new GraphPoint[size];
            }
        };
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

        if (a.hasValue(R.styleable.GraphView_graphColor)) {
            mGraphColor = a.getColor(
                    R.styleable.GraphView_graphColor,
                    mGraphColor);
        } else {
            mGraphColor = DEFAULT_GRAPH_COLOR;
        }

        a.recycle();

        // Set up a default TextPaint object
        initTextPaint();

        //initialise line paint
        initLinePaint();

        measureTextOffsets();
    }

    private void measureTextOffsets() {
        //we need to calculate width of the represented metrics on the graph
        mMaxValueTextWidth = mTextPaint.measureText(MEASUREMENT_VALUE_STRING_PATTERN);
        //we just calculate some reasonable offset from the text
        mOffsetFromTheText = mTextPaint.measureText("**");
    }

    private void initLinePaint() {
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
    }

    private void initTextPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setColor(Color.BLACK);
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

        //draw the values and dates
        drawCurrencyMetrics(canvas, contentWidth, contentHeight);

        //draw the actual graph
        plotGraph(canvas, contentWidth, contentHeight);

    }

    private void drawCurrencyMetrics(Canvas canvas, int contentWidth, int contentHeight) {

        final double currencyValueSpan = mMaxValueCurrencyVal - mMinValueCurrencyVal;
        final int amountOfCheckpoints = AMOUNT_OF_MEASUERD_CURRENCY_LINES;
        final float distanceBetweenCheckpoints = (float) contentHeight / (float) amountOfCheckpoints;

        final float amountOfCurrencyForOnePoint = (float) currencyValueSpan / (float) AMOUNT_OF_MEASUERD_CURRENCY_LINES;

        //setup the line paint for graph
        mLinePaint.setColor(DEFAULT_VALUE_LINES_COLOR);
        mLinePaint.setStrokeWidth(DEFAULT_VALUE_LINES_STOKE_WIDTH);

        //we draw the lines with offset from the border
        final float startX = getPaddingLeft() + mMaxValueTextWidth + mOffsetFromTheText;

        for (int i = 0; i < amountOfCheckpoints; i++) {

            //skip first line (Looks prettier)
            if (i == 0) continue;

            final float yOffset = (i * distanceBetweenCheckpoints);
            final int currencyAmount = (int) (amountOfCurrencyForOnePoint * i);
            final int stopX = getWidth() - getPaddingRight();
            canvas.drawLine(startX, yOffset + getPaddingTop(), stopX, yOffset + getPaddingTop(), mLinePaint);
            canvas.drawText(String.format("%03d", currencyAmount),
                    getPaddingLeft(),
                    (getPaddingTop() + contentHeight - yOffset),
                    mTextPaint);
        }

    }

    /**
     * This function plots the graph on the canvas
     */
    private void plotGraph(Canvas canvas, int contentWidth, int contentHeight) {

        //we are not drawing the graph on the entire view space
        //we need to shrink the space by the offset from the edges
        final float leftOffset = mMaxValueTextWidth + mOffsetFromTheText;
        contentWidth -= leftOffset;

        //setup the line paint for graph
        mLinePaint.setColor(mGraphColor);
        mLinePaint.setStrokeWidth(DEFAULT_GRAPH_STOKE_WIDTH);

        //we need to calculate the total length of the values that lies between maximum and minimum
        final double unixTimestampSpan = ((double) mMaxUnixTimeStampVal - mMinUnixTimestampVal);
        final double currencyValueSpan = mMaxValueCurrencyVal - mMinValueCurrencyVal;

        //those are the values of the view that will be drawn
        float startX, startY, stopX, stopY;


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
            float relativeStartY = 1f - (float) toRelative(currencyValueSpan, currItem.getCurrencyValue(), mMinValueCurrencyVal);
            float relativeStopY = 1f - (float) toRelative(currencyValueSpan, nextItem.getCurrencyValue(), mMinValueCurrencyVal);

            //to obtain the actual points on the view we need
            //to translate back the relative positions to actual
            //view dimensions
            startX = contentWidth * relativeStartX;
            stopX = contentWidth * relativeStopX;
            startY = getPaddingTop() + (contentHeight * relativeStartY);
            stopY = getPaddingTop() + (contentHeight * relativeStopY);

            //correct with the offset
            startX += getPaddingLeft() + leftOffset;
            stopX += getPaddingLeft() + leftOffset;

            canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);
        }
    }

    /**
     * Takes the total units span (length) and the current value
     * that will be translated to the value span between 0 to 1
     */
    private double toRelative(double totalSpan, double currentValue, double minValue) {
        return ((currentValue - minValue) / totalSpan);
    }

    public void setDataList(List<GraphPoint> dataList) {
        mDataList = dataList;
        onDataSetChanged();
    }

    private void onDataSetChanged() {
        //we must recalculate the edge values of the graph to present it exactly
        //within the boundaries of the view
        calculateEdgeValues();

        //force redraw
        invalidate();
    }

    private void calculateEdgeValues() {
        //find min and max values of currency
        mMinValueCurrencyVal = Stream.of(mDataList).map(GraphPoint::getCurrencyValue).min(Double::compareTo).orElse(0.0);
        mMaxValueCurrencyVal = Stream.of(mDataList).map(GraphPoint::getCurrencyValue).max(Double::compareTo).orElse(0.0);

        //find min and max values of timestamp
        mMinUnixTimestampVal = Stream.of(mDataList).map(GraphPoint::getUnixTimestamp).min(Long::compareTo).orElse(0l);
        mMaxUnixTimeStampVal = Stream.of(mDataList).map(GraphPoint::getUnixTimestamp).max(Long::compareTo).orElse(0l);
    }
}