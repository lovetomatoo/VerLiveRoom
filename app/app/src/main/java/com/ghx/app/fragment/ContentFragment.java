package com.ghx.app.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;
import com.ghx.app.R;


public class ContentFragment extends Fragment {

    private VideoView mVidioView;

    public ContentFragment() {
    }

    public static Fragment newInstance(String title, int position) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        ContentFragment fragment = new ContentFragment();
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

    private void initAllViews(View view) {
        mVidioView = (VideoView)view.findViewById(R.id.videoview);

//        Uri uri = Uri.parse("rtsp://v2.cache2.c.youtube.com/CjgLENy73wIaLwm3JbT_%ED%AF%80%ED%B0%819HqWohMYESARFEIJbXYtZ29vZ2xlSARSB3Jlc3VsdHNg_vSmsbeSyd5JDA==/0/0/0/video.3gp");
        Uri uri = Uri.parse("rtmp://rtmp.ws.videoappjg.inhand.tv/lizi/z10835601478250900");
        mVidioView.setMediaController(new MediaController(getContext()));
        mVidioView.setVideoURI(uri);
        mVidioView.start();
        mVidioView.requestFocus();
    }

}
