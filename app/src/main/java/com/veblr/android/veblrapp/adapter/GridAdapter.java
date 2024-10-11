package com.veblr.android.veblrapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.ui.ChannelSetting;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<VIdeoItem> sites=null;
    LayoutInflater inflater ;
    Activity activity;

    public GridAdapter(Context c, ArrayList<VIdeoItem> videos) {

        super();
        this.context=c;
        this.sites=videos;

    }

    public GridAdapter(Context context,Activity activity, ArrayList<VIdeoItem> sites) {

        super();
        this.context = context;
        this.activity = activity;
        this.sites = sites;

    }
    @Override
    public int getCount() {

        return sites.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        NewHolder holder=null;

        if(view == null) {//if convert view is null then only inflate the row
            inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view = inflater.inflate(R.layout.image_item,viewGroup,false);

            holder = new NewHolder();

            //find views in item row
            holder.imageView = view.findViewById(R.id.thumbImage);/*
            holder.textView = view.findViewById(R.id.tvWebNames);*/
            holder.llgrid = view.findViewById(R.id.llGrid);
            view.setTag(holder);
        }
        else{
            holder = (NewHolder) view.getTag();
        }

        if(! sites.isEmpty()){
            final NewHolder finalHolder = holder;
            Glide.with(context).asBitmap()
                    .load(sites.get(i).getVideoChannelImage())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            finalHolder.imageView.setImageBitmap(resource);

                        }
                    });
        }

        holder.llgrid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        return view;
    }
    public class NewHolder {
        public ImageView imageView;
        public FrameLayout llgrid;

    }

}
