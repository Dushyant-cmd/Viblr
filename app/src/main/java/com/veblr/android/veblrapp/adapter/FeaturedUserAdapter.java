package com.veblr.android.veblrapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.ui.FollowFragment;
import com.veblr.android.veblrapp.ui.ProfileActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.internal.operators.completable.CompletableOnErrorComplete;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.lang.UCharacter.JoiningGroup.FE;
import static java.security.AccessController.getContext;

public class FeaturedUserAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Channel> userList;
    Context context;
    public   FeaturedUserAdapter(List<Channel> userList, Context context){
        this.userList =userList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeaturedUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_vertical,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (userList!=null)
            ((FeaturedUserHolder) holder).bind(userList.get(position),context);
    }

    @Override
    public int getItemCount() {
        if (userList!=null)
        return userList.size();
        else return 0;
    }

    private class FeaturedUserHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUserIcon;
        androidx.appcompat.widget.AppCompatToggleButton buttonFollow;
        Channel channel;
        public FeaturedUserHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUserIcon = (ImageView)itemView.findViewById(R.id.ivUserIcon);
            buttonFollow = (androidx.appcompat.widget.AppCompatToggleButton)itemView.findViewById(R.id.btnFollow);

            ((LinearLayout)itemView.findViewById(R.id.llfChannel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   final String id = channel.getChName();
                    if(id!=null){
                        JsonObject obj = new JsonObject();
                        JsonObject payerReg = new JsonObject();
                        payerReg.addProperty("channel_name", id);
                        obj.add("param",payerReg);
                        Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call =  ApiService.getService(context)
                                .create(ApiInterface.class).getUserDetails(obj);
                        call.enqueue(new Callback<ChannelDetailResponse>() {
                            @Override
                            public void onResponse(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                                if(response.isSuccessful()){
                                    try{
                                        List<Channel>  channels =  response.body().getUSERDetailResponse().getResultList();
                                        Channel channel =channels.get(0);

                                            Intent intent = new Intent(context, ProfileActivity.class);
                                            intent.putExtra("channel",(Serializable)channel );
                                            intent.putExtra("channelLink",id);
                                            context.startActivity(intent);
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
        }
       public void bind(final Channel user, final Context context)
       {
        this.channel=user;
         /*if(  Objects.requireNonNull(AppUtils.getFavoriteUser(context)).contains(userList.get(getPosition())))
         {   buttonFollow.setChecked(true);
             buttonFollow.setTextColor(Color.WHITE);

         }
*/        Glide.with(context).asBitmap()
                    .load(user.getChImage())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageViewUserIcon.setImageBitmap(resource);
                        }
                    });
          // imageViewUserIcon.setImageResource(R.drawable.ic_user_icon);

            updateFollowButton();
            buttonFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean b= true;
                    if(AppUtils.getFavoriteUser(context) != null && user!=null)
                    for (Channel c: Objects.requireNonNull(AppUtils.getFavoriteUser(context))) {
                        if(Objects.equals(c.getChId(), user.getChId()))
                        {
                            b=false;
                            break;
                        }
                    }
                    if(b){
                        buttonFollow.setChecked(true);
                        buttonFollow.setTextColor(Color.WHITE);
                        Toast.makeText(context, "You now follow " + user.getChNameDisp() + "", Toast.LENGTH_LONG).show();
                    }
                    else{
                        buttonFollow.setChecked(false);
                        buttonFollow.setTextColor(Color.parseColor("#0a90ed"));
                        Toast.makeText(context, user.getChNameDisp()+" is unfollowed by you",Toast.LENGTH_LONG).show();

                    }

                    addittofollowingList(user,b);}
            });
        }

        private void addittofollowingList(Channel chId, boolean b) {
            Call<com.veblr.android.veblrapp.model.Response> responseCall;
            if(b) {
                if(AppUtils.getRegisteredUserId(context)!=null){
                 responseCall =   ApiService.getService(context).create(ApiInterface.class)
                            .followaChannel(AppUtils.getJSonOBJForFolloworUnfollowReg(chId.getChId(),
                                    Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));

                }
                else {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class)
                                    .followaChannel(AppUtils.getJSonOBJForFolloworUnfollow(chId.getChId(), Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                }
                                AppUtils.addFavoriteUser(context, chId);
                FollowFragment.updateFollowedList(context);

            }
            else {
                if(AppUtils.getRegisteredUserId(context)!=null){
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class)
                                    .unFollowaChannel (AppUtils.getJSonOBJForFolloworUnfollowReg(chId.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));

                }
                else {
                responseCall =
                        ApiService.getService(context).create(ApiInterface.class)
                                .unFollowaChannel(AppUtils.getJSonOBJForFolloworUnfollow(chId.getChId(), Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                }
                AppUtils.removeFavoriteUser(context,chId.getChId());

            }
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
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void updateFollowButton() {
            List<Channel> channels = new ArrayList<>();
            if( AppUtils.getFavoriteUser(Objects.requireNonNull(context))!=null)channels =  AppUtils.getFavoriteUser(context);

            assert channels != null;
            for (Channel c :channels) {
                if(Objects.equals(c.getChName(),channel.getChName())) {
//                    Log.d("channel is",c.getChNameDisp());
                    buttonFollow.setChecked(true);
                    buttonFollow.setTextColor(Color.WHITE);

                  break;
                }
                else{
                    buttonFollow.setChecked(false);
                    buttonFollow.setTextColor(Color.parseColor("#0a90ed"));
                }
            }
        }

    }
}


