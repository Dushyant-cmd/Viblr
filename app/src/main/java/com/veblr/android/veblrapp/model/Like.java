package com.veblr.android.veblrapp.model;

import androidx.room.PrimaryKey;

public class Like {
   @PrimaryKey(autoGenerate = true)
   private int likeId;

   private String like_no;
   private String video_id;
   private  String user_id;

   public  Like(){}
   public  Like(String video_id,String like_no){this.video_id = video_id;
   this.like_no  =like_no;
   }


   public int getStringId(){return this.likeId;}
   public void setStringId(int id){this.likeId  = id;}
   public void setLike_no(String like_no){this.like_no = like_no;}
   public String getLike_no(){return  this.like_no;}
   public void setVideo_id(String video_id){this.video_id = video_id;}
   public String getVideo_id(){return  this.video_id;}
   public void setUser_id(String video_id){this.video_id = video_id;}
   public String getUser_id(){return  this.video_id;}


}
