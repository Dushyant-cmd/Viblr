package com.veblr.android.veblrapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.ui.VideoWatchActivity;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListOfVideosAdapter extends RecyclerView.Adapter<ListOfVideosAdapter.ListOfVideosViewHolder> {

    RequestManager requestManager;
    List<VIdeoItem> vIdeoItemArrayList;
    Context mContext;
    public ListOfVideosViewHolder.onItemClickListener mListener;
    public ListOfVideosAdapter(Context mContext, RequestManager initGlide, List<VIdeoItem> mediaObjects) {
       // super(VIdeoItem.CALLBACK);
        this.requestManager = initGlide;
        this.vIdeoItemArrayList = mediaObjects;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ListOfVideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
/*        ListOfVideosViewHolder.onItemClickListener onItemClickListener;
        onItemClickListener = new ListOfVideosViewHolder.onItemClickListener(){

            @Override
            public void onRecyclerItemClick(View view, int position) {

                    List<VIdeoItem> vIdeolist = new ArrayList<>();
                vIdeolist = vIdeoItemArrayList;
                vIdeolist.add(0, vIdeoItemArrayList.get(position));
                vIdeolist.remove(position);
                    Intent intent = new Intent(mContext, VideoWatchActivity.class);
                    intent.putExtra("videoList", (Serializable) vIdeolist);
                    mContext.startActivity(intent);

        }};*/

        return new ListOfVideosViewHolder( LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_videos_listitem, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ListOfVideosViewHolder holder,  int position) {
        VIdeoItem vIdeoItem  = vIdeoItemArrayList.get(position);
        ListOfVideosViewHolder.onItemClickListener onItemClickListener;
        onItemClickListener = new ListOfVideosViewHolder.onItemClickListener(){

            @Override
            public void onRecyclerItemClick(View view, int position) {

                List<VIdeoItem> vIdeolist = new ArrayList<>();
                vIdeolist = vIdeoItemArrayList;
                vIdeolist.add(0, vIdeoItemArrayList.get(position));
                vIdeolist.remove(position);
                Intent intent = new Intent(mContext, VideoWatchActivity.class);
                intent.putExtra("videoList", (Serializable) vIdeolist);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);

            }};
        if(vIdeoItem!=null) ((ListOfVideosViewHolder) holder).onBind(vIdeoItem, vIdeoItemArrayList,requestManager,mContext,onItemClickListener);

    }


    @Override
    public int getItemCount() {
        return vIdeoItemArrayList.size();
    }


   /* @Override
    public int getItemCount() {
        return 50;
    }*/


public static class ListOfVideosViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

     ImageView videoThumbnail;
     TextView noOfLikes,noOfComments,noOfViews,videoTitle;
     ImageView like,comment,share;
     RequestManager requestManager;
     onItemClickListener mListener;
     View ll;
     List<VIdeoItem> vIdeoItems;
    public ListOfVideosViewHolder(@NonNull View itemView) {
        super(itemView);
     //   mListener =listener;
        ll= itemView;
        videoThumbnail = (ImageView)itemView.findViewById(R.id.ivVideothumbnail);
        noOfLikes = (TextView) itemView.findViewById(R.id.tvLikes);
        noOfComments = (TextView) itemView.findViewById(R.id.tvVideoComment);
        noOfViews  = (TextView)itemView.findViewById(R.id.tvViews);
        videoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
        like = (ImageView)itemView.findViewById(R.id.btnLike);
        comment = (ImageView)itemView.findViewById(R.id.btnComment);
        share = (ImageView)itemView.findViewById(R.id.btnShare);

    }
    public void onBind(final VIdeoItem video, List<VIdeoItem> vlist,RequestManager requestManager2, final Context mContext,onItemClickListener listener) {

        this.mListener = listener;
        this.vIdeoItems   = vlist;
        this.requestManager = requestManager2;
        videoTitle.setText(video.getVideoTitle());
        if(video.getVideoViews()==null)
            noOfViews.setText("0");
        else
        noOfViews.setText(AppUtils.format(Long.valueOf(video.getVideoViews())));
        if(video.getVideoLikes()==null)noOfLikes.setText("0");
        else noOfLikes.setText(AppUtils.format(Long.valueOf(video.getVideoLikes())));
        if(video.getVideoComments()==null)
            noOfComments.setText("0");
        else noOfComments.setText(AppUtils.format(Long.valueOf(video.getVideoComments())));
        this.requestManager
                .load(video.getVideoPoster())
                .into(videoThumbnail);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, video.getVideoTitle());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "https://veblr.com/m/watch/"+video.getVideoId()+"/"+"\n \n "+ video.getVideoTitle()+
                                "\n \n  For more information go to\n \n App Package"+org.apache.commons.lang3.StringEscapeUtils.unescapeJava(" \uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46 ") +
                                Uri.parse( "https://play.google.com/store/apps/details?id=com.veblr.videomate&launch=true"));
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        List<VIdeoItem> vIdeolist = new ArrayList<>();
                        vIdeolist = vIdeoItems;
                        vIdeolist.add(0, vIdeoItems.get(getPosition()));
                        vIdeolist.remove(getPosition());
                        if(AppUtils.getFavorites(v.getContext())!=null &&vIdeolist.size()<10){
                            vIdeolist.addAll(vIdeolist.size()-1,
                                    AppUtils.getFavorites(v.getContext()));
                        }
                        Intent intent = new Intent(mContext, VideoWatchActivity.class);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("videoList", (Serializable) vIdeolist);
                        v.getContext().startActivity(intent);

            }
        });

  }

    @Override
    public void onClick(View v) {
      mListener.onRecyclerItemClick(v,getPosition());

    }

    public static interface onItemClickListener {
        public void onRecyclerItemClick(View view , int position);
    }

    public void setOnCardClickListner(onItemClickListener onCardClickListner) {
        this.mListener = onCardClickListner;
    }
  }

}
