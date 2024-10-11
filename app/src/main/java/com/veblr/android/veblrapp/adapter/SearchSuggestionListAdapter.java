package com.veblr.android.veblrapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.model.VIdeoItem;

import java.util.ArrayList;
import java.util.List;


public class SearchSuggestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{
    Context context;
    List<String> searchTextList;
    ArrayList<VIdeoItem> vIdeoItemArrayList;
    RequestManager requestManager;
    public static final int SEARCHTEXT_TYPE =1;
    public static final int VIDEOS_TYPE =2;
    private Filter fRecords;
    int viewType;
    public SearchSuggestionListAdapter(Context context,List<String> searchTextList,int viewType){
        this.context = context;
        this.searchTextList = searchTextList;
        this.viewType = viewType;
    }
    public SearchSuggestionListAdapter(RequestManager requestManager,ArrayList<VIdeoItem> vIdeoItemArrayList){
        this.requestManager = requestManager;
        this.vIdeoItemArrayList = vIdeoItemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null ;
        switch (viewType) {
            case SEARCHTEXT_TYPE:
                viewHolder = (RecyclerView.ViewHolder)new SearchHistoryViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_suggestion_listitem, parent, false));
            break;
          /*  case VIDEOS_TYPE:
                viewHolder = (RecyclerView.ViewHolder)new ListOfVideosAdapter.ListOfVideosViewHolder
                        (LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardview_videos_listitem, parent, false));

            break;*/
        }
     return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
          /* int pos=0;
           switch (position)
           {
               case 1:
                   pos = SEARCHTEXT_TYPE;
                   break;
               case 2:
                   pos = VIDEOS_TYPE;
           }*/

            return viewType;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

      if(holder instanceof SearchHistoryViewHolder){
             ((SearchHistoryViewHolder) holder).onBind(searchTextList.get(position));
         }
     else if(holder instanceof ListOfVideosAdapter.ListOfVideosViewHolder){
            //((ListOfVideosAdapter.ListOfVideosViewHolder) holder).onBind(vIdeoItemArrayList.get(position),requestManager);
       }
    }

    @Override
    public int getItemCount() {
        return searchTextList.size();
    }

    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords=new RecordFilter();
        }
        return fRecords;    }
    //filter class
    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            //Implement filter logic
            // if edittext is null return the actual list
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = searchTextList;
                results.count = searchTextList.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<String> fRecords = new ArrayList<String>();

                for (String s : searchTextList) {
                    if (s.toLowerCase().trim().contains(constraint.toString().toLowerCase().trim())) {
                        fRecords.add(s);
                    }
                }
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

            //it set the data from filter to adapter list and refresh the recyclerview adapter
            searchTextList = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }


    class SearchHistoryViewHolder extends RecyclerView.ViewHolder{

         TextView textViewSearch;

        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
         textViewSearch = (TextView) itemView.findViewById(R.id.tvSearchText);

        }
        public void onBind(String searchText){
            textViewSearch.setText(searchText);
        }
    }


}
