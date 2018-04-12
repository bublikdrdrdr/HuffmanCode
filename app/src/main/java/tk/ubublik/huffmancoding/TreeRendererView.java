package tk.ubublik.huffmancoding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import tk.ubublik.huffmancoding.logic.Leaf;
import tk.ubublik.huffmancoding.logic.Pair;
import tk.ubublik.huffmancoding.logic.Utils;

/**
 * TODO: document your custom view class.
 */
public class TreeRendererView extends View implements View.OnTouchListener {

    private int branchColor;
    private int nitLeafColor;
    private int charLeafColor;
    private int nullLeafColor;
    private float branchThickness;
    private float leafSize;
    private float textSize;
    private int textColor;
    private PointF treePadding;

    private ArrayList<VisualizedLeaf> list = new ArrayList<>();

    private PointF currentPointerPosition = new PointF();
    private PointF screenCenterPoint = new PointF();

    public TreeRendererView(Context context) {
        super(context);
        init(null, 0);
    }

    public TreeRendererView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TreeRendererView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TreeRendererView, defStyle, 0);
        branchColor = a.getColor(R.styleable.TreeRendererView_branchColor, Color.BLACK);
        nitLeafColor = a.getColor(R.styleable.TreeRendererView_nitLeafColor, Color.parseColor("#FF6D6D"));
        charLeafColor = a.getColor(R.styleable.TreeRendererView_charLeafColor, Color.parseColor("#3D81FF"));
        nullLeafColor = a.getColor(R.styleable.TreeRendererView_nullLeafColor, Color.parseColor("#AAAAAA"));
        textColor = a.getColor(R.styleable.TreeRendererView_textColor, Color.BLACK);
        branchThickness = a.getDimension(R.styleable.TreeRendererView_branchThickness, dpToPx(2));
        leafSize = a.getDimension(R.styleable.TreeRendererView_leafSize, dpToPx(25));
        textSize = a.getDimension(R.styleable.TreeRendererView_textSize, spToPx(20));
        treePadding = new PointF(a.getDimension(R.styleable.TreeRendererView_leafHorizontalPadding, dpToPx(50)),
                a.getDimension(R.styleable.TreeRendererView_leafVerticalPadding, dpToPx(50)));
        a.recycle();
        this.setOnTouchListener(this);
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float spToPx(float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCenterOfScreen(w, h);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable background = getBackground();
        if (background!=null) background.draw(canvas);
        ListIterator<VisualizedLeaf> backwardIterator = list.listIterator(list.size());
        while (backwardIterator.hasPrevious()){
            VisualizedLeaf current = backwardIterator.previous();
            if (current.connectedTo!=null && branchFitsOnScreen(current)){
                drawBranch(canvas, current);
            }
            if (nodeFitsOnScreen(current)){
                drawNode(canvas, current);
            }
        }
        // Draw the text.
        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);*/
    }



    private void drawBranch(Canvas canvas, VisualizedLeaf leaf){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(branchThickness);
        paint.setColor(branchColor);
        PointF positionScreenCoordinates = toScreenCoordinates(leaf.position);
        PointF connectedScreenCoordinates = toScreenCoordinates(leaf.connectedTo);
        canvas.drawLine(positionScreenCoordinates.x, positionScreenCoordinates.y, connectedScreenCoordinates.x, connectedScreenCoordinates.y, paint);
    }

    private void drawNode(Canvas canvas, VisualizedLeaf leaf){
        Paint fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        int color = charLeafColor;
        if (leaf.character==null) color = nullLeafColor;
        else if (leaf.character==Leaf.NIT_CHAR) color = nitLeafColor;
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        PointF screenCoordinates = toScreenCoordinates(leaf.position);
        canvas.drawCircle(screenCoordinates.x, screenCoordinates.y, leafSize, fillPaint);
        drawText(canvas, screenCoordinates, String.valueOf(leaf.weight));
        if (leaf.character!=null){
            screenCoordinates.y += leafSize+textSize/2;
            String text = leaf.character==Leaf.NIT_CHAR?"NIT":String.valueOf(leaf.character);
            drawText(canvas, screenCoordinates, text);
        }
    }

    private void drawText(Canvas canvas, PointF screenCoordinates, String text){
        Paint fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        Paint outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        fillPaint.setColor(textColor);
        outlinePaint.setColor(getOutlineTextColor(textColor));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(textSize/30);
        outlinePaint.setTextSize(textSize);
        fillPaint.setTextSize(textSize*1.0f);
        outlinePaint.setFakeBoldText(true);
        fillPaint.setFakeBoldText(true);
        outlinePaint.setTextAlign(Paint.Align.CENTER);
        fillPaint.setTextAlign(Paint.Align.CENTER);
        outlinePaint.setStrokeJoin(Paint.Join.ROUND);

        PointF textPosition = new PointF(screenCoordinates.x, screenCoordinates.y - ((fillPaint.descent() + fillPaint.ascent()) / 2));
        canvas.drawText(text, textPosition.x, textPosition.y, fillPaint);
        canvas.drawText(text, textPosition.x, textPosition.y, outlinePaint);
    }

    private int getOutlineTextColor(int textColor){
        float[] hsv = new float[3];
        Color.colorToHSV(textColor, hsv);
        return hsv[2]>0?Color.BLACK:Color.WHITE;
    }

    private boolean nodeFitsOnScreen(VisualizedLeaf leaf){
        PointF leftTop = new PointF(leaf.position.x-leafSize, leaf.position.y-leafSize);
        PointF rightBottom = new PointF(leftTop.x+leafSize*2, leftTop.y+leafSize*2);
        return fitsOnScreen(leftTop, rightBottom);
    }

    private boolean branchFitsOnScreen(VisualizedLeaf leaf){
        return fitsOnScreen(leaf.connectedTo, leaf.position);
    }

    private boolean fitsOnScreen(PointF leftTop, PointF rightBottom){
        leftTop = toScreenCoordinates(leftTop);
        rightBottom = toScreenCoordinates(rightBottom);
        if (leftTop.x > rightBottom.x){
            float swap = leftTop.x;
            leftTop.x = rightBottom.x;
            rightBottom.x = swap;
        }
        if (leftTop.y > rightBottom.y){
            float swap = leftTop.y;
            leftTop.y = rightBottom.y;
            rightBottom.y = swap;
        }
        return (leftTop.x <= getWidth() && rightBottom.x >= 0 && leftTop.y <= getHeight() && rightBottom.y >= 0);
    }

    private boolean pointOnScreen(PointF point) {
        point = toScreenCoordinates(point);
        return between(point.x, 0, getWidth()) &&
                between(point.y, 0, getHeight());
    }

    private PointF toScreenCoordinates(PointF point){
        return new PointF(point.x + (currentPointerPosition.x + screenCenterPoint.x),
                point.y + (currentPointerPosition.y + screenCenterPoint.y));
    }

    private boolean between(float value, float leftBorder, float rightBorder) {
        return (leftBorder <= value && value <= rightBorder);
    }

    private void updateCenterOfScreen(float screenWidth, float screenHeight){
        screenCenterPoint.x = screenWidth/2;
        screenCenterPoint.y = screenHeight*0.1f;
    }

    public void setTree(Leaf tree) {
        list = (tree==null)?new ArrayList<>():VisualizedLeaf.treeToList(tree, treePadding);
    }

    public void toFirstLeaf(){
        currentPointerPosition.set(0,0);
    }

    private PointF actionDownPointer;
    private PointF actionDownEvent;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownEvent = new PointF(event.getX(), event.getY());
                actionDownPointer = new PointF(currentPointerPosition.x, currentPointerPosition.y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (actionDownPointer != null)
                    currentPointerPosition.set(actionDownPointer.x + event.getX() - actionDownEvent.x, actionDownPointer.y + event.getY() - actionDownEvent.y);
                break;
        }
        invalidate();
        return true;
    }

    public Bundle writeToBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("branchColor", branchColor);
        bundle.putInt("nitLeafColor", nitLeafColor);
        bundle.putInt("charLeafColor", charLeafColor);
        bundle.putInt("nullLeafColor", nullLeafColor);
        bundle.putFloat("branchThickness", branchThickness);
        bundle.putFloat("leafSize", leafSize);
        bundle.putFloat("textSize", textSize);
        bundle.putParcelable("treePadding", treePadding);
        bundle.putParcelable("currentPointerPosition", currentPointerPosition);
        bundle.putParcelableArrayList("list", list);
        return bundle;
    }

    public void readFromBundle(@Nullable Bundle bundle){
        if (bundle==null) return;
        branchColor = bundle.getInt("branchColor", branchColor);
        nitLeafColor = bundle.getInt("nitLeafColor", nitLeafColor);
        charLeafColor = bundle.getInt("charLeafColor", charLeafColor);
        nullLeafColor = bundle.getInt("nullLeafColor", nullLeafColor);
        branchThickness = bundle.getFloat("branchThickness", branchThickness);
        leafSize = bundle.getFloat("leafSize", leafSize);
        textSize = bundle.getFloat("textSize", textSize);
        treePadding = AppUtils.valueOrDefault(bundle.getParcelable("treePadding"), treePadding);
        currentPointerPosition = AppUtils.valueOrDefault(bundle.getParcelable("currentPointerPosition"), currentPointerPosition);
        list = AppUtils.valueOrDefault(bundle.getParcelableArrayList("list"), list);
    }
}
