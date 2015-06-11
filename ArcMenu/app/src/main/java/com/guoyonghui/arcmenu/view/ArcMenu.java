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
 * Created by ���� on 2015/6/11.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    /**
     * ���Ͻ�λ�þ�̬����
     */
    private static final int POS_LEFT_TOP = 0;

    /**
     * ���½�λ�þ�̬����
     */
    private static final int POS_LEFT_BOTTOM = 1;

    /**
     * ���Ͻ�λ�þ�̬����
     */
    private static final int POS_RIGHT_TOP = 2;

    /**
     * ���½�λ�þ�̬����
     */
    private static final int POS_RIGHT_BOTTOM = 3;

    /**
     * �˵���λ�ã�Ĭ��Ϊ���½�
     */
    private ArcMenuPosition mMenuPosition = ArcMenuPosition.LEFT_TOP;

    /**
     * �˵��ĵ�ǰ״̬��Ĭ��Ϊ�ر�
     */
    private ArcMenuStatus mMenuCurrentStatus = ArcMenuStatus.CLOSE;

    /**
     * �˵��İ뾶��Ĭ��Ϊ100dp
     */
    private int mMenuRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

    /**
     * �˵�����ʱ�䣬Ĭ��Ϊ300ms
     */
    private int mMenuAnimDuration = 400;

    /**
     * ��һ�ε����ʱ�䣬��ֹ���ٵ��
     */
    private long mLastClickTimeMillis = 0;

    /**
     * �˵������ص��ӿ�
     */
    private OnArcMenuItemClickListener mOnArcMenuItemClickListener;

    /**
     * �˵�λ��ö��
     * @author ����
     *
     */
    public enum ArcMenuPosition {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * �˵�״̬ö��
     * @author ����
     *
     */
    public enum ArcMenuStatus {
        OPEN, CLOSE
    }

    public interface OnArcMenuItemClickListener {
        void onArcMenuItemClick(View v, int pos);
    }

    /***
     * ���ò˵�����ʱ��
     * @param menuAnimDuration ����ʱ��
     */
    public void setMenuAnimDuration(int menuAnimDuration) {
        mMenuAnimDuration = menuAnimDuration;
    }

    /***
     * ���ò˵������ص��ӿ�
     * @param onArcMenuItemClickListener �˵���ص��ӿ�
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
     * ��ʼ��
     * @param attrs ���Լ���
     */
    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcMenu);

        try {
            if(a != null) {
                //��ȡ�˵�λ��
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

                //��ȡ�˵��뾶
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

        //��������ͼ�ĳߴ�
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            //���ֲ˵�����ť
            layoutMenuMainButton();

            //���ֲ˵���
            layoutMenuItem();
        }
    }

    @Override
    public void onClick(View v) {
        if(System.currentTimeMillis() - mLastClickTimeMillis > mMenuAnimDuration) {
            animateMenuMainButton(v, 0f, 360f);

            animateMenuItem();

            //��ֹ���ٵ��
            mLastClickTimeMillis = System.currentTimeMillis();
        }
    }

    /***
     * ���ֲ˵�����ť
     */
    private void layoutMenuMainButton() {
        //��ȡ�˵�����ť��Ϊ����ť��ӵ���¼�������
        View childView = getChildAt(0);
        childView.setOnClickListener(this);

        //����ť���λ�úͶ���λ��
        int left = 0;
        int top = 0;

        //����ť�Ŀ�Ⱥ͸߶�
        int width = childView.getMeasuredWidth();
        int height = childView.getMeasuredHeight();

        //���˵���λ��λ�����½ǻ����½�
        //������ť�Ķ���λ�õ���ΪViewGroup�ĸ߶ȼ�ȥ����ť�ĸ߶�
        if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            top = getMeasuredHeight() - height;
        }

        //���˵���λ��λ�����Ͻǻ����½�
        //������ť�����λ�õ���ΪViewGroup�Ŀ�ȼ�ȥ����ť�Ŀ��
        if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
            left = getMeasuredWidth() - width;
        }

        //��������ť�����λ�úͶ���λ���Լ���߲�������ť
        childView.layout(left, top, left + width, top + width);
    }

    /***
     * ���ֲ˵���
     */
    private void layoutMenuItem() {
        //��ȡ����ͼ�ĸ�������������ͼ�ĸ�����ʼ���Ƕ�
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            //��ȡ�����˵���
            View childView = getChildAt(i);

            //���ò˵���Ŀɼ���Ϊ���ɼ�
            childView.setVisibility(View.GONE);

            //���ݲ˵��뾶�ͽǶȻ�ȡ�˵���ĳ�ʼ���λ�úͶ���λ��
            int left = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int top = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //��ȡ�˵���Ŀ�Ⱥ͸߶�
            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            //���˵���λ��λ�����½ǻ����½�
            //���˵���Ķ���λ�õ���ΪViewGroup�ĸ߶ȼ�ȥ�˵���Ķ���λ���Լ��˵���ĸ߶�
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                top = getMeasuredHeight() - top - height;
            }

            //���˵���λ��λ�����Ͻǻ����½�
            //���˵�������λ�õ���ΪViewGroup�Ŀ�ȼ�ȥ�˵�������λ���Լ��˵���Ŀ��
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                left = getMeasuredWidth() - left - width;
            }

            childView.layout(left, top, left + width, top + height);
        }
    }

    /***
     * �˵�����ť���ʱ����ת����
     * @param v �˵�����ť
     * @param start ������ʼʱ����ת�Ƕ�
     * @param end ��������ʱ����ת�Ƕ�
     */
    private void animateMenuMainButton(View v, float start, float end) {
        //Ϊ�˵�����ť��������Ϊ���ĵ���ת����
        RotateAnimation rotateAnim = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setFillAfter(true);
        rotateAnim.setDuration(mMenuAnimDuration);

        v.startAnimation(rotateAnim);
    }

    /***
     * ����˵�����ťʱ�Ķ���˵����չ��/���𶯻�
     */
    private void animateMenuItem() {
        //��ȡ����ͼ�ĸ�������������ͼ�ĸ�����ʼ���Ƕ�
        int childCount = getChildCount();
        double angle = Math.PI / 2 / (childCount - 2);

        for(int i = 1; i < childCount; i++) {
            //��ȡ�����˵���
            final View childView = getChildAt(i);

            //���ò˵���Ŀɼ���Ϊ�ɼ�
            childView.setVisibility(View.VISIBLE);

            //X��Y���λ�����ľ���ֵ
            int deltaX = (int) (mMenuRadius * Math.sin(angle * (i - 1)));
            int deltaY = (int) (mMenuRadius * Math.cos(angle * (i - 1)));

            //X��Y���λ�����ӣ�Ĭ�Ͼ�Ϊ-1
            int factorX = -1;
            int factorY = -1;

            //���˵���λ��λ�����½ǻ����½�
            //���˵���Ķ���λ�õ���ΪViewGroup�ĸ߶ȼ�ȥ�˵���Ķ���λ���Լ��˵���ĸ߶�
            if(mMenuPosition == ArcMenuPosition.LEFT_BOTTOM || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorY = 1;
            }

            //���˵���λ��λ�����Ͻǻ����½�
            //���˵�������λ�õ���ΪViewGroup�Ŀ�ȼ�ȥ�˵�������λ���Լ��˵���Ŀ��
            if(mMenuPosition == ArcMenuPosition.RIGHT_TOP || mMenuPosition == ArcMenuPosition.RIGHT_BOTTOM) {
                factorX = 1;
            }

            //������������
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

            //�����������͸���ȶ�����λ�ƶ���
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

        //�л��˵�״̬
        toogleMenuCurrentStatus();
    }

    /***
     * ���˵�����ʱ�Ķ���
     * @param pos ������˵����λ��
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
     * �л��˵��ĵ�ǰ״̬
     */
    private void toogleMenuCurrentStatus() {
        mMenuCurrentStatus = (mMenuCurrentStatus == ArcMenuStatus.CLOSE) ? ArcMenuStatus.OPEN : ArcMenuStatus.CLOSE;
    }

    /***
     * �˵�����ʱ�����ź�͸���ȶ���
     * @param zoomBig �Ƿ�Ŵ�
     * @return ���ź�͸���ȶ���
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
