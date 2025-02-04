package com.veblr.android.veblrapp.record;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.amosyuen.videorecorder.BuildConfig;
import com.amosyuen.videorecorder.activity.AbstractDynamicStyledActivity;
import com.amosyuen.videorecorder.activity.manager.TapToFocusManager;
import com.amosyuen.videorecorder.activity.params.FFmpegRecorderActivityParams;
import com.amosyuen.videorecorder.activity.params.InteractionParamsI;
import com.amosyuen.videorecorder.activity.params.RecorderActivityThemeParamsI;
import com.amosyuen.videorecorder.camera.CameraController;
import com.amosyuen.videorecorder.camera.CameraControllerI;
import com.amosyuen.videorecorder.recorder.FFmpegFrameRecorder;
import com.amosyuen.videorecorder.recorder.MediaClipsRecorder;
import com.amosyuen.videorecorder.recorder.VideoTransformerTask;
import com.amosyuen.videorecorder.recorder.params.CameraParams;
import com.amosyuen.videorecorder.recorder.params.RecorderParamsI;
import com.amosyuen.videorecorder.ui.CameraPreviewView;
import com.amosyuen.videorecorder.ui.CameraTapAreaView;
import com.amosyuen.videorecorder.ui.ProgressSectionsView;
import com.amosyuen.videorecorder.ui.ViewUtil;
import com.amosyuen.videorecorder.util.Util;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.ui.PreviewActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpDraw;
import VideoHandle.EpEditor;
import VideoHandle.EpText;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class FFmpegRecordActivity extends AbstractDynamicStyledActivity implements
        View.OnTouchListener, View.OnClickListener ,
        CameraControllerI.CameraListener,
        MediaClipsRecorder.MediaClipsRecorderListener,
        MediaClipsRecorder.MediaRecorderConfigurer,
        SurfaceHolder.Callback{
    public static final String REQUEST_PARAMS_KEY =
            BuildConfig.APPLICATION_ID + ".FFmpegRecorderActivityParams";

    public static final int RESULT_ERROR = RESULT_FIRST_USER;
    public static final String RESULT_ERROR_PATH_KEY = "error";
    public static final String RESULT_THUMBNAIL_URI_KEY = "thumbnail";

    protected static final String LOG_TAG = "FFmpegRecorderActivity";
    protected static final int PREVIEW_ACTIVITY_RESULT = 10000;
    protected static final int FOCUS_WEIGHT = 1000;
    public static final int REQUEST_PERMISSIONS = 1;

    // User params
    FFmpegRecorderActivityParams mParams;

    // UI variables
    protected ProgressSectionsView mProgressView;
    protected CameraPreviewView mCameraPreviewView;
    protected CameraTapAreaView mTapToFocusView;
    protected AppCompatImageButton mRecordButton;
    protected AppCompatImageButton mFlashButton;
    protected AppCompatImageButton mSwitchCameraButton;
    protected CircularProgressView mProgressBar;
    protected TextView mProgressText;
    protected AppCompatImageButton mNextButton;

    // State variables
    protected ActivityOrientationEventListener mOrientationEventListener;
    protected TapToFocusManager mFocusManager;
    protected int mContextOrientation;
    protected int mOpenCameraOrientationDegrees;
    protected MediaClipsRecorder mMediaClipsRecorder;
    protected CameraControllerI mCameraController;
    protected int mOriginalRequestedOrientation;
    protected OpenCameraTask mOpenCameraTask;
    protected SaveVideoTask mSaveVideoTask;
    protected File mVideoOutputFile;
    protected File mVideoThumbnailOutputFile;
    private Runnable doAfterAllPermissionsGranted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!extractIntentParams()) {
            return;
        }
        layoutView();
        setupToolbar((Toolbar) findViewById(R.id.toolbar));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOrientationEventListener = new ActivityOrientationEventListener();

        mOriginalRequestedOrientation = getRequestedOrientation();

        InteractionParamsI interactionParams =  getInteractionParams();
        RecorderActivityThemeParamsI themeParams = getThemeParams();
        mProgressView = (ProgressSectionsView) findViewById(R.id.recorderProgress);
        mProgressView.setMinProgress(interactionParams.getMinRecordingMillis());
        mProgressView.setMaxProgress(interactionParams.getMaxRecordingMillis());
        mProgressView.setProgressColor(themeParams.getProgressColor());
        mProgressView.setCursorColor(themeParams.getProgressCursorColor());
        mProgressView.setMinProgressColor(themeParams.getProgressMinProgressColor());

        mProgressView.setProvider(new ProgressSectionsView.ProgressSectionsProvider() {
            @Override
            public boolean hasCurrentProgress() {
                return mMediaClipsRecorder.isRecording();
            }

            @Override
            public List<Integer> getProgressSections() {
                List<MediaClipsRecorder.Clip> clips = mMediaClipsRecorder.getClips();
                ArrayList<Integer> progressList = Lists.newArrayListWithCapacity(
                        clips.size() + (mMediaClipsRecorder.isRecording() ? 1 : 0));
                long totalRecordedMillis = 0;
                for (MediaClipsRecorder.Clip clip : clips) {
                    totalRecordedMillis += clip.getDurationMillis();
                    progressList.add((int) clip.getDurationMillis());
                }
                if (mMediaClipsRecorder.isRecording()) {
                    progressList.add((int) mMediaClipsRecorder.getCurrentRecordedTimeMillis());
                }
                if (mNextButton.getVisibility() == View.INVISIBLE) {
                    final long minRecordingMillis = getInteractionParams().getMinRecordingMillis();
                    if (totalRecordedMillis > minRecordingMillis) {
                        Log.v(LOG_TAG, "Reached min recording time");
                        final long finalTotalRecordedMillis = totalRecordedMillis;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(finalTotalRecordedMillis >=10000)
                                    mNextButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                return progressList;
            }
        });
                // TODO: Support Camera 2
                mCameraController = new CameraController();
                mCameraController.addListener(this);
                mCameraPreviewView = (CameraPreviewView) findViewById(R.id.recorder_view);
                mCameraPreviewView.getHolder().addCallback(this);
                mCameraPreviewView.setParams(mParams.getRecorderParams());
                mCameraPreviewView.setVisibility(View.INVISIBLE);

                mTapToFocusView = (CameraTapAreaView) findViewById(R.id.tap_to_focus_view);
                mTapToFocusView.setTapSizePercent(getInteractionParams().getTapToFocusSizePercent());

                mFocusManager = new TapToFocusManager(mCameraController, mCameraPreviewView, mTapToFocusView,
                        FOCUS_WEIGHT, mParams.getInteractionParams().getTapToFocusHoldTimeMillis());

                mRecordButton = (AppCompatImageButton) findViewById(R.id.record_button);
                mRecordButton.setOnTouchListener(FFmpegRecordActivity.this);

                mSwitchCameraButton = (AppCompatImageButton) findViewById(R.id.switch_camera_button);
                mSwitchCameraButton.setOnClickListener(FFmpegRecordActivity.this);
                mSwitchCameraButton.setColorFilter(themeParams.getWidgetColor());

                mFlashButton = (AppCompatImageButton) findViewById(R.id.flash_button);
                mFlashButton.setOnClickListener(this);
                mFlashButton.setColorFilter(themeParams.getWidgetColor());

                mProgressBar = (CircularProgressView) findViewById(R.id.progress_bar) ;
                mProgressBar.setColor(themeParams.getProgressColor());
                mProgressText = (TextView) findViewById(R.id.progress_text) ;

                mNextButton = (AppCompatImageButton) findViewById(R.id.next_button);
                mNextButton.setColorFilter(themeParams.getToolbarWidgetColor());
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMediaClipsRecorder.getRecordedMillis() > 0) {
                            saveRecording();
                        }
                    }
                });

    }


    protected boolean extractIntentParams() {
        mParams = (FFmpegRecorderActivityParams)
                getIntent().getSerializableExtra(REQUEST_PARAMS_KEY);
        if (mParams == null) {
            onError(new InvalidParameterException(
                    "Intent did not have FFmpegRecorderActivity.REQUEST_PARAMS_KEY set."));
            return false;
        }
        return true;
    }

    protected InteractionParamsI getInteractionParams() {
        return mParams.getInteractionParams();
    }

    protected RecorderParamsI getRecorderParams() {
        return mParams.getRecorderParams();
    }

    protected RecorderActivityThemeParamsI getThemeParams() {
        return mParams.getThemeParams();
    }

    @Override
    protected void layoutView() {
        setContentView(R.layout.activity_record);

    }

    /*  @Override
      protected void layoutView() {
          setContentView(R.layout.activity_record);
      }
 */
      @Override
      protected void setupToolbar(Toolbar toolbar) {
          Drawable navButtonDrawable =
                  ContextCompat.getDrawable(this, R.drawable.ic_action_navigation_close);
          toolbar.setNavigationIcon(ViewUtil.tintDrawable(
                  navButtonDrawable, getThemeParams().getToolbarWidgetColor()));
          super.setupToolbar(toolbar);
      }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    protected void initRecorders() {
        mProgressView.setVisibility(View.GONE);
        mNextButton.setVisibility(View.INVISIBLE);

        mMediaClipsRecorder = new MediaClipsRecorder(this, getCacheDir());
        mMediaClipsRecorder.setMediaCLipstRecorderListener(this);

        setRequestedOrientation(mOriginalRequestedOrientation);
        openCamera(getRecorderParams().getVideoCameraFacing());
    }

    @Override
    public void configureMediaRecorder(MediaRecorder recorder) {
        Log.v(LOG_TAG, String.format("Remaining millis %d",
                getInteractionParams().getMaxRecordingMillis()
                        - mMediaClipsRecorder.getRecordedMillis()));
        // Camera must be set first
        mCameraController.setMediaRecorder(recorder);
        // Then other config
        Util.setMediaRecorderEncoderParams(recorder, getRecorderParams());
        Util.setMediaRecorderInteractionParams(recorder, getInteractionParams(),
                (int) mMediaClipsRecorder.getRecordedMillis(), mMediaClipsRecorder.getRecordedBytes());
        Util.setMediaRecorderCameraParams(recorder, mCameraController);
    }

    @Override
    protected void onResume() {
            mOrientationEventListener.enable();
            initRecorders();

        super.onResume();

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PREVIEW_ACTIVITY_RESULT:
                switch (resultCode) {
                    case RESULT_OK:
                        previewAccepted();
                        break;
                    case RESULT_CANCELED:
                    default:
                        discardRecording();
                        initRecorders();
                        supportInvalidateOptionsMenu();
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMediaClipsRecorder.getRecordedMillis() > 0) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.discard_video_msg)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            discardRecordingAndFinish();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            discardRecordingAndFinish();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        mMediaClipsRecorder.deleteClips();
        mCameraController.closeCamera();
        releaseResources();
        mOrientationEventListener.disable();

    }

    protected void openCamera(CameraControllerI.Facing facing) {
        if (mOpenCameraTask != null) {
            return;
        }

        Log.d(LOG_TAG, String.format("Opening camera facing %s", facing));
        showProgress(R.string.initializing);
        mCameraPreviewView.setPreviewSize(null);
        mOpenCameraOrientationDegrees = mOrientationEventListener.mOrientationDegrees;
        mOpenCameraTask = new OpenCameraTask();
        mOpenCameraTask.execute(Preconditions.checkNotNull(facing));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCameraPreviewDisplayIfReady();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public void onCameraOpen() {
        mCameraPreviewView.setPreviewSize(mCameraController.getPreviewSize());
        setCameraPreviewDisplayIfReady();
        mCameraController.startPreview();
    }

    protected void setCameraPreviewDisplayIfReady() {
        if (mCameraController.isCameraOpen()) {
            try {
                mCameraController.setPreviewDisplay(mCameraPreviewView.getHolder());
            } catch (IOException exception) {
                Log.e(LOG_TAG, "Error setting camera preview display", exception);
            }
        }
    }

    @Override
    public void onCameraClose() {
        mCameraPreviewView.setPreviewSize(null);
    }

    @Override
    public void onCameraStartPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                mCameraPreviewView.setVisibility(View.VISIBLE);
            }
        });
        Log.d(LOG_TAG, "Ready to record");
    }

    @Override
    public void onCameraStopPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraPreviewView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onFlashModeChanged(CameraControllerI.FlashMode flashMode) {}

    @Override
    public void onCameraFocusOnRect(Rect rect) {}

    @Override
    public void onCameraAutoFocus() {}

    protected void showProgress(@StringRes int progressTextRes) {
        if (!mProgressBar.isIndeterminate()) {
            mProgressBar.setIndeterminate(true);
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressText.setVisibility(View.VISIBLE);
        mProgressText.setText(progressTextRes);
        hideControls();
    }

    protected void hideUI() {
        mProgressView.setVisibility(View.GONE);
        hideControls();
    }

    protected void hideControls() {
        mRecordButton.setVisibility(View.INVISIBLE);
        mSwitchCameraButton.setVisibility(View.INVISIBLE);
        mFlashButton.setVisibility(View.INVISIBLE);
    }

    protected void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mProgressText.setVisibility(View.GONE);

        mProgressView.setVisibility(View.VISIBLE);
        mRecordButton.setVisibility(View.VISIBLE);
        if (mCameraController.getCameraCount() > 1) {
            mSwitchCameraButton.setVisibility(View.VISIBLE);
        }
        if (mCameraController.supportsFlashMode(CameraControllerI.FlashMode.ON)) {
            mFlashButton.setVisibility(View.VISIBLE);
        } else {
            mFlashButton.setVisibility(View.INVISIBLE);
        }
    }

    protected void startRecording() {
        synchronized (mCameraController) {
            Log.v(LOG_TAG, "Start recording");
            try {
                mCameraController.unlock();
            } catch (Throwable e) {
                Log.e(LOG_TAG, "Error unlocking the camera when starting recording");
            }
            mMediaClipsRecorder.start();
            mSwitchCameraButton.setVisibility(View.INVISIBLE);
            // Lock the orientation the first time we start recording if there is no request orientation

            if (mMediaClipsRecorder.getClips().isEmpty() && mOriginalRequestedOrientation == -1) {
                setRequestedOrientation(getResources().getConfiguration().orientation);
            }
            mFocusManager.cancelDelayedAutoFocus();
        }
    }


    @Override
    public void onMediaRecorderMaxDurationReached() {
        Log.d(LOG_TAG, "Max recording time reached");
        saveRecording();
    }

    @Override
    public void onMediaRecorderMaxFileSizeReached() {
        Log.d(LOG_TAG, "Max file size reached");
        saveRecording();
    }

    protected void stopRecordingAndPrepareForNext() {
        if (!mMediaClipsRecorder.isRecording()) {
            return;
        }
        stopRecording();
        mFocusManager.autoFocusAfterDelay();
    }

    protected void stopRecording() {
        synchronized (mCameraController) {
            if (mParams == null || !mMediaClipsRecorder.isRecording()) {
                return;
            }
            Log.v(LOG_TAG, "Stop recording");
            mMediaClipsRecorder.stop();
            try {
                mCameraController.lock();
            } catch (Throwable e) {
                Log.e(LOG_TAG, "Error locking the camera when stopping recording");
            }
            if (mSaveVideoTask == null && mCameraController.getCameraCount() > 1) {
                mSwitchCameraButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mSaveVideoTask == null && mCameraController.isCameraOpen()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    stopRecordingAndPrepareForNext();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    if (x < 0 || x > v.getWidth() || y < 0 || y > v.getHeight()) {
                        stopRecordingAndPrepareForNext();
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.flash_button) {
            if (!mCameraController.isCameraOpen()) {
                return;
            }
            CameraControllerI.FlashMode flashMode;
            switch (mCameraController.getFlashMode()) {
                case ON:
                    flashMode = CameraControllerI.FlashMode.OFF;
                    break;
                case OFF:
                default:
                    flashMode = CameraControllerI.FlashMode.ON;
                    break;
            }
            updateCameraFlash(flashMode);
        } else if (v.getId() == R.id.switch_camera_button) {
            if (!mCameraController.isCameraOpen()) {
                return;
            }
            CameraControllerI.Facing cameraFacing =
                    mCameraController.getCameraFacing() == CameraControllerI.Facing.BACK
                            ? CameraControllerI.Facing.FRONT
                            : CameraControllerI.Facing.BACK;
            openCamera(cameraFacing);
        }
    }

    protected void updateCameraFlash(CameraControllerI.FlashMode flashMode) {
        CameraControllerI cameraManager = mCameraController;
        if (cameraManager.setFlashMode(flashMode)) {
            @DrawableRes int resId = flashMode == CameraControllerI.FlashMode.ON
                    ? R.drawable.ic_flash_on_white_36dp
                    : R.drawable.ic_flash_off_white_36dp;
            mFlashButton.setImageDrawable(ContextCompat.getDrawable(this, resId));
        }
    }

    @Override
    public void onMediaRecorderError(final Exception e) {
        Log.e(LOG_TAG, "Media recorder error", e);
        onError(e);
    }

    public void onError(final Exception e) {
        discardRecording();
        Intent intent = new Intent();
        intent.putExtra(RESULT_ERROR_PATH_KEY, e);
        setResult(RESULT_ERROR, intent);
        finish();
    }

    public void discardRecordingAndFinish() {
        discardRecording();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void finish() {
        if (mFocusManager != null) {
            mFocusManager.close();
        }
        super.finish();
    }

    public void discardRecording() {
        Log.i(LOG_TAG, "Discard recording");
        if (mParams == null) {
            return;
        }
        if (mSaveVideoTask != null) {
            mSaveVideoTask.cancel(true);
            mSaveVideoTask = null;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideUI();
            }
        });
        releaseResources();
        mMediaClipsRecorder.deleteClips();
        if (mVideoOutputFile != null && mVideoOutputFile.exists()) {
            mVideoOutputFile.delete();
            mVideoOutputFile = null;
        }
        if (mVideoThumbnailOutputFile != null && mVideoThumbnailOutputFile.exists()) {
            mVideoThumbnailOutputFile.delete();
            mVideoThumbnailOutputFile = null;
        }
    }

    protected void saveRecording() {
        stopRecording();
        if (mSaveVideoTask == null) {
            Log.i(LOG_TAG, "Saving recording");
            showProgress(R.string.preparing);

            // Start the task slightly delayed so that the UI can update first
            mSaveVideoTask = new SaveVideoTask();
            mSaveVideoTask.execute();
        }
    }

    protected void startPreviewActivity() {
        Log.i(LOG_TAG, "Saved recording. Starting preview");
       /* Intent previewIntent = new Intent(this, FFmpegPreviewActivity.class);
        previewIntent.putExtra(
                FFmpegPreviewActivity.REQUEST_PARAMS_KEY,
                FFmpegPreviewActivityParams.builder()
                        .setVideoFileUri(mVideoOutputFile)
                        .setThemeParams(ActivityThemeParams.Builder.mergeOnlyClass(
                                ActivityThemeParams.builder(), getThemeParams()).build())
                        .setConfirmation(true)
                        .build());
        startActivityForResult(previewIntent, PREVIEW_ACTIVITY_RESULT);*/
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mVideoOutputFile.getPath());
        int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        retriever.release();
        Intent intent = new Intent(FFmpegRecordActivity.this,PreviewActivity.class);
        intent.putExtra("fileUri",mVideoOutputFile.getPath());
        intent.putExtra("fileTitle",mVideoOutputFile.getName());
        intent.putExtra("duration",duration);
        intent.putExtra("fromRecorder",false);

        startActivity(intent);
    }

    protected void previewAccepted() {
        Log.d(LOG_TAG, "Preview accepted");
        Intent resultIntent = new Intent();
        resultIntent.setData(Uri.fromFile(mVideoOutputFile));
        if (mVideoThumbnailOutputFile != null) {
            resultIntent.putExtra(RESULT_THUMBNAIL_URI_KEY, Uri.fromFile(mVideoThumbnailOutputFile));
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    protected void releaseResources() {
        if (mParams == null) {
            return;
        }
        mMediaClipsRecorder.release();
        // Try to lock the camera if we can so that we can close it
        try {
            mCameraController.lock();
        } catch (Throwable expected) {}
        mCameraController.closeCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected class ActivityOrientationEventListener extends OrientationEventListener {
        protected int mOrientationDegrees;

        public ActivityOrientationEventListener() {
            super(FFmpegRecordActivity.this);
        }

        @Override
        public void onOrientationChanged(int orientation)
        {
            // Check that the configuration orientation matches the size orientation to ensure
            // re-layouts have already happened.
            // Also check if the orientation degrees don't match the degrees when we opened the
            // camera.
            mOrientationDegrees = ViewUtil.getContextRotation(getApplicationContext());
            boolean isDegreesLandscape = Util.isLandscapeAngle(mOrientationDegrees);
            View decorView = getWindow().getDecorView();
            boolean isSizeLandscape = decorView.getMeasuredWidth() > decorView.getMeasuredHeight();
            if (isDegreesLandscape == isSizeLandscape
                    && mOpenCameraOrientationDegrees != mOrientationDegrees
                    && mCameraController.isCameraOpen()) {
                openCamera(mCameraController.getCameraFacing());
            }
        }
    }

    protected class OpenCameraTask extends AsyncTask<CameraControllerI.Facing, Void, Exception> {

        @Override
        protected Exception doInBackground(CameraControllerI.Facing[] params) {
            try {
                CameraControllerI.Facing facing = Preconditions.checkNotNull(params[0]);
                CameraParams.Builder cameraParamsBuilder =
                        CameraParams.Builder.merge(CameraParams.builder(), getRecorderParams());
                if (getRecorderParams().getVideoCameraFacing() != facing) {
                    cameraParamsBuilder.setVideoCameraFacing(facing);
                }
                mCameraController
                        .openCamera(cameraParamsBuilder.build(), mOpenCameraOrientationDegrees);

            } catch (Exception e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            mMediaClipsRecorder.setFacing(mCameraController.getCameraFacing());
            mMediaClipsRecorder.setViewOrientationDegrees(
                    mCameraController.getPreviewDisplayOrientationDegrees());
            mOpenCameraTask = null;
            if (e != null) {
                Log.e(LOG_TAG, "Error opening camera", e);
                onError(e);
            }
        }
    }

    protected class SaveVideoTask extends AsyncTask<Object, Object, Exception>
            implements VideoTransformerTask.TaskListener {

        private VideoTransformerTask mVideoTransformerTask;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            releaseResources();

            Log.d(LOG_TAG, "VideoRecorder length " + mMediaClipsRecorder.getRecordedMillis());
        }

        @Override
        protected Exception doInBackground(Object... params) {
            mVideoOutputFile = new File(Uri.parse(mParams.getVideoOutputFileUri()).getPath());

            mVideoThumbnailOutputFile = mParams.getVideoThumbnailOutputFileUri().isPresent()
                    ? new File(Uri.parse(mParams.getVideoThumbnailOutputFileUri().get()).getPath())
                    : null;
/*            try {
                System.loadLibrary("swresample");
                ReLinker.Logger logger = new ReLinker.Logger() {
                    @Override
                    public void log(String message) {
                        Log.v("Relinker log " , message);
                    }
                };
                ReLinker.log(logger).recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");
                ReLinker.log(logger).recursively().loadLibrary(FFmpegRecordActivity.this, "swresample");

            }catch (NoClassDefFoundError e){e.printStackTrace();}*/
            FFmpegFrameRecorder recorder = Util.createFrameRecorder(mVideoOutputFile, getRecorderParams());
            mVideoTransformerTask = new VideoTransformerTask(recorder, getRecorderParams(),
                    mMediaClipsRecorder.getClips());
            mVideoTransformerTask.setProgressListener(this);

            try {
                mVideoTransformerTask.run();
                mMediaClipsRecorder.deleteClips();
                publishProgress();
                // Create video thumbnail
                if (mVideoThumbnailOutputFile != null) {
                    MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(mVideoOutputFile.getAbsolutePath());
                    Bitmap frame = metadataRetriever.getFrameAtTime(
                            0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
                    FileOutputStream outputStream = new FileOutputStream(mVideoThumbnailOutputFile);
                    frame.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                }

           //     editvideo(mVideoOutputFile.getPath());

                return null;
            } catch (Throwable e) {
                return (e instanceof Exception) ? (Exception) e : new RuntimeException(e);
            }
        }

        @Override
        public void onStart() {
            publishProgress(0f);
        }

        @Override
        public void onProgress(int progress, int total) {
            publishProgress(progress, total);
        }

        @Override
        public void onDone() {
            publishProgress(1f);
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            if (values.length == 0) {
                if (!mProgressBar.isIndeterminate()) {
                    mProgressBar.setIndeterminate(true);
                    mProgressBar.startAnimation();
                    mProgressText.setText(R.string.saving_video);
                }
            } else {
                if (mProgressBar.isIndeterminate()) {
                    mProgressBar.setIndeterminate(false);
                }
                float progress = getFloatValue(values[0]);
                if (values.length > 1) {
                    progress /= getFloatValue(values[1]);
                }
                mProgressBar.setProgress(progress * mProgressBar.getMaxProgress());
                mProgressText.setText(String.format(getString(R.string.encoding_percent), (int)(100 * progress)));
            }
        }

        protected float getFloatValue(Object o) {
            if (o instanceof Number) {
                return ((Number) o).floatValue();
            }
            throw new InvalidParameterException(String.format("Invalid progress value %s", o));
        }

        @Override
        protected void onPostExecute(Exception result) {
            super.onPostExecute(result);
            mVideoTransformerTask = null;

            if (result == null) {
                startPreviewActivity();
            } else {
                Log.e(LOG_TAG, "Error saving video", result);
                onError(result);
            }
        }
    }


    public void editvideo(String path) {
        // System.loadLibrary("EpMedia");

        final EpVideo epVideo = new EpVideo(path);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        int frames = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.e("Video Duration", epVideo.getClipDuration() + " " + frames);
        int duration = frames;
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        String fileTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        retriever.release();

        AppUtils.saveLogoIntoLocal(FFmpegRecordActivity.this);
        String username = AppUtils.getRegisteredUserId(FFmpegRecordActivity.this).getName();

        String text =  "@"+username+ "\n" + AppUtils.getSavedLocation(FFmpegRecordActivity.this);
        int picWidth = width / 5;
        int picHeight = height / 20;
        int textSize = height / 27;
        epVideo.addDraw(new EpDraw(Environment.getExternalStorageDirectory()
                + "/veblrAppData/veblr-logo.png",
                width - 150, height - 50, picWidth, picHeight, false));
        epVideo.addText(new EpText(width - 160, height - (picHeight + (2 * textSize) + 50), textSize,
                EpText.Color.Yellow,
                Environment.getExternalStorageDirectory() + "/veblrAppData/msyh.ttf", text, new EpText.Time(0, frames)));
        String outPath;

        sendCmd(path, epVideo);
    }

    private void sendCmd(String path, EpVideo epVideo) {
        EpEditor.exec(epVideo, new EpEditor.OutputOption
                        (path),
                new OnEditorListener(){

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onProgress(float progress) {

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean permissionsAllGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsAllGranted = false;
                    break;
                }
            }
            if (permissionsAllGranted) {

            }
        }
    }
}


/*
 implements TextureView.SurfaceTextureListener, View.OnClickListener {
    private static final String LOG_TAG = FFmpegRecordActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS = 1;

    private static final int PREFERRED_PREVIEW_WIDTH = 720;
    private static final int PREFERRED_PREVIEW_HEIGHT = 480;

    // both in milliseconds
    private static final long MIN_VIDEO_LENGTH = 1 * 1000;
    private static final long MAX_VIDEO_LENGTH = 300 * 1000;

    private FixedRatioCroppedTextureView mPreview;
    private ImageButton mBtnResumeOrPause;
    private ImageButton mBtnDone;
    private ImageButton mBtnSwitchCamera;
    private ImageButton mBtnReset;

    private int mCameraId;
    private Camera mCamera;
    private FFmpegFrameRecorder mFrameRecorder;
    private VideoRecordThread mVideoRecordThread;
    private AudioRecordThread mAudioRecordThread;
    private volatile boolean mRecording = false;
    private File mVideo;
    private LinkedBlockingQueue<FrameToRecord> mFrameToRecordQueue;
    private LinkedBlockingQueue<FrameToRecord> mRecycledFrameQueue;
    private int mFrameToRecordCount;
    private int mFrameRecordedCount;
    private long mTotalProcessFrameTime;
    private Stack<RecordFragment> mRecordFragments;

    private int sampleAudioRateInHz = 44100;

 *//*The sides of width and height are based on camera orientation.
    That is, the preview size is the size before it is rotated. *//*
    private int mPreviewWidth = PREFERRED_PREVIEW_WIDTH;
    private int mPreviewHeight = PREFERRED_PREVIEW_HEIGHT;
    // Output video size
    private int videoWidth = 720;
    private int videoHeight = 480;
    private int frameRate = 30;
    private int frameDepth = Frame.DEPTH_UBYTE;
    private int frameChannels = 2;

    // Workaround for https://code.google.com/p/android/issues/detail?id=190966
    private Runnable doAfterAllPermissionsGranted;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        mPreview = findViewById(R.id.camera_preview);
        mBtnResumeOrPause = findViewById(R.id.btn_resume_or_pause);
        mBtnDone = findViewById(R.id.btn_done);
        mBtnSwitchCamera = findViewById(R.id.btn_switch_camera);
        mBtnReset = findViewById(R.id.btn_reset);

//        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        setPreviewSize(videoWidth, videoHeight);
        mPreview.setCroppedSizeWeight(videoWidth, videoHeight);
        mPreview.setSurfaceTextureListener(this);
        mBtnResumeOrPause.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
        mBtnSwitchCamera.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);

        // At most buffer 10 Frame
        mFrameToRecordQueue = new LinkedBlockingQueue<>(10);
        // At most recycle 2 Frame
        mRecycledFrameQueue = new LinkedBlockingQueue<>(2);
        mRecordFragments = new Stack<>();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecorder();
        releaseRecorder(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (doAfterAllPermissionsGranted != null) {
            doAfterAllPermissionsGranted.run();
            doAfterAllPermissionsGranted = null;
        } else {
            String[] neededPermissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            List<String> deniedPermissions = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                // All permissions are granted
                doAfterAllPermissionsGranted();
            } else {
                String[] array = new String[deniedPermissions.size()];
                array = deniedPermissions.toArray(array);
                ActivityCompat.requestPermissions(this, array, REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseRecording();
        stopRecording();
        stopPreview();
        releaseCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean permissionsAllGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsAllGranted = false;
                    break;
                }
            }
            if (permissionsAllGranted) {
                doAfterAllPermissionsGranted = new Runnable() {
                    @Override
                    public void run() {
                        doAfterAllPermissionsGranted();
                    }
                };
            } else {
                doAfterAllPermissionsGranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FFmpegRecordActivity.this, "PERMISSIONS DENIED", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        startPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_resume_or_pause) {
            if (mRecording) {
                pauseRecording();
            } else {
                resumeRecording();
            }

        } else if (i == R.id.btn_done) {
            pauseRecording();
            // check video length
            if (calculateTotalRecordedTime(mRecordFragments) < MIN_VIDEO_LENGTH) {
                Toast.makeText(this,"VIDeo is too short", Toast.LENGTH_SHORT).show();
                return;
            }
            new FinishRecordingTask().execute();

        } else if (i == R.id.btn_switch_camera) {
            final SurfaceTexture surfaceTexture = mPreview.getSurfaceTexture();
            new ProgressDialogTask<Void, Integer, Void>(R.string.processing) {

                @Override
                protected Void doInBackground(Void... params) {
                    stopRecording();
                    stopPreview();
                    releaseCamera();

                    mCameraId = (mCameraId + 1) % 2;

                    acquireCamera();
                    startPreview(surfaceTexture);
                    startRecording();
                    return null;
                }
            }.execute();

        } else if (i == R.id.btn_reset) {
            pauseRecording();
            new ProgressDialogTask<Void, Integer, Void>(R.string.processing) {

                @Override
                protected Void doInBackground(Void... params) {
                    stopRecording();
                    stopRecorder();

                    startRecorder();
                    startRecording();
                    return null;
                }
            }.execute();
        }
    }

    private void doAfterAllPermissionsGranted() {
        acquireCamera();
        SurfaceTexture surfaceTexture = mPreview.getSurfaceTexture();
        if (surfaceTexture != null) {
            // SurfaceTexture already created
            startPreview(surfaceTexture);
        }
        new ProgressDialogTask<Void, Integer, Void>(R.string.processing) {

            @Override
            protected Void doInBackground(Void... params) {
                if (mFrameRecorder == null) {
                    initRecorder();
                    startRecorder();
                }
                startRecording();
                return null;
            }
        }.execute();
    }

    private void setPreviewSize(int width, int height) {
        if (MiscUtil.isOrientationLandscape(this)) {
            mPreview.setPreviewSize(width, height);
        } else {
            // Swap width and height
            mPreview.setPreviewSize(height, width);
        }
    }

    private void startPreview(SurfaceTexture surfaceTexture) {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = CameraHelper.getOptimalSize(previewSizes,
                PREFERRED_PREVIEW_WIDTH, PREFERRED_PREVIEW_HEIGHT);
        // if changed, reassign values and request layout
        if (mPreviewWidth != previewSize.width || mPreviewHeight != previewSize.height) {
            mPreviewWidth = previewSize.width;
            mPreviewHeight = previewSize.height;
            setPreviewSize(mPreviewWidth, mPreviewHeight);
            mPreview.requestLayout();
        }
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
//        parameters.setPreviewFormat(ImageFormat.NV21);
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(parameters);

        mCamera.setDisplayOrientation(CameraHelper.getCameraDisplayOrientation(
                this, mCameraId));

        // YCbCr_420_SP (NV21) format
        byte[] bufferByte = new byte[mPreviewWidth * mPreviewHeight * 3 / 2];
        mCamera.addCallbackBuffer(bufferByte);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {

            private long lastPreviewFrameTime;

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                long thisPreviewFrameTime = System.currentTimeMillis();
                if (lastPreviewFrameTime > 0) {
                    Log.d(LOG_TAG, "Preview frame interval: " + (thisPreviewFrameTime - lastPreviewFrameTime) + "ms");
                }
                lastPreviewFrameTime = thisPreviewFrameTime;

                // get video data
                if (mRecording) {
                    if (mAudioRecordThread == null || !mAudioRecordThread.isRunning()) {
                        // wait for AudioRecord to init and start
                        mRecordFragments.peek().setStartTimestamp(System.currentTimeMillis());
                    } else {
                        // pop the current record fragment when calculate total recorded time
                        RecordFragment curFragment = mRecordFragments.pop();
                        long recordedTime = calculateTotalRecordedTime(mRecordFragments);
                        // push it back after calculation
                        mRecordFragments.push(curFragment);
                        long curRecordedTime = System.currentTimeMillis()
                                - curFragment.getStartTimestamp() + recordedTime;
                        // check if exceeds time limit
                        if (curRecordedTime > MAX_VIDEO_LENGTH) {
                            pauseRecording();
                            new FinishRecordingTask().execute();
                            return;
                        }

                        long timestamp = 1000 * curRecordedTime;
                        Frame frame;
                        FrameToRecord frameToRecord = mRecycledFrameQueue.poll();
                        if (frameToRecord != null) {
                            frame = frameToRecord.getFrame();
                            frameToRecord.setTimestamp(timestamp);
                        } else {
                            Loader.load(opencv_core.class);
                            frame = new Frame(mPreviewWidth, mPreviewHeight, frameDepth, frameChannels);
                            frameToRecord = new FrameToRecord(timestamp, frame);
                        }
                        ((ByteBuffer) frame.image[0].position(0)).put(data);

                        if (mFrameToRecordQueue.offer(frameToRecord)) {
                            mFrameToRecordCount++;
                        }
                    }
                }
                mCamera.addCallbackBuffer(data);
            }
        });

        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallbackWithBuffer(null);
        }
    }

    private void acquireCamera() {
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private void initRecorder() {
        Log.i(LOG_TAG, "init mFrameRecorder");

        String recordedTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
       // mVideo = CameraHelper.getOutputMediaFile(recordedTime, CameraHelper.MEDIA_TYPE_VIDEO);
        mVideo = new File(Environment.getExternalStorageDirectory()+"/veblrAppData/"+recordedTime+"_veblrrecording.mp4");
        Log.i(LOG_TAG, "Output Video: " + mVideo);
        try {
             ReLinker.Logger logger = new ReLinker.Logger() {
                @Override
                public void log(String message) {
                    Log.v("HODOR", "(hold the door) " + message);
                }
            };


          ReLinker.log(logger).recursively().loadLibrary(this, "ffmpeg","1.3");
          ReLinker.log(logger).recursively().loadLibrary(this, "avutil");
            ReLinker.log(logger).recursively().loadLibrary(this, "avcodec");


            mFrameRecorder = new FFmpegFrameRecorder(mVideo, videoWidth, videoHeight, 2);
            mFrameRecorder.setFormat("mp4");
            mFrameRecorder.setSampleRate(sampleAudioRateInHz);
            mFrameRecorder.setFrameRate(frameRate);
            mFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            mFrameRecorder.setVideoOption("crf", "29");
            mFrameRecorder.setVideoOption("preset", "slower");
            mFrameRecorder.setVideoOption("tune", "zerolatency");
        }
        catch(NoClassDefFoundError noClassDefFoundError){

            noClassDefFoundError.printStackTrace();
            ReLinker.Logger logger = new ReLinker.Logger() {
                @Override
                public void log(String message) {
                    Log.v("HODOR", "(hold the door) " + message);
                }
            };
            ReLinker.log(logger).recursively().loadLibrary(this, "avutil");

        }
        catch (UnsatisfiedLinkError error){
            error.printStackTrace();
            System.loadLibrary("jniavutil");
        }

        Log.i(LOG_TAG, "mFrameRecorder initialize success");

    File outputrecordedvideo;
        try {
            outputrecordedvideo = new File(mVideo.getPath() + "/" + recordedTime
                    + ".mp4");
            Log.i("in save()", "after file");
            FileOutputStream out = new FileOutputStream(outputrecordedvideo);
            Log.i("in save()", "after outputstream");
            out.flush();
            out.close();
            Log.i("in save()", "after outputstream closed");
            //File parent = filename.getParentFile();
            ContentValues video = getVideoContent(outputrecordedvideo);
            Uri result = getContentResolver().insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, video);
            Toast.makeText(getApplicationContext(),
                    "File is Saved in  " + outputrecordedvideo+  "  "+result, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBroadcast(  new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mVideo) ));
    }

    private ContentValues getVideoContent(File outputrecordedvideo) {
        ContentValues video = new ContentValues();
        video.put(MediaStore.Video.Media.TITLE, getString(R.string.app_name));
        video.put(MediaStore.Video.Media.DISPLAY_NAME, "outputfile");
        video.put(MediaStore.Video.Media.DESCRIPTION, "App Video");
        video.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
        video.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        video.put(MediaStore.Video.Media.BUCKET_ID, outputrecordedvideo.toString()
                .toLowerCase().hashCode());
        video.put(MediaStore.Video.Media.BUCKET_DISPLAY_NAME, outputrecordedvideo.getName()
                .toLowerCase());
        video.put(MediaStore.Video.Media.SIZE, outputrecordedvideo.length());
        video.put(MediaStore.Video.Media.DATA, outputrecordedvideo.getAbsolutePath());
        return video;
    }


    private void releaseRecorder(boolean deleteFile) {
        if (mFrameRecorder != null) {
            try {
                mFrameRecorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            mFrameRecorder = null;

            if (deleteFile) {
                mVideo.delete();
            }
        }
    }

    private void startRecorder() {
        try {
            if(mFrameRecorder!=null)
            mFrameRecorder.start();
          //  else initRecorder();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecorder() {
        if (mFrameRecorder != null) {
            try {
                mFrameRecorder.stop();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }

        mRecordFragments.clear();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnReset.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startRecording() {
        mAudioRecordThread = new AudioRecordThread();
        mAudioRecordThread.start();
        mVideoRecordThread = new VideoRecordThread();
        mVideoRecordThread.start();
    }

    private void stopRecording() {
        if (mAudioRecordThread != null) {
            if (mAudioRecordThread.isRunning()) {
                mAudioRecordThread.stopRunning();
            }
        }

        if (mVideoRecordThread != null) {
            if (mVideoRecordThread.isRunning()) {
                mVideoRecordThread.stopRunning();
            }
        }

        try {
            if (mAudioRecordThread != null) {
                mAudioRecordThread.join();
            }
            if (mVideoRecordThread != null) {
                mVideoRecordThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAudioRecordThread = null;
        mVideoRecordThread = null;


        mFrameToRecordQueue.clear();
        mRecycledFrameQueue.clear();
    }

    private void resumeRecording() {
        if (!mRecording) {
            RecordFragment recordFragment = new RecordFragment();
            recordFragment.setStartTimestamp(System.currentTimeMillis());
            mRecordFragments.push(recordFragment);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnReset.setVisibility(View.VISIBLE);
                    mBtnSwitchCamera.setVisibility(View.INVISIBLE);
                }
            });
            mRecording = true;
        }
    }

    private void pauseRecording() {
        if (mRecording) {
            mRecordFragments.peek().setEndTimestamp(System.currentTimeMillis());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnSwitchCamera.setVisibility(View.VISIBLE);
                    mBtnReset.setVisibility(View.INVISIBLE);
                }
            });
            mRecording = false;
        }
    }

    private long calculateTotalRecordedTime(Stack<RecordFragment> recordFragments) {
        long recordedTime = 0;
        for (RecordFragment recordFragment : recordFragments) {
            recordedTime += recordFragment.getDuration();
        }
        return recordedTime;
    }

    class RunningThread extends Thread {
        boolean isRunning;

        public boolean isRunning() {
            return isRunning;
        }

        public void stopRunning() {
            this.isRunning = false;
        }
    }

    class AudioRecordThread extends RunningThread {
        private AudioRecord mAudioRecord;
        private ShortBuffer audioData;

        public AudioRecordThread() {
            int bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            audioData = ShortBuffer.allocate(bufferSize);
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            Log.d(LOG_TAG, "mAudioRecord startRecording");
            mAudioRecord.startRecording();

            isRunning = true;

 //ffmpeg_audio encoding loop
            while (isRunning) {
                if (mRecording && mFrameRecorder != null) {
                    int bufferReadResult = mAudioRecord.read(audioData.array(), 0, audioData.capacity());
                    audioData.limit(bufferReadResult);
                    if (bufferReadResult > 0) {
                        Log.v(LOG_TAG, "bufferReadResult: " + bufferReadResult);
                        try {
                            mFrameRecorder.recordSamples(audioData);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.d(LOG_TAG, "mAudioRecord stopRecording");
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
            Log.d(LOG_TAG, "mAudioRecord released");
        }
    }

    class VideoRecordThread extends RunningThread {

 @Override
        public void interrupt() {
            try {
                super.interrupt();
            } finally {
                // you can use any logging services that you already have
                System.out.println("--> Interrupted from thread: " + Thread.currentThread().getName());
                Thread.dumpStack();
            }
        }

        @Override
        public void run() {
            int previewWidth = mPreviewWidth;
            int previewHeight = mPreviewHeight;

            List<String> filters = new ArrayList<>();
            // Transpose
            String transpose = null;
            String hflip = null;
            String vflip = null;
            String crop = null;
            String scale = null;
            int cropWidth;
            int cropHeight;
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    switch (info.orientation) {
                        case 270:
                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                transpose = "transpose=clock_flip"; // Same as preview display
                            } else {
                                transpose = "transpose=cclock"; // Mirrored horizontally as preview display
                            }
                            break;
                        case 90:
                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                transpose = "transpose=cclock_flip"; // Same as preview display
                            } else {
                                transpose = "transpose=clock"; // Mirrored horizontally as preview display
                            }
                            break;
                    }
                    cropWidth = previewHeight;
                    cropHeight = cropWidth * videoHeight / videoWidth;
                    crop = String.format("crop=%d:%d:%d:%d",
                            cropWidth, cropHeight,
                            (previewHeight - cropWidth) / 2, (previewWidth - cropHeight) / 2);
                    // swap width and height
                    scale = String.format("scale=%d:%d", videoHeight, videoWidth);
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    switch (rotation) {
                        case Surface.ROTATION_90:
                            // landscape-left
                            switch (info.orientation) {
                                case 270:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                        hflip = "hflip";
                                    }
                                    break;
                            }
                            break;
                        case Surface.ROTATION_270:
                            // landscape-right
                            switch (info.orientation) {
                                case 90:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                        hflip = "hflip";
                                        vflip = "vflip";
                                    }
                                    break;
                                case 270:
                                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                        vflip = "vflip";
                                    }
                                    break;
                            }
                            break;
                    }
                    cropHeight = previewHeight;
                    cropWidth = cropHeight * videoWidth / videoHeight;
                    crop = String.format("crop=%d:%d:%d:%d",
                            cropWidth, cropHeight,
                            (previewWidth - cropWidth) / 2, (previewHeight - cropHeight) / 2);
                    scale = String.format("scale=%d:%d", videoWidth, videoHeight);
                    break;
                case Surface.ROTATION_180:
                    break;
            }
            // transpose
            if (transpose != null) {
                filters.add(transpose);
            }
            // horizontal flip
            if (hflip != null) {
                filters.add(hflip);
            }
            // vertical flip
            if (vflip != null) {
                filters.add(vflip);
            }
            // crop
            if (crop != null) {
                filters.add(crop);
            }
            // scale (to designated size)
            if (scale != null) {
                filters.add(scale);
            }
            ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");
            ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avcodec");

            FFmpegFrameFilter frameFilter = new FFmpegFrameFilter(TextUtils.join(",", filters),
                    previewWidth, previewHeight);
            frameFilter.setPixelFormat(avutil.AV_PIX_FMT_NV21);
            frameFilter.setFrameRate(frameRate);
            try {
                ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");
                ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avcodec");

                frameFilter.start();
            } catch (FrameFilter.Exception e) {
                e.printStackTrace();
                ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");
            }
            catch (NoClassDefFoundError error){
                error.printStackTrace();
                ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");

            }
            isRunning = true;
            FrameToRecord recordedFrame;

            while (isRunning || !mFrameToRecordQueue.isEmpty()) {
                try {
                    recordedFrame = mFrameToRecordQueue.take();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    try {
                    ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");
                    frameFilter.stop();
                    Thread.currentThread().interrupt();
                } catch (FrameFilter.Exception e) {
                    e.printStackTrace();
                }
                break;
                }

                if (mFrameRecorder != null) {
                    long timestamp = recordedFrame.getTimestamp();
                    if (timestamp > mFrameRecorder.getTimestamp()) {
                        mFrameRecorder.setTimestamp(timestamp);
                    }
                    long startTime = System.currentTimeMillis();
//                    Frame filteredFrame = recordedFrame.getFrame();
                    Frame filteredFrame = null;
                    try {
                        ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");

                        frameFilter.push(recordedFrame.getFrame());
                        filteredFrame = frameFilter.pull();
                    } catch (FrameFilter.Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        ReLinker.recursively().loadLibrary(FFmpegRecordActivity.this, "avutil");

                        mFrameRecorder.record(filteredFrame);
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    mTotalProcessFrameTime += processTime;
                    Log.d(LOG_TAG, "This frame process time: " + processTime + "ms");
                    long totalAvg = mTotalProcessFrameTime / ++mFrameRecordedCount;
                    Log.d(LOG_TAG, "Avg frame process time: " + totalAvg + "ms");
                }
                Log.d(LOG_TAG, mFrameRecordedCount + " / " + mFrameToRecordCount);
                mRecycledFrameQueue.offer(recordedFrame);
            }
        }

        public void stopRunning() {
            super.stopRunning();
            if (getState() == WAITING) {
                interrupt();
            }
        }
    }

    abstract class ProgressDialogTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

        private int promptRes;
        private ProgressDialog mProgressDialog;

        public ProgressDialogTask(int promptRes) {
            this.promptRes = promptRes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(FFmpegRecordActivity.this,null, getString(promptRes), true);
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress((Integer) values[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if(mProgressDialog!=null)
            mProgressDialog.dismiss();
        }
    }

    class FinishRecordingTask extends ProgressDialogTask<Void, Integer, Void> {

        public FinishRecordingTask() {
            super(R.string.processing);
        }

        @Override
        protected Void doInBackground(Void... params) {
            stopRecording();
            stopRecorder();
            releaseRecorder(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           //  String dest=   copyFile();


            Intent intent = new Intent(FFmpegRecordActivity.this, PreviewActivity.class);
            intent.putExtra("fileTitle", mVideo.getName());
            intent.putExtra("VideoFile",mVideo);
            Log.d("FILEURI ",Uri.fromFile(mVideo).toString());
            intent.putExtra("fileUri",mVideo.getPath());
            intent.putExtra("fromRecorder",true);

            startActivity(intent);
        }
    }

    private String copyFile() {

       String destPath = Environment.getExternalStorageDirectory() + "/veblrAppData/" + mVideo.getName();
        File root=new File(Environment.getExternalStorageDirectory(),getPackageName());

        File  file=new File(root,destPath);

        try {
            destPath = FileProvider.getUriForFile(FFmpegRecordActivity.this,
                    "com.veblr.android.veblrapp.fileprovider",
                    file).toString();
        } catch (IllegalArgumentException e) {

        }
       String path = Environment.getExternalStorageDirectory().toString()+"/pictures/"+getApplicationContext().getPackageName()
                +"/"+mVideo.getAbsolutePath();
        if (path == null) {
            finish();
        }

        AssetFileDescriptor videoAsset = null;
        try {
            videoAsset = getContentResolver().openAssetFileDescriptor(Uri.parse(path), "r");
            FileInputStream fis = videoAsset.createInputStream();
            File root1=new File(Environment.getExternalStorageDirectory(),getPackageName());

            if (!root1.exists()) {
                root1.mkdirs();
            }
            File f = new File(root,destPath);
            FileOutputStream fos = new FileOutputStream(f);

            try {

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            } finally {
                fos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){e.printStackTrace();}


        return destPath;
    }


}
*/