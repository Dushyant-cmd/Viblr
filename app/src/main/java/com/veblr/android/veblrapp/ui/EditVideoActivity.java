package com.veblr.android.veblrapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseTag;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.Response;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.Constants;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class EditVideoActivity  extends AppCompatActivity {
    VIdeoItem videoItem;
    EditText title,desc;
    Spinner category,language;
    Switch adult,monetize;
    Button updateVideo,updateTags;
    AppCompatMultiAutoCompleteTextView autoCompleteTextView;
    String adultstatus="no";
    String monetizestatus="yes";
    Channel channel;
    String[] langList;
    String[] categoryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_edit);
        if (getIntent().getSerializableExtra("video") != null)
            videoItem = (VIdeoItem) getIntent().getSerializableExtra("video");
        if (getIntent().getSerializableExtra("channel") != null)
            channel = (Channel) getIntent().getSerializableExtra("channel");

        autoCompleteTextView = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.tagSearchView);
        ( (ImageView)findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditVideoActivity.super.onBackPressed();
            }
        });

        langList =getApplicationContext().getResources().getStringArray(R.array.languageId_array);
        categoryList =getApplicationContext().getResources().getStringArray(R.array.category_id_array);

        title=(EditText)findViewById(R.id.etTitle);
        desc=(EditText)findViewById(R.id.etDescription);

        category = (Spinner) findViewById(R.id.spinnerCategory);
        language = (Spinner) findViewById(R.id.spinnerLanguage);

        adult = (Switch)findViewById(R.id.swAdult);
        monetize = (Switch)findViewById(R.id.swMonetize);
        updateVideo=(Button)findViewById(R.id.btnUpdateVideo);
        updateTags=(Button)findViewById(R.id.btnUpdateTag);


        title.setText(videoItem.getVideoTitle());
        desc.setText(videoItem.getVideoDescription());
        monetize.setChecked(true);
        int position=0;
        for (int i=0;i<categoryList.length;i++){
            if(Arrays.asList(categoryList).contains(videoItem.getVideoCategory())){
                position=i;
                break;
            }
        }
        category.setSelection(position);

        for (int i=0;i<langList.length;i++){
            if(Arrays.asList(langList).contains(videoItem.getVideoLanguageShort())){
                position=i;
                break;
            }
        }
        language.setSelection(position);

        for(String s:videoItem.getVideoKeywords())
        autoCompleteTextView.setText(s+",");
        autoCompleteTextView.setPadding(15,15,15,15);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        MultiAutoCompleteTextView.CommaTokenizer cm = new MultiAutoCompleteTextView.CommaTokenizer(
        ){
            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                Call<ResponseTag> responseCommentCall =
                        ApiService.getService(EditVideoActivity.this).create(ApiInterface.class)
                                .getTagList(AppUtils.getJSonOBJForTAgList(text.toString()));
                responseCommentCall.enqueue(new Callback<ResponseTag>() {
                    @Override
                    public void onResponse(Call<ResponseTag> call, retrofit2.Response<ResponseTag> response) {
                        List<String> s=  response.body().getResponse().getResult();
                        if(response.isSuccessful() && s!=null){
                            ArrayAdapter<String> stringadapter = new ArrayAdapter<String>
                                    (EditVideoActivity.this,android.R.layout.simple_list_item_1,
                                            response.body().getResponse().getResult());

                            autoCompleteTextView.setAdapter(stringadapter);}
                    }
                    @Override
                    public void onFailure(Call<ResponseTag> call, Throwable t) {

                    }

                });
                return super.findTokenStart(text, cursor);

            }
        };
        autoCompleteTextView.setTokenizer(cm);
        autoCompleteTextView.getListSelection();
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                Call<ResponseTag> responseCommentCall =
//                        ApiService.getService(EditVideoActivity.this).create(ApiInterface.class)
//                                .getTagList(AppUtils.getJSonOBJForTAgList(s.toString()));
//                responseCommentCall.enqueue(new Callback<ResponseTag>() {
//                    @Override
//                    public void onResponse(Call<ResponseTag> call, retrofit2.Response<ResponseTag> response) {
//                        List<String> s=  response.body().getResponse().getResult();
//                        if(response.isSuccessful() && s!=null){
//                            ArrayAdapter<String> stringadapter = new ArrayAdapter<String>
//                                    (EditVideoActivity.this,android.R.layout.simple_list_item_1,
//                                            response.body().getResponse().getResult());
//
//                            autoCompleteTextView.setAdapter(stringadapter);}
//                    }
//                    @Override
//                    public void onFailure(Call<ResponseTag> call, Throwable t) {
//
//                    }
//                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) adultstatus ="yes";
                else adultstatus= "no";
            }
        });

        monetize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) monetizestatus ="yes";
                else monetizestatus= "no";
            }
        });


        updateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Response> videodetailsentResponse =ApiService.getService(EditVideoActivity.this)
                        .create(ApiInterface.class).sendEditMyAccontVideoDetails
                                (AppUtils.getJsonObjforupdateVideo(
                                AppUtils.getUserId(EditVideoActivity.this),
                                        AppUtils.getRegisteredUserId(EditVideoActivity.this).getUserId(),
                                        channel.getChId()
                                ,videoItem.getVideoId(),title.getText().toString(),desc.getText().toString(),
                                categoryList[category.getSelectedItemPosition()],
                                langList[language.getSelectedItemPosition()],
                                adultstatus,monetizestatus
                        ));


                videodetailsentResponse.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                        if(response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){
                            //show message on sucessful edit video
                            response.body().getResonse().getResult();
                            Intent myIntent = new Intent(
                                    EditVideoActivity.this,
                                    UserProfileActivity.class);
                            startActivityForResult(myIntent, Constants.ACTIVITY_EDIT_REQUEST);
                        }

                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });
            }
        });
        updateTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] s=new String[]{};
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    s = autoCompleteTextView.getAutofillHints();
                }
                else {
                    String str=  autoCompleteTextView.getText().toString();
                    s=  str.split(",");
                }
              Call<Response > tagResponse = ApiService.getService().sendEditMyAccountVideoTagDetails
                      (AppUtils.getJsonobjForTagUpdate(AppUtils.getRegisteredUserId(EditVideoActivity.this)
                                      .getChannel().get(0).getChId(),
                              AppUtils.getRegisteredUserId(EditVideoActivity.this).getUserId(),
                              videoItem.getVideoId(),s));

                tagResponse.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if(response.isSuccessful() && response.body().getResonse().getStatus().equals("200")){

                        }
                    }
                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });
            }
        });

    }
}
