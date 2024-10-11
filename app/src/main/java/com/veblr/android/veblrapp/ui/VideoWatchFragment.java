package com.veblr.android.veblrapp.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.CommentListAdapter;
import com.veblr.android.veblrapp.datasource.ResponseComment;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.Comment;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.repositories.VideoRepository;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerViewHolder;
import com.veblr.android.veblrapp.videoplayer.PlayerManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;


public class VideoWatchFragment extends Fragment  implements AdListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "videoitem";
    private static final String ARG_PARAM2 = "param2";
    private  NestedScrollView nestedScrollView;
    private ImageView btnLike;
    View view;
    Handler handler;
    Runnable runnable;
    int count = 0;
    private AdView adView;

    // TODO: Rename and change types of parameters
    private  TextView videoTitle,videoDesc,views,like,comment,userName,videoPostedDate,commentCount;
    private  EmojiEditText etComment;
    VIdeoItem video;
    private RecyclerView recyclerViewComments;
    private CircularImageView ivUserPostedIcon;
    private  ImageView btnMore,btnShare,btnSendCommnet;
    private LinearLayout llShareBTN;
    ProgressBar progressBar;
    private VIdeoItem vIdeoItem;
    PlayerManager manager;
    private  PlayerView playerView;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon, mVideoReport, mBack;
    String selectedReportCategory = "";
    private  FrameLayout mFullScreenButton;
    private Dialog mFullScreenDialog;
    private FrameLayout playerFrame;
    private androidx.appcompat.widget.AppCompatToggleButton btnfollow;
    boolean liked=true;
    long lastClickTime = 0;

     int mResumeWindow;
     long mResumePosition;

    private final String STATE_RESUME_WINDOW  = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";


    private OnFragmentInteractionListener mListener;
    public VideoWatchFragment() {
    }

    public static VideoWatchFragment newInstance(VIdeoItem vIdeoItemArrayList,boolean fromComments) {
        VideoWatchFragment fragment = new VideoWatchFragment();
        Bundle args = new Bundle();
        args.putSerializable(
                ARG_PARAM1, vIdeoItemArrayList);
        args.putBoolean("comm",fromComments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vIdeoItem = (VIdeoItem)getArguments().getSerializable(ARG_PARAM1);
        }
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_video_watch, container, false);
        this.view = view;
        llShareBTN=(LinearLayout)view.findViewById(R.id.llShare);

        ivUserPostedIcon = (CircularImageView)view.findViewById(R.id.ivUserImage) ;
        commentCount = (TextView)view.findViewById(R.id.tvCommentCount);
        videoTitle = (TextView)view.findViewById(R.id.tvVideoTitle);
        videoDesc = (TextView)view.findViewById(R.id.tvDesc);
        views = (TextView)view.findViewById(R.id.tvViews);
        like = (TextView)view.findViewById(R.id.tvLikes);
         btnLike = (ImageView) view.findViewById(R.id.btnLike);
        comment = (TextView)view.findViewById(R.id.tvComments);
        userName = (TextView) view.findViewById(R.id.tvUserName);
        videoPostedDate = (TextView) view.findViewById(R.id.tvVideoPostedDate);
        btnMore = (ImageView) view.findViewById(R.id.btnMore);
        btnShare =(ImageView)view.findViewById(R.id.btnShare);
        btnfollow = (AppCompatToggleButton)view.findViewById(R.id.btnFollow);
        playerFrame = (FrameLayout)view.findViewById(R.id.ivVideoPlayer);
        etComment = (EmojiEditText) view.findViewById(R.id.etComment);
        recyclerViewComments = (RecyclerView)view.findViewById(R.id.rvCommentList);
        nestedScrollView = (NestedScrollView)view.findViewById(R.id.ns);
        progressBar = (ProgressBar) view.findViewById(R.id.pbComment);
        btnSendCommnet = (ImageView)view.findViewById(R.id.btnSendComment);
        if(getActivity()!=null) {
            adView = new AdView(getActivity(), getString(R.string.facebook_placement_id), AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);

            //Comment By Rakesh
           //AudienceNetworkAds.isInAdsProcess(getContext());
           //adView.setAdListener(this);

            adContainer.addView(adView);
            adView.loadAd();
        }
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        }

        if (getArguments() != null) {
            vIdeoItem = (VIdeoItem)getArguments().getSerializable(ARG_PARAM1);

             playerView = view.findViewById(R.id.player_view);
          //   playerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(AppUtils.getDeviceHeight())/3));
             manager = new PlayerManager(getContext(),vIdeoItem.getVastTag());
            initFullscreenDialog();
            //mFullScreenIcon = inflater.inflate(R.layout.custom_player_controller,null) .findViewById(R.id.exo_fullscreen_icon);
            mFullScreenButton =(FrameLayout) inflater.inflate(R.layout.custom_player_controller,null).findViewById(R.id.exo_fullscreen_button);
            mFullScreenIcon =view.findViewById(R.id.exo_fullscreen_icon);
            mVideoReport =view.findViewById(R.id.exo_report_video);
            mBack =view.findViewById(R.id.exo_back);
            initFullscreenButton();
            if(vIdeoItem.getVideoMp4()!=null)
            manager.init(getContext(),playerView,new String[]{vIdeoItem.getVideoMp4()},VideoWatchFragment.this);
            videoTitle.setText(vIdeoItem.getVideoTitle());
            userName.setText(vIdeoItem.getVideoPosted());
            videoPostedDate.setText(vIdeoItem.getVideoUploadedDate());

            mVideoReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),"HIII",Toast.LENGTH_SHORT).show();
                    openVideoReportDialog();
                }
            });

            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

           /* ((FrameLayout) view.findViewById(R.id.exo_fullscreen_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"HIII",Toast.LENGTH_SHORT).show();
                }
            });*/

            if(vIdeoItem.getVideoComments()!=null)
                comment.setText(vIdeoItem.getVideoComments()+"");
            else  comment.setText("0");

            if(vIdeoItem.getVideoComments()!=null)
                commentCount.setText("Comments("+vIdeoItem.getVideoComments()+")");
            else  commentCount.setText("Comments(0)");

            if(vIdeoItem.getVideoViews()==null) views.setText("0");
                else views.setText(AppUtils.format(Long.valueOf(vIdeoItem.getVideoViews())));

            if(vIdeoItem.getVideoLikes()==null) like.setText("0");
            else
                like.setText(vIdeoItem.getVideoLikes());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                videoDesc.setText(Html.fromHtml(vIdeoItem.getVideoDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                videoDesc.setText(Html.fromHtml(vIdeoItem.getVideoDescription()).toString());
            }

            Glide.with(getContext()).asBitmap()
                    .load(vIdeoItem.getVideoChannelImage())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ivUserPostedIcon.setImageBitmap(resource);
                        }
                    });
        }

        if(getArguments()!=null){
            if(getArguments().getBoolean("comm"))
            etComment.setFocusable(true);
        }
        if(getArguments()!=null)
            getArguments().clear();

        llShareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, vIdeoItem.getVideoTitle());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "https://veblr.com/m/watch/"+vIdeoItem.getVideoId()+"/"+"\n \n "+ vIdeoItem.getVideoTitle()+
                                "\n \n  For more information go to\n \n App Package"+org.apache.commons.lang3.StringEscapeUtils.unescapeJava(" \uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46 ") +
                                Uri.parse( "https://play.google.com/store/apps/details?id=com.veblr.android.veblrapp"));
                if(getContext()!=null)
                getContext().startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoDesc.getVisibility()==View.GONE){
                    videoDesc.setVisibility(View.VISIBLE);
                    videoTitle.setMaxLines(5);
                }
                else{
                    videoDesc.setVisibility(View.GONE);
                    videoTitle.setMaxLines(2);
                }
            }
        });

        playerView.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playerView!=null)
                    Log.e("Player current duration",playerView.getPlayer().getCurrentPosition()+"");

                    if (playbackState == ExoPlayer.STATE_ENDED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                        ((VideoWatchActivity) (Objects.requireNonNull(getActivity()))).swipeRight();
                    }
                }
                if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                    //long delayMs;
                        if(playerView.getPlayer()!=null){
                            Log.e("Player current duration",playerView.getPlayer().getCurrentPosition()+"");
                        if (playerView.getPlayer().getContentPosition() >= 5000) {
                            views.setText(AppUtils.format(Long.valueOf(vIdeoItem.getVideoViews()) + 1));
                            sendView();
                        }
                        }


                }
             /*   if(playerView.getPlayer().getContentPosition()>=5000){
                    views.setText(AppUtils.format(Long.valueOf(vIdeoItem.getVideoViews())+1));
                    sendView();
                }*/
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });



        view.findViewById(R.id.lluserdeatails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = vIdeoItem.getVideoChannelLink();
                if(id!=null){
                    JsonObject obj = new JsonObject();
                    JsonObject payerReg = new JsonObject();
                    payerReg.addProperty("channel_name", id);
                    obj.add("param",payerReg);
                    Call<ChannelDetailResponse> call =  ApiService.getService(getContext())
                            .create(ApiInterface.class).getUserDetails(obj);
                    call.enqueue(new Callback<ChannelDetailResponse>() {
                        @Override
                        public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                            if(response.isSuccessful()){
                                try{
                                    List<Channel>  channels =  response.body().getUSERDetailResponse().getResultList();
                                    Channel channel =channels.get(0);

                                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                                    intent.putExtra("channel",(Serializable)channel );
                                    intent.putExtra("channelLink",id);
                                    startActivity(intent);
                                }

                                catch(NullPointerException ignored){}
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

                        }

                    });
                }

            }
        });
        view.findViewById(R.id.llComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               etComment.setFocusable(true);
                ((NestedScrollView)view.findViewById(R.id.ns)).fullScroll(View.FOCUS_DOWN);

                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        etComment.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                        etComment.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
                    }
                }, 200);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((VideoWatchActivity) Objects.requireNonNull(getActivity())).setSwipeListener(view);
        }
        mFullScreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("VideoWatchFragment","mFullScreenButton Click First");
                if (!mExoPlayerFullscreen) {
                    openFullscreenDialog();
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    closeFullscreenDialog();
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        if(vIdeoItem.isLikedbyME()){
            btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));
        }
        updateLikeButton();
        updateFollowButton();
        getComments();

      /*  btnLike.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                List<String> videolistLiked = new ArrayList<>();
                videolistLiked = AppUtils.getLikedVideos(getContext());
                if (!videolistLiked.isEmpty()) {

                    boolean b = false;

                    for (String c : videolistLiked) {

                        if (c.equalsIgnoreCase(vIdeoItem.getVideoId())) {
                            b = true;
                            break;
                        }
                    }
                    if (b) {
                        btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_like));
                        if (vIdeoItem.getVideoLikes() != null && !like.getText().equals("0")) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) - 1 + "");
                            like.setText(vIdeoItem.getVideoLikes());
                        } else {
                            vIdeoItem.setVideoLikes(vIdeoItem.getVideoLikes());
                            like.setText(vIdeoItem.getVideoLikes());
                        }
                        //local db
                        AppUtils.removeLikedVideo(getContext(), vIdeoItem.getVideoId());
                        vIdeoItem.setLikedbyME(false);

                        //send to server
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            AppUtils.unlikeTheVideo(vIdeoItem.getVideoId(), getContext());
                        }
                    } else {
                        btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));
                        if (vIdeoItem.getVideoLikes() != null) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                            like.setText(vIdeoItem.getVideoLikes());
                        } else {
                            vIdeoItem.setVideoLikes("1");
                            like.setText(vIdeoItem.getVideoLikes());
                        }

                        AppUtils.addLikedVideo(getContext(), vIdeoItem.getVideoId());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            AppUtils.likeTheVideo(vIdeoItem.getVideoId(),  getContext());
                        }

                    }
                }

                else {
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));

                    if (vIdeoItem.getVideoLikes() != null) {
                        vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                        like.setText(vIdeoItem.getVideoLikes());
                    } else {
                        vIdeoItem.setVideoLikes("1");
                        like.setText(vIdeoItem.getVideoLikes());
                    }

                    AppUtils.addLikedVideo(getContext(), vIdeoItem.getVideoId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        AppUtils.likeTheVideo(vIdeoItem.getVideoId(),  getContext());
                    }

                }
            }
        });*/
        btnfollow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                boolean b= true;
                Channel ch=null;
                List<Channel> channels = new ArrayList<>();
                if( AppUtils.getFavoriteUser(getContext())!=null)channels =  AppUtils.getFavoriteUser(getContext());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (Channel c: channels) {
                        if(c.getChName()!=null && c.getChName().equalsIgnoreCase(vIdeoItem.getVideoChannelLink()))
                        {
                            b=false;
                            ch =c;
                            break;
                        }

                    }
                }
                if(b){
                    vIdeoItem.setFollowedByMe(true);
                    btnfollow.setChecked(true);
                    btnfollow.setTextColor(Color.WHITE);
                   Toast toast = Toast.makeText(getContext(), "You now follow " + vIdeoItem.getVideoPosted() + "", Toast.LENGTH_LONG);
                    TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                    toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    toast.show();

                }
                else{
                    vIdeoItem.setFollowedByMe(false);
                    btnfollow.setChecked(false);
                    btnfollow.setTextColor(Color.parseColor("#0a90ed"));
                   Toast toast= Toast.makeText(getContext(),vIdeoItem.getVideoPosted()+" is unfollowed by you",
                            Toast.LENGTH_LONG);
                    TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                    toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    toast.show();
                }
                getUserPostedDetails(vIdeoItem.getVideoChannelLink(),getContext(),true,b);

            }
        });

        btnSendCommnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Call<com.veblr.android.veblrapp.model.Response> call ;
                    if(AppUtils.getRegisteredUserId(getContext())!=null)
                    { call= ApiService.getService(getContext()).create(ApiInterface.class)
                            .addCommentToVideo(AppUtils.getJSonOBJForAddingCommentRegUser(
                                    vIdeoItem.getVideoId(),etComment.getText().toString(),
                                    AppUtils.getRegisteredUserId(getContext()).getUserId(),AppUtils.getRegisteredUserId(getContext()).getChannel().get(0).getChId()));  }
                   else{ call= ApiService.getService(getContext()).create(ApiInterface.class)
                                    .addCommentToVideo(AppUtils.getJSonOBJForAddingComment(
                                    vIdeoItem.getVideoId(),etComment.getText().toString(),
                                            AppUtils.getAppUserId(getContext()).getGuestUserId()));}
                   hideKeyboardFrom(getContext(),view);
            if(etComment.getText().toString().toCharArray().length>2 && etComment.getText().toString().toCharArray().length<499){
                    {
                        etComment.setText("");
                        call.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                           if( response.isSuccessful())
                           {
                               commentCount.setText("Comments("+(Integer.parseInt(comment.getText().toString())+1)+")");
                               comment.setText(Integer.parseInt(comment.getText().toString())+1+"");
                               getComments();
                               recyclerViewComments.setVisibility(View.GONE);
                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                   Objects.requireNonNull(recyclerViewComments.getLayoutManager()).scrollToPosition(0);
                               }
                               nestedScrollView.fullScroll(View.FOCUS_UP);

                           }
                        }

                        @Override
                        public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                        }
                    });
                }
                }
                else {
                    AlertDialog alertDialog =new AlertDialog.Builder(getContext()).create();
                    alertDialog.setMessage("Enter text length between 3 to 499 ");
                    alertDialog.show();
                }
            }
        });
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TEXT IS",s.toString()+"\n");
                StringEscapeUtils.unescapeJava(s.toString());
                Log.e("TEXT IS",s.toString()+"\n");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        view.findViewById(R.id.llLike).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - lastClickTime < 3000){
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();

                if (Objects.requireNonNull(AppUtils.getLikedVideos(getContext())!=null)) {


                    for (String c : Objects.requireNonNull(AppUtils.getLikedVideos(getContext()))) {
                        if (Objects.equals(c, vIdeoItem.getVideoId())) {
                            vIdeoItem.setLikedbyME(true);
                            break;
                        }
                    }
                    if (vIdeoItem.isLikedbyME() ) {
                        btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_like));
                       /* ImageViewCompat.setImageTintList
                                (likeButton, ColorStateList.valueOf(Color.parseColor("#414141")));*/
                        //like.setTextColor(Color.parseColor("#414141"));
                        if (Integer.parseInt(vIdeoItem.getVideoLikes()) > 0)
                            like.setTextColor(Color.parseColor("#414141"));
                        if (vIdeoItem.getVideoLikes() != null && !like.getText().equals("0")) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) - 1 + "");
                            like.setText(vIdeoItem.getVideoLikes());
                            //local db
                            AppUtils.removeLikedVideo(getContext(), vIdeoItem.getVideoId());
                            vIdeoItem.setLikedbyME(false);
                            //send to server
                            AppUtils.unlikeTheVideo(vIdeoItem.getVideoId(),  getContext());
                        }
                    } else {
                        btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));
                        if (vIdeoItem.getVideoLikes() != null) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                            like.setText(vIdeoItem.getVideoLikes());
                            vIdeoItem.setLikedbyME(true);
                        } else {
                            vIdeoItem.setVideoLikes("1");
                            like.setText(vIdeoItem.getVideoLikes());
                        }

                        AppUtils.addLikedVideo(getContext(), vIdeoItem.getVideoId());
                        AppUtils.likeTheVideo(vIdeoItem.getVideoId(), getContext());
                    }
                }
                else {
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));

                   /* ImageViewCompat.setImageTintList
                            (likeButton, ColorStateList.valueOf(Color.parseColor("#0a90ed")));*/
                    // likes.setTextColor(Color.parseColor("#0a90ed"));
                    if (vIdeoItem.getVideoLikes() != null) {
                        vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                        like.setText(vIdeoItem.getVideoLikes());
                    } else {
                        vIdeoItem.setVideoLikes("1");
                        like.setText(vIdeoItem.getVideoLikes());
                    }

                    AppUtils.addLikedVideo(getContext(), vIdeoItem.getVideoId());
                    AppUtils.likeTheVideo(vIdeoItem.getVideoId(), getContext());
                }
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendView() {
        Call<com.veblr.android.veblrapp.model.Response> responseCall;

            if(AppUtils.getRegisteredUserId(getContext())!=null)
            {
                JsonObject obj = new JsonObject();
                JsonObject payerReg = new JsonObject();
                payerReg.addProperty("video_id", vIdeoItem.getVideoId());
                payerReg.addProperty("user_id",
                        Objects.requireNonNull(AppUtils.getRegisteredUserId(getContext()).getUserId()));
                payerReg.addProperty("channel_id",AppUtils.getRegisteredUserId(getContext()).getChannel().get(0).getChId());

                obj.add("param",payerReg);
                responseCall =
                        ApiService.getService(getContext()).create(ApiInterface.class).sendViewsFromRegAppUser(obj);

                responseCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                    @Override
                    public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                        if(response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){
                            count ++;
                        }
                    }

                    @Override
                    public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                    }
                });

            }
            else {
                JsonObject obj = new JsonObject();
                JsonObject payerReg = new JsonObject();
                payerReg.addProperty("video_id", vIdeoItem.getVideoId());
                obj.add("param",payerReg);
                responseCall =
                    ApiService.getService(getContext()).create(ApiInterface.class).sendViewsFromAppUser(obj);
                responseCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                    @Override
                    public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                        if(response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){
                             count++;
                        }
                    }

                    @Override
                    public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                    }
                });

            }



    }

    private void updateLikeButton() {


        List<String> videolistLiked = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if( AppUtils.getLikedVideos(Objects.requireNonNull(getContext()))!=null)videolistLiked =  AppUtils.getLikedVideos(getContext());
        }

        assert videolistLiked != null;

        for (String c :videolistLiked) {
            if(c.equalsIgnoreCase(vIdeoItem.getVideoId())) {
                btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_icon_liked));
                vIdeoItem.setLikedbyME(true);
                break;
            }
        }}

    private void updateFollowButton() {
        List<Channel> channels = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if( AppUtils.getFavoriteUser(Objects.requireNonNull(getContext()))!=null)
                channels = AppUtils.getFavoriteUser(getContext());
        }

        if(channels!=null &&channels.size()>0 && vIdeoItem!=null){
        for (Channel c :channels) {
            if(c.getChName()!=null && vIdeoItem!=null){
            if(c.getChName().equalsIgnoreCase(vIdeoItem.getVideoChannelLink())) {
                Log.d("channel is",c.getChNameDisp());
                btnfollow.setChecked(true);
                btnfollow.setTextColor(Color.WHITE);
                vIdeoItem.setFollowedByMe(true);
                break;
            }
            else{
                btnfollow.setChecked(false);
                btnfollow.setTextColor(Color.parseColor("#0a90ed"));
                vIdeoItem.setFollowedByMe(false);
            }
            }
        }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getUserPostedDetails(String userId, final Context context,final boolean forFollowing,
                              final  boolean isFollowing){
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("channel_name", userId);
        obj.add("param",payerReg);
        Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call =  ApiService.getService(context)
                .create(ApiInterface.class).getUserDetails(obj);

                call.enqueue(new Callback<com.veblr.android.veblrapp.model.ChannelDetailResponse>() {
            @Override
            public void onResponse(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Response<com.veblr.android.veblrapp.model.ChannelDetailResponse> response) {
                if(response.isSuccessful()){
                    try{
                        List<Channel>  channels =  response.body().getUSERDetailResponse().getResultList();
                        Channel channel =channels.get(0);
                        if(forFollowing){
                            addittofollowingList(channel,isFollowing);
                        }
                        else{
                            Intent intent = new Intent(context,ProfileActivity.class);
                            intent.putExtra("channel",(Serializable)channel );
                            intent.putExtra("channelLink",vIdeoItem.getVideoChannelLink());
                            context.startActivity(intent);}
                    }
                    catch(NullPointerException ignored){}
                }
            }

            @Override
            public void onFailure(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Throwable t) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addittofollowingList(Channel videoChannel, boolean b) {

            Call<com.veblr.android.veblrapp.model.Response> responseCall;
            if(b) {
                if(AppUtils.getRegisteredUserId(getContext())!=null)
                {
                    responseCall =
                            ApiService.getService(getContext()).create(ApiInterface.class).followaChannel
                                    (AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannel.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(getContext())).getGuestUserId() ,
                                            Objects.requireNonNull(AppUtils.getRegisteredUserId(getContext()).getUserId()),AppUtils.getRegisteredUserId(getContext()).getChannel().get(0).getChId()));
                    AppUtils.addFavoriteUser(getContext(),videoChannel);

                }
                else { responseCall =
                        ApiService.getService(getContext()).create(ApiInterface.class).followaChannel
                                (AppUtils.getJSonOBJForFolloworUnfollow(videoChannel.getChId(),
                                        Objects.requireNonNull(AppUtils.getAppUserId(getContext())).getGuestUserId()));
                AppUtils.addFavoriteUser(getContext(),videoChannel);
                }
                FollowFragment.updateFollowedList(getContext());

            }
            else {
                if(AppUtils.getRegisteredUserId(getContext())!=null)
                {responseCall =
                        ApiService.getService(getContext()).create(ApiInterface.class).unFollowaChannel
                                (AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannel.getChId(),
                                        Objects.requireNonNull(AppUtils.getAppUserId(getContext())).getGuestUserId(), Objects.requireNonNull(AppUtils.getRegisteredUserId(getContext()).getUserId())
                                        ,AppUtils.getRegisteredUserId(getContext()).getChannel().get(0).getChId()));
                    AppUtils.removeFavoriteUser(getContext(), videoChannel.getChId());

                }
                else {
                    responseCall =
                            ApiService.getService(getContext()).create(ApiInterface.class).unFollowaChannel
                                    (AppUtils.getJSonOBJForFolloworUnfollow(videoChannel.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(getContext())).getGuestUserId()));
                    AppUtils.removeFavoriteUser(getContext(), videoChannel.getChId());

                }
                FollowFragment.updateFollowedList(getContext());

            }

            responseCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                @Override
                public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                    if(response.isSuccessful()){

                    }
                }

                @Override
                public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                }
            });




    }

    private void getComments() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewComments.setFocusable(false);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        recyclerViewComments.setHasFixedSize(true);
        Call<ResponseComment> responseCommentCall =
        ApiService.getService(getContext()).create(ApiInterface.class).getCommentListOfVideo(AppUtils.getJSonOBJForCommentList(vIdeoItem.getVideoId()));
        responseCommentCall.enqueue(new Callback<ResponseComment>() {
            @Override
            public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body().getResonse() != null) {
                    List<Comment> commentList = response.body().getResonse().getResult();
                    recyclerViewComments.setVisibility(View.VISIBLE);
                    recyclerViewComments.setAdapter(new CommentListAdapter(commentList, getContext()));
                }
            }
            @Override
            public void onFailure(Call<ResponseComment> call, Throwable t) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d("FB AD BANNER","AD Error"+adError.getErrorMessage());
    }

    @Override
    public void onAdLoaded(Ad ad) {
    Log.d("FB AD BANNER","AD LOADED");
    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(playerView!=null){
        playerView.getPlayer().setPlayWhenReady(false);
        playerView.getPlayer().getPlaybackState();}
        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();

    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO:player resume properly after going to phone home and revoke
        if(playerView!=null)
            manager.reset();
        if (mExoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_fullscreen_exit));
            mFullScreenDialog.show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        if(playerView!=null)
            playerView.getPlayer().getPlaybackState();
        if(playerView!=null)
            Log.e("Player onstart",playerView.getPlayer().getCurrentPosition()+"");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(playerView!=null){
            playerView.getPlayer().setPlayWhenReady(false);
            playerView.getPlayer().getPlaybackState();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(playerView!=null)
            manager.release();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        progressBar = new ProgressBar(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressBar.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
        }

    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

            outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
            outState.putLong(STATE_RESUME_POSITION, mResumePosition);
            outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

            super.onSaveInstanceState(outState);
            outState.clear();

    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen) {
                    closeFullscreenDialog();
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                super.onBackPressed();
            }
        };
    }


    private void openFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_fullscreen_exit));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        (playerFrame).addView(playerView);
        mExoPlayerFullscreen = false;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_fullscreen_enter));
    }


    private void initFullscreenButton() {


        mFullScreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("VideoWatchFragment","mFullScreenButton Click Second");
                if (!mExoPlayerFullscreen) {
                    openFullscreenDialog();
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    closeFullscreenDialog();
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
    }
    public int getmResumeWindow()
    {
        return mResumeWindow;
    }

    public long getmResumePosition()
    {
        return mResumePosition;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openVideoReportDialog(){
        String[] reportCategory = {"Violent or repulsive content","Hateful or abusive content","child abuse"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Report Video");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(reportCategory, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedReportCategory = reportCategory[which];
            }
        });
        builder.setPositiveButton("Report", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        alert.show();
        //builder.show();
    }
}
