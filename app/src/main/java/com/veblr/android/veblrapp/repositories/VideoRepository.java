package com.veblr.android.veblrapp.repositories;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.veblr.android.veblrapp.dao.VIdeoDao;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.room.AppDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class VideoRepository
{
    private VIdeoDao vIdeoDao;
    private AppDataBase appDataBaseInstance;
    LiveData<List<VIdeoItem>> listLiveData;
    public VideoRepository(@NonNull Context application) {
        super();
        appDataBaseInstance = AppDataBase.getDatabaseInstance(application);
        vIdeoDao = appDataBaseInstance.vIdeoDao();
    }

    public List<VIdeoItem> getAllVideos() throws ExecutionException, InterruptedException {
       return new GetAllVideosAsyncTask(vIdeoDao).execute().get();
    }

    public void insertVideo(VIdeoItem vIdeoItem) {
      new   InsertVideoItemAsyncTask(vIdeoDao).execute(vIdeoItem);

    }

    public void setLike(VIdeoItem vIdeoItem)  {
        new InsertLikeAsyncTask(vIdeoDao).execute(vIdeoItem);
    }

    public boolean getisLiked(VIdeoItem videoItem)  {
        Boolean b=false;
        try {
          b = new GetIsLikedAsyncTask(vIdeoDao).execute().get();
         return b;
     }
     catch (Exception e){e.printStackTrace();}
        return b;
    }

    public void deleteAllText() {
        vIdeoDao.deleteAllVideos();
    }
    public void updateVideoItem(VIdeoItem vIdeoItem){
        vIdeoDao.updateVideoItem(vIdeoItem);
    }

    private static class InsertVideoItemAsyncTask extends AsyncTask<VIdeoItem, Void, Void> {
        VIdeoDao vIdeoDao;
         InsertVideoItemAsyncTask(VIdeoDao vIdeoDao) {
            this.vIdeoDao =vIdeoDao;
        }

        @Override
        protected Void doInBackground(VIdeoItem... vIdeoItems) {
            vIdeoDao.insertVideoItem(vIdeoItems[0]);
            return null;
        }
    }

    private static class GetAllVideosAsyncTask extends AsyncTask<Void, Void, List<VIdeoItem>>{
        VIdeoDao vIdeoDao;
         GetAllVideosAsyncTask(VIdeoDao dao) {
            this.vIdeoDao = dao;
        }

        @Override
        protected List<VIdeoItem> doInBackground(Void... voids) {

            return vIdeoDao.getAllVideos();
        }
    }
    private static class InsertLikeAsyncTask extends AsyncTask<VIdeoItem, Void, Void> {
        VIdeoDao vIdeoDao;

        InsertLikeAsyncTask(VIdeoDao vIdeoDao) {
            this.vIdeoDao =vIdeoDao;

        }


        @Override
        protected Void doInBackground(VIdeoItem... voids) {
        //    vIdeoDao.setLike(voids[0].isLikedbyME(),voids[0].getVideoId());
            vIdeoDao.updateVideoItem(voids[0]);
            return null;
        }
    }
    private static class GetIsLikedAsyncTask extends AsyncTask<Void, Void, Boolean>{
        VIdeoDao vIdeoDao;
        GetIsLikedAsyncTask(VIdeoDao dao) {
            this.vIdeoDao = dao;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return (Boolean) vIdeoDao.getLike("");
        }
    }
}
