package com.veblr.android.veblrapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.exoplayer2.C;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.work.WorkRequest;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.CommentListAdapter;
import com.veblr.android.veblrapp.datasource.ResponseComment;
import com.veblr.android.veblrapp.datasource.ResponseTag;
import com.veblr.android.veblrapp.datasource.ResponseVideoUpload;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.Comment;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.services.FileUploadNotification;
import com.veblr.android.veblrapp.ui.tags.TagsAdapter;
import com.veblr.android.veblrapp.ui.tags.TagsCompletionView;
import com.veblr.android.veblrapp.upload.UploadIntentService;
import com.veblr.android.veblrapp.util.AppUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import io.tus.android.client.TusAndroidUpload;
import io.tus.android.client.TusPreferencesURLStore;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusURLMemoryStore;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

    public  class UploadEditActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<String> {

        Uri videoUri;
        String videoUriString;
        TusClient tusClient;
        private UploadTask uploadTask;
        FileUploadNotification fileUploadNotification;

     //ui
     TagsCompletionView completionView;
     ArrayAdapter<String> adapter;
     String[] listOfTags;
     AppCompatMultiAutoCompleteTextView autoCompleteTextView;
     EditText etTitle;
     EditText etDetails;
     int duration=10;
     String uploadend;
     Spinner spinnerChooseUser;
      List<Channel>spinnerArray =new ArrayList<>();
        String[] langList;
        String[] categoryList;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
            langList =getApplicationContext().getResources().getStringArray(R.array.languageId_array);
            categoryList =getApplicationContext().getResources().getStringArray(R.array.category_id_array);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDetails = (EditText)findViewById(R.id.etDescription);
        spinnerChooseUser = (Spinner)findViewById(R.id.spinnerChooseUser);
        videoUriString = getIntent().getStringExtra("fileUri");
        videoUri = Uri.parse(videoUriString);

  /*    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Uri urivid = FileProvider.getUriForFile(UploadEditActivity.this,
                    "com.veblr.android.veblrapp.fileprovider",
                    new File(videoUriString));
      retriever.setDataSource(urivid.toString());
      duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
      retriever.release();*/
     duration =getIntent().getIntExtra("duration",0);
     duration = duration/1000;
      Log.d("video duration",getIntent().getIntExtra("duration",0)+"");
        User user = AppUtils.getRegisteredUserId(UploadEditActivity.this);
            List<String > spinnerStringArray =new ArrayList<>();
        if(user!=null){
        for(int i=0;i<user.getChannel().size();i++){
            spinnerArray.add(user.getChannel().get(i));
            spinnerStringArray.add(user.getChannel().get(i).getChNameDisp());
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerStringArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerChooseUser.setAdapter(spinnerArrayAdapter);

        tusClient = new TusClient();
        try {

            SharedPreferences pref = getSharedPreferences("tus", 0);
            tusClient.setUploadCreationURL(new URL("https://mobiupload.veblr.com/files"));
            tusClient.enableResuming(new TusPreferencesURLStore(pref));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 /*   if(3>start||start>150)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                        builder.setTitle("Title limit");
                        builder.setMessage("Video Title limit should be in between 3 to 150 Characters.");
                        builder.setIcon(android.R.drawable.stat_notify_error);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if(3>start||start>1500)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                    builder.setTitle("Description limit");
                    builder.setMessage("Video Description limit should be in between 3 to 1500 Characters.");
                    builder.setIcon(android.R.drawable.stat_notify_error);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //tag
            autoCompleteTextView = (AppCompatMultiAutoCompleteTextView)findViewById(R.id.tagSearchView);
            autoCompleteTextView.setPadding(15,15,15,15);
            autoCompleteTextView.setThreshold(1);
            autoCompleteTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            MultiAutoCompleteTextView.CommaTokenizer cm = new MultiAutoCompleteTextView.CommaTokenizer(
            ){
                @Override
                public int findTokenStart(CharSequence text, int cursor) {
                    return super.findTokenStart(text, cursor);

                }
            };
            autoCompleteTextView.setTokenizer(cm);
            autoCompleteTextView.getListSelection();
//        completionView = (TagsCompletionView)findViewById(R.id.searchView);
//        listOfTags = this.getResources().getStringArray(R.array.CountryArray);
//        adapter = new TagsAdapter(this, R.layout.tag_layout, listOfTags);
//        completionView.setAdapter(adapter);
//        completionView.setThreshold(3);
//        completionView.setTokenListener(this);
//        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
//        completionView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                //gettext to make tags
//              //  ((TextView)findViewById(R.id.textValue)).setText(editable.toString());
//            }
//        });



        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                        Call<ResponseTag> responseCommentCall =
                                ApiService.getService(UploadEditActivity.this).create(ApiInterface.class)
                                        .getTagList(AppUtils.getJSonOBJForTAgList(s.toString()));
                        responseCommentCall.enqueue(new Callback<ResponseTag>() {
                            @Override
                            public void onResponse(Call<ResponseTag> call, Response<ResponseTag> response) {
                                List<String> s=  response.body().getResponse().getResult();
                                if(response.isSuccessful() && s!=null){
                                ArrayAdapter<String> stringadapter = new ArrayAdapter<String>
                                        (UploadEditActivity.this,android.R.layout.simple_list_item_1,
                                                response.body().getResponse().getResult());

                                autoCompleteTextView.setAdapter(stringadapter);}
                            }
                            @Override
                            public void onFailure(Call<ResponseTag> call, Throwable t) {

                            }
                         });
                    }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ((Button)findViewById(R.id.btnSubmitUpload)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUploadable = true;
                if(etTitle.getText().toString().length()>150 || etTitle.getText().toString().length()<3)
                {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                        builder.setTitle("Title limit");
                        builder.setMessage("Video Title limit should be in between 3 to 150 Characters.");
                        builder.setIcon(android.R.drawable.stat_notify_error);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        isUploadable=false;

                }
                if(etDetails.getText().toString().length()>1500 || etTitle.getText().toString().length()<3)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                    builder.setTitle("Title limit");
                    builder.setMessage("Video Title limit should be in between 3 to 1500 Characters.");
                    builder.setIcon(android.R.drawable.stat_notify_error);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    isUploadable=false;
                }
                if(Integer.parseInt(String.valueOf(new File(videoUriString).length()/2048))>200){
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                    builder.setTitle("Video Size Error");
                    builder.setMessage("Video size should be less thn 200 mb");
                    builder.setIcon(android.R.drawable.stat_notify_error);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    isUploadable=false;
                }



                if(isUploadable){
                    ((FrameLayout)findViewById(R.id.pbupload)).setVisibility(View.VISIBLE);

                    beginUpload(videoUri);
                    ((Button)findViewById(R.id.btnSubmitUpload)).setClickable(false);
                    ((Button)findViewById(R.id.btnSubmitUpload)).setBackground
                            (getResources().getDrawable(R.drawable.unfollow_bg));
                }

            }
        });

        ImageView imageView= (ImageView) findViewById(R.id.ivThumbnail);
        Uri uri = Uri.parse(getIntent().getStringExtra("fileThumbNailUri"));
        Log.e("URIIMAGE",uri.toString()+"");
        assert uri!=null;
        imageView.setImageURI(uri);
       /* try {
            imagefileUri = FileProvider.getUriForFile(UploadEditActivity.this,
                    "com.veblr.android.veblrapp.fileprovider",
                    new File(String.valueOf(uri)));
            Log.e("Image FileUri is:",imagefileUri.toString() +"\n"+uri);
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: " + new File(String.valueOf(uri)));
        }*/


        Log.e("converted image",img2Text(uri.toString()));


    }

   /* public void uploadSubmitButtonClicked(View view) {
        beginUpload(videoUri);
    }*/
    public void beginUpload(Uri uri){

     //   UploadIntentService.enqueueWork(UploadEditActivity.this,UploadEditActivity.class,1000,);

        resumeUpload(uri);
    }

    private void resumeUpload(Uri uri) {
        try {
            TusUpload upload = new TusAndroidUpload(uri, this);
            uploadTask = new UploadTask(this, tusClient, upload);
            fileUploadNotification = new FileUploadNotification(UploadEditActivity.this);
            uploadTask.execute(new Void[0]);
        } catch (Exception e) {
            showError(e);
        }
    }

    private class UploadTask extends AsyncTask<Void, Long, URL> {
        private UploadEditActivity activity;
        private TusClient client;
        private TusUpload upload;
        private Exception exception;

        public UploadTask(UploadEditActivity activity, TusClient client, TusUpload upload) {
            this.activity = activity;
            this.client = client;
            this.upload = upload;
        }

        @Override
        protected void onPreExecute() {
           /* activity.setStatus("Upload selected...");
            activity.setPauseButtonEnabled(true);
            activity.setUploadProgress(0);*/

        }




        @Override
        protected void onPostExecute(final URL uploadURL) {
           /* activity.setStatus("Upload finished!\n" + uploadURL.toString());
            activity.setPauseButtonEnabled(false);*/
              runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    final ProgressBar pb = new ProgressBar(UploadEditActivity.this);
                    pb.setIndeterminate(true);
                    uploadend =uploadURL.getFile().replace("files/","");
                    Log.e("endpoint",uploadend);
                    String[] s=new String[]{};
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        s = autoCompleteTextView.getAutofillHints();
                    }
                    else {

                       String str=  autoCompleteTextView.getText().toString();
                       s=  str.split(",");
                    }

                    Call<ResponseVideoUpload> call = ApiService.getService(UploadEditActivity.this)
                            .create(ApiInterface.class).addNewVideoBYUser(AppUtils.getJSonOBJForaddingNewVideo(

                                    Objects.requireNonNull(AppUtils.getRegisteredUserId(UploadEditActivity.this)).getUserId(),
                                    spinnerArray.get(spinnerChooseUser.getSelectedItemPosition()).getChId()
                                    ,etTitle.getText().toString(),
                                    etDetails.getText().toString(),
                                    uploadend,duration+"",
                                    langList[((Spinner)findViewById(R.id.spinnerLanguage)).getSelectedItemPosition()],
                                    categoryList[((Spinner)findViewById(R.id.spinnerCategory)).getSelectedItemPosition()],
                                    img2Text(getIntent().getStringExtra("fileThumbNailUri")),
                                    s
                                    ));
                    call.enqueue(new Callback<ResponseVideoUpload>() {

                        @Override
                        public void onResponse(Call<ResponseVideoUpload> call, final Response<ResponseVideoUpload> response) {
                            pb.setIndeterminate(false);
                            pb.setVisibility(View.GONE);
                            if(response.isSuccessful()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                                    builder.setTitle("Upload Status");
                                    builder.setMessage(response.body().getResponse().getResult());
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    }});
                                ((FrameLayout)findViewById(R.id.pbupload)).setVisibility(View.GONE);

                                finish();
                            startActivity(new Intent(UploadEditActivity.this,UserProfileActivity.class));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseVideoUpload> call, Throwable t) {
                            pb.setIndeterminate(false);
                            pb.setVisibility(View.GONE);
                            ((FrameLayout)findViewById(R.id.pbupload)).setVisibility(View.GONE);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
                                    builder.setTitle("Upload status");
                                    builder.setMessage("Upload is failed due to  error");
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    }});
                        }
                    });
                }
            });
        }
        @Override
        protected void onCancelled() {
            if(exception != null) {
                ((FrameLayout)findViewById(R.id.pbupload)).setVisibility(View.GONE);

                activity.showError(exception);
            }

            //    activity.setPauseButtonEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Long... updates) {
            long uploadedBytes = updates[0];
            long totalBytes = updates[1];
            Log.e("Uploaded:", uploadedBytes+" "+ totalBytes);
            //  activity.setUploadProgress((int) ((double) uploadedBytes / totalBytes * 100));*/
            String progress =(int) ((double) uploadedBytes / totalBytes * 100)+"";
            String fileTitle = getIntent().getStringExtra("fileTitle");
            fileUploadNotification.updateNotification(
                     progress,fileTitle,
                     etTitle.getText().toString(),
                    UploadEditActivity.this);
        }

        @Override

        protected URL doInBackground(Void... params) {
            try {
               // client.enableResuming(new TusURLMemoryStore());
                TusUploader uploader = client.resumeOrCreateUpload(upload);
                long uploadedBytes = uploader.getOffset();
                long totalBytes = this.upload.getSize();
              //  uploader.setRequestPayloadSize(1024*1024);
                // Upload file in 1MB chunks
                uploader.setChunkSize(1024*1024);
                Log.e("OFFSET CLIENT",uploader.getOffset()+"");
                while(!isCancelled() && uploader.uploadChunk() > 0) {
                    uploadedBytes = uploader.getOffset();
                    publishProgress(uploadedBytes, totalBytes);
                }

                System.out.println("Upload finished.");
                System.out.format("Upload available at: %s", uploader.getUploadURL().toString());
                Log.e("UPLOAD URL",uploader.getUploadURL()+"");
                uploader.finish();

                return uploader.getUploadURL();

            } catch(Exception e) {
                exception = e;
                FileUploadNotification.failUploadNotification();
                cancel(true);

            }

            return null;
        }
    }
    private void showError(final Exception e) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadEditActivity.this);
        builder.setTitle("Internal error");
        builder.setMessage("There is some problem with the Server");
        AlertDialog dialog = builder.create();
        dialog.show();
        e.printStackTrace();}});
    }


    private void updateTokenConfirmation() {
        StringBuilder sb = new StringBuilder("Current tokens:\n");
        for (Object token: completionView.getObjects()) {
            sb.append(token.toString());
            sb.append("\n");
            Log.e("tags are : ",sb.toString());
        }

     //   ((TextView)findViewById(R.id.tokens)).setText(sb);
    }


    @Override
    public void onTokenAdded(String token) {
        //((TextView)findViewById(R.id.lastEvent)).setText("Added: " + token);
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(String token) {
       // ((TextView)findViewById(R.id.lastEvent)).setText("Removed: " + token);
        updateTokenConfirmation();
    }

    @Override
    public void onTokenIgnored(String token) {
       // ((TextView)findViewById(R.id.lastEvent)).setText("Ignored: " + token);
        updateTokenConfirmation();
    }


    public String img2Text(String path){
        String base64="";
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64 = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);

           // System.out.println(base64);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "data:image/jpeg;base64,"+base64;
    }
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

}
