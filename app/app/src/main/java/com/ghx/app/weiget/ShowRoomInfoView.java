package com.ghx.app.weiget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ghx.app.R;

/**
 * Created by sun on 16/8/15.
 */
public class ShowRoomInfoView extends LinearLayout implements View.OnClickListener {
    ImageView ivHead;
    TextView tvName, tvWatchNum, tvFavorite;

    public ShowRoomInfoView(Context context) {
        super(context);
        init(context);
    }

    public ShowRoomInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowRoomInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_show_room_info, this);


        ivHead = (ImageView) findViewById(R.id.iv_head);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvWatchNum = (TextView) findViewById(R.id.tv_watch_num);
        tvFavorite = (TextView) findViewById(R.id.tv_favorite);
        tvFavorite.setOnClickListener(this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_favorite:


                break;
            default:

                break;
        }

    }

}
