package com.veblr.android.veblrapp.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.FollowedListRecyclerViewAdapter;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FollowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private final String TAG = FollowFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static androidx.appcompat.app.ActionBar actionBar;
    static  RecyclerView recyclerView;
    static  FollowedListRecyclerViewAdapter followedListRecyclerViewAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   static List<Channel> featuredChannels;
   static List<Channel> followingChannels;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
   // private OnFragmentInteractionListener mListener;

    public FollowFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            //followingChannels= AppUtils.getFavoriteUser(getContext());
            //if(followingChannels==null){
                getFollowingList(featuredChannels);
                followedListRecyclerViewAdapter = new FollowedListRecyclerViewAdapter(followingChannels, getContext(),
                        featuredChannels);
                recyclerView.setAdapter( followedListRecyclerViewAdapter);
            //}
        }
    }

    public  static void updateFollowedList(Context context){
        if(followedListRecyclerViewAdapter!=null){
            followingChannels = AppUtils.getFavoriteUser(context);
            if(followingChannels!=null){
                followedListRecyclerViewAdapter = new FollowedListRecyclerViewAdapter(followingChannels, context, featuredChannels);

                recyclerView.setAdapter( followedListRecyclerViewAdapter);
                followedListRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     /** @param param1 Parameter 1.
     /* @param param2 Parameter 2.*/
     /* @return A new instance of fragment FollowFragment.
     */
    // TODO: Rename and change types and number of parameters
 /*   public static FollowFragment newInstance(String param1, String param2) {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public static FollowFragment newInstance(androidx.appcompat.app.ActionBar actionbar) {
        actionBar = actionbar;
        return new FollowFragment();
    }
    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        featuredChannels =new ArrayList<>();
        followingChannels =new ArrayList<>();
        featuredChannels = AppUtils.getFeaturedUser(getActivity());
        followingChannels = AppUtils.getFavoriteUser(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_follow, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.rvFollow);
        progressBar = (ProgressBar) v.findViewById(R.id.pbProgress);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
      //  swipeRefreshLayout.setRefreshing(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) );

            Call<ChannelDetailResponse> channelsCall = ApiService.getService(getContext()).create(ApiInterface.class)
                    .getFeaturedChannels(AppUtils.getJSonOBJForFeaturedChannels("10"));
            channelsCall.enqueue(new Callback<ChannelDetailResponse>() {
                @Override
                public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                    Log.e("FollowFragment","Response 134:"+response.code());
                    List<Channel> userList = new ArrayList<>();
                    if (response.isSuccessful() && response.body().getUSERDetailResponse() != null) {
                        userList = response.body().getUSERDetailResponse().getResultList();
                        Log.e("FollowFragment","UserList:"+userList.size());
                        try {
                            if (userList != null) {
                                AppUtils.saveFeatured(getActivity(),
                                        Objects.requireNonNull(userList));
                            } else
                                AppUtils.saveFeatured(getActivity(), userList);
                        }
                        catch (NullPointerException e){e.printStackTrace();}
                        featuredChannels = userList;

                    }
                }

                @Override
                public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {
t.getMessage();
                }
            });

        /*if(followingChannels==null){
            getFollowingList(featuredChannels);
        }*/

            followedListRecyclerViewAdapter = new FollowedListRecyclerViewAdapter( followingChannels, getContext(),
                    featuredChannels);
            recyclerView.setAdapter(followedListRecyclerViewAdapter);


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
                Log.d("dyis ",dy+"");
                if (dy > 10) {
                    //scroll down
                    scroll_down[0] = true;

                } else if (dy < -5) {
                    //scroll up
                    scroll_down[0] = false;
                }
            }
        });
        getFollowingList(featuredChannels);
        return  v;
    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getFollowingList(final List<Channel> featuredchannelList) {
        try{
        User user = AppUtils.getAppUserId(Objects.requireNonNull(getContext()));
        if(user!=null || featuredchannelList!=null) {
            Call<ChannelDetailResponse> channelsCall ;
            if(AppUtils.getRegisteredUserId(getContext())!=null){
                channelsCall = ApiService.getService(getContext())
                        .create(ApiInterface.class).getaUsersFollowingListBYChannel
                                (AppUtils.getJSonOBJForFollowingListReg
                                        (Objects.requireNonNull(AppUtils.getAppUserId(getContext())).getGuestUserId(),
                                                Objects.requireNonNull(AppUtils.getRegisteredUserId(getContext()).getUserId()),
                                                AppUtils.getRegisteredUserId(getContext()).getChannel().get(0).getChId()));

            }
            else {
                channelsCall = ApiService.getService(getContext())
                        .create(ApiInterface.class).getaUsersFollowingList
                                (AppUtils.getJSonOBJForFollowingList(Objects.requireNonNull
                                        (AppUtils.getAppUserId(getContext())).getGuestUserId(), ""));
            }

            channelsCall.enqueue(new Callback<ChannelDetailResponse>() {
                @Override
                public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                    Log.e("FollowFragment","channelsCall Response:"+response.code());
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body().getUSERDetailResponse()!=null ) {
                        Log.e("FollowFragment","channelsCall ResultList:"+response.body().getUSERDetailResponse().getResultList().size());
                        followedListRecyclerViewAdapter = new FollowedListRecyclerViewAdapter(response.body().getUSERDetailResponse().getResultList(), getContext(), featuredchannelList);
                        recyclerView.setAdapter(followedListRecyclerViewAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) );
                        followedListRecyclerViewAdapter.notifyDataSetChanged();
                        AppUtils.saveFeatured(getActivity(),Objects.requireNonNull(featuredchannelList));
                        AppUtils.saveFavUser(getActivity(),response.body().getUSERDetailResponse().getResultList());
                        Toast.makeText(getActivity(), response.message(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("FollowFragment","channelsCall onFailure"+t.getMessage());
                }
            });
        }

        else{
            Log.e(TAG, "Create New User From getFollowingList");
            AppUtils.createNewUser(getContext(),AppUtils.getDeviceUniqueId(getContext()));
            followedListRecyclerViewAdapter =
                    new FollowedListRecyclerViewAdapter( Objects.requireNonNull(AppUtils.getFavoriteUser
                            (Objects.requireNonNull(getContext()))), getContext(), featuredchannelList);

            recyclerView.setAdapter(followedListRecyclerViewAdapter);
        }}
        catch (NullPointerException e){e.printStackTrace();}

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
       /* if (mListener != null) {
         //   mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        followingChannels= AppUtils.getFavoriteUser(getContext());
        if(featuredChannels!=null&&followingChannels!=null){
            followedListRecyclerViewAdapter = new FollowedListRecyclerViewAdapter(followingChannels, getContext(),
                    featuredChannels);

        recyclerView.setAdapter( followedListRecyclerViewAdapter);
        //progressBar.setVisibility(View.GONE);

        }

 }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh() {
Log.e("FollowFragment","Inside OnRefresh"+featuredChannels.size());
        if(featuredChannels!=null) {
            getFollowingList(featuredChannels);
        }
        swipeRefreshLayout.setRefreshing(false);
}


}
