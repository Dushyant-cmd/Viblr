package com.veblr.android.veblrapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.ui.ChannelSetting;
import com.veblr.android.veblrapp.ui.EditVideoActivity;
import com.veblr.android.veblrapp.ui.RegisterActivity;
import com.veblr.android.veblrapp.ui.SearchActivity;
import com.veblr.android.veblrapp.ui.UserProfileActivity;
import com.veblr.android.veblrapp.ui.VideoWatchActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public  class ListofMyVideosAdapter extends RecyclerView.Adapter<ListofMyVideosAdapter.ListOfMYVideosViewHolder> {

    RequestManager requestManager;
 public   List<VIdeoItem> vIdeoItemArrayList;
    Context mContext;
    Channel channel;
    public ListOfVideosAdapter.ListOfVideosViewHolder.onItemClickListener mListener;
    public ListofMyVideosAdapter(Context mContext, RequestManager initGlide,
                                 List<VIdeoItem> mediaObjects,Channel channel) {
        // super(VIdeoItem.CALLBACK);
        this.requestManager = initGlide;
        this.vIdeoItemArrayList = mediaObjects;
        this.mContext = mContext;
        this.channel = channel;
    }


    @NonNull
    @Override
    public ListofMyVideosAdapter.ListOfMYVideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        return new ListofMyVideosAdapter.ListOfMYVideosViewHolder( LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_account_videos, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull ListofMyVideosAdapter.ListOfMYVideosViewHolder holder, int position) {
        VIdeoItem vIdeoItem  = vIdeoItemArrayList.get(position);
        ListofMyVideosAdapter.ListOfMYVideosViewHolder.onItemClickListener onItemClickListener;
        onItemClickListener = new ListofMyVideosAdapter.ListOfMYVideosViewHolder.onItemClickListener(){

            @Override
            public void onRecyclerItemClick(View view, int position) {

                List<VIdeoItem> vIdeolist = new ArrayList<>();
                vIdeolist = vIdeoItemArrayList;
                vIdeolist.add(0, vIdeoItemArrayList.get(position));
                vIdeolist.remove(position);
                Intent intent = new Intent(mContext, VideoWatchActivity.class);
                intent.putExtra("videoList", (Serializable) vIdeolist);
                mContext.startActivity(intent);

            }};
        if(vIdeoItem!=null) ((ListofMyVideosAdapter.ListOfMYVideosViewHolder) holder).onBind(vIdeoItem, vIdeoItemArrayList,requestManager,mContext,
                onItemClickListener,channel);

    }


    @Override
    public int getItemCount() {
        return vIdeoItemArrayList.size();
    }
    public void removeItem(int pos){
        vIdeoItemArrayList.remove(pos);
        notifyItemRemoved(pos);
    }

   /* @Override
    public int getItemCount() {
        return 50;
    }*/


    public static class ListOfMYVideosViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView videoThumbnail;
        TextView noOfLikes,noOfComments,noOfViews,videoTitle;
        ImageView share;
        LinearLayout delete,edit;
        RequestManager requestManager;
        ListofMyVideosAdapter.ListOfMYVideosViewHolder.onItemClickListener mListener;
        View ll;
        List<VIdeoItem> vIdeoItems;
        public ListOfMYVideosViewHolder(@NonNull View itemView) {
            super(itemView);
            //   mListener =listener;
            ll= itemView;
            videoThumbnail = (ImageView)itemView.findViewById(R.id.ivVideothumbnail);
            noOfViews  = (TextView)itemView.findViewById(R.id.tvViews);
            videoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            delete = (LinearLayout)itemView.findViewById(R.id.btnDlt);
            edit = (LinearLayout)itemView.findViewById(R.id.btnEdit);
            share = (ImageView)itemView.findViewById(R.id.btnShare);

        }
        public void onBind(final VIdeoItem video, List<VIdeoItem> vlist, RequestManager requestManager2,
                           final Context mContext,
                           ListofMyVideosAdapter.ListOfMYVideosViewHolder.onItemClickListener listener,
                           final Channel channel) {

            this.mListener = listener;
            this.vIdeoItems   = vlist;
            this.requestManager = requestManager2;
            videoTitle.setText(video.getVideoTitle());
            if(video.getVideoViews()==null)
                noOfViews.setText("0");
            else
                noOfViews.setText(AppUtils.format(Long.valueOf(video.getVideoViews())));

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
                    Intent intent = new Intent(mContext, VideoWatchActivity.class);
                    intent.putExtra("videoList", (Serializable) vIdeolist);
                    mContext.startActivity(intent);


                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                      final AlertDialog.Builder builder = new AlertDialog.Builder(
                            v.getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    builder.setTitle("Video Deletion");
                    builder.setMessage("Do you want to delete this video?");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Response>  deletevideoresponse = ApiService.getService(v.getContext())
                                    .create(ApiInterface.class).deleteMyAccountVideo(AppUtils.getJSonOBJForDeleteAVideo
                                            (AppUtils.getRegisteredUserId(v.getContext()).getUserId(),channel.getChId(),video.getVideoId()));

                            deletevideoresponse.enqueue(new Callback<Response>() {

                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if(response.isSuccessful() && response.body().getResonse().getStatus()=="200"){
                                        //notifydata set changed
                                        UserProfileActivity.refreshListAfterDeletion(getPosition());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    builder.setCancelable(false);

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       alertDialog.dismiss();
                        }
                    });

                 alertDialog.show();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditVideoActivity.class);
                    intent.putExtra("video",(Serializable)video);
                    intent.putExtra("channel",(Serializable)channel);

                    mContext.startActivity(intent);
                }
            });

        }


        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v,getPosition());

        }

        public  interface onItemClickListener {
            public void onRecyclerItemClick(View view , int position);
        }

        public void setOnCardClickListner(ListOfVideosAdapter.ListOfVideosViewHolder.onItemClickListener onCardClickListner) {
          //  this.mListener = onCardClickListner;
        }
    }

}
