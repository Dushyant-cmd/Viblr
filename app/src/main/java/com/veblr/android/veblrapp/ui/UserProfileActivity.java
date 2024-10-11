package com.veblr.android.veblrapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.CustomExpandableListAdapter;
import com.veblr.android.veblrapp.adapter.GridAdapter;
import com.veblr.android.veblrapp.adapter.ListofMyVideosAdapter;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.Constants;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerView;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class UserProfileActivity extends AppCompatActivity {
    LinearLayout actionbar;
    ImageView back;
    ImageView listSetting;
    ExpandableListView  expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    CustomExpandableListAdapter expandableListAdapter;

   static RecyclerView rvListOfUserVideos;
    TextView tvUserAbout,userNameheader;
    Button btnMore;
    androidx.appcompat.widget.AppCompatToggleButton followButton;
    boolean moreButtonVisible = true;
    NestedScrollView scrollView;
    AutoVideoPlayerRecyclerView rvVideoListOfUser;
    Channel channel;
    List<Channel> channelList;
    Spinner spUserChannel;
    CardView cardView;
    TextView acSetting,chSetting;
    GridView gvVideosList;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ((androidx.appcompat.widget.AppCompatToggleButton) findViewById
                (R.id.btnFollowChannel)).setVisibility(View.GONE);

        actionbar = (LinearLayout) findViewById(R.id.llBar);
        actionbar.setVisibility(View.VISIBLE);
        back = (ImageView) findViewById(R.id.ivBack);
        listSetting = (ImageView)findViewById(R.id.ivSetting);
        cardView = (CardView)findViewById(R.id.cvSetting);
        spUserChannel = (Spinner) findViewById(R.id.spUserChannel);
        rvListOfUserVideos = (RecyclerView)findViewById(R.id.rvListOfUserVodeos);
        rvVideoListOfUser = (AutoVideoPlayerRecyclerView)findViewById(R.id.rvListOfUserVideos);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
//        (NestedScrollView)findViewById(R.id.scrollView)
        tvUserAbout = (TextView)findViewById(R.id.tvAboutUser);
        userNameheader = (TextView)findViewById(R.id.tvUsernameheader);
        acSetting = (TextView)findViewById(R.id.tvacSetting);
        chSetting = (TextView)findViewById(R.id.tvChannelSetting);
        gvVideosList =(GridView)findViewById(R.id.gvVideosList);

        btnMore = (Button)findViewById(R.id.btnMore);
        ((TextView)findViewById(R.id.tvUserID)).setVisibility(View.GONE);
        spUserChannel.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.onBackPressed();
            }
        });
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.cardview_post_listitem, null);
        ((androidx.appcompat.widget.AppCompatToggleButton)v.findViewById(R.id.btnFollow)).setVisibility(View.GONE);

        listSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView.getVisibility()==View.VISIBLE){
                    cardView.setVisibility(View.INVISIBLE);
                }
                else cardView.setVisibility(View.VISIBLE);


            }
        });
        acSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.GONE);
                Intent i = new Intent(UserProfileActivity.this,AccountSetting.class);

                startActivity(i);

            }
        });


        channelList =  AppUtils.getRegisteredUserId(UserProfileActivity.this).getChannel();
        spUserChannel.setVisibility(View.VISIBLE);
        List<String > spinnerStringArray =new ArrayList<>();
        if(channelList!=null){
            for(int i=0;i<channelList.size();i++){
                spinnerStringArray.add(channelList.get(i).getChNameDisp());
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerStringArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spUserChannel.setAdapter(spinnerArrayAdapter);
        userNameheader.setText(AppUtils.getRegisteredUserId(UserProfileActivity.this).getName());


        if(channelList!=null){
            setchannel();}

        spUserChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setchannel();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         // if(channelList.size()>1) {

     // }
        chSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.GONE);
                Intent i = new Intent(UserProfileActivity.this,ChannelSetting.class);
                i.putExtra("channel",(Serializable) channelList.get(spUserChannel.getSelectedItemPosition()));
                startActivity(i);

            }
        });
        expandableListView = (ExpandableListView) findViewById(R.id.exp);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle,
                expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
               Toast toast= Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT);
                TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                toast.show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
               Toast toast = Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT);
                TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                toast.show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
             Toast toast =   Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                );
                TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                toast.show();
                return false;
            }
        });
        initRecyclerViewVideos();
        saveFav();
        }



    private void setchannel() {
        ((TextView)findViewById(R.id.tvUserID)).setText(channelList.get(spUserChannel.getSelectedItemPosition()).getChNameDisp());
        if(moreButtonVisible)
            tvUserAbout.setSingleLine(true);
        Glide.with(this).asBitmap()
                .load(channelList.get(spUserChannel.getSelectedItemPosition()).getChImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ((ImageView)findViewById(R.id.ivChannelIcon)).setImageBitmap(resource);
                        tvUserAbout.setText(channelList.get(spUserChannel.getSelectedItemPosition()).getChAbout());

                    }
                });
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("channel_name", channelList.get(spUserChannel.getSelectedItemPosition()).getChName());
        obj.add("param",payerReg);
        Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call =  ApiService.getService(UserProfileActivity.this)
                .create(ApiInterface.class).getUserDetails(obj);
        call.enqueue(new Callback<com.veblr.android.veblrapp.model.ChannelDetailResponse>() {
            @Override
            public void onResponse(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Response<com.veblr.android.veblrapp.model.ChannelDetailResponse> response) {
                if(response.isSuccessful()){
                    try{
                        List<Channel>  channels =  response.body().getUSERDetailResponse().getResultList();
                        Channel channel =channels.get(0);

                        if(channel.getChTotalVideos()==null) ( (TextView)findViewById(R.id.tvNoOfVideos)).setText("0");
                        else ( (TextView)findViewById(R.id.tvNoOfVideos)).setText(channel.getChTotalVideos()+"  videos");

                        if(channel.getChTotalVideoViews()==null) ( (TextView)findViewById(R.id.tvNoOfViews)).setText("0");
                        else ( (TextView)findViewById(R.id.tvNoOfViews)).setText(channel.getChTotalVideoViews()+"  views");

                        if(channel.getChTotalFollowing()==null) ( (TextView)findViewById(R.id.tvNoOfFollowing)).setText("0");
                        else ( (TextView)findViewById(R.id.tvNoOfFollowing)).setText(channel.getChTotalFollowing());

                        if(channel.getChTotalFollower()==null) ( (TextView)findViewById(R.id.tvNoOfFollowers)).setText("0");
                        else ( (TextView)findViewById(R.id.tvNoOfFollowers)).setText(channel.getChTotalFollower());

                        if(channel.getChAbout()!=null)
                        tvUserAbout.setText(channel.getChAbout());
                    }
                    catch(NullPointerException ignored){}
                }
            }

            @Override
            public void onFailure(Call<com.veblr.android.veblrapp.model.ChannelDetailResponse> call, Throwable t) {

            }
        });
    }
    private void initRecyclerViewVideos() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfileActivity.this,
                RecyclerView.VERTICAL,false);
     //   rvVideoListOfUser.setLayoutManager(layoutManager);
        rvListOfUserVideos.setLayoutManager(layoutManager);
        ViewCompat.setNestedScrollingEnabled(rvListOfUserVideos, true);
            Call<ResponseVideoList>  responseVideoListCall = ApiService.getService(UserProfileActivity.this)
                    .create(ApiInterface.class)
                    .getVideoListForChannels(AppUtils.getJSonOBJForFollwedChannels
                            (channelList.get(spUserChannel.getSelectedItemPosition()).getChName()));
            responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                @Override
                public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {

                    if(response.isSuccessful()) {
                        try {
                            List<VIdeoItem> vIdeoItemArrayList = response.body().getResonse().getResult();
                            Log.e("RESPONSE From repo",response.body().getResonse().getStatus()+"");
                           // rvVideoListOfUser.setMediaObjects(vIdeoItemArrayList);
                            ArrayList<VIdeoItem> arrayList = new ArrayList<VIdeoItem>(vIdeoItemArrayList);
                            /*AutoVideoPlayerRecyclerViewAdapter adapter = new AutoVideoPlayerRecyclerViewAdapter
                                    (UserProfileActivity.this,arrayList,AppUtils.initGlide(getApplicationContext()),false);
                             rvVideoListOfUser.setAdapter(adapter);
                             adapter.getHolder().setVisibilityGoneFollow();

                            gvVideosList.setAdapter(new GridAdapter(getApplicationContext(),arrayList));*/
                            Channel channel =    channelList.get(spUserChannel.getSelectedItemPosition());

                            rvListOfUserVideos.setAdapter(new ListofMyVideosAdapter
                                    (getApplicationContext(),AppUtils.initGlide(getApplicationContext())
                                            ,arrayList,channel));

                            ((ProgressBar)findViewById(R.id.pbProgress)).setVisibility(View.GONE);
                        } catch (NullPointerException e) {
                            Log.e("EXCEPTION", e.getMessage()+" ");
                        }
                    }
                    else{
                        Gson gson = new Gson();
                        Type type = new TypeToken<ApiError>() {}.getType();
                        ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                        Log.e("Error",errorResponse.getError().getMessage());
                    }
                }
                @Override
                public void onFailure(Call<ResponseVideoList> call, Throwable t) {

                }

            });


    }


    public static class ExpandableListDataPump {
        public  static HashMap<String, List<String>> getData() {
            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

            List<String> account = new ArrayList<String>();
            account.add("Account");

            List<String> channel = new ArrayList<String>();
            channel.add("Your Videos");
            channel.add("Channel Setting");
            channel.add("Video PlayList");


         /*   List<String> analytics = new ArrayList<String>();
            analytics.add("United States");
            analytics.add("Spain");
            analytics.add("Argentina");
            analytics.add("France");
            analytics.add("Russia");*/

            expandableListDetail.put("Account Setting", account);

            expandableListDetail.put("Channel Setting", channel);
         //   expandableListDetail.put("Analytics", analytics);
            return expandableListDetail;
        }

    }
    public void OnMoreButtonClicked(View view)
    {
        moreButtonVisible=false;
        btnMore.setVisibility(View.GONE);
        tvUserAbout.setSingleLine(false);
        tvUserAbout.setMaxLines(8);
        tvUserAbout.setWidth(AppUtils.getDeviceWidth());

    }
    private void saveFav() {
        SaveUserfavDetails savedUserfavDetails = new SaveUserfavDetails();
        savedUserfavDetails.execute();
    }

    private class SaveUserfavDetails extends AsyncTask<Void,Void,Void> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {

            Call<ChannelDetailResponse> channelsCall ;
            if(AppUtils.getRegisteredUserId(getApplicationContext())!=null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    channelsCall = ApiService.getService(UserProfileActivity.this)
                            .create(ApiInterface.class).getaUsersFollowingListBYChannel
                                    (AppUtils.getJSonOBJForFollowingListReg
                                            (Objects.requireNonNull(AppUtils.getAppUserId(UserProfileActivity.this)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(UserProfileActivity.this).getUserId()),
                                                    AppUtils.getRegisteredUserId(UserProfileActivity.this).getChannel().get(0).getChId()));

                    channelsCall.enqueue(new Callback<ChannelDetailResponse>() {
                        @Override
                        public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {

                            if (response.isSuccessful() && response.body().getUSERDetailResponse() != null)
                            {
                                AppUtils.removeFavoriteUsers(getApplicationContext());
                                AppUtils.saveFavUser(getApplicationContext(),
                                        response.body().getUSERDetailResponse().getResultList());
                        }
                        }

                        @Override
                        public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

                        }
                    });
                }
            }




            Call<ResponseVideoList> responseVideoListCall;
            if(AppUtils.getRegisteredUserId(getApplicationContext())!=null){
                responseVideoListCall=
                        ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                                .getLikedVideosListBYChannel(AppUtils.getJSonOBJForLikedVideoListReg( Objects.requireNonNull(AppUtils.getAppUserId(UserProfileActivity.this)).getGuestUserId() ,AppUtils.getRegisteredUserId(getApplicationContext()).getUserId(),
                                        AppUtils.getRegisteredUserId(getApplicationContext()).getChannel().get(0).getChId()));
                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        if (response.isSuccessful() && response.body().getResonse() != null) {
                            for (VIdeoItem v : response.body().getResonse().getResult()) {
                                List<String> likedVideoList = new ArrayList<>();
                                likedVideoList.add(v.getVideoId());
                                AppUtils.saveLikedVideo(UserProfileActivity.this,new ArrayList<String>());
                                AppUtils.saveLikedVideo(Objects.requireNonNull(getApplicationContext()),
                                        likedVideoList);

                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {

                    }
                });
            }
            return null;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_OK && resultCode == Constants.ACTIVITY_EDIT_REQUEST){
            initRecyclerViewVideos();
        }
    }
    public static void refreshListAfterDeletion(int position){
        if(rvListOfUserVideos.getAdapter()!=null) {
            ((ListofMyVideosAdapter)rvListOfUserVideos.getAdapter()).removeItem(position);
            rvListOfUserVideos.getAdapter().notifyDataSetChanged();
        }
    }
}
