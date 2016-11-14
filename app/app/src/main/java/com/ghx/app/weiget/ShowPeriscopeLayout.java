package com.ghx.app.weiget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ghx.app.R;
import com.ghx.app.utils.DensityUtil;
import java.util.Random;

public class ShowPeriscopeLayout extends RelativeLayout {
    private final int MAX_DELAY_MILLIS = 4000;

    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dec = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    private Interpolator[] interpolators;

    private int mHeight;
    private int mWidth;
    private LayoutParams lp;
    private Drawable[] drawables;
    private Random random = new Random();

    private int dHeight;
    private int dWidth;
    int select = -1;

    public ShowPeriscopeLayout(Context context) {
        super(context);
        init();
    }

    public ShowPeriscopeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ShowPeriscopeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawables = new Drawable[10];
        drawables[0] = getResources().getDrawable(R.mipmap.img_player__like_blue);
        drawables[1] = getResources().getDrawable(R.mipmap.img_player__like_blue1);
        drawables[2] = getResources().getDrawable(R.mipmap.img_player__like_orange);
        drawables[3] = getResources().getDrawable(R.mipmap.img_player__like_purple);
        drawables[4] = getResources().getDrawable(R.mipmap.img_player__like_red);
        drawables[5] = getResources().getDrawable(R.mipmap.img_player__like_yellow);
        drawables[6] = getResources().getDrawable(R.mipmap.img_player__like_emoji_favorite);
        drawables[7] = getResources().getDrawable(R.mipmap.img_player__like_emoji_glasses);
        drawables[8] = getResources().getDrawable(R.mipmap.img_player__like_emoji_happy);
        drawables[9] = getResources().getDrawable(R.mipmap.img_player__like_emoji_tongue);

        dWidth = drawables[0].getIntrinsicWidth();
        dHeight = drawables[0].getIntrinsicHeight();

        lp = new LayoutParams(dWidth, dHeight);
        //lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        lp.addRule(ALIGN_PARENT_RIGHT, TRUE);
        lp.rightMargin = DensityUtil.dip2px(10);

        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dec;
        interpolators[3] = accdec;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private void addOtherPraise(int num) {
        if (drawables != null && drawables.length > 0) {
            for (int i = 0; i < num; i++) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addPraise(random.nextInt(drawables.length));
                    }
                }, random.nextInt(MAX_DELAY_MILLIS) * i);
            }
        }
    }

    private void addMyPraise() {
        if (drawables != null && drawables.length > 0) {
            if (select < 0) {
                select = random.nextInt(drawables.length);
            }
            addPraise(select);
        }
    }

    private void addPraise(int i) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawables[i]);
        imageView.setLayoutParams(lp);

        addView(imageView);

        Animator animator2 = getBezierValueAnimator(imageView);
        animator2.addListener(new AnimEndListener(imageView));
        animator2.start();

        Animator animator = getPraiseAnimator(imageView);
        animator.start();
    }

    // 1
    private Animator getPraiseAnimator(View target) {
        return getEnterAnimtor(target);
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        //ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.1f, 0.5f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.1f, 0.5f, 1.2f, 1.0f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(400);
        enter.setInterpolator(dec);
        enter.playTogether(scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    private AnimatorSet getBezierValueAnimator(View target) {
        AnimatorSet enter = new AnimatorSet();

        ShowBezierEvaluator evaluator = new ShowBezierEvaluator(getPointF1(), getPointF2());
        ValueAnimator animator = ValueAnimator.ofObject(
                evaluator,
                new PointF(mWidth - dWidth - DensityUtil.dip2px(7), mHeight - dHeight / 2),
                new PointF(getRandomX(), 0)
        );
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(2300);

        enter.playTogether(animator);
        enter.setInterpolator(dec);
        return enter;
    }

    private int getRandomBX() {
        try {
            int i = random.nextInt(2);//2分之一的机会 B点向右偏移
            if (i == 1) {
                return random.nextInt(mWidth / 2) + mWidth / 2;
            } else {
                return random.nextInt(mWidth / 2);
            }
        } catch (Exception e) {

        }
        return 0;


    }

    private int getRandomCX() {
        int i = random.nextInt(3);//3分之一的机会　   C点向左偏移
        if (i == 1) {
            return random.nextInt(mWidth / 2);
        } else {
            return mWidth / 2 + random.nextInt(mWidth / 2);
        }
    }

    private int getRandomX() {
        return random.nextInt(mWidth);
    }

    private PointF getPointF1() {
        PointF pointF = new PointF();
        pointF.x = getRandomBX();
        pointF.y = mHeight * 3 / 4;
        return pointF;
    }

    private PointF getPointF2() {
        PointF pointF = new PointF();
        pointF.x = getRandomCX();
        pointF.y = mHeight / 4;
        return pointF;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            target.setAlpha(1 - animation.getAnimatedFraction() * animation.getAnimatedFraction());
        }
    }

    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            removeView((target));
        }
    }
}
