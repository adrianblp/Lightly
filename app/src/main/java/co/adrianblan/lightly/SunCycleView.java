package co.adrianblan.lightly;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * A custom view which shows the cycle of the sun.
 */
public class SunCycleView extends View {

    private static final int PATH_ITERATIONS = 100;
    private static final float PATH_HEIGHT_SCALE = 0.80f;
    private static final float VIEW_HEIGHT_RATIO = 0.314f;

    private static final String DEFAULT_PRIMARY_COLOR_STRING = "#009688";

    private int accentColor;
    private ArrayList<Drawable> sunDrawables;

    private float sunPositionHorizontal;
    private float cycleOffsetHorizontal;
    private float twilightPositionVertical;

    private int canvasWidth;
    private int canvasHeight;

    private Path sunPath;

    private Paint sunPathPaint;
    private Paint sunCirclePaint;
    private Paint twilightDividerPaint;
    private Paint twilightPointPaint;

    public SunCycleView(Context context) {
        super(context);
        init();
    }

    public SunCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.SunCycleView, 0, 0);

        // Attempt to get attributes from XML
        try {
            // TODO: get system accent color
            accentColor = typedArray.getColor(R.styleable.SunCycleView_primaryColor, Color.parseColor(DEFAULT_PRIMARY_COLOR_STRING));
            sunDrawables = new ArrayList<Drawable>();
            sunDrawables.add(typedArray.getDrawable(R.styleable.SunCycleView_sunDrawable));
        } finally {
            typedArray.recycle();
        }

        init();
    }

    /**
     * Initializes the member variables of the class.
     *
     * We are using an init methods because multiple constructors need to initialize the values.
     */
    private void init() {

        sunPositionHorizontal = 0.5f;
        cycleOffsetHorizontal = 0.25f;
        twilightPositionVertical = 0.5f;

        // In case we couldn't get optional drawables
        if(sunDrawables == null) {
            sunDrawables = new ArrayList<Drawable>();
        }

        sunPath = new Path();

        // TODO: fix colors properly

        sunPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunPathPaint.setColor(accentColor);
        sunPathPaint.setStyle(Paint.Style.STROKE);
        sunPathPaint.setStrokeWidth(8);

        sunCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunCirclePaint.setColor(accentColor);
        sunCirclePaint.setStrokeWidth(8);

        twilightDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        twilightDividerPaint.setColor(Color.LTGRAY);
        twilightDividerPaint.setStrokeWidth(2);

        twilightPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        twilightPointPaint.setColor(Color.LTGRAY);
        twilightPointPaint.setStrokeWidth(4);
    }

    /**
     * Calculates PATH_ITERATIONS number of discrete points on the curve of the path.
     */
    private void calculatePath() {

        double cycleOffsetRadians = cycleOffsetHorizontal * Constants.tau;

        System.err.println("PO: " + cycleOffsetHorizontal + ", TDP: " + twilightPositionVertical + ", SO: " + sunPositionHorizontal);

        sunPath.reset();

        // Initial point of the path
        sunPath.moveTo(0, (float) -Math.sin(-cycleOffsetRadians) * PATH_HEIGHT_SCALE * canvasHeight / 2);

        for(int i = 0; i <= PATH_ITERATIONS; i++) {

            float percent = (float) i / PATH_ITERATIONS;

            double pathY = -Math.sin(percent * Constants.tau - cycleOffsetRadians) * PATH_HEIGHT_SCALE;

            sunPath.lineTo(percent * canvasWidth, (float) pathY * canvasHeight / 2);
        }
    }

    /**
     * Sets a list of Drawables that will be used for displaying the sun during a full cycle.
     *
     * The cycle is assumed to begin at sunrise. Can take any number of drawables and scales
     * accordingly, but a power of two is recommended. For example {sun, moon},
     * {sunrise, sun, sunset, moon} or more.
     */
    public void setSunDrawables(ArrayList<Drawable> sunDrawables) {
        this.sunDrawables = sunDrawables;
    }

    /**
     * Takes in a ArrayList of Drawable, together with the offset of the cycle, and position of the sun.
     * Assumes that the ArrayList is a set of sun icons starting from sunrise to a full cycle.
     * Returns the correct drawable for the sun icon to use.
     *
     * Can take any number of drawables and scales accordingly, but a power of two is recommended.
     * For example {sun, moon}, {sunrise, sun, sunset, moon} or more.
     *
     * @param sunDrawables the ArrayList of all sun icons to use over the cycle
     * @param cycleOffsetHorizontal the offset of the cycle [0, 1]
     * @param sunPositionHorizontal  the offset of the sun [0, 1]
     * @return the drawable based on the sun cycle
     */
    private Drawable getSunDrawableInCycle(ArrayList<Drawable> sunDrawables, float cycleOffsetHorizontal,
                                   float sunPositionHorizontal) {

        // If no elements, return null
        if(sunDrawables.isEmpty()) {
            return null;
        }

        // If there's N icons, the first icon does not start applying at zero, but rather -(1.0 / N) / 2.0
        double iconSelectionOffset = (1.0 / (double) sunDrawables.size()) / 2.0;

        // Progress since sunrise scaled to [0, 1]
        double progressSinceSunrise = (sunPositionHorizontal - cycleOffsetHorizontal +
                iconSelectionOffset + 1.0) % 1.0;

        // Scales [0, 1] to [0, arraySize[
        int index = (int) Math.floor(progressSinceSunrise * sunDrawables.size());

        // If underflow, return first
        if(index <= 0) {
            return sunDrawables.get(0);
        }

        // If overflow, return last
        if(index >= sunDrawables.size()) {
            return sunDrawables.get(sunDrawables.size() - 1);
        }

        return sunDrawables.get(index);
    }

    public void setSunPositionHorizontal(float sunPositionHorizontal) {
        this.sunPositionHorizontal = sunPositionHorizontal;
    }

    public void setCycleOffsetHorizontal(float cycleOffsetHorizontal) {
        this.cycleOffsetHorizontal = cycleOffsetHorizontal;
    }

    public void setTwilightPositionVertical(float twilightPositionVertical) {
        this.twilightPositionVertical = twilightPositionVertical;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Scales the height according to the width
        int minh = (int) (MeasureSpec.getSize(w) * VIEW_HEIGHT_RATIO) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int width, int height, int previousWidth, int previousHeight) {
        super.onSizeChanged(width, height, previousWidth, previousHeight);
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculatePath();

        canvas.save();

        // Translate canvas so that (0, 0) targets 0 horizontal, but halfway vertical
        canvas.translate(0, getMeasuredHeight() / 2F);

        float twilightDividerPositionScaled = -twilightPositionVertical * PATH_HEIGHT_SCALE * (canvasHeight / 2f);

        canvas.drawLine(0, twilightDividerPositionScaled, canvasWidth,
                twilightDividerPositionScaled, twilightDividerPaint);

        int dividerPointHeight = 20;

        canvas.drawLine(canvasWidth / 2f, twilightDividerPositionScaled - dividerPointHeight / 2,
                canvasWidth / 2f, twilightDividerPositionScaled + dividerPointHeight / 2,
                twilightPointPaint);

        /**
        canvas.drawCircle(canvasWidth / 2f, twilightDividerPositionScaled * (canvasHeight / 2f), 5f,
                twilightDividerPaint);
         */

        // Draws the path of the sun
        canvas.drawPath(sunPath, sunPathPaint);

        // Draws the sun
        double sunY = -Math.sin(sunPositionHorizontal * Constants.tau - cycleOffsetHorizontal * Constants.tau) * PATH_HEIGHT_SCALE * canvasHeight / 2f;

        if(!sunDrawables.isEmpty()) {

            // Get the appropriate sun drawable, convert to bitmap
            Drawable sunDrawable = getSunDrawableInCycle(sunDrawables, cycleOffsetHorizontal, sunPositionHorizontal);
            Bitmap sunBitmap = ((BitmapDrawable) sunDrawable).getBitmap();

            // Tint sun drawable
            ColorFilter sunIconColorFilter = new PorterDuffColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
            sunCirclePaint.setColorFilter(sunIconColorFilter);

            canvas.drawBitmap(sunBitmap, sunPositionHorizontal * canvasWidth - (sunBitmap.getWidth() / 2f), (float) sunY - (sunBitmap.getHeight() / 2f), sunCirclePaint);
        } else {
            canvas.drawCircle(sunPositionHorizontal * canvasWidth, (float) sunY, 22f, sunCirclePaint);
        }

        canvas.restore();
    }
}
