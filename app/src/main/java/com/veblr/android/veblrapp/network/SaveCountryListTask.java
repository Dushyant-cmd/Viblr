package com.veblr.android.veblrapp.network;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.datasource.ResponseCountry;
import com.veblr.android.veblrapp.model.Country;
import com.veblr.android.veblrapp.util.AppUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveCountryListTask extends AsyncTask<Void,Void,Void> {
    Context context;

public SaveCountryListTask(Context context) {this.context = context;}

    @Override
    protected Void doInBackground(Void... voids) {
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("cache_data_chk","false");
        obj.add("param", payerReg);

        Call<ResponseCountry> responseCountryCall = ApiService.getService(context)
                .create(ApiInterface.class).getCountries(obj);

        responseCountryCall.enqueue(new Callback<ResponseCountry>() {
            @Override
            public void onResponse(Call<ResponseCountry> call, Response<ResponseCountry> response) {
                try{
                    if(response.isSuccessful() && response.body().getResponse().getResult()!=null){
                    List<Country> countries = response.body().getResponse().getResult();
                    AppUtils.saveCountriesWithCodes(countries,context);
                }}catch (NullPointerException e){e.printStackTrace();}
            }

            @Override
            public void onFailure(Call<ResponseCountry> call, Throwable t) {

            }
        });

        return null;
    }
}