package com.ghx.app.weiget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by sun on 16/8/3.
 */
public class KeyboardLinearLayout extends LinearLayout implements View.OnClickListener{
    private static final int KEYBOARD_MIN_HEIGHT = 1;
    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_HIDE = 0;

    public static final int CLICK_TOP=2;

    private Handler mHandler = new Handler();
    private KeyboardStateListener mListener;
    private boolean isKeyboardDisplay;

    public KeyboardLinearLayout(Context context) {
        super(context);
        init();
    }

    public KeyboardLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null && oldh > 0) {
                    if (oldh - h > KEYBOARD_MIN_HEIGHT) {
                        isKeyboardDisplay=true;
                        mListener.stateChange(KEYBOARD_SHOW);

                    } else if (h - oldh > KEYBOARD_MIN_HEIGHT) {
                        isKeyboardDisplay=false;
                        mListener.stateChange(KEYBOARD_HIDE);
                    }
                }
            }
        });
    }

    public void setKeyboardStateListener(KeyboardStateListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (!isKeyboardDisplay){

        }
    }

    public interface KeyboardStateListener {
        void stateChange(int state);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isKeyboardDisplay){

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
