package com.veblr.android.veblrapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Proxy;
import android.os.Handler;
import android.os.Looper;
import android.os.TokenWatcher;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.VeblrApplication;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class ApiService {

    private static final String TAG = ApiService.class.getSimpleName();

    private static final String API_BASE_URL = "https://api.veblr.com/";
    private static Retrofit retrofit = null;
    static    Context context ;
    static String  newAccessToken = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).
                            addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        }
        return  retrofit;
    }
    public static ApiInterface getService() {

        Retrofit.Builder builder = new  Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClientOk())
                .baseUrl("https://api.veblr.com/");
        return builder.build().create(ApiInterface.class);

    }
    public static Retrofit getService(Context context) {
        AppUtils.initializeSSLContext(context);
        ApiService.context = context;

        if((AppUtils.gettimeDiff(context))<15) {
            //Log.e(TAG, "Inside getService: Iff get from shared preference");
            SharedPreferences sharedPreferences = context.getSharedPreferences("api_auth", MODE_PRIVATE);
            newAccessToken = sharedPreferences.getString("token", "");
        } else {
            //Log.e(TAG, "Inside getService: Else get from service");
            newAccessToken = AppUtils.getResponseValidation(context);
        }

        if(newAccessToken==null) {
            //Log.e(TAG, "Inside getService: if token null");
            newAccessToken = AppUtils.getResponseValidation(context);
        }


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
               /* .authenticator(new TokenAuthenticator(context))*/
                .addInterceptor(new CustomInterceptor()).build();
                       /* new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + newAccessToken)
                        .build();
                return chain.proceed(newRequest);
            }
        }*/
     //  if (retrofit==null)

        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .client(client)
               .build();
        return retrofit;
    }
    public static OkHttpClient getClientOk()  {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();
        return client;
    }
    public static Retrofit get() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    public static class MyServiceHolder {
        ApiInterface myService = null;

        @Nullable
        public ApiInterface get() {
            return myService;
        }

        public void set(ApiInterface myService) {
            this.myService = myService;
        }
    }
    public static class  TokenAuthenticator implements Authenticator
    {
        private Context context;
        private MyServiceHolder myServiceHolder;

        public TokenAuthenticator(Context context, MyServiceHolder myServiceHolder) {
            this.context = context;
            this.myServiceHolder = myServiceHolder;
        }


        @Nullable
        @Override
        public Request authenticate(@Nullable Route route, Response response) throws IOException {
            if (myServiceHolder == null) {
                return null;
            }
                if((AppUtils.gettimeDiff(context))<15) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("api_auth", MODE_PRIVATE);
                    newAccessToken = sharedPreferences.getString("token", "");
                }
                else
                    newAccessToken = AppUtils.getResponseValidation(context);
            if(newAccessToken!=null){
                newAccessToken  = AppUtils.getResponseValidation(context);
                newAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NTQ3NzA5MTgsImlzcyI6ImxvY2FsaG9zdCIsImV4cCI6MTY1NDc3MTgxOCwidXNlcklkIjoxMjg0fQ.m6UUrF_d8JbRivyayawWtqkzJrYytjAGYP9I1wPgHLw";

                return response.request().newBuilder()
                        .header("Authorization", "Bearer "+newAccessToken)
                        .build();
            }
          return null;
        }
    }
    public static class OkHttpClientInstance {
        public static class Builder {
            private HashMap<String, String> headers = new HashMap<>();
            private Context context;
            private MyServiceHolder myServiceHolder;

            public Builder(Context context, MyServiceHolder myServiceHolder) {
                this.context = context;
                this.myServiceHolder = myServiceHolder;
            }

            public Builder addHeader(String key, String value) {
                headers.put(key, value);
                return this;
            }

            public OkHttpClient build() {
                TokenAuthenticator authenticator = new TokenAuthenticator(context, myServiceHolder);

                OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                        .addInterceptor(
                                new Interceptor() {
                                    @Override
                                    public Response intercept(Interceptor.Chain chain) throws IOException {
                                        // Add default headers
                                        Request.Builder requestBuilder = chain.request().newBuilder()
                                                .addHeader("Content-Type", "application/json");

                                        // Add additional headers
                                        Iterator it = headers.entrySet().iterator();

                                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                                            if (entry.getKey() != null && entry.getValue() != null) {
                                                requestBuilder.addHeader(entry.getKey(), entry.getValue());
                                            }
                                        }

                                        if (context != null) {
                                            SharedPreferences settings = context.getSharedPreferences("api_auth",MODE_PRIVATE);

                                            String token = settings.getString("token", null);

                                            if (token != null) {

                                                requestBuilder.addHeader("Authorization","Bearer "+ token);
                                            }
                                        }

                                        return chain.proceed(requestBuilder.build());
                                    }
                                }
                        )
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS);

                okHttpClientBuilder.authenticator(authenticator);
                okHttpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
                return okHttpClientBuilder.build();
            }
        }
    }
    public static class CustomInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            // try the request
            Response response = chain.proceed(request);
            if (!response.isSuccessful()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Inside CustomInterceptor: In Handler call from service");
                    AppUtils.getResponseValidation(context);
                }
            });}
                // get a new token (I use a synchronous Retrofit call)

            if((AppUtils.gettimeDiff(context))<15) {
                //Log.e(TAG, "Inside CustomInterceptor: if from sharedpreference");
                SharedPreferences sharedPreferences = context.getSharedPreferences("api_auth", MODE_PRIVATE);
                newAccessToken = sharedPreferences.getString("token", "");
            } else {
                //Log.e(TAG, "Inside CustomInterceptor: Else get from service");
                newAccessToken = AppUtils.getResponseValidation(context);
            }

            if(newAccessToken==null) {
                //Log.e(TAG, "Inside CustomInterceptor: token is null");
                newAccessToken = AppUtils.getResponseValidation(context);
            }
                // create a new request and modify it accordingly using the new token
                Request newRequest = request.newBuilder().addHeader("Content-Type","application/json; charset=utf-8")
                        .header("Authorization","Bearer "+newAccessToken).build();

                // retry the request
                return chain.proceed(newRequest);
          //  }

            // otherwise just pass the original response on
        //    return response;
        }

    }
}