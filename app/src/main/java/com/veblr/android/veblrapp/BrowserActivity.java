package com.veblr.android.veblrapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.util.Objects;

public class BrowserActivity extends Activity {

    WebView page;
    TextView url;
    String urlLoading;
    ProgressBar progressBar;
    ProgressBar progressBarHorizontal;
    myWebChromeClient myWebChromeClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {

                                                     @Override
                                                     public void onClick(View v) {
                                                         BrowserActivity.super.onBackPressed();
                                                         finish();
                                                     }
                                                 }

            );
        }

       else{
            try {
                ProviderInstaller.installIfNeeded(this);
            }
            catch(NullPointerException e){
                e.printStackTrace();
            }
            catch (GooglePlayServicesRepairableException e) {
                // Thrown when Google Play Services is not installed, up-to-date, or enabled
                // Show dialog to allow users to install, update, or otherwise enable Google Play services.
                Log.e("SecurityException", "Google Play Services not available. "+e.getStackTrace().toString());
                GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(),BrowserActivity.this, 0);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e("SecurityException", "Google Play Services not available.");
            }
        }
        myWebChromeClient = new myWebChromeClient();

        page =(WebView) findViewById(R.id.wvPage);
        progressBarHorizontal  = findViewById(R.id.pbLoading);
        page.setWebChromeClient(myWebChromeClient);
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.N) {
            page.setWebViewClient(new MyWebViewClient(BrowserActivity.this) {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.loadUrl(request.getUrl().toString());
                    }
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Toast.makeText(getApplicationContext(), "your current url when webpage loading.. finish", Toast.LENGTH_LONG).show();
                    super.onPageFinished(view, url);
                    //    AppUtil.dismissPG(BrowserActivity.this);
                    // progressBar.setVisibility(View.GONE);
                }


                @Override
                public void onLoadResource(WebView view, String url) {
                    // TODO Auto-generated method stub
                    super.onLoadResource(view, url);


                }

            });
        } else {
            page.setWebViewClient(new MyWebViewClient(BrowserActivity.this) {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Toast.makeText(getApplicationContext(), "your current url when webpage loading.. finish", Toast.LENGTH_LONG).show();
                    super.onPageFinished(view, url);
                    // progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    // TODO Auto-generated method stub
                    super.onLoadResource(view, url);
                }

            });
        }

        page.getSettings().setAllowFileAccess(true);
        page.getSettings().setPluginState(WebSettings.PluginState.ON);
        page.getSettings().setJavaScriptEnabled(true);
        page.getSettings().setUseWideViewPort(true);
        page.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        page.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        page.getSettings().setLoadWithOverviewMode(true);
        page.getSettings().setUseWideViewPort(true);
        page.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        page.getSettings().setAllowUniversalAccessFromFileURLs(true);
        page.getSettings().setAllowContentAccess(true);
        page.getSettings().setDomStorageEnabled(true);
        if(!Objects.requireNonNull(getIntent().getStringExtra("browser_url")).isEmpty()) {
            urlLoading = getIntent().getStringExtra("browser_url");
        }
        page.loadUrl(urlLoading);


    }


    private class myWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;


        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            BrowserActivity.this.runOnUiThread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    // request.grant(request.getResources());

                }
            });
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBarHorizontal.setProgress(newProgress);
            if (newProgress == 100)
                progressBarHorizontal.setVisibility(View.GONE);
            else
                progressBarHorizontal.setVisibility(View.VISIBLE);
            super.onProgressChanged(view, newProgress);

        }


        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(BrowserActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }
    }
    public class MyWebViewClient extends WebViewClient {

        private Context context;

        public MyWebViewClient(Context context) {
            this.context = context;
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;

        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            view.loadUrl(uri.toString());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //view.loadUrl(url);
        }

    }
}
