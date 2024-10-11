package com.veblr.android.veblrapp.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.gms.common.util.IOUtils;
import com.veblr.android.veblrapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import VideoHandle.OnEditorListener;

import static VideoHandle.EpEditor.execCmd;

public class PreviewActivity extends AppCompatActivity {

    VideoView vv;
    ImageView ivPlay;
    Button btnNext;
    Uri  fileUri;
    boolean fromRecord=false;
    int duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        vv = (VideoView)findViewById(R.id.vv);
        ivPlay =(ImageView)findViewById(R.id.ivPlay) ;
        btnNext = (Button) findViewById(R.id.btnNext);
        final String videoUri = getIntent().getStringExtra("fileUri");
        final String fileTitle = getIntent().getStringExtra("fileTitle");
       duration  = getIntent().getIntExtra("duration",0);
        fromRecord = getIntent().getBooleanExtra("fromRecorder",false);
       if(!fromRecord){
        assert videoUri != null;
        File requestFile = new File(videoUri);

        try {
            fileUri = FileProvider.getUriForFile(PreviewActivity.this,
                    "com.veblr.android.veblrapp.fileprovider",
                    requestFile);
            Log.e("FileUri is:",fileUri.toString() +"\n"+videoUri);
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: " + requestFile.toString());
        }
        }
        else{
            fileUri = Uri.parse(videoUri);
        }
        vv.setVideoURI(fileUri);
        vv.start();
        /*
         * Most file-related method calls need to be in
         * try-catch blocks.
         */
        // Use the FileProvider to get a content URI

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vv.setVideoURI(fileUri);
          //      vv.setVideoPath(videoUri);
            }
        });
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ivPlay.setVisibility(View.GONE);
                vv.start();
            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivPlay.setVisibility(View.VISIBLE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewActivity.this,UploadEditActivity.class);
                intent.putExtra("fileUri",fileUri.toString());
                intent.putExtra("fileTitle",fileTitle);
                intent.putExtra("fileThumbNailUri",getThumbNail(videoUri));
                intent.putExtra("duration",duration);

                startActivity(intent);
            }
        });

    }
    public  String getThumbNail(String videouri) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videouri,
                MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
         String imgName = videouri.replace(".mp4",".jpg");
        File file = new File (imgName);
        if (file.exists ()) file.delete ();
        Log.d("name is ",Uri.parse(imgName).getLastPathSegment()+"");

        try {
            FileOutputStream out = new FileOutputStream(file);
            assert thumb != null;
            thumb.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
