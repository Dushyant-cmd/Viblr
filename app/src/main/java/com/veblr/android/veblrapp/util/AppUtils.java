package com.veblr.android.veblrapp.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.Country;
import com.veblr.android.veblrapp.model.Notification;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.repositories.UserProfileRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static com.veblr.android.veblrapp.util.Constants.APP_USER;
import static com.veblr.android.veblrapp.util.Constants.COUNTRY_LIST;
import static com.veblr.android.veblrapp.util.Constants.FAVORITES;
import static com.veblr.android.veblrapp.util.Constants.FAVORITES_USER;
import static com.veblr.android.veblrapp.util.Constants.LIKED_LIST;
import static com.veblr.android.veblrapp.util.Constants.NOTIVFICATION_LIST;
import static com.veblr.android.veblrapp.util.Constants.PREFS_NAME;
import static com.veblr.android.veblrapp.util.Constants.RESGISTER_APP_USER;
import static com.veblr.android.veblrapp.util.Constants.SAVED_LOCATION;
import static com.veblr.android.veblrapp.util.Constants.TEMP_COUNTRYCODE;
import static com.veblr.android.veblrapp.util.Constants.TEMP_EMAILID;
import static com.veblr.android.veblrapp.util.Constants.TEMP_MOBNO;

public class AppUtils {

    private final static String TAG = AppUtils.class.getSimpleName();
    public ArrayList<VIdeoItem> vIdeoItemArrayList;
    private static String[] suffix = new String[]{"", "k", "m", "b", "t"};

    //prefs
    /*SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    SharedPreferences.Editor editor = pref.edit();*/

    public static int getDeviceHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getDeviceWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }


    public static void disableButton(ImageView imageView, TextView textView, Context context) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(Color.parseColor("#D4D4D4")));
        textView.setTextColor(context.getResources().getColor(R.color.colorBottomNavigationInactive));

    }

    public static void enableButton(ImageView imageView, TextView textView, Context context) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(Color.parseColor("#2C98FF")));
        textView.setTextColor(context.getResources().getColor(R.color.bottombarbackground));

    }


    public static String getResponseValidation(final Context context) {

        final String[] token = {""};

        new Thread() {
            public void run() {

                JsonObject obj = new JsonObject();
                JsonObject payerReg = new JsonObject();

                payerReg.addProperty("username", "testapiuser");
                payerReg.addProperty("api_key", "54OFaOmKPM4wM5vg8JVBfDaoce4JKF9t");
                payerReg.addProperty("password", "38qmGcz5FJ7nZjEW");
                obj.add("param", payerReg);

                ApiInterface apiInterface = ApiService.get().create(ApiInterface.class);
                Call<Response> responseCall = apiInterface.getResponseValidToken(obj);

                responseCall.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                        if (response.isSuccessful()) {
                            try {
                                if (response.body().getResonse() != null) {
                                    token[0] = response.body().getResonse().getResult().getToken();
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("api_auth", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token", token[0]);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.getDefault());
                                    //Comment By Rakesh
                                    /*String time;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        //time = DateFormat.getDateTimeInstance().format(new Date());
                                        time = sdf.format(new Date());
                                        editor.putString("savedTime", time);
                                    } else {
                                        time = sdf.format(new Date());
                                        editor.putString("savedTime", time);
                                    }*/
                                    String time = sdf.format(new Date());
                                    //String time2 = sdf.format(Calendar.getInstance().getTime());
                                    editor.putString("savedTime", time);
                                    Log.d(TAG, "getResponseValidation: Time is: "+ time);//+" \n "+time2);
                                    editor.apply();
                                    Log.e(TAG, "getResponseValidation: onResponse Token: "+ token[0]);

                                    /*if (AppUtils.getAppUserId(context) == null) {
                                        Log.e(TAG, "getResponseValidation: Inside Create New User After Get Token");
                                        AppUtils.createNewUser(context, AppUtils.getDeviceUniqueId(context));
                                    }*/
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(context, "No Data", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            token[0] = null;
                            Log.e("ResponseList", response.code() + " " + response.errorBody().contentType().type() + " " + response.raw().body().toString().toLowerCase());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Log.d(TAG,"onFailure: "+ t.getMessage());
                    }
                });
            }
        }.start();
        return token[0];
    }

    public static JsonObject getJSonOBJForSearch(String searchText) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", "50");
        payerReg.addProperty("search", searchText);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSONOBJForCategory(String category_name) {
        String s = "{\"param\":{\"max_results\":\"100\",\"category\":[\"" + category_name + "\"]}}";
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(s);

        return json;
    }

    public static JsonObject getJSonOBJForFollwedChannels(String searchText) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", "50");
        payerReg.addProperty("channel_name", searchText);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForFeaturedChannels(String numbers) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", numbers);
        payerReg.addProperty("cache_data_chk", false);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLocation(String searchText) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", "50");
        payerReg.addProperty("search", searchText);
        payerReg.addProperty("cache_data_chk", false);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForHome() {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", "200");
        payerReg.addProperty("sort", "latest");
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForRecommendate(Context c) {
        JsonObject obj = null;
        if (AppUtils.getRegisteredUserId(c) != null) {
            obj = new JsonObject();
            JsonObject payerReg = new JsonObject();
            payerReg.addProperty("app_user_id", AppUtils.getAppUserId(c).getGuestUserId());
            payerReg.addProperty("channel_id", AppUtils.getRegisteredUserId(c).getChannel().get(0).getChId());
            payerReg.addProperty("user_id", AppUtils.getRegisteredUserId(c).getUserId());
            payerReg.addProperty("max_results_from_each_channel", 7);
            payerReg.addProperty("max_results", "200");
            //   payerReg.addProperty("sort", "latest");
            obj.add("param", payerReg);

        } else {
            obj = new JsonObject();
            JsonObject payerReg = new JsonObject();
            payerReg.addProperty("app_user_id", AppUtils.getAppUserId(c).getGuestUserId());
            payerReg.addProperty("channel_id", "null");
            payerReg.addProperty("user_id", "null");
            payerReg.addProperty("max_results_from_each_channel", 7);
            payerReg.addProperty("max_results", "200");
            //   payerReg.addProperty("sort", "latest");
            obj.add("param", payerReg);
        }


        return obj;

    }

    public static JsonObject getJSonOBJForTAgList(String s) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", 5);
        payerReg.addProperty("search", s);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForCommentList(String videoid) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", "50");
        payerReg.addProperty("video_id", videoid);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForAddingComment(String videoid, String comment, String appUserID) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("comment", comment);
        payerReg.addProperty("video_id", videoid);
        payerReg.addProperty("app_user_id", appUserID);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForAddingCommentRegUser(String videoid, String comment, String userID, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("comment", comment);
        payerReg.addProperty("video_id", videoid);
        payerReg.addProperty("user_id", userID);
        payerReg.addProperty("channel_id", channelId);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLikeorUnlike(String videoId, String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("video_id", videoId);
        payerReg.addProperty("app_user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLikeorUnlikeReg(String videoId, String appuserid, String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("video_id", videoId);
        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForFolloworUnfollow(String followedchannelid,
                                                           String userId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("followed_channel_id", followedchannelid);
        payerReg.addProperty("app_user_id", userId);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForFolloworUnfollowReg(String followedchannelid,
                                                              String appuserid, String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("followed_channel_id", followedchannelid);
        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("channel_id", channelId);

        obj.add("param", payerReg);
        return obj;
    }


    public static JsonObject getJSonOBJForFollowingList(String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", 90);
        payerReg.addProperty("app_user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForFollowingListReg(String appuserid, String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", 90);
        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForDeleteAVideo(String userId, String channelId, String videoId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("video_id", videoId);
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("channel_id", channelId);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLikedVideoList(String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", 90);

        payerReg.addProperty("app_user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLikedVideoListReg(String appuserid, String userId, String channelId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("max_results", 90);
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("channel_id", channelId);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static void createNewUser(final Context context, final String deviceUniqueId) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("device_id", deviceUniqueId);
        obj.add("param", payerReg);

        Call<Response> responseCall = ApiService.getService(context).create(ApiInterface.class).registerGuestUser(obj);
        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResonse() != null) {
                            String appUserID = response.body().getResonse().getResult().getAppUserId();
                            Log.e(TAG, "createNewUser: appUserID: " + appUserID);
                            saveAppUserId(context, appUserID);
                            // if(AppDataBase.getDatabaseInstance(context).userDao().getUser(appUserID)==null)
                            getUser(context, appUserID);
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(context, "No Data", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "createNewUser: Response is not successful");
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "createNewUser: onFailure: " + t.getMessage());
            }
        });
    }

    public static void saveLocation(Context c, String result) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = c.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(SAVED_LOCATION, result);

        editor.apply();
    }

    public static String getSavedLocation(Context context) {
        SharedPreferences settings;
        String lastLocation = "";
        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(SAVED_LOCATION)) {
            lastLocation = settings.getString(SAVED_LOCATION, null);
        }
        if (lastLocation == null)
            lastLocation = "India";
        return lastLocation;
    }

    public static boolean checkForLoggedInUser(Context context) {
        boolean loggedIn = false;
        String userid = getUserId(context);
        if (userid != null)
            loggedIn = true;
        return loggedIn;
    }

    public static void saveuserId(String userID, Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(userID);
        editor.putString("user_id", jsonFavorites);
        editor.apply();

    }

    public static String getUserId(Context context) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        String userid = settings.getString("user_id", null);
        return userid;
    }

    public static void saveRegisteredUserId(Context c, User user) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = c.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(user);
        editor.putString(RESGISTER_APP_USER, jsonFavorites);
        editor.apply();
    }

    public static User getRegisteredUserId(Context c) {

        SharedPreferences settings;
        User user;
        settings = c.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        String username = settings.getString(RESGISTER_APP_USER, null);
        Gson gson = new Gson();
        user = (User) gson.fromJson(username,
                User.class);
        if (user != null) {
            Log.e("USER", user.getName());
            return user;
        } else return null;
    }

    public static JsonObject getJsonObjforupdateVideo(String appuserid, String userid, String channelid, String videoid, String title, String desc, String cataId, String langId
            , String videoAdult, String videoMonetize) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userid);
        payerReg.addProperty("channel_id", channelid);
        payerReg.addProperty("video_id", videoid);
        payerReg.addProperty("video_title", title);
        payerReg.addProperty("video_description", desc);
        payerReg.addProperty("video_category", cataId);
        payerReg.addProperty("video_language", langId);
        payerReg.addProperty("video_adult", videoAdult);
        payerReg.addProperty("video_monetize", videoMonetize);
        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJsonobjForTagUpdate(String channelid, String userId, String videoId, String[] tags) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        JsonArray tagArray = new JsonArray();
        for (String s : tags) {
            tagArray.add(s);
        }
        payerReg.addProperty("user_id", userId);
        payerReg.addProperty("video_id", videoId);
        payerReg.addProperty("channel_id", channelid);
        payerReg.add("video_tags_arr", tagArray);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForUpdatingUserImage(String appuserid, String userid, String image) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", 90);

        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userid);
        payerReg.addProperty("user_profile_pic", image);

        obj.add("param", payerReg);
        return obj;

    }

    public static JsonObject getJSonOBJForUpdatingUserAccountDetails(String appuserid, String userId, String name,
                                                                     String day, String month, String year,
                                                                     String gender, String subscription) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("app_user_id", appuserid);
        payerReg.addProperty("user_id", userId);

        payerReg.addProperty("user_name", name);
        payerReg.addProperty("user_dob_day", day);
        payerReg.addProperty("user_dob_month", month);
        payerReg.addProperty("user_dob_year", year);
        payerReg.addProperty("user_gender", gender); //male/female
        payerReg.addProperty("user_newsletter", subscription); //yes no

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSONOBJForMailIdExists(String mailid) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("user_email_id", mailid);
        obj.add("param", payerReg);
        return obj;

    }

    public static JsonObject getJsonObjForNoVerification(int countryId, String mobNo, String emailId) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("user_country_id", countryId);
        payerReg.addProperty("user_mobile_no", mobNo);
        payerReg.addProperty("user_email_id", emailId);
        obj.add("param", payerReg);
        return obj;
    }

    public static void saveCountriesWithCodes(List<Country> countries, Context context) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(countries);

        editor.putString(COUNTRY_LIST, jsonFavorites);

        editor.apply();

    }

    public static List<Country> getCountriesList(Context context) {
        SharedPreferences settings;
        List<Country> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(COUNTRY_LIST)) {
            String jsonFavorites = settings.getString(COUNTRY_LIST, null);

            Gson gson = new Gson();
            final Country[] favoriteItems = gson.fromJson(jsonFavorites,
                    Country[].class);
            favorites = Arrays.asList(favoriteItems);


        } else
            return null;

        return favorites;
    }

    public static void saveNotification(Context c, List<Notification> list) {
        SharedPreferences pref = c.getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String listOfNotification = gson.toJson(list);

        editor.putString(Constants.NOTIVFICATION_LIST, listOfNotification);
        editor.commit();
    }

    public static List<Notification> getNotificationList(Context context) {
        SharedPreferences settings;
        List<Notification> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(NOTIVFICATION_LIST)) {
            String jsonFavorites = settings.getString(NOTIVFICATION_LIST, null);

            Gson gson = new Gson();
            final Notification[] favoriteItems = gson.fromJson(jsonFavorites,
                    Notification[].class);
            favorites = Arrays.asList(favoriteItems);


        } else
            return null;

        return favorites;
    }

    public static void addNotificationToList(Context context, Notification notification) {

        List<Notification> favoriteUsers = getNotificationList(context);
        if (favoriteUsers == null) {
            favoriteUsers = new ArrayList<>();
        }
        favoriteUsers.add(notification);
        saveNotification(context, favoriteUsers);


    }

    public static void setTempMobNumber(String mobNo, Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(TEMP_MOBNO, mobNo);
        editor.apply();
    }

    public static void setTempEmailId(String emailId, Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(TEMP_EMAILID, emailId);
        editor.apply();
    }

    public static void setTempCountryCode(int countryCode, Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putInt(TEMP_COUNTRYCODE, countryCode);
        editor.apply();
    }

    public static String getTempMobNo(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        String tempMOB = settings.getString(TEMP_MOBNO, null);
        return tempMOB;
    }

    public static String getTEmpEmailId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        String tempEMAIL = settings.getString(TEMP_EMAILID, null);
        return tempEMAIL;
    }

    public static int getTempCountryCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        int tempCC = settings.getInt(TEMP_COUNTRYCODE, 91);
        return tempCC;
    }

    public static JsonObject getJsonObjForChannelDetail(String userid, String chId, String name, String about, String website, String fb,
                                                        String twitter, String youtube, String dailyMotion, String blog,
                                                        String pinterest, String category, String lang, String countryid) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("user_id", userid);
        payerReg.addProperty("channel_id", chId);
        payerReg.addProperty("channel_name_display", name);
        payerReg.addProperty("channel_about", about);
        payerReg.addProperty("channel_website_link", website);
        payerReg.addProperty("channel_facebook_link", fb);
        payerReg.addProperty("channel_twitter_link", twitter);
        payerReg.addProperty("channel_youtube_link", youtube);
        payerReg.addProperty("channel_dailymotion_link", dailyMotion);
        payerReg.addProperty("channel_blogger_link", blog);
        payerReg.addProperty("channel_pinterest_link", pinterest);
        payerReg.addProperty("channel_category", category);
        payerReg.addProperty("channel_language", lang);
        payerReg.addProperty("channel_country", countryid);
        obj.add("param", payerReg);
        return obj;

    }

    public static JsonObject getJSonObjForChannelimage(String userid, String chId, String image) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("user_id", userid);
        payerReg.addProperty("channel_id", chId);
        payerReg.addProperty("channel_profile_pic", image);
        obj.add("param", payerReg);
        return obj;

    }

    public static JsonObject getVerifiedUserData(String tempuserid, String username, String password,
                                                 String retypePW, String day, String month, String year,
                                                 String gen, boolean terms) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        JsonArray tagArray = new JsonArray();

        payerReg.addProperty("temp_user_id", tempuserid);
        payerReg.addProperty("user_name", username);
        payerReg.addProperty("user_password", password);
        payerReg.addProperty("user_password_retype", retypePW);
        payerReg.addProperty("user_dob_day", day);
        payerReg.addProperty("user_dob_month", month);
        payerReg.addProperty("user_dob_year", year);
        payerReg.addProperty("user_gender", gen);
        payerReg.addProperty("user_terms", terms);

        obj.add("param", payerReg);
        return obj;


    }


    public ArrayList<VIdeoItem> getListHome(String searchParam) {
        vIdeoItemArrayList = new ArrayList<>();
        return vIdeoItemArrayList;
    }

    public static RequestManager initGlide(Context c) {
        RequestOptions options =
                new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.white_background)
                        .error(R.drawable.white_background);
        if (c != null)
            return Glide.with(c)
                    .setDefaultRequestOptions(options);
        else return null;
    }

/*
    public static String uniCodeToString(JsonObject string) {
    }
       */

    // This four methods are used for maintaining favorites.
    public static void saveFeatured(Context context, List<Channel> user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(user);

        editor.putString("FeaturedUser", jsonFavorites);

        editor.apply();
    }

    public static ArrayList<Channel> getFeaturedUser(Context context) {
        SharedPreferences settings;
        List<Channel> favorites = new ArrayList<>();

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains("FeaturedUser")) {
            String jsonFavorites = settings.getString("FeaturedUser", null);
            Gson gson = new Gson();
            Channel[] favoriteItems = gson.fromJson(jsonFavorites,
                    Channel[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Channel>(favorites);
            return (ArrayList<Channel>) favorites;
        }

        return (ArrayList<Channel>) favorites;
    }

    public static void saveFavUser(Context context, List<Channel> user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(user);

        editor.putString(FAVORITES_USER, jsonFavorites);

        editor.apply();
    }

    public static void addFavoriteUser(Context context, Channel user) {
        List<Channel> favoriteUsers = getFavoriteUser(context);
        if (favoriteUsers == null)
            favoriteUsers = new ArrayList<>();
        if(favoriteUsers.size()==0) {
            favoriteUsers.add(user);
            saveFavUser(context, favoriteUsers);
        }else {
            for (int i = 0; i > favoriteUsers.size(); i++) {
                if (!favoriteUsers.get(i).getChId().equals(user.getChId())) {
                    favoriteUsers.add(user);
                    saveFavUser(context, favoriteUsers);
                    break;
                }
            }
        }
    }

    public static void removeFavoriteUser(Context context, String productID) {
        List<Channel> favorites = getFavoriteUser(context);
        if (favorites != null) {
            for (Channel c : favorites) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(c.getChId(), productID)) {
                        favorites.remove(c);
                        break;
                    }
                }

            }
            saveFavUser(context, favorites);
        }
    }

    public static void removeFavoriteUsers(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(FAVORITES_USER).apply();

    }

    public static ArrayList<Channel> getFavoriteUser(Context context) {
        SharedPreferences settings;
        List<Channel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                MODE_PRIVATE);

        if (settings.contains(FAVORITES_USER)) {
            String jsonFavorites = settings.getString(FAVORITES_USER, null);
            Gson gson = new Gson();
            final Channel[] favoriteItems = gson.fromJson(jsonFavorites,
                    Channel[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Channel>(favorites);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                favorites.stream()
                        .distinct()
                        .collect(Collectors.toList());
            } else {
                Collections.sort(favorites, new Comparator<Channel>() {
                    @Override
                    public int compare(Channel o1, Channel o2) {
                        if (o1.getChId().equalsIgnoreCase(o2.getChId()))
                            return 0;
                        else return 1;
                    }
                });
            }
        } else
            return null;

        return (ArrayList<Channel>) favorites;
    }

    public static void saveFavorites(Context context, List<VIdeoItem> favorites) {
        try {
            Gson gson = new Gson();
            String jsonFavorites = gson.toJson(favorites);

            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(FAVORITES, jsonFavorites);

            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* public void addFavorite(Context context, VIdeoItem product) {
         List<VIdeoItem> favorites = getFavorites(context);
         if (favorites == null)
             favorites = new ArrayList<>();
         favorites.add(product);
         saveFavorites(context, favorites);
     }
 
     public void removeFavorite(Context context, VIdeoItem product) {
         ArrayList<VIdeoItem> favorites = getFavorites(context);
         if (favorites != null) {
             favorites.remove(product);
             saveFavorites(context, favorites);
         }
     }
 */
    public static ArrayList<VIdeoItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<VIdeoItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            VIdeoItem[] favoriteItems = gson.fromJson(jsonFavorites,
                    VIdeoItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<VIdeoItem>(favorites);
        } else
            return null;

        return (ArrayList<VIdeoItem>) favorites;
    }

    public static String format(long number) {
   /*   String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > 4 || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;*/
        if (number < 1000) return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c",
                number / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public static String getDeviceUniqueId(Context context) {
        //Log.e(TAG,"getDeviceUniqueId: DEVICEID: "+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static int gettimeDiff(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("api_auth", MODE_PRIVATE);
        sharedPreferences.edit().putString("savedTime", "");
        String time1 = sharedPreferences.getString("savedTime", "");
        Date date1 = null;
        Date date2 = null;
        int x = 16;
        try {
            assert time1 != null;
            if (!time1.isEmpty()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Log.e(TAG, "Inside less than O");
                    date1 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.US).parse(time1);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.getDefault());
                    date2 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.US).parse(sdf.format(new Date()));
                    Log.e(TAG, "TIME TAG" + date1 + "  " + date2);
                } /*else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                    Log.e(TAG, "Inside equals to P");
                    date1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss", Locale.US).parse(time1);
                    date2 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss", Locale.US).parse(DateFormat.getDateTimeInstance().format(new Date()));

                    Log.e(TAG, "TIME TAG" + date1 + "  " + date2);
                }*/ else {
                    Log.e(TAG, "Inside Else");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.getDefault());
                    date1 = simpleDateFormat.parse(time1);//14 Jun 2022 05:23:14 pm
                    //date1 = simpleDateFormat.parse("10 Sep 2022 11:04:22 am");

                    String s = simpleDateFormat.format(new Date());
                    /*fdate2 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a",Locale.US)
                            .parse(DateFormat.getDateTimeInstance().format(new Date()));*/

                    date2 = simpleDateFormat.parse(s);
                    Log.e(TAG, "TIME TAG" + date1 + "  " + date2);
                    Log.e(TAG, "TIME TAG" + date1 + "  " + date2);
                }

                long diff = date2.getTime() - date1.getTime();

                //Log.d("time2" + "\n diff", time1 + " " + DateFormat.getDateTimeInstance().format(new Date()) + "\n " + diff / 60000);
                x = (int) (diff / 60000);
                Log.e(TAG, "gettimeDiff: x:" + x);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            try {
                Log.e(TAG, "Inside Catch");
                date1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.US).parse(time1);
                date2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.US).parse(DateFormat.getDateTimeInstance().format(new Date()));
                long diff = date2.getTime() - date1.getTime();
                //Log.d("time2" + "\n diff", time1 + " " + DateFormat.getDateTimeInstance().format(new Date()) + "\n " + diff / 60000);
                x = (int) (diff / 60000);
                Log.e(TAG, "inside catch: gettimeDiff: x:" + x);
            } catch (ParseException e2) {
                e2.printStackTrace();
                Log.e(TAG, "Inside Catch 2");
            }
        }
        return x;
    }

    public static void showKeyboard(final EditText ettext, final Context context) {
        ettext.requestFocus();
        ettext.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                   keyboard.showSoftInput(ettext, InputMethodManager.SHOW_IMPLICIT);

                               }
                           }
                , 200);
    }

    private static void hideSoftKeyboard(EditText ettext, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(ettext.getWindowToken(), 0);
    }

    public static boolean hasPermission(Context context, String permission) {

        int res = context.checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;

    }


    //forlikes
    public static void saveLikedVideo(Context context, List<String> videoidList) {

        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(videoidList);

        editor.putString(LIKED_LIST, json);
        editor.commit();

    }

    public static void addLikedVideo(Context context, String videoId) {
        List<String> favoriteVideos = getLikedVideos(context);
        if (favoriteVideos == null)
            favoriteVideos = new ArrayList<>();
        favoriteVideos.add(videoId);
        saveLikedVideo(context, favoriteVideos);
    }

    public static void removeLikedVideo(Context context, String videoId) {
        List<String> favorites = getLikedVideos(context);
        if (favorites != null) {
            for (String c : favorites) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(c, videoId)) {
                        favorites.remove(c);
                        break;
                    }
                }
            }
            saveLikedVideo(context, favorites);
        }
    }

    public static List<String> getLikedVideos(Context context) {

        SharedPreferences sharedPrefs = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(LIKED_LIST, "");

        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(json, type);

    }


    public static void saveAppUserId(Context context, String appuserID) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        User user = new User(appuserID);
        user.setDeviceId(AppUtils.getDeviceUniqueId(context));
        String jsonFavorites = new Gson().toJson(user);
        editor.putString(APP_USER, jsonFavorites);
        editor.apply();
        //getAppUserId(context);
    }

    public static User getAppUserId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        ;
        String username = settings.getString(APP_USER, null);
        User user = (User) new Gson().fromJson(username, User.class);
        if (user != null) {
            Log.d(TAG, "getAppUserId: user: " + user.getGuestUserId());
            return user;
        } else return null;
    }

    public static void getUser(final Context context, final String appUserId) {
        User user = new User(appUserId);
        user.setDeviceId(getDeviceUniqueId(context));
        new UserProfileRepository(context).insertUser(user);
    }

    public static void likeTheVideo(String videoid, Context context) {

        Call<Response> liked;
        if (AppUtils.getRegisteredUserId(context) != null) {
            liked = ApiService.getService(context).create(ApiInterface.class)
                    .setlikevideo(AppUtils.getJSonOBJForLikeorUnlikeReg(videoid, Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(), Objects.requireNonNull(AppUtils.
                                    getRegisteredUserId(context)).getUserId(),
                            Objects.requireNonNull(AppUtils.getRegisteredUserId(context)).getChannel().get(0).getChId()));
            liked.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {

                }
            });

        } else {
            if (AppUtils.getAppUserId(context).getGuestUserId() != null) {
                liked = ApiService.getService(context).create(ApiInterface.class)
                        .setlikevideo(AppUtils.getJSonOBJForLikeorUnlike(videoid,
                                Objects.requireNonNull(AppUtils.getAppUserId(context).getGuestUserId()), ""));
                liked.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });

            } else {
                Log.e(TAG, "Create New User From likeTheVideo");
                createNewUser(context, AppUtils.getDeviceUniqueId(context));
            }
        }

    }

    public static void unlikeTheVideo(String videoid, Context context) {

        Call<Response> liked;

        if (AppUtils.getRegisteredUserId(context) != null) {
            liked = ApiService.getService(context).create(ApiInterface.class)
                    .setlikevideo(AppUtils.getJSonOBJForLikeorUnlikeReg(videoid, Objects.requireNonNull(AppUtils.getAppUserId(context)).getGuestUserId(), Objects.requireNonNull(AppUtils.
                                    getRegisteredUserId(context)).getUserId(),
                            Objects.requireNonNull(AppUtils.getRegisteredUserId(context)).getChannel().get(0).getChId()));

        } else {
            liked = ApiService.getService(context).create(ApiInterface.class)
                    .setUnlikeVideo(AppUtils.getJSonOBJForLikeorUnlike(videoid, Objects.requireNonNull(AppUtils.getAppUserId(context).getGuestUserId()), ""));
        }

        liked.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

    }

    public static String getRealVideoPathFromURI(ContentResolver contentResolver,
                                                 Uri contentURI) {
        Cursor cursor = contentResolver.query(contentURI, null, null, null,
                null);
        if (cursor == null)
            return contentURI.getPath();
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            try {
                return cursor.getString(idx);
            } catch (Exception exception) {
                return null;
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static JsonObject getJSonOBJForaddingNewVideo(String userid, String channelId, String title, String desc, String endpoint,
                                                         String duration, String lang, String cat,
                                                         String thumb, String[] tags) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        JsonArray tagArray = new JsonArray();
        for (String s : tags) {
            tagArray.add(s);
        }
        payerReg.addProperty("user_id", userid);
        payerReg.addProperty("channel_id", channelId);
        payerReg.addProperty("video_title", title);
        payerReg.addProperty("video_description", desc);
        payerReg.addProperty("video_temp_uploaded", endpoint);
        payerReg.addProperty("video_duration_seconds", duration);
        payerReg.addProperty("video_category", cat);
        payerReg.addProperty("video_adult", "no");
        payerReg.addProperty("video_monetize", "yes");
        payerReg.addProperty("video_language", lang);
        payerReg.addProperty("video_thumbnail", thumb);
        payerReg.add("video_tags_arr", tagArray);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForLogin(int countryCode, String mobNo, String password) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("country_code", countryCode);
        payerReg.addProperty("mobile_no", mobNo);
        payerReg.addProperty("password", password);

        obj.add("param", payerReg);
        return obj;
    }

    public static JsonObject getJSonOBJForUserDetails(String user_id) {

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        user_id = user_id.replaceAll("\"", "");
        payerReg.addProperty("user_id", user_id);
        payerReg.addProperty("cache_data_chk", false);

        obj.add("param", payerReg);
        return obj;
    }

    public static void saveLogoIntoLocal(Context context) {

        File file = new File(Environment.getExternalStorageDirectory() + "/veblrAppData/veblr-logo.png");
        File dir = new File(Environment.getExternalStorageDirectory() + "/veblrAppData");
        if (!dir.exists()) {
            // do something here
            dir.mkdir();
        }
        if (!file.exists()) {

            AssetManager assetManager = context.getAssets();
            String[] files = null;
            try {
                files = assetManager.list("");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }
            if (files != null) for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(Environment.getExternalStorageDirectory() + "/veblrAppData/", filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}