package com.veblr.android.veblrapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji.widget.EmojiEditText;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Comment> commentArrayList;
    Context context;
    RequestManager requestManager;



    public  CommentListAdapter(List<Comment> commentArrayList, Context context){
       this.context = context;
       this.commentArrayList = commentArrayList;
       this.requestManager = requestManager;
    }

    public  CommentListAdapter(Context context){
       this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          return new CommentListViewHolder( LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_comment_listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CommentListViewHolder)holder).onBind(commentArrayList.get(position), requestManager);
       // ((CommentListViewHolder)holder).onBind();
    }

    @Override
    public int getItemCount() {
     //return 6;
       return commentArrayList.size();
    }

    public class CommentListViewHolder extends RecyclerView.ViewHolder{

        ImageView userImage;
        TextView userPosted;
        EmojiTextView commentText;
        TextView dateOfPosted;
        RequestManager requestManagerForVH;

        public CommentListViewHolder(@NonNull View itemView) {
            super(itemView);

            dateOfPosted  = (TextView)itemView.findViewById(R.id.tvDateofPosted);
            commentText = (EmojiTextView) itemView.findViewById(R.id.tvCommentText);
            userPosted  = (TextView)itemView.findViewById(R.id.tvUserName);
            userImage = (ImageView) itemView.findViewById(R.id.tvUserImage);
        }
        public void onBind(Comment comment, RequestManager requestManagerForVH) {
            this.requestManagerForVH = requestManagerForVH;
            dateOfPosted.setText(comment.getDateOfPosted());
           commentText.setText(comment.getCommentText());
           if(comment.getChannel()!=null) {
               userPosted.setText(comment.getChannel().getChNameDisp());
               Glide.with(context).asBitmap()
                       .load(comment.getChannel().getChImage())
                       .into(new SimpleTarget<Bitmap>() {
                           @Override
                           public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                               userImage.setImageBitmap(resource);
                           }
                       });
           }
           else{userPosted.setText(comment.getUser().getAppUserName());
               userImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_user_icon));}

               // this.requestManagerForVH.load(comment.getUserPostedImage()).into(userImage);
           // userPosted.setText(context.getResources().getText(R.string.username));

        }
        public void onBind() {
            dateOfPosted.setText("");
            commentText.setText(context.getResources().getText(R.string.new_message_notification_placeholder_text_template));
            userPosted.setText(context.getResources().getText(R.string.username));
            userImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_user_icon));
        }
    }
}
