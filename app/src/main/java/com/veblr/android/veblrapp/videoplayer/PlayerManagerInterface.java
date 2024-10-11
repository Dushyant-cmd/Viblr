package com.veblr.android.veblrapp.videoplayer;

import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;

interface PlayerManagerInterface {
    MediaSource createMediaSource(
            Uri uri, @Nullable Handler handler, @Nullable MediaSourceEventListener listener);
}
