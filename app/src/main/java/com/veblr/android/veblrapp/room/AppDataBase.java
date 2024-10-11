package com.veblr.android.veblrapp.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.veblr.android.veblrapp.dao.Converters;
import com.veblr.android.veblrapp.dao.SearchHistoryDao;
import com.veblr.android.veblrapp.dao.UserDao;
import com.veblr.android.veblrapp.dao.VIdeoDao;
import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;

import static androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2;

@Database(entities = {VIdeoItem.class, User.class, SearchHistory.class,}, version = 2,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
    public abstract SearchHistoryDao searchHistoryDao();
    public  abstract VIdeoDao vIdeoDao();
    public  abstract UserDao userDao();
    public static AppDataBase appInstance;

    // Get a database instance
    public static synchronized AppDataBase getDatabaseInstance(Context context) {
        if (appInstance == null) {
            appInstance = create(context);
        }
        return appInstance;
    }

    private static AppDataBase create(Context context) {

        RoomDatabase.Builder<AppDataBase> builder = Room.databaseBuilder(context.getApplicationContext(),
                AppDataBase.class, "VeblrAppDatabase.db");
        builder.addMigrations(MIGRATION_1_2);
        return builder.build();
    }
}
