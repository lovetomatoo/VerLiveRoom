package com.ghx.app.weiget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ghx.app.R;

/**
 * Created by sun on 16/8/15.
 */
public class ShowBottomView extends LinearLayout implements View.OnClickListener {
    ImageView ivSwitch, iv_praise;
    private Interpolator linearInterpolator = new LinearInterpolator();
    ObjectAnimator mAnimator;

    public ShowBottomView(Context context) {
        super(context);
        init(context);
    }

    public ShowBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_show_bottom, this);

        ivSwitch = (ImageView) findViewById(R.id.iv_switch);
        ivSwitch.setOnClickListener(this);
        iv_praise = (ImageView) findViewById(R.id.iv_praise);
        iv_praise.setOnClickListener(this);
        findViewById(R.id.iv_gift).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        mAnimator = ObjectAnimator.ofFloat(iv_praise, View.ROTATION, 0f, -10f, 0f, 10f, 0f);
        mAnimator.setInterpolator(linearInterpolator);
        mAnimator.setDuration(500);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_switch:

                this.setVisibility(View.GONE);
                break;
            case R.id.iv_praise:

                break;
            case R.id.iv_gift:


                break;
            case R.id.iv_share: {

            }
            break;
        }

    }


}
