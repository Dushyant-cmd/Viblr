package com.veblr.android.veblrapp.repositories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.veblr.android.veblrapp.dao.UserDao;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.room.AppDataBase;
import com.veblr.android.veblrapp.util.AppUtils;

import java.util.concurrent.ExecutionException;

public class UserProfileRepository {

    private final static String TAG = UserProfileRepository.class.getSimpleName();
    private final UserDao userDao;
    private String deviceid;
    //private LiveData<List<SearchHistory>> listLiveData;

    public UserProfileRepository(@NonNull Context application) {
        AppDataBase appDataBaseInstance = AppDataBase.getDatabaseInstance(application);
        userDao = appDataBaseInstance.userDao();
        deviceid =  AppUtils.getDeviceUniqueId(application);
    }

    public static void setLike(VIdeoItem vIdeoItem) {
    }

    public void insertUser(User user) {
        user.setDeviceId(deviceid);
        new InsertUserAsyncTask(userDao, deviceid).execute(user);
    }

    public User getUser()throws ExecutionException, InterruptedException  {
       return new getUser(userDao).execute().get();
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private final UserDao userDao;
        String deviceid;

        private InsertUserAsyncTask(UserDao userDao, String deviceId) {
            this.userDao = userDao;
            this.deviceid = deviceId;
        }

        @Override
        protected Void doInBackground(User... user) {
            user[0].setDeviceId(deviceid);
            userDao.insertUser(user[0]);
             userDao.setDeviceId(deviceid,user[0].getGuestUserId());
             User user1 = userDao.getUser(user[0].getGuestUserId());
            if(user1!=null)
                Log.e( TAG,"InsertUsertAsyncTask: NEW GUEST USERID:"+user1.getDeviceId());
            return null;
        }
    }

    private static class GetUserAsyncTask extends  AsyncTask<Void, Void, User> {
        UserDao userDao;
        public GetUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected User doInBackground(Void... voids) {
            return userDao.getALLUsers().get(0);
        }
    }

    private static class getUser extends AsyncTask<Void, Void, User>{
        UserDao vIdeoDao;
        getUser(UserDao dao) {
            this.vIdeoDao = dao;
        }

        @Override
        protected User doInBackground(Void... voids) {
            return vIdeoDao.getALLUsers().get(0);
        }
    }
}
