package com.ghx.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ghx.app.R;
import com.ghx.app.adapter.VerAdapter;
import com.ghx.app.fragment.ContentFragment;
import com.ghx.app.weiget.VerticalViewPager;

public class VerLiveActivity extends AppCompatActivity {

    private VerticalViewPager mVerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ver_live);

        initAllViews();

    }

    private void initAllViews() {
        mVerViewPager = (VerticalViewPager) findViewById(R.id.ver_viewpager);
        mVerViewPager.setAdapter(new VerAdapter.Holder(getSupportFragmentManager())
                .add(ContentFragment.newInstance("1", 1))
                .add(ContentFragment.newInstance("2", 2))
                .add(ContentFragment.newInstance("3", 3))
                .add(ContentFragment.newInstance("4", 4))
                .add(ContentFragment.newInstance("5", 5))
                .set()
        );
    }
}
