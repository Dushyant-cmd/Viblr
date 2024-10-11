package com.veblr.android.veblrapp.ui;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.provider.Browser;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.veblr.android.veblrapp.BrowserActivity;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.Country;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;

import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacpp.avformat;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.VISIBLE;
import static com.veblr.android.veblrapp.util.Constants.EMAIL_NO_VERIFIED;
import static com.veblr.android.veblrapp.util.Constants.MOB_NO_VERIFIED;
import static com.veblr.android.veblrapp.util.Constants.PREFS_NAME;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ScrollView scrollView  ;
    FrameLayout pb;
    ImageView done;
    GoogleApiClient mGoogleApiClient;
    String accountName="";
    PopupWindow popupWindow;
    List<Country> countryList;
    String countrycode;
    Spinner spin;
    User user;
    EditText mobNo,etName,etPassword,etRePassword;
    Dialog dialog;
    Button btnRegister;
    boolean enableRegister=true;
    TextView error;
    RadioGroup gender;
    CheckBox terms;
    Spinner spDay,spMonth,spYear;
    boolean checkUserNumberVerified=false;
      String tempuserforverification="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1008:
                if (resultCode == RESULT_OK) {
                    Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                    Log.e("cred.getId", cred.getId());
                    String userMob = cred.getId();

                  /*  if(AppUtils.getCountriesList(RegisterActivity.this)!=null){
                        countryList =AppUtils.getCountriesList(RegisterActivity.this);
                        for (Country c:countryList) {
                            String s= StringUtils.substring(userMob, 0, 3);
                            String s2=StringUtils.substring(userMob, 0, 4).replace("+","");

                           if(c.getCountryCode().equals(s)){
                               countrycode = c.getCountryCode();
                               userMob.replaceAll("+"+s,"");
                               break;
                           }

                           else if(c.getCountryCode().equals(s2))
                           {
                               countrycode = c.getCountryCode();
                               userMob.replaceAll(s2,"");
                               break;
                           }
                        }
                    }*/
                 /*   if(countrycode!=null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+914071011900"));
                        startActivity(intent);
                        startTimerForVerification(userMob, countrycode, accountName);

                    }
                    else{*/
                        LayoutInflater inflater = (LayoutInflater)  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                         dialog = new Dialog(RegisterActivity.this);
                        dialog.setContentView(R.layout.forgot_password);
                        dialog.setCancelable(false);
                        View view = inflater.inflate(R.layout.forgot_password, null);
                        List<Country> countries= AppUtils.getCountriesList(RegisterActivity.this);
                        List<String> countrynameList =new ArrayList<>();

                        for (Country c:countries) {
                            countrynameList.add(c.getCountryName());
                        }
                        ArrayAdapter<CharSequence> adapter =new ArrayAdapter(RegisterActivity.this
                                , android.R.layout.simple_spinner_item, countrynameList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin = (Spinner) dialog.findViewById(R.id.spinnerFPW);
                        spin.setAdapter(adapter);
                        spin.setOnItemSelectedListener(RegisterActivity.this);
                        try {
                            // phone must begin with '+'
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(userMob, "");
                            int countryCode = numberProto.getCountryCode();
                            long nationalNumber = numberProto.getNationalNumber();
                            ((EditText) dialog.findViewById(R.id.etPhoneFPW)).setText(nationalNumber+"");

                            Log.i("code", "code " + countryCode);
                            Log.i("code", "national number " + nationalNumber);

                            AppUtils.setTempMobNumber(String.valueOf(nationalNumber),RegisterActivity.this);
                            AppUtils.setTempEmailId(accountName,RegisterActivity.this);
                            AppUtils.setTempCountryCode(countryCode,RegisterActivity.this);
                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }
                        mobNo = (EditText) dialog.findViewById(R.id.etPhoneFPW);
                        TextView tvEmail = (TextView) dialog.findViewById(R.id.etEmailFPW);
                        tvEmail.setText(accountName);

                        //  mobNo.setText(userMob);
                        dialog.setCanceledOnTouchOutside(false);
                       // dialog.show();
                   // }
                } else {
                    // Sim Card not found!
                    LayoutInflater inflater = (LayoutInflater)  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    dialog = new Dialog(RegisterActivity.this);
                    dialog.setContentView(R.layout.forgot_password);
                    dialog.setCancelable(false);
                    List<Country> countries= AppUtils.getCountriesList(RegisterActivity.this);
                    List<String> countrynameList =new ArrayList<>();

                    for (Country c:countries) {
                        countrynameList.add(c.getCountryName());
                    }
                    ArrayAdapter<CharSequence> adapter =new ArrayAdapter(RegisterActivity.this
                            , android.R.layout.simple_spinner_item, countrynameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin = (Spinner) dialog.findViewById(R.id.spinnerFPW);
                    spin.setAdapter(adapter);
                    spin.setOnItemSelectedListener(RegisterActivity.this);
                    TextView tvEmail = (TextView) dialog.findViewById(R.id.etEmailFPW);
                    tvEmail.setText(accountName);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    return;
                }
                break;
            case 1009:
            if ( resultCode == RESULT_OK) {
                ( (FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(VISIBLE);
                  accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                ///String[] s =data.getStringArrayExtra(AccountManager.KEY_ACCOUNTS);
                Log.e("email id is",accountName);
                Call<Response> checkEmailexistResponse= ApiService.getService(getApplicationContext())
                        .create(ApiInterface.class).checkEmailIdExisted(AppUtils.getJSONOBJForMailIdExists(accountName));
                checkEmailexistResponse.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if(response.isSuccessful()){
                           if(! response.body().getResonse().getResult().getEmail_id_status().equals("Existed")){

                               mGoogleApiClient = new GoogleApiClient.Builder(RegisterActivity.this)
                                       .addConnectionCallbacks(RegisterActivity.this)
                                       .addOnConnectionFailedListener(RegisterActivity.this)
                                       .addApi(Auth.CREDENTIALS_API)
                                       .build();

                               if (mGoogleApiClient != null) {
                                   mGoogleApiClient.connect();
                               }
                               ( (FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(View.GONE);
                           }
                           else{
                               ( (FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(View.GONE);

                               AlertDialog.Builder builder = new AlertDialog.Builder(
                                       RegisterActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                               builder.setTitle("Email ID Exists");
                               builder.setMessage("Email id, you have selected already exists.");

                               builder.setPositiveButton("OK",
                                       new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog,
                                                               int which) {
                                               try {
                                                   Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                                           new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
                                                   startActivityForResult(intent, 1009);
                                               } catch (ActivityNotFoundException e) {
                                                   // TODO
                                                   Toast.makeText(getApplicationContext(),"Can't found a google account",Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       });

                               builder.setNegativeButton("Login",
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                           }});
                               builder.show();
                           }
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        ( (FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(View.GONE);

                    }
                });

            }
            else{  try {
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);

                startActivityForResult(intent, 1009);
            } catch (ActivityNotFoundException e) {
                // TODO

               Toast toast = Toast.makeText(getApplicationContext(),
                        "Can't found a google account",Toast.LENGTH_LONG);
                TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                toast.show();

            }
           }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startTimerForVerification(final String mobNo, final String countryCode, final String emailId) {
        popupWindow.getContentView().getRootView().setVisibility(VISIBLE);
        popupWindow.showAtLocation(findViewById(R.id.llRegister), Gravity.BOTTOM,0,0);
        new CountDownTimer(50000, 1000) {
            public void onTick(long millisUntilFinished) {
           //     mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);

                ((TextView)popupWindow.getContentView().getRootView()
                        .findViewById(R.id.timer)).setText("00:"+millisUntilFinished/1000);


             Call<Response> responseCall = ApiService.getService(RegisterActivity.this)
                     .create(ApiInterface.class).checktMobNoVerifiedDuringMissedCall
                             (AppUtils.getJsonObjForNoVerification(Integer.valueOf(countryCode),mobNo,emailId));

                 responseCall.enqueue(new Callback<Response> (){
                 @Override
                 public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    if(response.isSuccessful()&& response.body().getResonse().getStatus()=="200"){
                       String tempuser= response.body().getResonse().getResult().getEmail_id_status();
                        if(!tempuser.isEmpty()){
                            checkTempUser(tempuser);
                            tempuserforverification=tempuser;
                        }
                    }
                 }
                     @Override
                 public void onFailure(Call<Response> call, Throwable t) {

                 }
             });

            }

            public void onFinish() {
              //  mTextField.setText("done!");
                popupWindow.getContentView().getRootView().setVisibility(View.GONE);

             //   Toast.makeText(getApplicationContext(),"Mobile Number is verified",Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void checkTempUser(String tempUserID) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("temp_user_id",tempUserID);
        obj.add("param", payerReg);


        Call<Response> responseCall = ApiService.getService(RegisterActivity.this)
                .create(ApiInterface.class).checkTempUserIdIsVerifiedOrNot(obj);

        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
              if(  response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){
                  checkUserNumberVerified=true;
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          popupWindow.getContentView().getRootView().setVisibility(View.GONE);
                    Toast toast=     Toast.makeText(getApplicationContext(),"Mobile Number is verified",Toast.LENGTH_SHORT);
                          TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                          toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                          toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                          toast.show();

                      }
                  });
              }
              else{
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          popupWindow.getContentView().getRootView().setVisibility(View.GONE);
                         Toast toast = Toast.makeText(getApplicationContext(),
                                  "Mobile Number is not verified,Kindly try again",Toast.LENGTH_SHORT);
                          TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                          toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                          toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                          toast.show();
                      }
                  });
              }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.getContentView().getRootView().setVisibility(View.GONE);
                          Toast.makeText(getApplicationContext(),
                                  "Mobile Number is not verified,Kindly try again",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        etName=(EditText)findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText)findViewById(R.id.etRePassword);
        error =(TextView)findViewById(R.id.error);
        gender =(RadioGroup)findViewById(R.id.rggender);
        terms =(CheckBox)findViewById(R.id.cbConditions);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.register_actionabar, null);
            v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RegisterActivity.super.onBackPressed();

                }
            });
            ActionBar.LayoutParams p = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);

            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setText(R.string.title_activity_register);
             done = (ImageView) v.findViewById(R.id.btnDoneRegister);
             done.setVisibility(View.GONE);
            getSupportActionBar().setCustomView(v, p);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater2.inflate(R.layout.fragment_timer, null);
            popupWindow = new PopupWindow(customView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= 21) {
                popupWindow.setElevation(5.0f);
            }
            popupWindow.setFocusable(true);
        }

        pb =(FrameLayout)findViewById(R.id.frgSIgnin);
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1920; i <= thisYear-17; i++) {
            years.add(Integer.toString(i));
        }
        Spinner spinYear = (Spinner)findViewById(R.id.spYear);
        spinYear.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years));

        LayoutInflater inflater = (LayoutInflater)  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.forgot_password, null);
        spin = (Spinner) view.findViewById(R.id.spinnerFPW);
        List<Country> countries= AppUtils.getCountriesList(RegisterActivity.this);
        List<String> countrynameList = new ArrayList<>();

        for (Country c:countries) {
            countrynameList.add(c.getCountryName());
        }
        ArrayAdapter<CharSequence> adapter =new ArrayAdapter(RegisterActivity.this
              , android.R.layout.simple_list_item_1, countrynameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(RegisterActivity.this);


         /*
            ArrayList<String>  years = new ArrayList<>();
            years.add(0,"Year");
            int y = Calendar.getInstance().get(Calendar.YEAR);
            for(int i=1900;i<y-18;i--){
                years.add(i+"");
            }
            ((Spinner)findViewById(R.id.spYear)).setAdapter(new ArrayAdapter(RegisterActivity.this,
                    android.R.layout.simple_spinner_item,
                    years));


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this,ProfileActivity.class));
            }
        });
     /*   Spinner spin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,getResources().getStringArray( R.array.CountryArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(RegisterActivity.this);
*/
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);

            startActivityForResult(intent, 1009);
        } catch (ActivityNotFoundException e) {
            // TODO
            Toast toast =Toast.makeText(getApplicationContext(),"Can't found a google account",Toast.LENGTH_LONG);
            TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(getResources().getColor(android.R.color.black));
            toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            toast.show();
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

       Toast toast = Toast.makeText(getApplicationContext(), "Selected User: "+getResources().getStringArray
                ( R.array.CountryArray)[position] ,Toast.LENGTH_SHORT);
        TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(getResources().getColor(android.R.color.black));
        toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        toast.show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }


    public void registerButtonClicked(View view) {

        if(etName.getText().toString().isEmpty()){
            error.setVisibility(VISIBLE);
            error.setText("Username can't be left empty.");
            enableRegister=false;
        }
        if(etPassword.getText().toString().isEmpty()){
            error.setVisibility(VISIBLE);
            error.setText("Password can't be left empty.");
            enableRegister=false;

        }
        if(etRePassword.getText().toString().isEmpty()){
            error.setVisibility(VISIBLE);
            error.setText("ReType Password can't be left empty.");
            enableRegister=false;

        }
        String gen ="";
       if( gender.getCheckedRadioButtonId()==R.id.rbmale){
           gen="male";
       }
       else gen="female";

       if(!terms.isChecked()){
           error.setVisibility(VISIBLE);
           error.setText("Kindly ,agree to terms & conditions.");
           enableRegister=false;
       }
        if(checkUserNumberVerified){
            error.setVisibility(VISIBLE);
            error.setText("Please try again, your number is not verified");
            enableRegister=false;
        }
       if(enableRegister){

           Call<Response> call= ApiService.getService(getApplicationContext()).create(ApiInterface.class).registerVerifiedUser(
                   AppUtils.getVerifiedUserData(tempuserforverification,etName.getText().toString(),etPassword.getText().toString(),
                           etRePassword.getText().toString(),getResources().getStringArray(R.array.day_array)[spDay.getSelectedItemPosition()],
                           getResources().getStringArray(R.array.month_array)[spMonth.getSelectedItemPosition()],spYear.getSelectedItem().toString(),gen,true));
           call.enqueue(new Callback<Response>() {
               @Override
               public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                   if(response.isSuccessful()){

                   }
               }

               @Override
               public void onFailure(Call<Response> call, Throwable t) {

               }
           });

       }
    }

    public void onSignInBttonClicked(View view) {
       /* FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        scrollView.setVisibility(View.GONE);
        fragmentTransaction.replace(R.id.frgSIgnin,new LoginFragment()).commit();*/
       startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), 1008, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            Log.e("", "Could not start hint picker Intent", e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onTermsAndConditionClicked(View view) {

        Intent intent = new Intent (getApplicationContext(), BrowserActivity.class);
        intent.putExtra("browser_url","https://veblr.com/m/terms-of-use");
        startActivity(intent);

    }

    public void onSubmitPhoneNumberClosed(View view) {
       String  mobNoFromEditText =((EditText) dialog.findViewById(R.id.etPhoneFPW)).getText().toString();

       if( mobNoFromEditText.equals(""))
       {

         Toast toast =   Toast.makeText(getApplicationContext(),"Kindly,enter your number",Toast.LENGTH_LONG);
           TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
           toastMessage.setTextColor(getResources().getColor(android.R.color.black));
           toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
           toast.show();
       }
      else{
     ((FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(VISIBLE);
      dialog.dismiss();
      Country c =   Objects.requireNonNull(AppUtils.getCountriesList(RegisterActivity.this)).get(spin.getSelectedItemPosition());
      String countryC= c.getCountryCode();
      String email  = accountName;
      @SuppressLint("CutPasteId") String mob = ((EditText) dialog.findViewById(R.id.etPhoneFPW)).getText().toString();
      boolean getData=false;
      if(!countryC.isEmpty() && !email.isEmpty() && !mob.isEmpty()){
         getData=true;
      }
      if(getData){
          ( (FrameLayout)findViewById(R.id.frgSIgnin)).setVisibility(View.GONE);
          AppUtils.setTempMobNumber(mob,RegisterActivity.this);
          AppUtils.setTempEmailId(accountName,RegisterActivity.this);
          AppUtils.setTempCountryCode(Integer.parseInt(countryC),RegisterActivity.this);
          Intent intent = new Intent(Intent.ACTION_DIAL);
          intent.setData(Uri.parse("tel:+914071011900"));
          startActivity(intent);
          startTimerForVerification(mob, countryC, email);
        }
      }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
