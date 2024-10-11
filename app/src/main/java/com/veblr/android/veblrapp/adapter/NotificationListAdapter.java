package com.veblr.android.veblrapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Notification;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Notification> notifications;
    Context c;
    public  NotificationListAdapter(List<Notification> notifications, Context c){
        this.notifications= notifications;
        this.c = c;

    };
    public  NotificationListAdapter(List<Notification> notifications){
        this.notifications= notifications;

    }
    public NotificationListAdapter(){}
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (new NotificationListViewHolder((LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_notification_itemview, parent, false))));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(notifications!=null)
        ( (NotificationListViewHolder)holder).bind(notifications.get(position));


    }

    @Override
    public int getItemCount() {
        if(notifications.size()!=0)return notifications.size();
        else return 0;
    }
    class NotificationListViewHolder extends RecyclerView.ViewHolder{
        ImageView ivUserChannelIcon;
        ImageView ivNotification;
        TextView tvNotification,tvDateOfNotiFication;

        public NotificationListViewHolder(@NonNull View itemView) {
            super(itemView);

            ivNotification = (ImageView)itemView.findViewById(R.id.ivNotification);
            ivUserChannelIcon = (ImageView)itemView.findViewById(R.id.ivUserChannelIcon);
            tvNotification = (TextView) itemView.findViewById(R.id.tvNotification);
            tvDateOfNotiFication = (TextView) itemView.findViewById(R.id.tvDateOfNotification);


        }

        public void bind(Notification notification) {
            if(notification!=null) {
                tvDateOfNotiFication.setText(notification.getMessage());
                tvDateOfNotiFication.setText(notification.getDate());

                if(notification.getPayload()!=null && c!=null)
                {
                    Glide.with(c).load(notification.getPayload()).into(ivNotification);
                }
            }
        }
    }
}
