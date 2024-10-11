package com.veblr.android.veblrapp.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.FeaturedUserAdapter;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.ui.VideoWatchActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoVideoPlayerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VIdeoItem> mediaObjects;
    private RequestManager requestManager;
    Context context;
    private static  final int CUSTOM_CHANNELS_LIST = 0 ;
    private static  final int AUTO_VIDEOPLAYER = 1 ;
    private static  final int AD_VIEW = 2 ;
    boolean fromCata;
    List<Channel> userList =new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;

    AutoVideoPlayerViewHolder vh;
    private List<NativeAd> mAdItems;

    public AutoVideoPlayerRecyclerViewAdapter(ArrayList<VIdeoItem> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }
    public AutoVideoPlayerRecyclerViewAdapter(NativeAdsManager mNativeAdsManager
            ,Context context, List<VIdeoItem> mediaObjects, RequestManager requestManager,boolean fromCata) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.context = context;
        this.fromCata = fromCata;
        List<Channel> users =AppUtils.getFeaturedUser(context);
       if(!users.isEmpty())
            this.userList = AppUtils.getFeaturedUser(context);
        mAdItems = new ArrayList<>();
        this.mNativeAdsManager = mNativeAdsManager;


    }
    public AutoVideoPlayerViewHolder getHolder(){
     return  this.vh;
    }



    public AutoVideoPlayerRecyclerViewAdapter(Context context, List<VIdeoItem> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.context = context;


    }
    public void setUserList(List<Channel> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    public  void getListAndUpdate(int position,VIdeoItem value){
      //   notifyDataSetChanged();
        if(position!=-1 && position!=mediaObjects.size()) {
            try {
                mediaObjects.set(position, value);
                notifyItemChanged(position);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        AutoVideoPlayerViewHolder.onItemClickListener onItemClickListener;
        onItemClickListener = new AutoVideoPlayerViewHolder.onItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, int position, boolean fromComments) {

                Log.d("VIDEOID", mediaObjects.get(position).getVideoId());

                //position = position-1;

                List<VIdeoItem> vIdeoItemArrayList = new ArrayList<>();
                vIdeoItemArrayList = mediaObjects;
                vIdeoItemArrayList.add(0, mediaObjects.get(position));
                vIdeoItemArrayList.remove(position);
                Intent intent = new Intent(context, VideoWatchActivity.class);
       //         intent.putExtra("videoList", (Serializable) vIdeoItemArrayList);
                intent.putExtra("video_id",mediaObjects.get(position).getVideoId() );
                if(view.findViewById(R.id.lComment).isPressed())
                intent.putExtra("fromComments", true);
                context.startActivity(intent);
            }
        };
        AutoVideoPlayerViewHolder.onitemSetFollowing listener = new AutoVideoPlayerViewHolder.onitemSetFollowing() {
            @Override
            public void ongetItemFollowing(int position, VIdeoItem isfollowed) {
                getListAndUpdate(position,isfollowed);

            }

        };
        vh = new AutoVideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.cardview_post_listitem, viewGroup, false),
                context, onItemClickListener,listener);
        RecyclerView.ViewHolder viewHolder =null;

        switch (i) {
            case CUSTOM_CHANNELS_LIST:
                viewHolder =  new FeaturedChannelsRecyclerViewHolder(
                        LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.user_list_recyclerview, viewGroup, false),
                        context);
                return viewHolder;
            case AUTO_VIDEOPLAYER:
                viewHolder = new AutoVideoPlayerViewHolder(
                        LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.cardview_post_listitem, viewGroup, false),
                        context, onItemClickListener,listener);
                    return viewHolder;

            case AD_VIEW:
                NativeAdLayout inflatedView = (NativeAdLayout) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.ad_view, viewGroup, false);
                viewHolder=new AdViewHolder(inflatedView);
                return  viewHolder;

        }
        return  viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(AppUtils.getFeaturedUser(context)!=null)this.userList = AppUtils.getFeaturedUser(context);
       if(viewHolder instanceof AutoVideoPlayerViewHolder) {
           //((AutoVideoPlayerViewHolder) viewHolder).onBind(mediaObjects.get(i), requestManager, i);
            try {
                ((AutoVideoPlayerViewHolder) viewHolder).onBind(mediaObjects.get(i-1), requestManager, i-1);
            }catch (Exception e){
                e.printStackTrace();
            }
       }else if(viewHolder instanceof FeaturedChannelsRecyclerViewHolder)
            ((FeaturedChannelsRecyclerViewHolder)viewHolder).bindChannels(userList);
        else if (viewHolder.getItemViewType() == AD_VIEW) {
            NativeAd ad;

            if (mAdItems.size() > i / 3) {
                ad = mAdItems.get(i / 3);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                if(ad!=null){
                if (!ad.isAdInvalidated()) {
                    mAdItems.add(ad);
                } else {
                    Log.w("AD STATUS", "Ad is invalidated!");
                }}
            }

            AdViewHolder adHolder = (AdViewHolder) viewHolder;
            adHolder.adChoicesContainer.removeAllViews();

            if (ad != null) {

                adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                adHolder.tvAdBody.setText(ad.getAdBodyText());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.tvAdSponsoredLabel.setText("Sponsored");
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                adHolder.btnAdCallToAction.setVisibility(
                        ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                AdOptionsView adOptionsView =
                        new AdOptionsView(context, ad, adHolder.nativeAdLayout);
                adHolder.adChoicesContainer.addView(adOptionsView, 0);

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adHolder.ivAdIcon);
                clickableViews.add(adHolder.mvAdMedia);
                clickableViews.add(adHolder.btnAdCallToAction);
                ad.registerViewForInteraction(
                        adHolder.nativeAdLayout,
                        adHolder.mvAdMedia,
                        adHolder.ivAdIcon,
                        clickableViews);
            }
        }
    }

    @Override
    public int getItemCount() {

         if(mediaObjects!=null)
             return mediaObjects.size();
         else if (userList!=null)
             return  mediaObjects.size()+1;
         else if (userList!=null && mAdItems!=null)
             return  mediaObjects.size()+1+mAdItems.size();
         else if ( mAdItems!=null && mediaObjects!=null)
             return  mediaObjects.size()+mAdItems.size();

         else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0 && fromCata )
            return  CUSTOM_CHANNELS_LIST;
        else{
        if(position>1 && position%6==0)
        return AD_VIEW;
        else return AUTO_VIDEOPLAYER;
        }
    }

    public  class FeaturedChannelsRecyclerViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        Context context;
        public FeaturedChannelsRecyclerViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            recyclerView = (RecyclerView)itemView.findViewById(R.id.rvUser);
            this.context = context;
        }

       public  void bindChannels(List<Channel> userList){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(new FeaturedUserAdapter(userList,context));

        }
    }

    private   class AdViewHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;
        MediaView mvAdMedia;
        MediaView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdViewHolder(NativeAdLayout adLayout) {
            super(adLayout);

            nativeAdLayout = adLayout;
            mvAdMedia = adLayout.findViewById(R.id.native_ad_media);
            tvAdTitle = adLayout.findViewById(R.id.native_ad_title);
            tvAdBody = adLayout.findViewById(R.id.native_ad_body);
            tvAdSocialContext = adLayout.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = adLayout.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = adLayout.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = adLayout.findViewById(R.id.native_ad_icon);
            adChoicesContainer = adLayout.findViewById(R.id.ad_choices_container);
        }
    }

}
