package com.ghx.app.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import com.ghx.app.R;
import com.ghx.app.adapter.VerAdapter;
import com.ghx.app.utils.StatusBarUtils;
import com.ghx.app.weiget.VerScrollView;
import java.util.ArrayList;
import java.util.Map;

/**
 * 全民要 7*14了！！！
 * 草草草
 * 不尊重技术的公司，一定不会长久
 *
 * 这个项目  忙完再写吧
 *
 */

public class VerLiveActivity extends AppCompatActivity {

    private VerScrollView mVerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ver_live);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initAllViews();
    }

    private void initAllViews() {

        StatusBarUtils.setStatusbarTransparent(getWindow(), true);

        ArrayList<Map<String, Object>> list =  getDataFromServer();

        mVerViewPager = (VerScrollView) findViewById(R.id.ver_viewpager);
        VerAdapter adapter = new VerAdapter(getSupportFragmentManager(), list);
        mVerViewPager.setAdapter(adapter);
    }

    private ArrayList<Map<String, Object>> getDataFromServer() {



        return null;
    }


}
