package org.simple.password;

/**
 * org.simple.pass.widget
 *
 * @author Simple
 * @date 2020/10/15
 * @desc 小圆的实体类
 */
public class Circle {


    /**
     * 圆心的半径
     */
    private int centerRadiusWidth;
    /**
     * 圆的半径
     */
    private int circleRadiusWidth;
    /**
     * 边框的宽度
     */
    private int circleBorderWidth;

    /**
     * 圆心点的x，y
     */
    private float centerX;
    private float centerY;


    public int getCenterRadiusWidth() {
        return centerRadiusWidth;
    }

    public void setCenterRadiusWidth(int centerRadiusWidth) {
        this.centerRadiusWidth = centerRadiusWidth;
    }

    public int getCircleRadiusWidth() {
        return circleRadiusWidth;
    }

    public void setCircleRadiusWidth(int circleRadiusWidth) {
        this.circleRadiusWidth = circleRadiusWidth;
    }

    public int getCircleBorderWidth() {
        return circleBorderWidth;
    }

    public void setCircleBorderWidth(int circleBorderWidth) {
        this.circleBorderWidth = circleBorderWidth;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

}
