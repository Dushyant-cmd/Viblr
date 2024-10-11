package com.veblr.android.veblrapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String[] categoryStringArray;
    ArrayList<Drawable> drawableArrayList;
    String[] categoryLinkArray ;
    Context context;
    ClickInterface clickInterface;
    public RecyclerViewGridAdapter(Context context,String[] categoryStringArray,
                                   ArrayList<Drawable> drawableArrayList,ClickInterface clickInterface) {
        this.context = context;
        this.categoryStringArray = categoryStringArray;
        this.drawableArrayList = drawableArrayList;
        this.categoryLinkArray = context.getResources().getStringArray(R.array.category_link_array);
        this.clickInterface  = clickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (new RecyclerViewGridViewHolder (LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_category, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewGridViewHolder)holder).onBind(categoryStringArray[position],drawableArrayList.get(position),position) ; }

    @Override
    public int getItemCount() {
        return categoryStringArray.length;
    }
    class RecyclerViewGridViewHolder extends RecyclerView.ViewHolder{

        ImageView iconCatagory;
        TextView tvCatagoryName;
        LinearLayout cardView;
        public RecyclerViewGridViewHolder(@NonNull View itemView) {
            super(itemView);
            iconCatagory = (ImageView)itemView.findViewById(R.id.ivIconCatagory);
            tvCatagoryName = (TextView)itemView.findViewById(R.id.tvCatagoryName);
            cardView = (LinearLayout)itemView.findViewById(R.id.cvCategory);

        }

        public void onBind(String category, Drawable drawable, final int position){
            tvCatagoryName.setText(category);
            iconCatagory.setImageDrawable(drawable);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getRootView().findViewById(R.id.pbProgress).setVisibility(View.VISIBLE);
                    CategoryFragment.setAutoscrollHere();
                    clickInterface.recyclerviewOnClick(position);
                 //   CategoryFragment.setListView(position);
              /*      Call<ResponseVideoList> responseVideoListCall = ApiService.getService(context).create(ApiInterface.class).getVideoListFromCategory(AppUtils.getJSONOBJForCategory(categoryLinkArray[position]));
                    responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                        @Override
                        public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                            if(response.isSuccessful()) {
                                try {
                                    List<VIdeoItem> vIdeoItemArrayList = response.body().getResonse().getResult();
                                    Log.e("RESPONSE",response.body().getResonse().getStatus()+"FROMCATA");
                                    Intent intent = new Intent(context, VideoWatchActivity.class);
                                    intent.putExtra("videoList",(Serializable)vIdeoItemArrayList);
                                    context.startActivity(intent);
                                    itemView.getRootView().findViewById(R.id.pbProgress).setVisibility(View.GONE);

                                } catch (NullPointerException e) {
                                    Log.e("EXCEPTION", e.getMessage());
                                    itemView.getRootView().findViewById(R.id.pbProgress).setVisibility(View.GONE);

                                }
                            }
                            else{
                                Gson gson = new Gson();
                                Type type = new TypeToken<ApiError>() {}.getType();
                                ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                                Log.e("Error",errorResponse.getError().getMessage());
                                itemView.getRootView().findViewById(R.id.pbProgress).setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                            itemView.getRootView().findViewById(R.id.pbProgress).setVisibility(View.GONE);


                        }
                    });*/

                }
            });
        }
    }
    public interface ClickInterface {

        public void recyclerviewOnClick(int position);
    }
}
