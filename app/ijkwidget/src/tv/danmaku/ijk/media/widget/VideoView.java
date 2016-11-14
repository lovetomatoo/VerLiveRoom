/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.*;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;

import java.io.IOException;
import java.util.List;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * 
 * VideoView also provide many wrapper methods for
 */
public class VideoView extends SurfaceView implements
        MediaController.MediaPlayerControl {
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    public static final int VIDEO_LAYOUT_SCALE = 1;
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    public static final int VIDEO_LAYOUT_ZOOM = 3;
    private static final String TAG = VideoView.class.getName();
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_SUSPEND = 6;
    private static final int STATE_RESUME = 7;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;
    boolean mIsScreenOffStop = false;
    private Uri mUri;
    private long mDuration;
    private String mUserAgent;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mVideoLayout = VIDEO_LAYOUT_SCALE;
    private SurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private MediaController mMediaController;
    private View mMediaBufferingIndicator;
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private IMediaPlayer.OnConnectedListener mOnConnectedListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private long mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private Context mContext;
    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                int sarNum, int sarDen) {
            DebugLog.dfmt(TAG, "onVideoSizeChanged: (%dx%d)", width, height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;
            if (mVideoWidth != 0 && mVideoHeight != 0)
                setVideoLayout(mVideoLayout);
        }
    };

    public boolean isUserPause() {

        return mIsUserPause;
    }

    private boolean mIsUserPause=false;

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            DebugLog.d(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer);
            if (mMediaController != null)
                mMediaController.setEnabled(true);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            long seekToPosition = mSeekWhenPrepared;

            if (seekToPosition != 0)
                seekTo(seekToPosition);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                setVideoLayout(mVideoLayout);
                if (mSurfaceWidth == mVideoWidth
                        && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null)
                            mMediaController.show();
                    } else if (!isPlaying()
                            && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null)
                            mMediaController.show(0);
                    }
                }
            } else if (mTargetState == STATE_PLAYING) {
                start();
            }

            MediaInfo info = mMediaPlayer.getMediaInfo();
            Log.d(TAG, "diandian cdn ip = : " + info.mMeta.getString("cdn_ip"));
        }
    };
    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            DebugLog.d(TAG, "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };
    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
            DebugLog.dfmt(TAG, "Error: %d, %d", framework_err, impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();

            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err))
                    return true;
            }

            if (getWindowToken() != null) {

                //播放出错不再弹了 弹窗  不再弹了
//                int message = framework_err == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.vitamio_videoview_error_text_invalid_progressive_playback
//                        : R.string.vitamio_videoview_error_text_unknown;
//
//                new AlertDialog.Builder(mContext)
//                        .setTitle(R.string.vitamio_videoview_error_title)
//                        .setMessage(message)
//                        .setPositiveButton(
//                                R.string.vitamio_videoview_error_button,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                            int whichButton) {
//                                        if (mOnCompletionListener != null)
//                                            mOnCompletionListener
//                                                    .onCompletion(mMediaPlayer);
//                                    }
//                                }).setCancelable(false).show();
            }
            return true;
        }
    };
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };
    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
//            DebugLog.dfmt(TAG, "diandian onInfo: (%d, %d)", what, extra);
            DebugLog.d(TAG, "diandian onInfo: what = " + what + " extra = " + extra);
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            } else if (mMediaPlayer != null) {
                if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.GONE);
                } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                    DebugLog.dfmt(TAG, "txx onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
                    DebugLog.d(TAG, "diandian onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
                }
            }

            return true;
        }
    };
    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            DebugLog.d(TAG, "onSeekComplete" + IjkMediaPlayer.IJK_TXX_TEST);
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };
    private IMediaPlayer.OnConnectedListener mConnectedListener =
            new IMediaPlayer.OnConnectedListener() {
                @Override
                public void onConnected(IMediaPlayer mp, boolean bConnected) {
                    DebugLog.d(TAG, "diandian onConnected = " + bConnected);
                    if (mOnConnectedListener != null) {
                        mOnConnectedListener.onConnected(mp, bConnected);
                    }
                }
            };
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
//                mMediaPlayer.setDisplay(mSurfaceHolder);
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0)
                    seekTo(mSeekWhenPrepared);
                start();
                if (mMediaController != null) {
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
                    && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            if (mCurrentState != STATE_SUSPEND)
                release(true);
        }
    };

    public VideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    private void initVideoView(Context ctx) {
        mContext = ctx;
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoSarNum = 0;
        mVideoSarDen = 0;
        getHolder().addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity)
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView(context);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * Set the display options
     *
     * @param layout
     *            <ul>
     *            <li>{@link #VIDEO_LAYOUT_ORIGIN}
     *            <li>{@link #VIDEO_LAYOUT_SCALE}
     *            <li>{@link #VIDEO_LAYOUT_STRETCH}
     *            <li>{@link #VIDEO_LAYOUT_ZOOM}
     *            </ul>
     *            video aspect ratio, will audo detect if 0.
     */
    public void setVideoLayout(int layout) {
        LayoutParams lp = getLayoutParams();
        Pair<Integer, Integer> res  = ScreenResolution.getResolution(mContext);
        int windowWidth = res.first.intValue(), windowHeight = res.second.intValue();
        float windowRatio = windowWidth / (float) windowHeight;
        int sarNum = mVideoSarNum;
        int sarDen = mVideoSarDen;
        if (mVideoHeight > 0 && mVideoWidth > 0) {
            float videoRatio = ((float) (mVideoWidth)) / mVideoHeight;
            if (sarNum > 0 && sarDen > 0)
                videoRatio = videoRatio * sarNum / sarDen;
            mSurfaceHeight = mVideoHeight;
            mSurfaceWidth = mVideoWidth;

            if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth
                    && mSurfaceHeight < windowHeight) {
                lp.width = (int) (mSurfaceHeight * videoRatio);
                lp.height = mSurfaceHeight;
            } else if (layout == VIDEO_LAYOUT_ZOOM) {
                lp.width = windowRatio > videoRatio ? windowWidth
                        : (int) (videoRatio * windowHeight);
                lp.height = windowRatio < videoRatio ? windowHeight
                        : (int) (windowWidth / videoRatio);
            } else {
                boolean full = layout == VIDEO_LAYOUT_STRETCH;
                lp.width = (full || windowRatio < videoRatio) ? windowWidth
                        : (int) (videoRatio * windowHeight);
                lp.height = (full || windowRatio > videoRatio) ? windowHeight
                        : (int) (windowWidth / videoRatio);
            }
            setLayoutParams(lp);
            getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
            DebugLog.dfmt(
                    TAG,
                    "VIDEO: %dx%dx%f[SAR:%d:%d], Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f",
                    mVideoWidth, mVideoHeight, videoRatio, mVideoSarNum,
                    mVideoSarDen, mSurfaceWidth, mSurfaceHeight, lp.width,
                    lp.height, windowWidth, windowHeight, windowRatio);
        }
        mVideoLayout = layout;
    }

    public boolean isValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null)
            return;

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release(false);
        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            IjkMediaPlayer ijkMediaPlayer = null;
            if (mUri != null) {


                ijkMediaPlayer = new IjkMediaPlayer();

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);//这里硬解自动旋转

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 12);

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", mUserAgent);

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//
                //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 110*1000);//配置了 超时时间会导致一些房间频繁卡顿
            }
            mMediaPlayer = ijkMediaPlayer;

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnConnectedListener(mConnectedListener);
            if (mUri != null)
                mMediaPlayer.setDataSource(mUri.toString());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            DebugLog.e(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            DebugLog.e(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            DebugLog.d("VideoView","release()");
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate)
                mTargetState = STATE_IDLE;
        }
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this
                    .getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());

            if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null" : paths
                        .get(paths.size() - 1);
                mMediaController.setFileName(name);
            }
        }
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public void setUserAgent(String ua) {
    	mUserAgent = ua;
    }

    public int getCurrentState() {

        return mCurrentState;
    }

    public boolean isStart()
    {
        return mCurrentState>0;
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public void onScreeOff()
    {
        if(!mIsUserPause && !mIsUserPause)//没有被用户pause
        {
            pause();
            mIsScreenOffStop=true;
        }
    }

    @Override
    public void pause() {
        mIsUserPause=true;
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                DebugLog.d("VideoView","pause()");
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void onScreenOn()
    {
        if(mIsScreenOffStop )
        {
            mIsScreenOffStop=false;
            start();
        }
    }

    @Override
    public void start() {
        mIsUserPause=false;
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            DebugLog.d("VideoView","start()");
        }
        mTargetState = STATE_PLAYING;
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null)
            mMediaController.hide();
        mMediaController = controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.GONE);
        mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnConnectedListener(IMediaPlayer.OnConnectedListener l) {
        mOnConnectedListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported
                && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_SPACE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && mMediaPlayer.isPlaying()) {
                pause();
                mMediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
            DebugLog.d("VideoView","resume()");
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        mDuration = -1;
        return (int) mDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            long position = mMediaPlayer.getCurrentPosition();
            return (int) position;
        }
        return 0;
    }

    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    //onError之后需要手动的清楚最后一帧
    public void clearSurfaceView()
    {
        if(getHolder()!=null ) {
            Canvas canvas = getHolder().lockCanvas(new Rect(getLeft(), getTop(), getRight(), getBottom()));

            if (canvas != null) {
                canvas.drawColor(Color.BLACK);

                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }
}
