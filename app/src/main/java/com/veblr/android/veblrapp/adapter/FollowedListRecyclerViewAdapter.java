package com.veblr.android.veblrapp.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.veblr.android.veblrapp.ui.ProfileActivity;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter.FeaturedChannelsRecyclerViewHolder;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerViewHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowedListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Channel> userListFollowed;
    List<Channel> userListFeatured;
    Context context;
    private static final int FEATUREDUSERS_VIEW = 0;
    private static final int FOLLOWED_VIEW = 1;
    FollowedListRecyclerViewAdapter.OnchannelGotFollowed listener;
    private BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FollowedListRecyclerViewAdapter.this.notifyDataSetChanged();
        }
    };

    public FollowedListRecyclerViewAdapter(List<Channel> userListFollowed, Context context, List<Channel> userListFeatured) {
        this.userListFollowed = userListFollowed;
        // this.userListFollowed.add(0,new Channel());
        this.userListFeatured = userListFeatured;
        this.context = context;
        LocalBroadcastManager.getInstance(context).registerReceiver(message,
                new IntentFilter("followlist_updated"));

    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        observer.onChanged();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.e("FollowedListRecycl", "onCreateViewHolder"+viewType);
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {
            case FEATUREDUSERS_VIEW:
                vh = new FeatureRecyclerViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.user_list_recyclerview, parent, false),
                        context);
                return vh;
            case FOLLOWED_VIEW:
                vh = new FollowedListRecyclerViewHolder((LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardview_followitem, parent, false)));
                return vh;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Log.e("FollowedListRecycl", "onBindViewHolder"+position);
        /*if(position==0){
            ((FollowedListRecyclerViewHolder) holder).bind(userListFollowed.get(position), context);
        }*/
        try {
            if (holder instanceof FollowedListRecyclerViewHolder && userListFollowed != null)
                ((FollowedListRecyclerViewHolder) holder).bind(userListFollowed.get(position - 1), context);
            else {
                ((FeatureRecyclerViewHolder) holder).bindChannels(userListFeatured);
            }
        } catch (ClassCastException ignored) {
        }
    }

    public interface OnchannelGotFollowed {
        public void onDataChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //Log.e("FollowedListRecycl","getItemViewType:"+position);
        if (position == 0)
            return FEATUREDUSERS_VIEW;
        else
            return FOLLOWED_VIEW;

    }

    @Override
    public int getItemCount() {
        if (userListFollowed != null)
            return userListFollowed.size() + 1;
        else
            return 1;
    }

    class FollowedListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserChannelIcon;
        TextView tvUserChannelName;
        ImageView ivUserChannelVerified;
        androidx.appcompat.widget.AppCompatToggleButton btnFollow;

        public FollowedListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserChannelIcon = (ImageView) itemView.findViewById(R.id.ivUserChannelIcon);
            tvUserChannelName = (TextView) itemView.findViewById(R.id.tvUserChannelname);
            ivUserChannelVerified = (ImageView) itemView.findViewById(R.id.ivUserChannelVerified);
            btnFollow = (androidx.appcompat.widget.AppCompatToggleButton) itemView.findViewById(R.id.btnUserFollowed);

            //   ivUserChannelIcon.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_launcher_icon));
            ivUserChannelVerified.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_channel_verify_icon));
            //  tvUserChannelName.setText("Fame School Of Style and fashion");
        }

        public void bind(final Channel user, final Context context) {
            Log.e("FollowedListRecycl", "bind" + user.getChNameDisp());
            try {
                if (user.getChVerify().equals("y"))
                    ivUserChannelVerified.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_channel_verify_icon));
                else
                    ivUserChannelVerified.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_channel_unverified_icon));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            Glide.with(context).asBitmap()
                    .load(user.getChImage())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ivUserChannelIcon.setImageBitmap(resource);
                        }
                    });

            tvUserChannelName.setText(user.getChNameDisp());
            btnFollow.setChecked(true);
            btnFollow.setTextColor(Color.WHITE);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean b = false;

                    for (Channel c : Objects.requireNonNull(AppUtils.getFavoriteUser(context))) {
                        try {
                            if (c.getChId().equals(user.getChId())) {
                                b = false;
                                break;
                            } else b = true;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    addittofollowingList(user, b);
                }
            });

            itemView.findViewById(R.id.llfolllowing).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String id = user.getChName();
                    if (id != null) {
                        JsonObject obj = new JsonObject();
                        JsonObject payerReg = new JsonObject();
                        payerReg.addProperty("channel_name", id);
                        obj.add("param", payerReg);
                        Call<ChannelDetailResponse> call = ApiService.getService(context)
                                .create(ApiInterface.class).getUserDetails(obj);
                        call.enqueue(new Callback<ChannelDetailResponse>() {
                            @Override
                            public void onResponse(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                                if (response.isSuccessful() && response.body().getUSERDetailResponse().getStatus().equals("200")) {
                                    try {
                                        List<Channel> channels = response.body().getUSERDetailResponse().getResultList();
                                        Channel channel = channels.get(0);

                                        Intent intent = new Intent(context, ProfileActivity.class);
                                        intent.putExtra("channel", (Serializable) channel);
                                        intent.putExtra("channelLink", id);
                                        context.startActivity(intent);
                                    } catch (NullPointerException ignored) {

                                    }
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

        public void addittofollowingList(final Channel videoChannelID, boolean b) {
            Call<com.veblr.android.veblrapp.model.Response> responseCall;
            if (b) {
                btnFollow.setChecked(true);
                btnFollow.setTextColor(Color.WHITE);
                if (AppUtils.getRegisteredUserId(context) != null) {
                    responseCall = ApiService.getService(context).create(ApiInterface.class)
                            .followaChannel(AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannelID.getChId(),
                                    Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(),
                                    Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()),
                                    AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));

                } else {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class)
                                    .followaChannel(AppUtils.getJSonOBJForFolloworUnfollow(videoChannelID.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                }

                //Toast toast = Toast.makeText(context, "You now follow " + videoChannelID.getChNameDisp()+"", Toast.LENGTH_LONG);
                Toast toast = Toast.makeText(context,
                        HtmlCompat.fromHtml("<font color='black'> You now follow " + videoChannelID.getChNameDisp(), HtmlCompat.FROM_HTML_MODE_LEGACY),
                        Toast.LENGTH_LONG);
                //TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                //toastMessage.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black));
                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                toast.show();
                AppUtils.addFavoriteUser(context, userListFollowed.get(getPosition()));
            } else {
                btnFollow.setChecked(false);
                btnFollow.setTextColor(Color.parseColor("#0a90ed"));
                if (AppUtils.getRegisteredUserId(context) != null) {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class)
                                    .unFollowaChannel(AppUtils.getJSonOBJForFolloworUnfollowReg(videoChannelID.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(), Objects.requireNonNull(AppUtils.getRegisteredUserId(context).getUserId()), AppUtils.getRegisteredUserId(context).getChannel().get(0).getChId()));

                } else {
                    responseCall =
                            ApiService.getService(context).create(ApiInterface.class)
                                    .unFollowaChannel(AppUtils.getJSonOBJForFolloworUnfollow(videoChannelID.getChId(), Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId()));
                }
                userListFollowed.remove(getAdapterPosition() - 1);
                AppUtils.removeFavoriteUser(context, videoChannelID.getChId());
                //Toast toast = Toast.makeText(context, videoChannelID.getChNameDisp() + " is unfollowed by you", Toast.LENGTH_LONG);
                Toast.makeText(context,
                        HtmlCompat.fromHtml("<font color='white'>" + videoChannelID.getChNameDisp() + " is unfollowed by you</font>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                        Toast.LENGTH_LONG).show();
                //TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                //toastMessage.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black));
                //toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                //toast.show();


            }

            responseCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                @Override
                public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                    if (response.isSuccessful()) {

                    }

                }

                @Override
                public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                }
            });
            notifyDataSetChanged();

        }
    }

}

class FeatureRecyclerViewHolder extends RecyclerView.ViewHolder {

    RecyclerView recyclerView;
    Context context;

    public FeatureRecyclerViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.rvUser);
        this.context = context;
    }

    public void bindChannels(List<Channel> userList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new FeaturedUserAdapter(userList, context));
    }
}

