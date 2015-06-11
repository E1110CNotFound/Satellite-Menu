package com.guoyonghui.arcmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.guoyonghui.arcmenu.R;

/**
 * Created by 永辉 on 2015/6/11.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    /**
     * 左上角位置静态常量
     */
    private static final int POS_LEFT_TOP = 0;

    /**
     * 左下角位置静态常量
     */
    private static final int POS_LEFT_BOTTOM = 1;

    /**
     * 右上角位置静态常量
     */
    private static final int POS_RIGHT_TOP = 2;

    /**
     * 右下角位置静态常量
     */
    private static final int POS_RIGHT_BOTTOM = 3;

    /**
     * 菜单的位置，默认为右下角
     */
    private ArcMenuPosition mMenuPosition = ArcMenuPosition.LEFT_TOP;

    /**
     * 菜单的当前状态，默认为关闭
     */
    private ArcMenuStatus mMenuCurrentStatus = ArcMenuStatus.CLOSE;

    /**
     * 菜单的半径，默认为100dp
     */
    private int mMenuRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

    /**
     * 菜单动画时间，默认为300ms
     */
    private int mMenuAnimDuration = 400;

    /**
     * 上一次点击的时间，防止快速点击
     */
    private long mLastClickTimeMillis = 0;

    /**
     * 菜单项点击回调接口
     */
    private OnArcMenuItemClickListener mOnArcMenuItemClickListener;

    /**
     * 菜单位置枚举
     * @author 永辉
     *
     */
    public enum ArcMenuPosition {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * 菜单状态枚举
     * @author 永辉
     *
     */
    public enum ArcMenuStatus {
        OPEN, CLOSE
    }

    public interface OnArcMenuItemClickListener {
        void onArcMenuItemClick(View v, int pos);
    }

    /***
     * 设置菜单动画时间
     * @param menuAnimDuration 动画时间
     */
    public void setMenuAnimDuration(int menuAnimDuration) {
        mMenuAnimDuration = menuAnimDuration;
    }

    /***
     * 设置菜单项点击回调接口
     * @param onArcMenuItemClickListener 菜单项回调接口
     */
    public void setOnArcMenuItemClickListener(
            OnArcMenuItemClickListener onArcMenuItemClickListener) {
        mOnArcMenuItemClickListener = onArcMenuItemClickListener;
    }

    public ArcMenu(Context context) {
        super(context);

        initialize(null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(attrs);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(attrs);
    }

    /***
     * 初始化
     * @param attrs 属性集合
     */
    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcMenu);

        try {
            if(a != null) {
                //获取菜单位置
                int pos = a.getInt(R.styleable.ArcMenu_menu_position, POS_LEFT_TOP);
                switch (pos) {
                    case POS_LEFT_TOP:
                        mMenuPosition = ArcMenuPosition.LEFT_TOP;
                        break;
                    case POS_LEFT_BOTTOM:
                        mMenuPosition = ArcMenuPosition.LEFT_BOTTOM;
                        break;
                    case POS_RIGHT_TOP:
                        mMenuPosition = ArcMenuPosition.RIGHT_TOP;
                        break;
                    case POS_RIGHT_BOTTOM:
                        mMenuPosition = ArcMenuPosition.RIGHT_BOTTOM;
                        break;

                    default:
                        break;
                }

                //获取菜单半径
                mMenuRadius = (int) a.getDimension(R.styleable.ArcMenu_menu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
            }
        } finally {
            if(a != null) {
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量子视图的尺寸
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            //布局菜单主按钮
            layoutMenuMainButton();

            //布局菜单项
            layoutMenuItem();
        }
    }

    @Override
    public void onClick(View v) {
        if(System.currentTimeMillis() - mLastClickTimeMillis > mMenuAnimDuration) {
            animateMenuMainButton(v, 0f, 360f);

            animateMenuItem();

            //防止快速点击
            mLastClickTimeMillis = System.currentTimeMillis();
        }
    }

    /***
     * 布局菜单主按钮
     */
    private void layoutMenuMainButton() {
        //获取菜单主按钮并为主按钮添加点击事件监听器
        View childView = getChildAt(0);
        childView.setOnClickListener(this);

        //主按钮左侧位置和顶部位置
        int left = 0;
        int top = 0;

        //主按钮的宽度和高度
        int width = childView.getMeasuredWidth();
        int height = childView.getMeasuredHeight();

        //若菜单的位置位于左下角或右下角
        //将主按钮的顶部位置调整为ViewGroup的高度减去主按钮的高度
        if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            top = getMeasuredHeight() - height;
        }

        //若菜单的位置位于右上角或右下角
        //将主按钮的左侧位置调整为ViewGroup的宽度减去主按钮的宽度
        if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            left = getMeasuredWidth() - width;
        }

        //根据主按钮的左侧位置和顶部位置以及宽高布局主按钮
        childView.layout(left, top, left + width, top + width);
    }

    /***
     * 布局菜单项
     */
    private void layoutMenuItem() {
        //获取子视图的个数并根据子视图的个数初始化角度
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            //获取单个菜单项
            View childView = getChildAt(i);

            //设置菜单项的可见性为不可见
            childView.setVisibility(View.GONE);

            //根据菜单半径和角度获取菜单项的初始左侧位置和顶部位置
            int left = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int top = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //获取菜单项的宽度和高度
            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            //若菜单的位置位于左下角或右下角
            //将菜单项的顶部位置调整为ViewGroup的高度减去菜单项的顶部位置以及菜单项的高度
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                top = getMeasuredHeight() - top - height;
            }

            //若菜单的位置位于右上角或右下角
            //将菜单项的左侧位置调整为ViewGroup的宽度减去菜单项的左侧位置以及菜单项的宽度
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                left = getMeasuredWidth() - left - width;
            }

            childView.layout(left, top, left + width, top + height);
        }
    }

    /***
     * 菜单主按钮点击时的旋转动画
     * @param v 菜单主按钮
     * @param start 动画开始时的旋转角度
     * @param end 动画结束时的旋转角度
     */
    private void animateMenuMainButton(View v, float start, float end) {
        //为菜单主按钮设置以其为中心的旋转动画
        RotateAnimation rotateAnim = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setFillAfter(true);
        rotateAnim.setDuration(mMenuAnimDuration);

        v.startAnimation(rotateAnim);
    }

    /***
     * 点击菜单主按钮时的多个菜单项的展开/收起动画
     */
    private void animateMenuItem() {
        //获取子视图的个数并根据子视图的个数初始化角度
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            //获取单个菜单项
            final View childView = getChildAt(i);

            //设置菜单项的可见性为可见
            childView.setVisibility(View.VISIBLE);

            //X轴Y轴的位移量的绝对值
            int deltaX = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int deltaY = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //X轴Y轴的位移因子，默认均为-1
            int factorX = -1;
            int factorY = -1;

            //若菜单的位置位于左下角或右下角
            //将菜单项的顶部位置调整为ViewGroup的高度减去菜单项的顶部位置以及菜单项的高度
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorY = 1;
            }

            //若菜单的位置位于右上角或右下角
            //将菜单项的左侧位置调整为ViewGroup的宽度减去菜单项的左侧位置以及菜单项的宽度
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorX = 1;
            }

            //创建动画集合
            AnimationSet animSet = new AnimationSet(true);

            TranslateAnimation translateAnim;
            if(mMenuCurrentStatus == ArcMenuStatus.CLOSE) {
                translateAnim = new TranslateAnimation(factorX * deltaX, 0, factorY * deltaY, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {
                translateAnim = new TranslateAnimation(0, factorX * deltaX, 0, factorY * deltaY);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            translateAnim.setFillAfter(true);
            translateAnim.setDuration(mMenuAnimDuration);
            translateAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mMenuCurrentStatus == ArcMenuStatus.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });

            RotateAnimation rotateAnim = new RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setFillAfter(true);
            rotateAnim.setDuration(mMenuAnimDuration);

            AlphaAnimation alphaAnim;
            if(mMenuCurrentStatus == ArcMenuStatus.CLOSE) {
                alphaAnim = new AlphaAnimation(0.0f, 1.0f);
            } else {
                alphaAnim = new AlphaAnimation(1.0f, 0.0f);
            }
            alphaAnim.setFillAfter(true);
            alphaAnim.setDuration(mMenuAnimDuration);

            //动画集合添加透明度动画和位移动画
            animSet.addAnimation(alphaAnim);
            //animSet.addAnimation(rotateAnim);
            animSet.addAnimation(translateAnim);

            childView.startAnimation(animSet);

            final int pos = i;
            childView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mOnArcMenuItemClickListener != null) {
                        mOnArcMenuItemClickListener.onArcMenuItemClick(childView, pos);
                    }

                    animateMenuItemWhenClick(pos);

                    toogleMenuCurrentStatus();
                }
            });
        }

        //切换菜单状态
        toogleMenuCurrentStatus();
    }

    /***
     * 当菜单项被点击时的动画
     * @param pos 被点击菜单项的位置
     */
    private void animateMenuItemWhenClick(int pos) {
        int childCount = getChildCount();

        for(int i = 1; i < childCount; i++) {
            View childView = getChildAt(i);

            if(i == pos) {
                childView.startAnimation(zoomMenuItem(true));
            } else {
                childView.startAnimation(zoomMenuItem(false));
            }

            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    /***
     * 切换菜单的当前状态
     */
    private void toogleMenuCurrentStatus() {
        mMenuCurrentStatus = (mMenuCurrentStatus == ArcMenuStatus.CLOSE) ? ArcMenuStatus.OPEN : ArcMenuStatus.CLOSE;
    }

    /***
     * 菜单项被点击时的缩放和透明度动画
     * @param zoomBig 是否放大
     * @return 缩放和透明度动画
     */
    private Animation zoomMenuItem(boolean zoomBig) {
        AnimationSet animSet = new AnimationSet(true);

        ScaleAnimation scaleAnim;
        if(zoomBig) {
            scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }

        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);

        animSet.addAnimation(scaleAnim);
        animSet.addAnimation(alphaAnim);

        animSet.setFillAfter(true);
        animSet.setDuration(mMenuAnimDuration);

        return animSet;
    }

}
