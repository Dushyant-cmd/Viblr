package com.veblr.android.veblrapp.videoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.ui.VideoWatchFragment;

import java.io.IOException;

public class PlayerManager implements AdsMediaSource.MediaSourceFactory, PlayerManagerInterface{

    private final ImaAdsLoader adsLoader;
    private final DataSource.Factory manifestDataSourceFactory;
    private final DataSource.Factory mediaDataSourceFactory;

    private SimpleExoPlayer player;
    private long contentPosition;



    public PlayerManager(Context context,String adTag) {
//    String adTag = context.getString(R.string.ad_tag_url);

       adsLoader = new ImaAdsLoader(context, Uri.parse(adTag));
        manifestDataSourceFactory =
                new DefaultDataSourceFactory(
                        context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        mediaDataSourceFactory =
                new DefaultDataSourceFactory(
                        context,
                        Util.getUserAgent(context, context.getString(R.string.app_name)),
                        new DefaultBandwidthMeter());
    }


    @Override
    public MediaSource createMediaSource(
            Uri uri, @Nullable Handler handler, @Nullable MediaSourceEventListener listener) {
        return buildMediaSource(uri, handler, listener);
    }

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri, new Handler(), new MediaSourceEventListener() {
            @Override
            public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

            }

            @Override
            public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

            }

            @Override
            public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

            }

            @Override
            public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

            }

            @Override
            public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

            }

            @Override
            public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

            }

            @Override
            public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

            }
        });
    }

    @Override
    public int[] getSupportedTypes() {
        return new int[] {C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};

    }

    public void init(Context context, PlayerView playerView, String[] uriStrings, VideoWatchFragment fragment) {
        // Create a default track selector.
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

;
        playerView.setPlayer(player);

        ////// added code
        Uri[] uris= new Uri[uriStrings.length];
        MediaSource[] mediaSources = new MediaSource[uriStrings.length];
        for (int i = 0; i < uriStrings.length; i++) {
           /* player.prepare(mediaSources[i]);
            player.seekTo(3, C.TIME_UNSET);
            player.setPlayWhenReady(true);*/
            uris[i] = Uri.parse(uriStrings[i]);
            mediaSources[i] = buildMediaSource(uris[i], new Handler(), new MediaSourceEventListener() {
                @Override
                public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

                }

                @Override
                public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

                }

                @Override
                public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

                }

                @Override
                public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

                }

                @Override
                public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

                }

                @Override
                public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

                }

                @Override
                public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

                }

                @Override
                public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

                }

                @Override
                public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

                }
            });
            String captionUrl = null;
           /* if(captionUrl != null) {
                Uri subtitleUri = Uri.parse(captionUrl);
                String mimeType = MimeTypes.APPLICATION_SUBRIP;
                if (captionUrl.endsWith(".vtt") == true)
                    mimeType = MimeTypes.TEXT_VTT;

                Format subtitleFormat = Format.createTextSampleFormat(
                        "0", // An identifier for the track. May be null.
                        mimeType, // The mime type. Must be set correctly.
                        Format.NO_VALUE, // Selection flags for the track.
                        "kr");

                MediaSource textMediaSource = new SingleSampleMediaSource.Factory(mediaDataSourceFactory).createMediaSource(subtitleUri, subtitleFormat, C.SELECTION_FLAG_AUTOSELECT, null, null);
                mediaSources[i] = new MergingMediaSource(mediaSources[i], textMediaSource);
            }*/
        }
        MediaSource mediaSource = new ConcatenatingMediaSource(mediaSources);

        ////// added code end

        // Compose the content media source into a new AdsMediaSource with both ads and content.
        MediaSource mediaSourceWithAds ;
        AdsMediaSource.EventListener mediaSourceEventListener = new AdsMediaSource.EventListener() {
            @Override
            public void onAdLoadError(IOException error) {

            }

            @Override
            public void onInternalAdLoadError(RuntimeException error) {

            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdTapped() {

            }
        };
        mediaSourceWithAds =
                new AdsMediaSource(
                        mediaSource,
                        this,
                        adsLoader,
                        playerView.getOverlayFrameLayout(),
                        new Handler(),
                        (AdsMediaSource.EventListener) mediaSourceEventListener);

        boolean haveResumePosition = fragment.getmResumeWindow() != C.INDEX_UNSET;

        if (haveResumePosition) {
            player.seekTo(fragment.getmResumeWindow(), fragment.getmResumePosition());
        }

        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(mediaSourceWithAds);
        player.setPlayWhenReady(true);

    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
           // player.seekTo(contentPosition);
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        adsLoader.release();
    }

    public boolean isVideoEnded(){
        boolean endstate = false;
        if (player != null) {
         if(player.getDuration()==player.getContentPosition())
            endstate=true;
         else  endstate = false;

    }
    return endstate;
    }
    // AdsMediaSource.MediaSourceFactory implementation.




    // Internal methods.

    private MediaSource buildMediaSource(
            Uri uri, @Nullable Handler handler, @Nullable MediaSourceEventListener listener) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        manifestDataSourceFactory)
                        .createMediaSource(uri, handler, listener);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
                        .createMediaSource(uri, handler, listener);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri, handler, listener);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri, handler, listener);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }


}
