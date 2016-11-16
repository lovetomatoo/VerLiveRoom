package com.ghx.app.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.FrameLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.ghx.app.R;
import com.ghx.app.weiget.VerLiveShowView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContentFragment extends Fragment {

    private VideoView mVidioView;

    private FrameLayout buttomFrameLayout;
    private FrameLayout topFrameLayout;
    private int mPosition;
    private ArrayList<Map<String, Object>> list;
    private VerLiveShowView showView;
    private View view;

    List<View> mViewList = new ArrayList<>();
    private ViewPager viewPager;

    public ContentFragment() {

    }

    public static Fragment newInstance(int num, ArrayList<Map<String, Object>> list) {

        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putSerializable("list", list);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, null);

        initAllViews(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        mPosition = bundle.getInt("num");
        list = (ArrayList<Map<String, Object>>) bundle.getSerializable("list");
//        String thumb = (String) list.get(mPosition % list.size()).get("thumb");


        if (getUserVisibleHint()) {

            visibleHint();
        }
    }

    private void initAllViews(View view) {
       /* mVidioView = (VideoView)view.findViewById(R.id.videoview);

        long l = System.currentTimeMillis();
        String time = l + "";

        Uri uri = Uri.parse("http://dianbo.ws.videoappjg.inhand.tv/lizi-z11434641479087702--20161114104158.mp4");
        http://hdl.ws.videoappjg.inhand.tv/lizi/z11434641479094884.flv
//        Uri uri = Uri.parse("rtmp://rtmp.ws.videoappjg.inhand.tv/lizi/z10835601478250900");
        mVidioView.setMediaController(new MediaController(getContext()));
        mVidioView.setVideoURI(uri);
        mVidioView.start();
        mVidioView.requestFocus();*/

        buttomFrameLayout = (FrameLayout) view.findViewById(R.id.overlay_frg);
        topFrameLayout = (FrameLayout) view.findViewById(R.id.top_frg);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (getUserVisibleHint()) {
            visibleHint();


        } else {

            unVisibleHint();

        }

    }


    public void visibleHint() {
        Bundle bundle = getArguments();
        mPosition = bundle.getInt("num");
        list = (ArrayList<Map<String, Object>>) bundle.getSerializable("list");


        if (buttomFrameLayout != null) {

            mVidioView = new VideoView(getContext());



            Uri uri = Uri.parse("http://dianbo.ws.videoappjg.inhand.tv/lizi-z11434641479087702--20161114104158.mp4");
            MediaController controller = new MediaController(getContext());
            controller.setVisibility(View.INVISIBLE);
            mVidioView.setMediaController(controller);
            mVidioView.setVideoURI(uri);
            mVidioView.start();
            mVidioView.requestFocus();
            mVidioView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.i("guohongxin", "onCompletion");
                    Toast.makeText(getContext(), "dsadas", Toast.LENGTH_SHORT).show();
                }
            });

            buttomFrameLayout.addView(mVidioView);

        }
        if (topFrameLayout != null) {

            showView = new VerLiveShowView(getContext());
            view = new View(getContext());
            mViewList.add(view);
            mViewList.add(showView);

            viewPager = new ViewPager(getContext());
            viewPager.setAdapter(new ShowPagerAdapter());

            topFrameLayout.addView(viewPager);

            viewPager.setCurrentItem(1);

        }
    }

    public void unVisibleHint() {
        if (buttomFrameLayout != null) {

            buttomFrameLayout.removeView(mVidioView);

        }

        if (topFrameLayout != null) {

            if (viewPager != null) {
                mViewList.clear();
                viewPager.removeView(showView);
                topFrameLayout.removeView(viewPager);

            }
        }
    }



    class ShowPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViewList.get(position);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }


    }

}
