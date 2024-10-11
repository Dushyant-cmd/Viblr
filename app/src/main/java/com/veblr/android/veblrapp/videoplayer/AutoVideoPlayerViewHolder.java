package com.veblr.android.veblrapp.videoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.repositories.VideoRepository;
import com.veblr.android.veblrapp.ui.CircularImageView;
import com.veblr.android.veblrapp.ui.FollowFragment;
import com.veblr.android.veblrapp.ui.ProfileActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoVideoPlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    Uri uri;
    public onItemClickListener mListener;

    FrameLayout media_container;
    TextView title;
    ImageView thumbnail, volumeControl;
    CircularImageView ivUserChannelIcon;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    TextView videoTitle, views, likes, shares,comment,duration;
    androidx.appcompat.widget.AppCompatToggleButton followButton;
    ImageView shareButton, likeButton;
    boolean shared = true;
    LinearLayout userIcon;
    Context context;
    VIdeoItem vIdeoItem;
    boolean fromComments = false;
    VideoRepository videoRepository;
    onitemSetFollowing onitemSetFollowing;
    public AutoVideoPlayerViewHolder(@NonNull View itemView, final Context context,
                                     onItemClickListener onItemClickListener,onitemSetFollowing onitemSetFollowingListener) {
        super(itemView);
        parent = itemView;
        mListener  =onItemClickListener;
        this.onitemSetFollowing = onitemSetFollowingListener;
        this.context = context;
        userIcon = itemView.findViewById(R.id.forUserCLick);
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        thumbnail.setLayoutParams(new FrameLayout.LayoutParams(AppUtils.getDeviceWidth(),(int)(AppUtils.getDeviceWidth()*56)/100));
        title = itemView.findViewById(R.id.tvUserName);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        videoTitle = itemView.findViewById(R.id.tvVideoTitle);
        followButton =(androidx.appcompat.widget.AppCompatToggleButton) itemView.findViewById(R.id.btnFollow);
        shareButton = itemView.findViewById(R.id.btnShare);
        likeButton = itemView.findViewById(R.id.btnLike);
        views = itemView.findViewById(R.id.tvViews);
        likes = itemView.findViewById(R.id.tvLikes);
        shares = itemView.findViewById(R.id.tvShare);
        comment = itemView.findViewById(R.id.tvcomment);
        duration  = itemView.findViewById(R.id.tvDuration);
        ivUserChannelIcon = itemView.findViewById(R.id.ivUserImage);

        itemView.setOnClickListener(this);
         videoRepository = new VideoRepository(context);


        followButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                boolean b=  true;

                if(AppUtils.getFavoriteUser(context)!=null){
                for (Channel c: Objects.requireNonNull(AppUtils.getFavoriteUser(context))) {
                if(c.getChName()!=null && c.getChName().equalsIgnoreCase(vIdeoItem.getVideoChannelLink()))
                {
                    b=false;
                    break;
                }
                }
                }
             if(b ){
                 vIdeoItem.setFollowedByMe(true);
                 followButton.setChecked(true);
                 followButton.setTextColor(Color.WHITE);
                 Toast.makeText(context,
                         HtmlCompat.fromHtml("<font color='white'>You now follow " + vIdeoItem.getVideoPosted()+"</font>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                         Toast.LENGTH_LONG).show();
             }
             else   {
                 vIdeoItem.setFollowedByMe(false);
                 followButton.setChecked(false);
                 followButton.setTextColor(Color.parseColor("#0a90ed"));
                //Toast toast= Toast.makeText(context,vIdeoItem.getVideoPosted()+" is unfollowed by you", Toast.LENGTH_LONG);
                 //TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                 //toastMessage.setTextColor(context.getResources().getColor(android.R.color.white));
                 //toast.show();
                 Toast.makeText(context,
                         HtmlCompat.fromHtml("<font color='white'>"+vIdeoItem.getVideoPosted()+" is unfollowed by you</font>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                         Toast.LENGTH_LONG).show();
             }
             getUserPostedDetails(vIdeoItem.getVideoChannelLink(),context,true,b);
            }

        });
        //     shareButton.setEnabled(false);
        itemView.findViewById(R.id.lShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shared) {
                    ImageViewCompat.setImageTintList(shareButton, ColorStateList.valueOf(Color.parseColor("#0a90ed")));
                    shares.setTextColor(Color.parseColor("#0a90ed"));
                    shared = false;

                } else {
                    ImageViewCompat.setImageTintList(shareButton, ColorStateList.valueOf(Color.parseColor("#414141")));
                    shares.setTextColor(Color.parseColor("#414141"));
                    shared = true;
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, vIdeoItem.getVideoTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT,
                        "https://veblr.com/watch/"+vIdeoItem.getVideoId()+"/"+"\n \n "+ vIdeoItem.getVideoTitle()+
                                "\n \n  For more information go to\n \n App Package"+org.apache.commons.lang3.StringEscapeUtils.unescapeJava(" \uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46 ") +
                                Uri.parse( "https://play.google.com/store/apps/details?id=com.veblr.android.veblrapp"));
                context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        //   likeButton.setEnabled(false);
        itemView.findViewById(R.id.lLIke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(AppUtils.getLikedVideos(context)!=null)) {


                    for (String c : Objects.requireNonNull(AppUtils.getLikedVideos(context))) {

                        if (Objects.equals(c, vIdeoItem.getVideoId())) {
                            vIdeoItem.setLikedbyME(true);
                            break;
                        }
                    }
                    Log.e("Liked Clicked", "Like Item Title: "+vIdeoItem.getVideoTitle());
                    if (vIdeoItem.isLikedbyME() ) {
                        likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_like));
                       /* ImageViewCompat.setImageTintList
                                (likeButton, ColorStateList.valueOf(Color.parseColor("#414141")));*/
                        //likes.setTextColor(Color.parseColor("#414141"));
                        if (vIdeoItem.getVideoLikes() != null && !likes.getText().equals("0")) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) - 1 + "");
                            likes.setText(vIdeoItem.getVideoLikes());
                            //local db
                            AppUtils.removeLikedVideo(context, vIdeoItem.getVideoId());
                            vIdeoItem.setLikedbyME(false);
                            //send to server
                            AppUtils.unlikeTheVideo(vIdeoItem.getVideoId(),  context);

                        }
                    } else {
                        likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_liked));
                        if (vIdeoItem.getVideoLikes() != null) {
                            vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                            likes.setText(vIdeoItem.getVideoLikes());
                            vIdeoItem.setLikedbyME(true);

                        } else {
                            vIdeoItem.setVideoLikes("1");
                            likes.setText(vIdeoItem.getVideoLikes());
                        }

                        AppUtils.likeTheVideo(vIdeoItem.getVideoId(), context);
                        AppUtils.addLikedVideo(context, vIdeoItem.getVideoId());
                    }

                }
                  else {
                    likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_liked));

                   /* ImageViewCompat.setImageTintList
                            (likeButton, ColorStateList.valueOf(Color.parseColor("#0a90ed")));*/
                   // likes.setTextColor(Color.parseColor("#0a90ed"));
                    if (vIdeoItem.getVideoLikes() != null) {
                        vIdeoItem.setVideoLikes(Integer.parseInt(vIdeoItem.getVideoLikes()) + 1 + "");
                        likes.setText(vIdeoItem.getVideoLikes());
                    } else {
                        vIdeoItem.setVideoLikes("1");
                        likes.setText(vIdeoItem.getVideoLikes());
                    }

                    AppUtils.addLikedVideo(context, vIdeoItem.getVideoId());
                    AppUtils.likeTheVideo(vIdeoItem.getVideoId(), context);


                }
            }
        });

       /* itemView.findViewById(R.id.lComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromComments = true;

            }
        });
*/
    }

    public void onBind(final VIdeoItem mediaObject, RequestManager requestManager,int position) {
        this.requestManager = requestManager;
        parent.setTag(this);
        this.vIdeoItem = mediaObject;
        title.setText(mediaObject.getVideoPosted());
        videoTitle.setText(mediaObject.getVideoTitle());

       /* if(mediaObject.isLikedbyME()){ ImageViewCompat.setImageTintList
                (likeButton, ColorStateList.valueOf(Color.parseColor("#0a90ed")));
            likes.setTextColor(Color.parseColor("#0a90ed"));}
*/

        List<Channel> channels = new ArrayList<>();
        if( AppUtils.getFavoriteUser(context)!=null)channels =  AppUtils.getFavoriteUser(context);
        if(channels.isEmpty()) {
            getFollowingORNot(vIdeoItem.getVideoChannelLink());
        } else{
            for (Channel c :channels) {
                try{
                if(c.getChName().equalsIgnoreCase(vIdeoItem.getVideoChannelLink())) {
                    Log.d("channel is",c.getChNameDisp());
                    followButton.setChecked(true);
                    followButton.setTextColor(Color.WHITE);
                    vIdeoItem.setFollowedByMe(true);
                    break;
                }
                else{
                    followButton.setChecked(false);
                    followButton.setTextColor(Color.parseColor("#0a90ed"));
                    vIdeoItem.setFollowedByMe(false);
                }}catch (NullPointerException e){e.printStackTrace();}
            }
        }

        List<String> videolistLiked = new ArrayList<>();
        if( AppUtils.getLikedVideos(context)!=null) {
            videolistLiked =  AppUtils.getLikedVideos(context);
            for (String c :videolistLiked) {
                if(Objects.equals(c,vIdeoItem.getVideoId())) {

                    likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_icon_liked));

                    vIdeoItem.setLikedbyME(true);
                    break;
                }
        }
        }
        Glide.with(context).asBitmap()
                .load(mediaObject.getVideoChannelImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) { ivUserChannelIcon.setImageBitmap(resource);
                    }
                });
        this.requestManager
                .load(mediaObject.getVideoPoster())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(thumbnail);
        views.setText(AppUtils.format(Long.valueOf(mediaObject.getVideoViews())));
        if(mediaObject.getVideoLikes()==null)
            likes.setText("0");
        else
            likes.setText(mediaObject.getVideoLikes()+"");

        duration.setText(mediaObject.getVideoDuration());
        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getUserPostedDetails(vIdeoItem.getVideoChannelLink(),context,false,false);
            }
        });

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserPostedDetails(vIdeoItem.getVideoChannelLink(),context,false,false);
            }
        });
        if(vIdeoItem.getVideoComments()!=null)
        comment.setText(vIdeoItem.getVideoComments()+"");
        else  comment.setText("0");


    }

    @Override
    public void onClick(View v) {
      mListener.onRecyclerItemClick(v,getPosition(),fromComments);
    }
    public static interface onItemClickListener {
        public void onRecyclerItemClick(View view , int position,boolean fromComments);
    }
    public static interface onitemSetFollowing {
        public void ongetItemFollowing( int position,VIdeoItem isfollowed);
    }
    public void setVisibilityGoneFollow(){
        followButton.setVisibility(View.GONE);

    }
    void getUserPostedDetails(String userId, final Context context,final boolean forFollowing,final  boolean isFollowing){
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("channel_name", userId);
        obj.add("param",payerReg);
        Call<ChannelDetailResponse> call =  ApiService.getService(context)
                .create(ApiInterface.class).getUserDetails(obj);
        call.enqueue(new Callback<ChannelDetailResponse>() {
            @Override
            public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                if(response.isSuccessful()){
                  try{
                    List<Channel>  channels =  response.body().getUSERDetailResponse().getResultList();
                    Channel channel =channels.get(0);
                    if(forFollowing){
                        additToFollowingChannel(channel,isFollowing);
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
            public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void additToFollowingChannel(final Channel videoChannel, boolean b ) {
            Call<com.veblr.android.veblrapp.model.Response> responseCall;
            if(b) {
                if(AppUtils.getRegisteredUserId(context)!=null)
                {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class).followaChannel
                                    (AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannel.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId() , Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));
                    AppUtils.addFavoriteUser(context,videoChannel);
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
              else { if(AppUtils.getAppUserId(context)!=null){
                  responseCall =
                    ApiService.getService(context).create(ApiInterface.class).followaChannel
                            (AppUtils.getJSonOBJForFolloworUnfollow(videoChannel.getChId(),
                             Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                    AppUtils.addFavoriteUser(context,videoChannel);
                        FollowFragment.updateFollowedList(context);
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

              else{AppUtils.createNewUser(context,AppUtils.getDeviceUniqueId(context));}
            }

            }
            else {
                if(AppUtils.getRegisteredUserId(context)!=null)
                {responseCall =
                        ApiService.getService(context).create(ApiInterface.class).unFollowaChannel
                                (AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannel.getChId(),
                                        Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(), Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));
                    AppUtils.removeFavoriteUser(context, videoChannel.getChId());

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
                else {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class).unFollowaChannel
                                    (AppUtils.getJSonOBJForFolloworUnfollow(videoChannel.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                    AppUtils.removeFavoriteUser(context, videoChannel.getChId());
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
                FollowFragment.updateFollowedList(context);

            }



    }



     boolean getFollowingORNot(final String videoChannelLink){
         final List<Channel>[] channels = new List[]{new ArrayList<>()};
         final boolean[] following = {false};
         if(AppUtils.getAppUserId(context)!=null){
         Call<ChannelDetailResponse> channelDetailResponseCall;
         if(AppUtils.getRegisteredUserId(context)!=null){
             channelDetailResponseCall = ApiService.getService(context)
                     .create(ApiInterface.class).getaUsersFollowingListBYChannel
                             (AppUtils.getJSonOBJForFollowingListReg
                                     (Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));

         }
         else {
             channelDetailResponseCall = ApiService.getService(context)
                     .create(ApiInterface.class).getaUsersFollowingList
                             (AppUtils.getJSonOBJForFollowingList(Objects.requireNonNull(AppUtils.getAppUserId(context))
                                     .getGuestUserId(), ""));
         }
         channelDetailResponseCall.enqueue(new Callback<ChannelDetailResponse>() {
             @Override
             public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                 if(response.isSuccessful() && response.body().getUSERDetailResponse()!=null){
                     channels[0] = response.body().getUSERDetailResponse().getResultList();
                     AppUtils.saveFavUser(context,channels[0]);
                     for (Channel c:channels[0]) {
                         if(c.getChName().equals(videoChannelLink))
                         {
                             following[0] = true;
                             vIdeoItem.setFollowedByMe(true);
                           //  vIdeoItem.setVideoLikes(500+"");
                             onitemSetFollowing.ongetItemFollowing(getPosition(),vIdeoItem);
                         }
                     }
                 }
             }

             @Override
             public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

             }
         });}
         return  following[0];
     }

}