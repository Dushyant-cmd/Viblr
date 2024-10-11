package com.veblr.android.veblrapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.User;

import java.util.List;
@Dao
public interface UserDao {

    /**
     * Insert a user in the database. If the user already exists, replace it.
     *
     * @param user the user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("DELETE FROM USER_TABLE")
    void deleteAllSearchString ();


   /* @Query("SELECT * FROM USER_TABLE WHERE  deviceId= :deviceid" )
    User getUser(String deviceid);
*/
    @Query("SELECT * FROM USER_TABLE WHERE  guestUserId= :userid" )
    User getUser(String userid);
    @Query("SELECT * FROM USER_TABLE WHERE  userId= :userid" )
    User getRegisteredUser(String userid);
    @Query("UPDATE USER_TABLE SET deviceId= :deviceId WHERE guestUserId = :appUserId")
    void setDeviceId(String deviceId,String appUserId);

    @Query("SELECT * FROM USER_TABLE")
    List<User> getALLUsers();

}
