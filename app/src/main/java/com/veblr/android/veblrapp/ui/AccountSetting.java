package com.veblr.android.veblrapp.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseUser;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.Constants;
import com.veblr.android.veblrapp.util.RequestPermissionHandler;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.veblr.android.veblrapp.util.Constants.GALLEY_OPEN_REQUEST_CODE;

public class AccountSetting extends AppCompatActivity {

    User user;
    TextView emailid,mobileNo,name,dob,gender,newsletter;
    EditText userName,date;
    CircularImageView userimage;
    ImageView editEmail,editMobileNo,editDetails,editUserImage,verifiedEmail,verifiedMonNo;
    LinearLayout showdetailLL,editdetailLL;
    RadioGroup radioGroupGender,radioGroupNL;
    boolean eligibleForUpdate=true;
    String day,month,year;
    Spinner spDay,spMonth,spYear;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_summery);
        showdetailLL = (LinearLayout) findViewById(R.id.llShowDetails);
        editdetailLL = (LinearLayout) findViewById(R.id.llEdit);

        emailid = (TextView) findViewById(R.id.tvEmailId);
        mobileNo = (TextView) findViewById(R.id.mobno);
        name = (TextView) findViewById(R.id.tvUserName);
        dob = (TextView) findViewById(R.id.tvDOB);
        gender = (TextView) findViewById(R.id.tvGen);
        newsletter = (TextView) findViewById(R.id.tvNLSub);
        radioGroupGender = (RadioGroup) findViewById(R.id.rggender);
        radioGroupNL = (RadioGroup) findViewById(R.id.rgNL);
        spDay=(Spinner)findViewById(R.id.spday);
        spMonth=(Spinner)findViewById(R.id.spMonth);
        spYear=(Spinner)findViewById(R.id.spYear);

        userimage = (CircularImageView) findViewById(R.id.ivChannelIcon);
        editUserImage = (ImageView) findViewById(R.id.etprofileImage);
        editEmail = (ImageView) findViewById(R.id.etEmail);
        editMobileNo = (ImageView) findViewById(R.id.etMobile);
        editDetails = (ImageView) findViewById(R.id.etEditDeatail);
        verifiedEmail = (ImageView) findViewById(R.id.ivVerifiedEmail);
        verifiedMonNo = (ImageView) findViewById(R.id.ivVerifiedMOBNO);

        userName = (EditText) findViewById(R.id.etUserName);
        date = (EditText) findViewById(R.id.etDOB);
        user =AppUtils.getRegisteredUserId(AccountSetting.this);
        setuserData();

            Call<ResponseUser> responseVideoListCall =
                    ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                            .getUserDetailsAfterLogIn(AppUtils.getJSonOBJForUserDetails
                                    (AppUtils.getUserId(AccountSetting.this)));
            responseVideoListCall.enqueue(new Callback<ResponseUser>() {

                @Override
                public void onResponse(Call<ResponseUser> call, retrofit2.Response<ResponseUser> response) {

                    if(response.isSuccessful()){
                        user =   response.body().getResponse().getResult();
                        AppUtils.saveRegisteredUserId(AccountSetting.this,user);
                    //    startActivity(new Intent(AccountSetting.this,UserProfileActivity.class));
                        setuserData();
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {
                    user =AppUtils.getRegisteredUserId(AccountSetting.this);

                }
            });
        ((ImageView)findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSetting.super.onBackPressed();
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdetailLL.setVisibility(View.GONE);
                editdetailLL.setVisibility(View.VISIBLE);
            }
        });
        ((Button) findViewById(R.id.btnUpdateAccount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.error)).setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
                    if(userName.getText().toString()==null || 3>userName.getText().toString().length()
                            || userName.getText().toString().length()>=50){
                      ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                      ((TextView)findViewById(R.id.error)).setText("* Name should contain 4 to 50 character");
                        eligibleForUpdate=false;
                    }
                    if(date.getText().toString()==null){
                        ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.error)).setText("* Date of birth should not be blank");
                        eligibleForUpdate=false;
                    }
                String gen="male";
                if( radioGroupGender.getCheckedRadioButtonId()==R.id.rbFemale)
                    gen =  "female";
                else gen= "male";
                String sub ="yes";
                if( radioGroupNL.getCheckedRadioButtonId()==R.id.rbSub)
                    sub =  "yes";
                else sub= "no";
                if(((Spinner)findViewById(R.id.spday)).getSelectedItemPosition()>0)
                    day = ((Spinner)findViewById(R.id.spday)).getSelectedItem().toString();
                else{
                    ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.error)).setText("* Please, Choose a day");
                    eligibleForUpdate=false;
                }
                if(((Spinner)findViewById(R.id.spMonth)).getSelectedItemPosition()>0)
                    month = (getResources().getTextArray(R.array.day_array))[ spMonth.getSelectedItemPosition()].toString();
                else{
                    ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.error)).setText("*  Please, Choose a month");
                    eligibleForUpdate=false;
                }
                if(((Spinner)findViewById(R.id.spYear)).getSelectedItemPosition()>0)
                    year = ((Spinner)findViewById(R.id.spYear)).getSelectedItem().toString();
                else{
                    ((TextView)findViewById(R.id.error)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.error)).setText("*  Please, Choose a Year");
                    eligibleForUpdate=false;
                }
            if(eligibleForUpdate){
                Call<Response> responseVideoListCall;
                responseVideoListCall = ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                        .updateUserAccountDetails(AppUtils.getJSonOBJForUpdatingUserAccountDetails
                                (AppUtils.getUserId(AccountSetting.this),user.getUserId(),
                                        userName.getText().toString(),day,month,year,gen,sub));
                responseVideoListCall.enqueue(new Callback<Response>(){

                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if(response.body().getResonse().getStatus().equals("200")&& response.isSuccessful()){
                              Toast toast =  Toast.makeText(getApplicationContext(),"Update is successful",Toast.LENGTH_SHORT)
                                       ;
                                TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                                toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                                toast.show();
                                ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);
                                showdetailLL.setVisibility(View.VISIBLE);
                                editdetailLL.setVisibility(View.GONE);
                                finish();
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);

                            }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);

                    }
                });

                }
            else{
                ((FrameLayout)findViewById(R.id.progress)).setVisibility(View.GONE);

            }
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetting.this);
                builder.setTitle("Change emailid");
                builder.setMessage("To change your email id, Kindly mail us at support@veblr.com \nwith your registered email id ");
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        editMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetting.this);
                builder.setTitle("Change Mobile Number");
                builder.setMessage("To change your mobile number, Kindly mail us at support@veblr.com \nwith your registered mobile number or Click Here");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        editUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtils.hasPermission(AccountSetting.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    // Sets the type as image/*. This ensures only components of type image are selected
                    intent.setType("image/*");
                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    // Launching the Intent
                    startActivityForResult(intent, GALLEY_OPEN_REQUEST_CODE);
                } else {
                    new RequestPermissionHandler().requestPermission(AccountSetting.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10,
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
    }

    private void setuserData() {

        if(user!=null){

            emailid.setText(user.getEmailId());
            mobileNo.setText(user.getMobileNo());
            name.setText(user.getName());
            dob.setText(user.getDob());
            gender.setText(user.getGender());
            newsletter.setText(user.getNewsletter());
            Glide.with(AccountSetting.this).load(user.getImage()).into(userimage);

            if (user.getMobileVerify().equals("Verified")) {
                verifiedMonNo.setImageDrawable(getResources().getDrawable(R.drawable.ic_channel_verify_icon));
            } else
                verifiedMonNo.setImageDrawable(getResources().getDrawable(R.drawable.ic_channel_unverified_icon));

            if (user.getEmailVerify().equals("Verified")) {
                verifiedEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_channel_verify_icon));
            } else
                verifiedEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_channel_unverified_icon));

            if (user.getGender().equals("male"))
                radioGroupGender.check(radioGroupGender.getChildAt(0).getId());
             else  radioGroupGender.check(radioGroupGender.getChildAt(1).getId());


            if (user.getNewsletter().equals("Subscribed")) {
                radioGroupNL.check(radioGroupNL.getChildAt(0).getId());

            } else radioGroupNL.check(radioGroupNL.getChildAt(1).getId());

            userName.setText(user.getName());
            date.setText(user.getDob());
        }

        spDay.setAdapter(new ArrayAdapter(AccountSetting.this,
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.day_array)));
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1920; i <= thisYear-17; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years);
       ;
      spYear.setAdapter(adapter);

        if(user.getDob()!=null) {

         String[] dob = user.getDob().split("-");
         if(dob[2]!=null)
            ((Spinner) findViewById(R.id.spday)).setSelection
                    (((ArrayAdapter)spDay.getAdapter()).getPosition(dob[2]));
        if(dob[1]!=null)
            ((Spinner) findViewById(R.id.spMonth)).setSelection(((ArrayAdapter)spMonth.getAdapter()).getPosition(Integer.parseInt(dob[1]+1)));
        if(dob[0]!=null)
            ((Spinner) findViewById(R.id.spYear)).setSelection(adapter.getPosition(dob[0]));

    }

        // new SaveYearstask().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLEY_OPEN_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    Log.w("path", picturePath+"");
                    // Set the Image in ImageView after decoding the String
                    userimage.setImageBitmap(thumbnail);
                    Call<Response> responseVideoListCall = ApiService.getService(AccountSetting.this)
                            .create(ApiInterface.class)
                            .updateUserImageDetails(AppUtils.getJSonOBJForUpdatingUserImage(AppUtils.getAppUserId(AccountSetting.this).getGuestUserId(),
                                    AppUtils.getRegisteredUserId(AccountSetting.this).getUserId(),getImage(thumbnail)));
                    responseVideoListCall.enqueue(new Callback<Response>(){
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
               //  getImage(thumbnail);

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

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


    private class SaveYearstask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            final ArrayList<String>  years = new ArrayList<>();
            years.add(0,"Year");
            int y = Calendar.getInstance().get(Calendar.YEAR);
            int lim =y-18;
            for(int i=lim;i<lim+80;i--){
                years.add(i+"");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((Spinner)findViewById(R.id.spYear)).setAdapter(new ArrayAdapter(AccountSetting.this,
                            android.R.layout.simple_spinner_item,
                            years));
                }
            });

            return null;
        }
    }
}
