package com.guoyonghui.arcmenu.com.guoyonghui.arcmenu.view;

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
     * 菜单左上角位置静态常量
     */
    private static final int POS_LEFT_TOP = 0;

    /**
     * 菜单左下角位置静态常量
     */
    private static final int POS_LEFT_BOTTOM = 1;

    /**
     * 菜单右上角位置静态常量
     */
    private static final int POS_RIGHT_TOP = 2;

    /**
     * 菜单右下角位置静态常量
     */
    private static final int POS_RIGHT_BOTTOM = 3;

    /**
     * 菜单位置
     */
    private ArcMenuPosition mMenuPosition = ArcMenuPosition.LEFT_BOTTOM;

    /**
     * 菜单状态
     */
    private ArcMenuStatus mMenuStatus = ArcMenuStatus.CLOSE;

    /**
     * 菜单半径
     */
    private int mMenuRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

    /**
     * 菜单动画持续时间
     */
    private int mMenuAnimationDuration = 400;

    /**
     * 菜单项点击回调接口
     */
    private OnArcMenuItemClickListener mArcMenuItemClickListener;

    /**
     * 上次点击home按钮的毫秒数，防止快速点击
     */
    private long mLastClickMillis = 0;

    /**
     * 菜单位置枚举类型
     */
    public enum ArcMenuPosition {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM;
    }

    /**
     * 菜单状态枚举类型
     */
    public enum ArcMenuStatus {
        OPEN, CLOSE
    }

    public interface OnArcMenuItemClickListener {
        void onArcMenuItemClick(View v, int pos);
    }

    /***
     * 设置菜单动画持续时间
     * @param menuAnimationDuration 菜单动画持续时间
     */
    public void setMenuAnimationDuration(int menuAnimationDuration) {
        mMenuAnimationDuration = menuAnimationDuration;
    }

    /***
     * 设置菜单项点击回调接口
     * @param arcMenuItemClickListener 菜单项点击回调接口
     */
    public void setOnArcMenuItemClickListener(OnArcMenuItemClickListener arcMenuItemClickListener) {
        mArcMenuItemClickListener = arcMenuItemClickListener;
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            //布局菜单home按钮
            layoutMenuHome();

            //布局菜单项按钮
            layoutMenuItems();
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
    public void onClick(View v) {
        if(System.currentTimeMillis() - mLastClickMillis > mMenuAnimationDuration) {
            animateMenuHome(v, 0f, 360f);

            animateMenuItems();

            mLastClickMillis = System.currentTimeMillis();
        }
    }

    /***
     * 初始化
     * @param attrs 属性集合
     */
    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcMenu);

        try {
            if(a != null) {
                //根据自定义属性menu_position设置菜单位置
                int pos = a.getInt(R.styleable.ArcMenu_menu_position, POS_LEFT_BOTTOM);
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

                //根据自定义属性menu_radius设置菜单半径
                mMenuRadius = (int) a.getDimension(R.styleable.ArcMenu_menu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
            }
        } finally {
            if(a != null) {
                a.recycle();
            }
        }
    }

    /***
     * 布局菜单home按钮
     */
    private void layoutMenuHome() {
        View homeView = getChildAt(0);
        homeView.setOnClickListener(this);

        //home按钮的left、top位置
        int left = 0;
        int top = 0;

        //home按钮的宽度、高度
        int width = homeView.getMeasuredWidth();
        int height = homeView.getMeasuredHeight();

        //若菜单位置位于左下角或右下角
        //将home按钮的top置为ViewGroup的高度减去home按钮的高度
        if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            top = getMeasuredHeight() - height;
        }

        //若菜单位置位于右上角或右下角
        //将home按钮的left置为ViewGroup的宽度减去home按钮的宽度
        if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            left = getMeasuredWidth() - width;
        }

        //根据得到的left、top、width、height布局home按钮
        homeView.layout(left, top, left + width, top + height);
    }

    /***
     * 布局菜单项按钮
     */
    private void layoutMenuItems() {
        //获取子视图的个数并计算相邻菜单项之间的角度
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            View menuItemView = getChildAt(i);
            menuItemView.setVisibility(GONE);

            //menuItem按钮的left、top位置
            int left = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int top = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //menuItem按钮的宽度、高度
            int width = menuItemView.getMeasuredWidth();
            int height = menuItemView.getMeasuredHeight();

            //若菜单位置位于左下角或右下角
            //将menuItem按钮的top置为ViewGroup的高度减去menuItem按钮的高度以及menuItem按钮的top
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                top = getMeasuredHeight() - top - height;
            }

            //若菜单位置位于右上角或右下角
            //将menuItem按钮的left置为ViewGroup的宽度减去menuItem按钮的宽度以及menuItem按钮的left
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                left = getMeasuredWidth() - left - width;
            }

            menuItemView.layout(left, top, left + width, top + height);
        }
    }

    /***
     * 显示点击菜单home按钮时home按钮的旋转动画
     * @param v home按钮
     * @param start 旋转开始时的角度
     * @param end 旋转结束时的角度
     */
    private void animateMenuHome(View v, float start, float end) {
        RotateAnimation rotateAnimation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(mMenuAnimationDuration);

        v.startAnimation(rotateAnimation);
    }

    /***
     * 显示点击菜单home按钮时菜单项的动画
     */
    private void animateMenuItems() {
        //获取子视图的个数并计算相邻菜单项之间的角度
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            final View menuItemView = getChildAt(i);
            menuItemView.setVisibility(VISIBLE);

            //菜单项在X轴、Y轴的位移的绝对值
            int deltaX = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int deltaY = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //菜单项在X轴、Y轴的位移因子
            int factorX = -1;
            int factorY = -1;

            //若菜单的位置位于左下角或右下角
            //将菜单项在Y轴的位移因子置为1
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorY = 1;
            }

            //若菜单的位置位于右上角或右下角
            //将菜单项在X轴的位移因子置为1
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorX = 1;
            }

            AnimationSet animationSet = new AnimationSet(true);

            TranslateAnimation translateAnimation;
            AlphaAnimation alphaAnimation;

            if(mMenuStatus == ArcMenuStatus.CLOSE) {
                translateAnimation = new TranslateAnimation(factorX * deltaX, 0, factorY * deltaY, 0);
                alphaAnimation = new AlphaAnimation(0.0f, 1.0f);

                menuItemView.setClickable(true);
                menuItemView.setFocusable(true);
            } else {
                translateAnimation = new TranslateAnimation(0, factorX * deltaX, 0, factorY * deltaY);
                alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

                menuItemView.setClickable(false);
                menuItemView.setFocusable(false);
            }
            translateAnimation.setFillAfter(true);
            translateAnimation.setDuration(mMenuAnimationDuration);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mMenuStatus == ArcMenuStatus.CLOSE) {
                        menuItemView.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(mMenuAnimationDuration);

            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);

            menuItemView.startAnimation(animationSet);

            final int pos = i;
            menuItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mArcMenuItemClickListener != null) {
                        mArcMenuItemClickListener.onArcMenuItemClick(menuItemView, pos);
                    }

                    animateMenuItemsWhenItemClicked(pos);

                    toogleMenuStatus();
                }
            });
        }

        toogleMenuStatus();
    }

    /***
     * 显示点击菜单项时的动画
     * @param pos 被点击菜单项的位置
     */
    private void animateMenuItemsWhenItemClicked(int pos) {
        int childCount = getChildCount();
        for(int i = 1; i < childCount; i++) {
            View menuItemView = getChildAt(i);

            if(i == pos) {
                menuItemView.startAnimation(zoomMenuItem(true));
            } else {
                menuItemView.startAnimation(zoomMenuItem(false));
            }
            menuItemView.setClickable(false);
            menuItemView.setFocusable(false);
        }
    }

    /***
     * 菜单项缩放动画
     * @param zoomBig 是否放大
     * @return 缩放动画
     */
    private Animation zoomMenuItem(boolean zoomBig) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation;
        if(zoomBig) {
            scaleAnimation = new ScaleAnimation(1f, 4f, 1f, 4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        animationSet.setDuration(mMenuAnimationDuration);

        return animationSet;
    }

    /***
     * 切换菜单状态
     */
    private void toogleMenuStatus() {
        mMenuStatus = (mMenuStatus == ArcMenuStatus.CLOSE) ? ArcMenuStatus.OPEN : ArcMenuStatus.CLOSE;
    }

}
