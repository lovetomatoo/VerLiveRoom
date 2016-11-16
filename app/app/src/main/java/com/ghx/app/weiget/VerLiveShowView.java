package com.ghx.app.weiget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghx.app.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/11/4.
 */
public class VerLiveShowView extends LinearLayout implements View.OnClickListener, KeyboardLinearLayout.KeyboardStateListener {

    private KeyboardLinearLayout keyboardLinearLayout;

    View root, showBottomView, showRoomInfoView;
    private LinearLayout mLlRank;
    LinearLayout ll_webview_container;
    //键盘的高度
    private int height;
    private TextView tv_roomid;

    public VerLiveShowView(Context context) {
        super(context);
        init(context);
    }

    public VerLiveShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerLiveShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.fragment_ver_show, this);

        root = findViewById(R.id.root);
        keyboardLinearLayout = (KeyboardLinearLayout) findViewById(R.id.keyboardLinearLayout);
        keyboardLinearLayout.setKeyboardStateListener(this);
        showBottomView = findViewById(R.id.showBottomView);

        showRoomInfoView = findViewById(R.id.showRoomInfoView);

        ll_webview_container = (LinearLayout) findViewById(R.id.ll_webview_container);

        mLlRank = (LinearLayout) findViewById(R.id.ll_rank);
        mLlRank.setOnClickListener(this);

        tv_roomid = (TextView) findViewById(R.id.tv_roomid);

        findViewById(R.id.iv_close).setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_rank:


                break;
            case R.id.iv_close:
                getActivity().finish();
                break;

        }
    }

    private Activity getActivity() {

        return getActivity();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void stateChange(int state) {
        switch (state) {
            case KeyboardLinearLayout.KEYBOARD_HIDE:
                // showRoomInfoView.setVisibility(View.VISIBLE);
                // mLlRank.setVisibility(View.VISIBLE);
                break;
            case KeyboardLinearLayout.KEYBOARD_SHOW:
                //AndroidUtils.sendLocalBroadcast(BroadCofig.ACTION_SHOW_KEYBORAD);
                //showRoomInfoView.setVisibility(View.GONE);
                //mLlRank.setVisibility(View.GONE);
                break;
            case KeyboardLinearLayout.CLICK_TOP:
                //if (showSendBarrageView!=null && showSendBarrageView.getVisibility()==VISIBLE){
                //}
                break;
            default:
                break;
        }
    }


}
