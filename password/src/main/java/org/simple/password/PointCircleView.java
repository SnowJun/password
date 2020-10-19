package org.simple.password;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * org.simple.pass.widget
 *
 * @author Simple
 * @date 2020/10/15
 * @desc 手势密码的小圆圈
 */
public class PointCircleView extends View {


    /**
     * 圆形View的各个属性
     */
    private Circle circle;

    /**
     * 圆心的颜色
     */
    private int colorCenter = Color.BLUE;
    /**
     * 边框的颜色
     */
    private int colorBorder = Color.BLUE;
    /**
     * 圆圈的颜色
     */
    private int colorCircle = Color.GRAY;
    /**
     * 是否只有圆心点  默认false
     */
    private boolean onlyCenter = false;


    public PointCircleView(Context context, Circle circle) {
        super(context);
        this.circle = circle;
    }

    public PointCircleView(Context context) {
        super(context);
    }

    public PointCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PointCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置圆心颜色
     * @param colorCenter
     */
    public void setColorCenter(int colorCenter) {
        this.colorCenter = colorCenter;
    }

    /**
     * 设置边框颜色
     * @param colorBorder
     */
    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
    }

    /**
     * 设置圆形颜色
     * @param colorCircle
     */
    public void setColorCircle(int colorCircle) {
        this.colorCircle = colorCircle;
    }

    /**
     * 设置只保留圆心
     * @param onlyCenter
     */
    public void setOnlyCenter(boolean onlyCenter) {
        this.onlyCenter = onlyCenter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        //圆环
        paint.setColor(colorBorder);
        //这块设置模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circle.getCircleBorderWidth());
        if (!onlyCenter) {
            canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getCircleRadiusWidth(), paint);
        }

        //内圆
        paint.setColor(colorCircle);
        paint.setStyle(Paint.Style.FILL);
        if (!onlyCenter) {
            canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getCircleRadiusWidth(), paint);
        }


        //圆心
        paint.setStrokeWidth(0);
        paint.setColor(colorCenter);
        canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getCenterRadiusWidth(), paint);
        super.onDraw(canvas);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_MOVE
                && event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }
        if (null == circle) {
            return super.onTouchEvent(event);
        }
        //父控件总坐标转换为子控件坐标
        float x = event.getX() % (getWidth());
        float y = event.getY() % (getHeight());

        float dx = Math.abs(x - circle.getCenterX());
        float dy = Math.abs(y - circle.getCenterY());

        //勾股定理计算
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        if (distance <= circle.getCenterRadiusWidth() * 5) {
            //半径的五倍都算圆心
            //按到了中心点
            if (null != listener) {
                listener.onTouchCenter();
            }
        } else {
            //没按到中心点
            if (null != listener) {
                listener.onTouchOther(event.getX(), event.getY());
            }
        }
        return true;
    }

    private onTouchCenterListener listener;

    public void setListener(onTouchCenterListener listener) {
        this.listener = listener;
    }

    /**
     * 按到了中心点
     */
    public interface onTouchCenterListener {
        /**
         * 按到中心点回调
         */
        void onTouchCenter();

        /**
         * 接触其他地方
         *
         * @param parentX 父控件事件的x坐标
         * @param parentY 父控件事件的y坐标
         */
        void onTouchOther(float parentX, float parentY);
    }

}
