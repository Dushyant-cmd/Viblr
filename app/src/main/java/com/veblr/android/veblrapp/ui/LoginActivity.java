package com.veblr.android.veblrapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.veblr.android.veblrapp.BrowserActivity;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.dao.UserDao;
import com.veblr.android.veblrapp.datasource.ResponseLogin;
import com.veblr.android.veblrapp.datasource.ResponseUser;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.Country;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.room.AppDataBase;
import com.veblr.android.veblrapp.util.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText userName;
    EditText password;
    Button signIn;
    ProgressBar progressBar;
    Thread threadForchannel=new Thread();
    Thread threadFOrLikes =new Thread();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.register_actionabar, null);
            v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.super.onBackPressed();

                }
            });
            ActionBar.LayoutParams p = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);

            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setText(R.string.login);

            getSupportActionBar().setCustomView(v, p);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        userName = (EditText) findViewById(R.id.etLPhoneNO);
        password = (EditText) findViewById(R.id.etLPassword);
        signIn = (Button) findViewById(R.id.btnSignIn);
        progressBar = (ProgressBar)findViewById(R.id.pb);
        spinner = (Spinner) findViewById(R.id.spinnerLogin);
         List<Country> countries=new ArrayList<>();
        try {
           countries = AppUtils.getCountriesList(LoginActivity.this);
            List<String> countrynameList = new ArrayList<>();
            for (Country c : countries) {
                countrynameList.add(c.getCountryName());
            }
            ArrayAdapter<CharSequence> adapterC = new ArrayAdapter(LoginActivity.this
                    , android.R.layout.simple_spinner_item, countrynameList);
            adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterC);
            spinner.setOnItemSelectedListener(LoginActivity.this);
        }catch
        (NullPointerException e){e.printStackTrace();}
        finally {
            if(countries==null){
            ArrayAdapter<CharSequence> adapterC = new ArrayAdapter(LoginActivity.this
                    , android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.CountryArray));
            adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterC);
            spinner.setOnItemSelectedListener(LoginActivity.this);
            }
        }
        if(AppUtils.getTempMobNo(LoginActivity.this)!=null){
            userName.setText(AppUtils.getTempMobNo(LoginActivity.this));
        }

        final List<Country> finalCountries = countries;
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nonNullSignIn= false;

                if(userName.getText().toString().isEmpty() && password.getText().toString().isEmpty())
                {
                    ((TextView)findViewById(R.id.tvError)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.tvError)).setText("Mobile number and Password fields can't be left empty.");

                }
                if(userName.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    if (password.getText().toString().isEmpty()) {
                    ((TextView) findViewById(R.id.tvError)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tvError)).setText("Password field can't be left empty.");

                }
                    if (userName.getText().toString().isEmpty()) {
                        ((TextView) findViewById(R.id.tvError)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.tvError)).setText("Mobile number field can't be left empty.");

                    }

                }
                if(!userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    nonNullSignIn=true;
                }
                if(nonNullSignIn) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    int countryCode=91 ;
                    if(finalCountries!=null)
                     countryCode = Integer.valueOf(finalCountries.get(spinner.getSelectedItemPosition()).getCountryCode());

                    Call<ResponseLogin> responseVideoListCall =
                            ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                                    .getLoginResponse(AppUtils.getJSonOBJForLogin
                                            (countryCode,userName.getText().toString(), password.getText().toString()));
                    responseVideoListCall.enqueue(new Callback<ResponseLogin>() {

                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            progressBar.setVisibility(View.GONE);

                            if(response.isSuccessful()&& response.body().getResponse().getStatus()==200){
                               String userID = response.body().getResponse().getResult().getUserId();
                               Log.e("LOGGEDIN USERID",userID);

                                AppUtils.saveuserId(userID,getApplicationContext());
                                AppUtils.saveFavUser(LoginActivity.this,new ArrayList<Channel>());
                             //   AppUtils.saveFavorites(LoginActivity.this,new ArrayList<VIdeoItem>());
                                AppUtils.saveLikedVideo(LoginActivity.this,new ArrayList<String>());
                                AppUtils.removeFavoriteUsers(getApplicationContext());
                                FollowFragment.updateFollowedList(LoginActivity.this);
                                SaveUserDeatilsTask saveUserDeatilsTask = new SaveUserDeatilsTask(userID);
                                saveUserDeatilsTask.execute();
                            }
                         else{
                                ((TextView)findViewById(R.id.tvError)).setVisibility(View.VISIBLE);
                                ((TextView)findViewById(R.id.tvError)).setText("Incorrect login details. Kindly enter correct username and password.");
                             //   Log.e(  "loggedin response",response.body().getResponse().getResult()+"");
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.tvError)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.tvError)).setText("Incorrect login details. Kindly enter correct username and password.");

                            Log.e(  "loggedin response",t.getMessage()+"");
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
       // Toast.makeText(getApplicationContext(), "Selected User: "+getResources().getStringArray( R.array.CountryArray)[position] ,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
   public void OnSignUpFromLogin(View v){ startActivity(
           new Intent(LoginActivity.this,RegisterActivity.class));}

    public void OnForgotPasswordClicked(View view) {
      /*  final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.forgot_password);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();*/
        Intent intent = new Intent (LoginActivity.this, BrowserActivity.class);
        intent.putExtra("browser_url","https://veblr.com/m/forgot-password");
        startActivity(intent);
    }

    private class SaveUserDeatilsTask extends AsyncTask<Void,Void,Void> {
        String userid ;
        public SaveUserDeatilsTask(String userID) {
            this.userid = userID;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Call<ResponseUser> responseVideoListCall =
                    ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                            .getUserDetailsAfterLogIn(AppUtils.getJSonOBJForUserDetails
                                    (AppUtils.getUserId(LoginActivity.this)));
            responseVideoListCall.enqueue(new Callback<ResponseUser>() {

                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {

                    if(response.isSuccessful()){
                      User user =   response.body().getResponse().getResult();
                        AppUtils.saveRegisteredUserId(LoginActivity.this,user);
                        startActivity(new Intent(LoginActivity.this,UserProfileActivity.class));
                        saveFav();
                   /*   AppDataBase  appDataBaseInstance = AppDataBase.getDatabaseInstance(getApplicationContext());
                      UserDao  userDao = appDataBaseInstance.userDao();
                      userDao.insertUser(user);*/

                    }

                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {

                }
            });

            return null;
        }
    }

    private void saveFav() {
        Thread threadForchannel=new Thread(){
            @Override
            public void run() {
                super.run();
                Call<ChannelDetailResponse> channelsCall ;
                if(AppUtils.getRegisteredUserId(getApplicationContext())!=null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        channelsCall = ApiService.getService(LoginActivity.this)
                                .create(ApiInterface.class).getaUsersFollowingListBYChannel
                                        (AppUtils.getJSonOBJForFollowingListReg
                                                (Objects.requireNonNull(AppUtils.getAppUserId(LoginActivity.this)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(LoginActivity.this).getUserId()),
                                                        AppUtils.getRegisteredUserId(LoginActivity.this).getChannel().get(0).getChId()));

                    channelsCall.enqueue(new Callback<ChannelDetailResponse>() {
                    @Override
                    public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {

                        if (response.isSuccessful() && response.body().getUSERDetailResponse() != null)
                            AppUtils.saveFavUser(getApplicationContext(),response.body().getUSERDetailResponse().getResultList());
                    }

                    @Override
                    public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

                    }
                });
                }
            }
            }
        };threadForchannel.start();

        Thread threadFOrLikes =new Thread(){
            @Override
            public void run() {
                super.run();
                Call<ResponseVideoList> responseVideoListCall;
                if(AppUtils.getRegisteredUserId(getApplicationContext())!=null){
                    responseVideoListCall=
                            ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                                    .getLikedVideosListBYChannel(AppUtils.getJSonOBJForLikedVideoListReg( Objects.requireNonNull(AppUtils.getAppUserId(LoginActivity.this)).getGuestUserId() ,AppUtils.getRegisteredUserId(getApplicationContext()).getUserId(),
                                            AppUtils.getRegisteredUserId(getApplicationContext())
                                                    .getChannel().get(0).getChId()));
                    responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                            if (response.isSuccessful() && response.body().getResonse() != null) {
                                for (VIdeoItem v : response.body().getResonse().getResult()) {
                                    List<String> likedVideoList = new ArrayList<>();
                                    likedVideoList.add(v.getVideoId());
                                    AppUtils.saveLikedVideo(Objects.requireNonNull(getApplicationContext()), likedVideoList);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseVideoList> call, Throwable t) {

                        }
                    });
                }
            }
        };
        threadFOrLikes.start();

    }

}
