package com.ghx.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ghx.app.R;
import com.ghx.app.adapter.VerAdapter;
import com.ghx.app.weiget.VerScrollView;
import java.util.ArrayList;
import java.util.Map;

public class VerLiveActivity extends AppCompatActivity {

    private VerScrollView mVerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ver_live);

        initAllViews();
    }

    private void initAllViews() {

        ArrayList<Map<String, Object>> list =  getDataFromServer();

        mVerViewPager = (VerScrollView) findViewById(R.id.ver_viewpager);
        VerAdapter adapter = new VerAdapter(getSupportFragmentManager(), list);
        mVerViewPager.setAdapter(adapter);
    }

    private ArrayList<Map<String, Object>> getDataFromServer() {



        return null;
    }


}
