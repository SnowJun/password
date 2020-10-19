package org.simple.password;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * org.simple.pass.widget
 *
 * @author Simple
 * @date 2020/10/15
 * @desc
 */
public class PasswordView extends GridLayout {

    /**
     * 宫格View
     */
    private List<PointCircleView> childViews;
    /**
     * 走过的宫格  即密码
     */
    private List<Integer> password;
    /**
     * 最小位数 宫格数的一半+1  如  九宫格最小为5
     */
    private int minPasswordLength;

    /**
     * 设置View的行列数
     * 总数为 count*count
     */
    private int count = 3;
    /**
     * 是否隐藏路径
     */
    private boolean isHidden = false;

    /**
     * 路径经过的点 中心点
     */
    private List<PointF> pointFs;
    /**
     * 最终点   不是中心点
     */
    private PointF pointF;
    /**
     * handler对象
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 是否清除
     */
    boolean isClear;

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
     * 路径颜色
     */
    private int colorPath = Color.BLUE;
    /**
     * 错误路径颜色
     */
    private int colorPathError = Color.RED;
    /**
     * 是否只有圆心点  默认false
     */
    private boolean onlyCenter = false;
    /**
     * 绘制最终路径 会出现错误路径提示
     */
    private boolean isFinal;


    public PasswordView(Context context) {
        super(context);
        init();
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        //View初始化完成之后再进行各种初始化
        post(new Runnable() {
            @Override
            public void run() {
                addViewAndInit();
            }
        });
    }

    private void addViewAndInit() {
        setColumnCount(count);
        setRowCount(count);
        childViews = new ArrayList<>();
        password = new ArrayList<>();
        pointFs = new ArrayList<>();
        //pow为次方运算
        DecimalFormat decimalFormat = new DecimalFormat("0");
        decimalFormat.setRoundingMode(RoundingMode.UP);
        minPasswordLength = Integer.valueOf(decimalFormat.format(Math.pow(count, 2) / 2.0f));
        addChildView();
    }

    /**
     * 添加子View
     */
    private void addChildView() {
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        //间隔数量
        float circleSize = size / (count * 2.0f);
        for (int i = 0; i < Math.pow(count, 2); i++) {
            Circle circle = new Circle();
            circle.setCenterRadiusWidth(10);
            circle.setCircleBorderWidth(5);
            //半径为宽度的一半
            circle.setCircleRadiusWidth((int) (circleSize - 10) / 2);

            float centerX = circleSize;
            float centerY = circleSize;

            circle.setCenterX(centerX);
            circle.setCenterY(centerY);
            PointCircleView pointCircleView = new PointCircleView(getContext(), circle);
            pointCircleView.setColorBorder(colorBorder);
            pointCircleView.setOnlyCenter(onlyCenter);
            pointCircleView.setColorCenter(colorCenter);
            pointCircleView.setColorCircle(colorCircle);
            pointCircleView.setLayoutParams(new ViewGroup.LayoutParams((int) (circleSize * 2), (int) (circleSize * 2)));
            final int position = i;
            pointCircleView.setListener(new PointCircleView.onTouchCenterListener() {
                @Override
                public void onTouchCenter() {
                    if (!password.contains(position + 1)) {
                        password.add(position + 1);
                        addPoint(position);
                        pointF = null;
                        reDraw();
                    }
                }

                @Override
                public void onTouchOther(float parentX, float parentY) {
                    //回传事件的坐标
                    pointF = new PointF(parentX, parentY);
                    reDraw();
                }
            });

            childViews.add(pointCircleView);
            addView(pointCircleView);
        }
    }

    /**
     * 添加路径点
     *
     * @param position
     */
    private void addPoint(int position) {
        //第几行
        int line = position / count;
        //第几列
        int column = position % count;
        //九宫格的大小
        int size = Math.min(getWidth(), getHeight()) / count;

        //计算路径点的坐标
        float x = size * column + size / 2.0f;
        float y = size * line + size / 2.0f;
        Log.d("test", "添加点坐标：" + x + "->" + y);
        pointFs.add(new PointF(x, y));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            isFinal = true;
            //抬起来之后不绘制最后一段
            pointF = null;
            if (null != passwordFinishListener) {
                StringBuffer password = new StringBuffer();
                for (int passwordItem : this.password) {
                    password.append(passwordItem + "");
                }
                Log.d("test","密码："+password);
                if (this.password.size() >= minPasswordLength) {
                    passwordFinishListener.onPasswordFinish(password.toString());
                } else {
                    passwordFinishListener.onError("手势点太少");
                }
            }
            reDraw();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClear = true;
                    reDraw();
                }
            }, 1000);
            return super.dispatchTouchEvent(ev);
        } else {
            isFinal = false;
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isClear = true;
            reDraw();
            password.clear();
            pointFs.clear();
        }
        float x = ev.getX();
        float y = ev.getY();

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);

        int xCount = (int) (x / (size / count));
        if (x % (size / count) > 0
                && xCount < count) {
            xCount += 1;
        }
        int yCount = (int) (y / (size / count));
        if (y % (size / count) > 0
                && yCount < count) {
            yCount += 1;
        }

        int index = xCount + (yCount - 1) * count;
        if (index <= childViews.size()) {
            return childViews.get(index - 1).dispatchTouchEvent(ev);
        } else {
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置是否隐藏手势路径
     *
     * @param hidden
     */
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    private PasswordFinishListener passwordFinishListener;

    public void setPasswordFinishListener(PasswordFinishListener passwordFinishListener) {
        this.passwordFinishListener = passwordFinishListener;
    }

    public interface PasswordFinishListener {
        /**
         * 密码输入完成
         *
         * @param password
         */
        void onPasswordFinish(String password);

        /**
         * 错误
         *
         * @param reason
         */
        void onError(String reason);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isClear) {
            isClear = false;
            password.clear();
            pointFs.clear();
            clearCanvas(canvas);
        } else {
            drawPath(canvas);
        }
    }


    private void drawPath(Canvas canvas) {
        if (isHidden) {
            return;
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        if (password.size() < minPasswordLength && isFinal) {
            paint.setColor(colorPathError);
        } else {
            paint.setColor(colorPath);
        }
        if (pointFs.size() >= 1) {
            Path path = new Path();
            for (int i = 0; i < pointFs.size(); i++) {
                PointF pointStart = pointFs.get(i);
                if (i == 0) {
                    path.moveTo(pointStart.x, pointStart.y);
                } else {
                    path.lineTo(pointStart.x, pointStart.y);
                }
            }
            if (null != pointF) {
                path.lineTo(pointF.x, pointF.y);
            }
            canvas.drawPath(path, paint);
        } else {
            Log.d("test", "没有经过中心点  没有路径");
        }
    }


    /**
     * 清空画布
     *
     * @param canvas
     */
    private void clearCanvas(Canvas canvas) {
        canvas.drawPath(new Path(), new Paint());
    }


    /**
     * 视图重新绘制
     */
    private void reDraw() {
        invalidate();
    }


    /**
     * 设置圆心颜色
     *
     * @param colorCenter
     */
    public void setColorCenter(int colorCenter) {
        this.colorCenter = colorCenter;
    }

    /**
     * 设置边框颜色
     *
     * @param colorBorder
     */
    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
    }

    /**
     * 设置圆形颜色
     *
     * @param colorCircle
     */
    public void setColorCircle(int colorCircle) {
        this.colorCircle = colorCircle;
    }

    /**
     * 设置单圆心模式
     *
     * @param onlyCenter
     */
    public void setOnlyCenter(boolean onlyCenter) {
        this.onlyCenter = onlyCenter;
    }


}
