package com.veblr.android.veblrapp.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseHomeFeed;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.VerticalSpacingItemDecorator;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerView;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment implements RecyclerViewGridAdapter.ClickInterface,
        MainActivity.OnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ActionBar actionBar;
    RecyclerViewGridAdapter adapter;
    // TODO: Rename and change types of parameters
    private static  RecyclerView recyclerView;
    private static AutoVideoPlayerRecyclerView autoVideoPlayerRecyclerView;
   static String[] categoryLinkArray ;
   private ProgressBar progressBar;
   private  TextView tvButtonRetry;
    NativeAdsManager mNativeAdsManager;
    List<VIdeoItem> vIdeoItemArrayList=new ArrayList<>();
    AutoVideoPlayerRecyclerViewAdapter adaptervideo;
    static boolean isBackPressed;

    public CategoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(androidx.appcompat.app.ActionBar actionbar) {
        actionBar = actionbar;
        return new CategoryFragment();
    }

    public static void setAutoscrollHere() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        categoryLinkArray =  getResources().getStringArray(R.array.category_link_array);
        progressBar =(ProgressBar)  v.findViewById(R.id.pbProgress);
        tvButtonRetry = (TextView)v.findViewById(R.id.tvButtonRetry);

        //((SwipeRefreshLayout)v.findViewById(R.id.swipe)).setVisibility(View.GONE);
        v.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 16, 8, 0);
        recyclerView = (RecyclerView)v.findViewById(R.id.rvFollow);
        autoVideoPlayerRecyclerView = (AutoVideoPlayerRecyclerView)v.findViewById(R.id.avp) ;
        recyclerView.setLayoutParams(params);
        setcategoryRecyclerView();
        String placement_id = "424758711275495_730941363990560";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mNativeAdsManager = new NativeAdsManager(Objects.requireNonNull(getContext()), placement_id, 90);
        }
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {

            }

            @Override
            public void onAdError(AdError adError) {

            }
        });

        autoVideoPlayerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    if (getContext() != null) {
                        if (AppUtils.getFavorites(getContext()) != null) {
                            List<VIdeoItem> vIdeoItemList = AppUtils.getFavorites(getContext());
                            vIdeoItemArrayList.addAll(vIdeoItemArrayList.size() - 1, vIdeoItemList);
                            autoVideoPlayerRecyclerView.addMediaObjects(vIdeoItemArrayList);
                            adapter.notifyDataSetChanged();
                        } else {

                            JsonObject obj = new JsonObject();
                            JsonObject payerReg = new JsonObject();
                            payerReg.addProperty("max_results", "100");
                            payerReg.addProperty("cache_data_chk", false);
                            obj.add("param", payerReg);
                            Call<ResponseHomeFeed> responseVideoListCall =
                                    ApiService.getService(getContext()).create(ApiInterface.class)
                                            .getVideoListForWebHomePage(obj);
                            responseVideoListCall.enqueue(new Callback<ResponseHomeFeed>() {
                                @Override
                                public void onResponse(Call<ResponseHomeFeed> call,
                                                       Response<ResponseHomeFeed> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            Log.e("RESPONSE", response.body().getResponse().getStatus() + "");

                                            if (response.body().getResponse().getResult() != null) {
                                                List<VIdeoItem> vIdeoItemList = response.body().getResponse().getResult().getIndexTop().getVideos();
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexRight().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexMiddle1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexMiddle2().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexLeft1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexLeft2().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexBottom1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexBottom2().getVideos());
                                                vIdeoItemArrayList.addAll(vIdeoItemArrayList.size()-1,vIdeoItemList);
                                                autoVideoPlayerRecyclerView.addMediaObjects(vIdeoItemArrayList);
                                                adapter.notifyDataSetChanged();

                                            }

                                        }catch (NullPointerException e){e.printStackTrace();}
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseHomeFeed> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            }});
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = getContext().getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    private ArrayList<Drawable> getDrawables(){
        ArrayList<Drawable> drawableArrayList = new ArrayList<>();
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_news_politics_cat_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_entertainment_cat_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_sports_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_kids_cat_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_science_technology_cat_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_beauty_tips_icon)); //beauty
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_devotional_icon)); //devotional
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_peoples_blogs_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_health_fitness_icon));//health-fitness
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_autos_vehicles_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_cooking_icon)); //cooking
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_travel_events_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_education_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_comedy_amazing_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_tv_shows_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_pet_animals_icon));//pets/animals
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_dance_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_movies_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_music_icon));
        drawableArrayList.add(getResources().getDrawable(R.drawable.ic_trailers_icon));
       drawableArrayList.add(getResources().getDrawable(R.drawable.ic_gaming_icon));
        return drawableArrayList;
    }
   public void  setcategoryRecyclerView(){
       GridLayoutManager manager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
       recyclerView.setLayoutManager(manager);

        adapter = new RecyclerViewGridAdapter
               (getContext(),getResources().getStringArray(R.array.category_name_array),getDrawables()
                       ,this);
       recyclerView.setAdapter(adapter);

       final boolean[] scroll_down = new boolean[1];
       recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
               super.onScrollStateChanged(recyclerView, newState);
               if (actionBar != null) {
                   if (scroll_down[0]) {
                       actionBar.hide();
                   } else {
                       actionBar.show();
                   }
               }
           }
           @Override
           public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               if (dy > 10) {
                   //scroll down
                   scroll_down[0] = true;

               } else if (dy < -5) {
                   //scroll up
                   scroll_down[0] = false;
               }
           }
       });

    }


    @Override
    public void recyclerviewOnClick(int position) {
        recyclerView.setVisibility(View.GONE);
        autoVideoPlayerRecyclerView.setVisibility(View.GONE);
        autoVideoPlayerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        autoVideoPlayerRecyclerView.addItemDecoration(itemDecorator);
        Call<ResponseVideoList> responseVideoListCall = ApiService.getService(getContext())
                .create(ApiInterface.class).getVideoListFromCategory(AppUtils.getJSONOBJForCategory(categoryLinkArray[position]));
        responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @Override
            public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                if(response.isSuccessful()) {
                    try {
                         vIdeoItemArrayList = response.body().getResonse().getResult();
                        Log.e("RESPONSE",response.body().getResonse().getStatus()+"FROMCATA");
                        autoVideoPlayerRecyclerView.setVisibility(View.VISIBLE);
                        autoVideoPlayerRecyclerView.setMediaObjects(vIdeoItemArrayList );
                         adaptervideo = new AutoVideoPlayerRecyclerViewAdapter(mNativeAdsManager,getContext(),vIdeoItemArrayList, AppUtils.initGlide(getContext()),false);
                        autoVideoPlayerRecyclerView.setAdapter(adaptervideo);
                        LayoutInflater.from(getContext()).inflate(R.layout.fragment_follow,null).findViewById(R.id.pbProgress).setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } catch (NullPointerException e) {
                        Log.e("EXCEPTION", e.getMessage());
                        LayoutInflater.from(getContext()).inflate(R.layout.fragment_follow,null)
                                .findViewById(R.id.pbProgress).setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else{
                    Gson gson = new Gson();
                    Type type = new TypeToken<ApiError>() {}.getType();
                    ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    Log.e("Error",errorResponse.getError().getMessage());
                    LayoutInflater.from(getContext()).inflate(R.layout.fragment_follow,null).findViewById(R.id.pbProgress).setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (autoVideoPlayerRecyclerView.getVisibility() == View.VISIBLE) {
            autoVideoPlayerRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            isBackPressed = false;
        }else{
            isBackPressed = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(autoVideoPlayerRecyclerView.getVisibility()==View.VISIBLE && autoVideoPlayerRecyclerView!=null)
        {
            autoVideoPlayerRecyclerView.resetVideoView();
            progressBar.setVisibility(View.GONE);

        }
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(autoVideoPlayerRecyclerView.getVisibility()==View.VISIBLE &&autoVideoPlayerRecyclerView!=null)
            autoVideoPlayerRecyclerView.releasePlayer();
        super.onPause();
    }
}
