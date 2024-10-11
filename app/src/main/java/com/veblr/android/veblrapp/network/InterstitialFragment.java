package com.veblr.android.veblrapp.network;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.veblr.android.veblrapp.R;

import java.util.EnumSet;

public class InterstitialFragment extends Fragment implements InterstitialAdListener {

    private static final String TAG = InterstitialFragment.class.getSimpleName();

    private TextView interstitialAdStatusLabel;
    private Button loadInterstitialButton;
    private Button showInterstitialButton;
    private InterstitialAd interstitialAd;

    private String statusLabel = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interstitial, container, false);

        interstitialAdStatusLabel = (TextView)view.findViewById(R.id.interstitialAdStatusLabel);
        loadInterstitialButton = (Button)view.findViewById(R.id.loadInterstitialButton);
        showInterstitialButton = (Button)view.findViewById(R.id.showInterstitialButton);

        loadInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                    interstitialAd = null;
                }
                setLabel("Loading interstitial ad...");

                // Create the interstitial unit with a placement ID (generate your own on the Facebook app settings).
                // Use different ID for each ad placement in your app.
                interstitialAd = new InterstitialAd(InterstitialFragment.this.getActivity(),
                        "YOUR_PLACEMENT_ID");

                // Set a listener to get notified on changes or when the user interact with the ad.
                //interstitialAd.setAdListener(InterstitialFragment.this);

                // Load a new interstitial.
                //interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
                interstitialAd.loadAd(
                        interstitialAd.buildLoadAdConfig()
                                .withAdListener(InterstitialFragment.this)
                                .build());
            }
        });

        showInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    // Ad not ready to show.
                    setLabel("Ad not loaded. Click load to request an ad.");
                } else {
                    // Ad was loaded, show it!
                    interstitialAd.show();
                    setLabel("");
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        super.onDestroy();
    }

    @Override
    public void onError(Ad ad, AdError error) {
        if (ad == interstitialAd) {
            setLabel("Interstitial ad failed to load: " + error.getErrorMessage());
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad == interstitialAd) {
            setLabel("Ad loaded. Click show to present!");
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        if (isAdded()) {
            Toast.makeText(getActivity(), "Interstitial Displayed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        if (isAdded()) {
            Toast.makeText(getActivity(), "Interstitial Dismissed", Toast.LENGTH_SHORT).show();
        }

        // Cleanup.
        interstitialAd.destroy();
        interstitialAd = null;
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (isAdded()) {
            Toast.makeText(getActivity(), "Interstitial Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.d(TAG, "onLoggingImpression");
    }

    private void setLabel(String label) {
        statusLabel = label;
        if (interstitialAdStatusLabel != null) {
            interstitialAdStatusLabel.setText(statusLabel);
        }
    }

}
