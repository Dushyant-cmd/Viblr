package com.veblr.android.veblrapp.dao;

import androidx.annotation.NonNull;
import androidx.room.BuiltInTypeConverters;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.Comment;
import com.veblr.android.veblrapp.model.Like;
import com.veblr.android.veblrapp.model.VideoHashTag;
import com.veblr.android.veblrapp.model.VideoKeyWords;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CheckedOutputStream;

public class Converters implements TypeConverters {


    @Override
    public Class<?>[] value() {
        return new Class[0];
    }

    @Override
    public BuiltInTypeConverters builtInTypeConverters() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @TypeConverter
     public static String toStringKey(VideoKeyWords section) {
        return section == null ? null : section.getQueryString();
     }

    @TypeConverter
    public static List<VideoKeyWords> storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<VideoKeyWords>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(List<VideoKeyWords> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
    @TypeConverter
    public static String toStringTag(VideoHashTag section) {
        return section == null ? null : section.getQueryString();
    }
    @TypeConverter
    public static List<VideoHashTag> storedStringTagToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<VideoHashTag>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredStringTag(List<VideoHashTag> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

 /*     @TypeConverter
     public static String toStringLikes(Like like) {
        return like == null ? null : like.getLike_no();
    }
    @TypeConverter
    public static String myObjToLike(Like like) {
        Gson gson = new Gson();
        return gson.toJson(like);
    }
  @TypeConverter
    public static Object storedStringToLIKE(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return null;
        }
        Type listType = new TypeToken<Like>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String toStringComment(Comment comment) {
        return comment == null ? null : comment.getComment_no();
    }
    @TypeConverter
    public static String myObjToComment(Comment comment) {
        Gson gson = new Gson();
        return gson.toJson(comment);
    }
    @TypeConverter
    public static Object storedStringToComment(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return null;
        }
        Type listType = new TypeToken<Comment>() {}.getType();
        return gson.fromJson(data, listType);
    }
*/


    @TypeConverter
   public String  listToJson( List<String> videoKeyWords) {
        return new Gson().toJson(videoKeyWords);
    }

    @TypeConverter
    public List<String> jsonToList(String videoKeyword) {

        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(videoKeyword, listType);
    }


    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Channel> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Channel>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Channel> someObjects) {
        return gson.toJson(someObjects);
    }

}
