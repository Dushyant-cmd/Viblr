package com.veblr.android.veblrapp.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseVideoUpload;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.Country;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.RequestPermissionHandler;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.veblr.android.veblrapp.util.Constants.GALLEY_OPEN_REQUEST_CODE;

public class ChannelSetting extends AppCompatActivity {

    Channel channel;
    TextView tvChannelName,tvAbout,tvWeb,tvFB,tvTweetter,tvYT,tvDailymotion,tvBlogger,tvPinterest,
            tvCategory,tvLang,tvCountry;
    EditText etChannelName,etAbout,etWeb,etFB,etTweetter,etYT,etDailymotion,etBlogger,etPintrest;
    Spinner catagory,language,country;
    ImageView ivChannelIcon,editivChannelIcon,ivEditDetail;
    boolean eligibleForUpdate=true;
    LinearLayout llDetails,llEditDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_settings);
        if (getIntent().getSerializableExtra("channel") != null)
            channel = (Channel) getIntent().getSerializableExtra("channel");

        llEditDetails = (LinearLayout)findViewById(R.id.llEditDetails) ;
        llDetails = (LinearLayout)findViewById(R.id.llDetails) ;


        ivEditDetail = (ImageView)findViewById(R.id.etEditDetail) ;
        ivChannelIcon = (ImageView)findViewById(R.id.ivChannelIcon);
        editivChannelIcon = (ImageView)findViewById(R.id.etprofileImage);
        tvChannelName = (TextView)findViewById(R.id.tvChannelName);
        tvAbout = (TextView)findViewById(R.id.tvAbout);
        tvWeb = (TextView)findViewById(R.id.tvWeb);
        tvFB = (TextView)findViewById(R.id.tvFB);
        tvTweetter = (TextView)findViewById(R.id.tvTweetter);
        tvYT = (TextView)findViewById(R.id.tvYT);
        tvDailymotion = (TextView)findViewById(R.id.tvDailymotion);
        tvBlogger = (TextView)findViewById(R.id.tvBlogger);
        tvPinterest = (TextView)findViewById(R.id.tvPinterest);
        tvCategory = (TextView)findViewById(R.id.tvCategory);
        tvLang = (TextView)findViewById(R.id.tvLang);
        tvCountry = (TextView)findViewById(R.id.tvCountry);

        etChannelName = (EditText)findViewById(R.id.etChannelName);
        etAbout = (EditText)findViewById(R.id.etAbout);
        etWeb = (EditText)findViewById(R.id.etWeb);
        etFB = (EditText)findViewById(R.id.etFB);
        etTweetter = (EditText)findViewById(R.id.etTweetter);
        etYT = (EditText)findViewById(R.id.etYT);
        etDailymotion = (EditText)findViewById(R.id.etDailymotion);
        etBlogger = (EditText)findViewById(R.id.etBlogger);
        etPintrest = (EditText)findViewById(R.id.etPinterest);
        catagory =(Spinner)findViewById(R.id.etCategory);
        country =(Spinner)findViewById(R.id.spinnerCountry);
        language =(Spinner)findViewById(R.id.spinnerLanguage);

        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("channel_name", channel.getChName());
        obj.add("param",payerReg);

        Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call =  ApiService.getService(ChannelSetting.this)
                .create(ApiInterface.class).getUserDetails(obj);
        call.enqueue(new Callback<ChannelDetailResponse>() {

                         @Override
                         public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                             if(response.isSuccessful()){
                                 try{
                                     List<Channel> channels =  response.body().getUSERDetailResponse().getResultList();
                                      channel =channels.get(0);

                                     Glide.with(getApplicationContext()).load(channel.getChImage()).into(ivChannelIcon);
                                     tvChannelName.setText(channel.getChNameDisp());
                                     tvAbout.setText(channel.getChAbout());
                                     tvWeb.setText(channel.getWebsite());
                                     tvFB.setText(channel.getFacebook());
                                     tvTweetter.setText(channel.getTwitter());
                                     tvYT.setText(channel.getYoutube());
                                     tvDailymotion.setText(channel.getDailymotion());
                                     tvBlogger.setText(channel.getBlogger());
                                     tvPinterest.setText(channel.getPinterest());
                                     tvCategory.setText(channel.getChCategoryName());
                                     tvLang.setText(channel.getChLanguageName());
                                     tvCountry.setText(channel.getChCountryName());



                                     etChannelName.setText(channel.getChNameDisp());
                                     etAbout.setText(channel.getChAbout());
                                     etWeb.setText(channel.getWebsite());
                                     etFB.setText(channel.getFacebook());
                                     etTweetter.setText(channel.getTwitter());
                                     etYT.setText(channel.getYoutube());
                                     etDailymotion.setText(channel.getDailymotion());
                                     etBlogger.setText(channel.getBlogger());
                                     etPintrest.setText(channel.getPinterest());

                                 }
                             catch (NullPointerException e){

                             }
                             }
                         }

                         @Override
                         public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

                         }
                     });



        Glide.with(getApplicationContext()).load(channel.getChImage()).into(ivChannelIcon);
        tvChannelName.setText(channel.getChNameDisp());
        tvAbout.setText(channel.getChAbout());
        tvWeb.setText(channel.getWebsite());
        tvFB.setText(channel.getFacebook());
        tvTweetter.setText(channel.getTwitter());
        tvYT.setText(channel.getYoutube());
        tvDailymotion.setText(channel.getDailymotion());
        tvBlogger.setText(channel.getBlogger());
        tvPinterest.setText(channel.getPinterest());
        tvCategory.setText(channel.getChCategoryName());
        tvLang.setText(channel.getChLanguageName());
        tvCountry.setText(channel.getChCountryName());



        etChannelName.setText(channel.getChNameDisp());
        etAbout.setText(channel.getChAbout());
        etWeb.setText(channel.getWebsite());
        etFB.setText(channel.getFacebook());
        etTweetter.setText(channel.getTwitter());
        etYT.setText(channel.getYoutube());
        etDailymotion.setText(channel.getDailymotion());
        etBlogger.setText(channel.getBlogger());
        etPintrest.setText(channel.getPinterest());

        List<Country> countries= AppUtils.getCountriesList(ChannelSetting.this);
        List<String> countrynameList = new ArrayList<>();

        for (Country c:countries) {
            countrynameList.add(c.getCountryName());
        }
        ArrayAdapter<CharSequence> adapter =new ArrayAdapter(ChannelSetting.this
                , android.R.layout.simple_spinner_item, countrynameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);

        editivChannelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AppUtils.hasPermission(ChannelSetting.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    // Sets the type as image/*. This ensures only components of type image are selected
                    intent.setType("image/*");
                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    // Launching the Intent
                    startActivityForResult(intent,GALLEY_OPEN_REQUEST_CODE);
                }
                else{
                    new RequestPermissionHandler().requestPermission(ChannelSetting.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10,
                            new RequestPermissionHandler.RequestPermissionListener() {
                                @Override
                                public void onSuccess() {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    // Sets the type as image/*. This ensures only components of type image are selected
                                    intent.setType("image/*");
                                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                                    String[] mimeTypes = {"image/jpeg", "image/png"};
                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                    // Launching the Intent
                                    startActivityForResult(intent, GALLEY_OPEN_REQUEST_CODE);
                                }

                                @Override
                                public void onFailed() {

                                }
                            });
                }

            }
        });

        ivEditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDetails.setVisibility(View.GONE);
                llEditDetails.setVisibility(View.VISIBLE);

        }});
        ((Button)findViewById(R.id.btnUpdateProfile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.VISIBLE);

                if(etChannelName.getText().toString().isEmpty()){
                    ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.error)).setText("* Name should contain 4 to 50 characters");
                    eligibleForUpdate=false;
                    ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);

                }

               if(eligibleForUpdate){

                String website=   etWeb.getText().toString().replaceAll("https://","");
                String countrycode = Objects.requireNonNull(AppUtils.getCountriesList(getApplicationContext()))
                        .get(country.getSelectedItemPosition()).getCountryId();

                   Call<ResponseVideoUpload> responseVideoListCall;
                   responseVideoListCall = ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                           .updateChannelDetails(AppUtils.getJsonObjForChannelDetail
                                   (AppUtils.getRegisteredUserId(getApplicationContext()).getUserId(),
                                   channel.getChId(),
                                   etChannelName.getText().toString(),etAbout.getText().toString(),website,etFB.getText().toString(),
                                   etTweetter.getText().toString(),etYT.getText().toString(),etDailymotion.getText().toString(),
                                   etBlogger.getText().toString(),etPintrest.getText().toString(),
                                   getResources().getStringArray(R.array.category_id_array)[catagory.getSelectedItemPosition()],
                                   getResources().getStringArray(R.array.languageId_array)[language.getSelectedItemPosition()],
                                   countrycode));
                    responseVideoListCall.enqueue(new Callback<ResponseVideoUpload>() {
                        @Override
                        public void onResponse(Call<ResponseVideoUpload> call, Response<ResponseVideoUpload> response) {

                            ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);

                            if(response.isSuccessful() ){
                                if(response.body().getResponse().getResult().isEmpty())
                                Toast.makeText(getApplicationContext(),"Update is successful",Toast.LENGTH_SHORT).show();
                              else{Toast.makeText(getApplicationContext(),response.body().getResponse().getResult(),Toast.LENGTH_SHORT).show();}
                                if (android.os.Build.VERSION.SDK_INT >= 16) {
                                    //Code for recreate
                                    recreate();
                                    llDetails.setVisibility(View.VISIBLE);
                                    llEditDetails.setVisibility(View.GONE);
                                } else {
                                    //Code for Intent
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseVideoUpload> call, Throwable t) {
                            ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Update is not successful due to some error",Toast.LENGTH_SHORT).show();
                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                //Code for recreate
                                recreate();
                                llDetails.setVisibility(View.VISIBLE);
                                llEditDetails.setVisibility(View.GONE);
                            } else {
                                //Code for Intent
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    });

               }
               else{  ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);
               }


            }
        });
        ((ImageView)findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelSetting.super.onBackPressed();
            }
        });

    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLEY_OPEN_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                            null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    Bitmap thumbnail = BitmapFactory.decodeFile(imgDecodableString);
                    ivChannelIcon.setImageBitmap(thumbnail);
                    Call<com.veblr.android.veblrapp.model.Response> responseVideoListCall = ApiService.getService(
                            ChannelSetting.this)
                            .create(ApiInterface.class).updateUserImageChannel(AppUtils.getJSonObjForChannelimage(AppUtils.getRegisteredUserId(getApplicationContext()).getUserId(),
                                    channel.getChId(),getImage(thumbnail)));
                    responseVideoListCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                        @Override
                        public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                          /*  if(response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){

                            }*/
                        }

                        @Override
                        public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                        }
                    });
                    break;
            }
        }
    }
    private String getImage(Bitmap selectedImage) {
        String base64="";
        try{

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64 = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("img2text",base64);
            //          System.out.println(base64);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "data:image/jpeg;base64,"+base64;    }

}
