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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

    private List<VisualizedLeaf> list = new ArrayList<>();

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
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TreeRendererView, defStyle, 0);
        branchColor = a.getColor(R.styleable.TreeRendererView_branchColor, Color.BLACK);
        nitLeafColor = a.getColor(R.styleable.TreeRendererView_nitLeafColor, Color.parseColor("#FF6D6D"));
        charLeafColor = a.getColor(R.styleable.TreeRendererView_charLeafColor, Color.parseColor("#3D81FF"));
        nullLeafColor = a.getColor(R.styleable.TreeRendererView_nullLeafColor, Color.parseColor("#AAAAAA"));
        textColor = a.getColor(R.styleable.TreeRendererView_textColor, Color.BLACK);
        branchThickness = a.getDimension(R.styleable.TreeRendererView_branchThickness, dpToPx(2));
        leafSize = a.getDimension(R.styleable.TreeRendererView_leafSize, dpToPx(25));
        textSize = a.getDimension(R.styleable.TreeRendererView_textSize, spToPx(18));
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
                System.out.println(String.format("Node rendered: x=%f, y=%f", toScreenCoordinates(current.position).x, toScreenCoordinates(current.position).y ));
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
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(branchThickness);
        paint.setColor(branchColor);
        PointF positionScreenCoordinates = toScreenCoordinates(leaf.position);
        PointF connectedScreenCoordinates = toScreenCoordinates(leaf.connectedTo);
        canvas.drawLine(positionScreenCoordinates.x, positionScreenCoordinates.y, connectedScreenCoordinates.x, connectedScreenCoordinates.y, paint);
    }

    private void drawNode(Canvas canvas, VisualizedLeaf leaf){
        Paint fillPaint = new Paint();
        int color = charLeafColor;
        if (leaf.character==null) color = nullLeafColor;
        else if (leaf.character==Leaf.NIT_CHAR) color = nitLeafColor;
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        PointF screenCoordinates = toScreenCoordinates(leaf.position);
        canvas.drawCircle(screenCoordinates.x, screenCoordinates.y, leafSize, fillPaint);

        Paint outlinePaint = new Paint();
        fillPaint.setColor(textColor);
        outlinePaint.setColor(getOutlineTextColor(textColor));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(textSize/20);
        outlinePaint.setTextSize(textSize);
        fillPaint.setTextSize(textSize*1.1f);
        outlinePaint.setFakeBoldText(true);

        PointF textPosition = new PointF(screenCoordinates.x, screenCoordinates.y - ((fillPaint.descent() + fillPaint.ascent()) / 2));
        canvas.drawText(String.valueOf(leaf.weight), textPosition.x, textPosition.y, fillPaint);
        canvas.drawText(String.valueOf(leaf.weight), textPosition.x, textPosition.y, outlinePaint);
        // TODO: 05-Apr-18 continue
    }

    private int getOutlineTextColor(int textColor){
        float[] hsv = new float[3];
        Color.colorToHSV(textColor, hsv);
        return hsv[2]>0?Color.BLACK:Color.WHITE;
    }

    private boolean nodeFitsOnScreen(VisualizedLeaf leaf){
        PointF leftTop = new PointF(leaf.position.x-leafSize/2, leaf.position.y-leafSize/2);
        PointF rightBottom = new PointF(leftTop.x+leafSize, leftTop.y+leafSize);
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
        /*return (between(leftTop.x, 0, getWidth())||between(rightBottom.x, 0, getWidth())) &&
                (between(leftTop.y, 0, getHeight()) || between(rightBottom.y, 0, getHeight()));
                */
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
        list = VisualizedLeaf.treeToList(tree, treePadding);
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
}