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
 * Created by ���� on 2015/6/11.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    /**
     * �˵����Ͻ�λ�þ�̬����
     */
    private static final int POS_LEFT_TOP = 0;

    /**
     * �˵����½�λ�þ�̬����
     */
    private static final int POS_LEFT_BOTTOM = 1;

    /**
     * �˵����Ͻ�λ�þ�̬����
     */
    private static final int POS_RIGHT_TOP = 2;

    /**
     * �˵����½�λ�þ�̬����
     */
    private static final int POS_RIGHT_BOTTOM = 3;

    /**
     * �˵�λ��
     */
    private ArcMenuPosition mMenuPosition = ArcMenuPosition.LEFT_BOTTOM;

    /**
     * �˵�״̬
     */
    private ArcMenuStatus mMenuStatus = ArcMenuStatus.CLOSE;

    /**
     * �˵��뾶
     */
    private int mMenuRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

    /**
     * �˵���������ʱ��
     */
    private int mMenuAnimationDuration = 400;

    /**
     * �˵������ص��ӿ�
     */
    private OnArcMenuItemClickListener mArcMenuItemClickListener;

    /**
     * �ϴε��home��ť�ĺ���������ֹ���ٵ��
     */
    private long mLastClickMillis = 0;

    /**
     * �˵�λ��ö������
     */
    public enum ArcMenuPosition {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM;
    }

    /**
     * �˵�״̬ö������
     */
    public enum ArcMenuStatus {
        OPEN, CLOSE
    }

    public interface OnArcMenuItemClickListener {
        void onArcMenuItemClick(View v, int pos);
    }

    /***
     * ���ò˵���������ʱ��
     * @param menuAnimationDuration �˵���������ʱ��
     */
    public void setMenuAnimationDuration(int menuAnimationDuration) {
        mMenuAnimationDuration = menuAnimationDuration;
    }

    /***
     * ���ò˵������ص��ӿ�
     * @param arcMenuItemClickListener �˵������ص��ӿ�
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
            //���ֲ˵�home��ť
            layoutMenuHome();

            //���ֲ˵��ť
            layoutMenuItems();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //��������ͼ�ĳߴ�
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
     * ��ʼ��
     * @param attrs ���Լ���
     */
    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcMenu);

        try {
            if(a != null) {
                //�����Զ�������menu_position���ò˵�λ��
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

                //�����Զ�������menu_radius���ò˵��뾶
                mMenuRadius = (int) a.getDimension(R.styleable.ArcMenu_menu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
            }
        } finally {
            if(a != null) {
                a.recycle();
            }
        }
    }

    /***
     * ���ֲ˵�home��ť
     */
    private void layoutMenuHome() {
        View homeView = getChildAt(0);
        homeView.setOnClickListener(this);

        //home��ť��left��topλ��
        int left = 0;
        int top = 0;

        //home��ť�Ŀ�ȡ��߶�
        int width = homeView.getMeasuredWidth();
        int height = homeView.getMeasuredHeight();

        //���˵�λ��λ�����½ǻ����½�
        //��home��ť��top��ΪViewGroup�ĸ߶ȼ�ȥhome��ť�ĸ߶�
        if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            top = getMeasuredHeight() - height;
        }

        //���˵�λ��λ�����Ͻǻ����½�
        //��home��ť��left��ΪViewGroup�Ŀ�ȼ�ȥhome��ť�Ŀ��
        if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            left = getMeasuredWidth() - width;
        }

        //���ݵõ���left��top��width��height����home��ť
        homeView.layout(left, top, left + width, top + height);
    }

    /***
     * ���ֲ˵��ť
     */
    private void layoutMenuItems() {
        //��ȡ����ͼ�ĸ������������ڲ˵���֮��ĽǶ�
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            View menuItemView = getChildAt(i);
            menuItemView.setVisibility(GONE);

            //menuItem��ť��left��topλ��
            int left = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int top = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //menuItem��ť�Ŀ�ȡ��߶�
            int width = menuItemView.getMeasuredWidth();
            int height = menuItemView.getMeasuredHeight();

            //���˵�λ��λ�����½ǻ����½�
            //��menuItem��ť��top��ΪViewGroup�ĸ߶ȼ�ȥmenuItem��ť�ĸ߶��Լ�menuItem��ť��top
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                top = getMeasuredHeight() - top - height;
            }

            //���˵�λ��λ�����Ͻǻ����½�
            //��menuItem��ť��left��ΪViewGroup�Ŀ�ȼ�ȥmenuItem��ť�Ŀ���Լ�menuItem��ť��left
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                left = getMeasuredWidth() - left - width;
            }

            menuItemView.layout(left, top, left + width, top + height);
        }
    }

    /***
     * ��ʾ����˵�home��ťʱhome��ť����ת����
     * @param v home��ť
     * @param start ��ת��ʼʱ�ĽǶ�
     * @param end ��ת����ʱ�ĽǶ�
     */
    private void animateMenuHome(View v, float start, float end) {
        RotateAnimation rotateAnimation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(mMenuAnimationDuration);

        v.startAnimation(rotateAnimation);
    }

    /***
     * ��ʾ����˵�home��ťʱ�˵���Ķ���
     */
    private void animateMenuItems() {
        //��ȡ����ͼ�ĸ������������ڲ˵���֮��ĽǶ�
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            final View menuItemView = getChildAt(i);
            menuItemView.setVisibility(VISIBLE);

            //�˵�����X�ᡢY���λ�Ƶľ���ֵ
            int deltaX = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int deltaY = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //�˵�����X�ᡢY���λ������
            int factorX = -1;
            int factorY = -1;

            //���˵���λ��λ�����½ǻ����½�
            //���˵�����Y���λ��������Ϊ1
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorY = 1;
            }

            //���˵���λ��λ�����Ͻǻ����½�
            //���˵�����X���λ��������Ϊ1
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
     * ��ʾ����˵���ʱ�Ķ���
     * @param pos ������˵����λ��
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
     * �˵������Ŷ���
     * @param zoomBig �Ƿ�Ŵ�
     * @return ���Ŷ���
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
     * �л��˵�״̬
     */
    private void toogleMenuStatus() {
        mMenuStatus = (mMenuStatus == ArcMenuStatus.CLOSE) ? ArcMenuStatus.OPEN : ArcMenuStatus.CLOSE;
    }

}
