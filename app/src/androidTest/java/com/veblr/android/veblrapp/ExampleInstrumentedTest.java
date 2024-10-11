package com.veblr.android.veblrapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.veblr.android.veblrapp.dao.Converters;
import com.veblr.android.veblrapp.datasource.Resonse;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.VideoKeyWords;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.ui.PreviewActivity;
import com.veblr.android.veblrapp.util.AppUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import VideoHandle.OnEditorListener;
import io.fabric.sdk.android.services.network.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;

import static VideoHandle.EpEditor.execCmd;
import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.veblr.android.veblrapp", appContext.getPackageName());
    }


    @Test
    public  void getResponse(){

        ApiInterface apiService = ApiService.getService(InstrumentationRegistry.getTargetContext()).create(ApiInterface.class);
        Call<ResponseVideoList> responseVideoListCall = apiService.getVideoListFromSearchText(AppUtils.getJSonOBJForSearch("modi"));

        responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @Override
            public void onResponse(Call<ResponseVideoList> call, retrofit2.Response<ResponseVideoList> response) {
                if(response.isSuccessful()){
                    if (response.body() != null)
                    {

                       // new GsonBuilder().create().fromJson(string, ResponseVideoList.class);
                        /*ResponseVideoList array = gson.fromJson(response, ResponseVideoList.class);
                        ResponseVideoList responseVideoList  = new GsonBuilder().create().fromJson(response.body().toString(), ResponseVideoList.class);
                        Log.e("RESPONSE",responseVideoList.getResonse().getResult().get(0).getVideoId());
                  */
                        Log.e("RESPONSE",response.body().getResonse().getResult().get(0).getVideoId());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                Log.e("FAiled",t.getMessage());
            }
        });
    }


    @Test
    public void convertGson(){
        GsonBuilder gson = new GsonBuilder();
      //  gson.registerTypeAdapter(VideoKeyWords.class, new Converters());
        Gson gson1 = gson.create();
        String STR = "{\"resonse\":{\"status\":200,\"result\":[{\"video_id\":\"3c19979979\",\"video_title\":\"Sushil Kumar Modi press conference after serial bomb blasts at Modi rally in Patna\",\"video_description\":\"BJP at Patna serial blast in Bihar, Nitish government has stood in the dock. Former Deputy Chief Minister Sushil Kumar Modi said the blasts Narendra Modi were targeted. He said that Nitish Kumar look Modi as the enemy.<br \\/>\\r\\n\",\"video_poster\":\"https:\\/\\/vbcdn.com\\/cdn\\/download\\/2013102913830306761810268995.jpg\",\"video_duration\":\"02:02\",\"video_category\":\"News\\/Politics\",\"video_category_link\":\"news-politics\",\"video_posted\":\"Mansi\",\"video_views\":\"123\",\"video_likes\":null,\"video_comments\":null,\"video_uploaded_date\":\"2013-10-29\",\"video_language_short\":\"hi\",\"video_is_family_friendly\":\"true\",\"video_mp4\":\"aHR0cHM6Ly92YmNkbi5jb20vY2RuL2Rvd25sb2FkLzIwMTMxMDI5MTM4MzAzMDY3NjE4MTAyNjg5OTUubXA0\",\"vast_tag\":\"https:\\/\\/pubads.g.doubleclick.net\\/gampad\\/ads?sz=640x480&iu=\\/124319096\\/external\\/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=\",\"video_keywords\":[\"Sushil\",\"Kumar\",\"Modi\",\"press\",\"conference\",\"after\",\"serial\",\"bomb\",\"blasts\",\"at\",\"Modi\",\"rally\",\"in\",\"Patna\"]}]}}";
        ResponseVideoList responseVideoList  = gson1.fromJson(STR,ResponseVideoList.class);
        Log.e("RESPONSE",responseVideoList.getResonse().getResult().get(0).getVideoId());
    }
@Test
    public void unicodetoString() throws UnsupportedEncodingException {
    String s = "\uD83D\uDC46";
    //String s = "eng";
    s = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(s);
//    String str = new String(Character.toChars(unicode);
    Log.d("decoded",s+" ");


    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Log.d("decoded",Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)+"");
    } else {
        Log.d("decoded",Html.fromHtml(s).toString());
    }*/
}
@Test
    public void gettimeDiff() throws ParseException {
    SharedPreferences sharedPreferences = InstrumentationRegistry.getTargetContext().getSharedPreferences("api_auth", MODE_PRIVATE);
    String time1 = sharedPreferences.getString("savedTime", "");
    Date date1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").parse(time1);
    Date date2 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").parse(DateFormat.getDateTimeInstance().format(new Date()));
      long diff = date2.getTime()-date1.getTime();

       Log.d( "time2"+"\n diff",time1+" "+DateFormat.getDateTimeInstance().format(new Date())+"\n "+diff/60000);


    }
 @Test
   public  void getThumbNail() {
        String videouri ="/storage/emulated/0/veblrAppData/Nov 1, 2019 10:35:47 AM.mp4";
     Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videouri,
             MediaStore.Images.Thumbnails.MINI_KIND);

     String fname = Environment.getExternalStorageDirectory()+"/veblrAppData/Nov 1, 2019 10:35:47 AM.png";
     File file = new File (fname);
     if (file.exists ()) file.delete ();
     Log.d("name is ",Uri.parse(fname).getLastPathSegment()+"");

     try {
         FileOutputStream out = new FileOutputStream(file);
         thumb.compress(Bitmap.CompressFormat.JPEG, 90, out);
         out.flush();
         out.close();

     } catch (Exception e) {
         e.printStackTrace();
     }
 }

 @Test

    public void img2Text() throws URISyntaxException {
     String url = "http://mobiupload.veblr.com/files/5c74d4bbc5385dd85207085ebd0f8654";
     URL url2 = null;
     try {
         url2 = new URL(url);
     } catch (MalformedURLException e) {
         e.printStackTrace();
     }

     assert url2 != null;
     Log.e("replaced",url2.toURI().toString().replace("http://mobiupload.veblr.com/files/","")+
             "  "+url2.toURI()+" "+url2.toString().replace("http://mobiupload.veblr.com/files/",""));
     Log.e("replaced",url2.getRef()+ url2.getAuthority()+url2.getFile()+ url2.getPath()+url2.toExternalForm()+ url2.getUserInfo());

    /* // give your image file url in mCurrentPhotoPath
     Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/download/images.jpeg");

     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
     byte[] byteArray = byteArrayOutputStream.toByteArray();

     Log.d("encoded image",java.util.Base64.getEncoder().encodeToString(byteArray));
     Log.d("encoded image", Base64.encodeToString(byteArray, Base64.DEFAULT));*/
     }

     @Test
     public void getThumb(){
         execCmd("fmpeg -i ffmpeg -i /storage/emulated/0/veblrAppData/Nov 21, 2019 11:31:01 AM.mp4 -ss 00:00:1.020 -vframes 1 /storage/emulated/0/out.png ",14,
          new OnEditorListener(){
                     @Override
                     public void onSuccess() {
                        Log.e("THUMB NAIL","GENERATED");
                     }

                     @Override
                     public void onFailure() {
                         Log.e("THUMB NAIL","FAiled");

                     }

                     @Override
                     public void onProgress(float progress) {
                         Log.e("THUMB NAIL",progress+"");

                     }
                 });

     }
   public void  getEmail() {
       String gmail = null;
       Pattern gmailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
       Account[] accounts = AccountManager.get(InstrumentationRegistry.getTargetContext()).getAccounts();
       for (Account account : accounts) {

           // if (gmailPattern.matcher(account.name).matches()) {
           gmail = account.name;
           Log.d("EMAILID", "" + gmail);
           // }
       }
   }
}
