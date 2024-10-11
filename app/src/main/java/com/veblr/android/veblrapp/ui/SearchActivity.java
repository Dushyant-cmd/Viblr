package com.veblr.android.veblrapp.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.ListOfVideosAdapter;
import com.veblr.android.veblrapp.adapter.SearchSuggestionListAdapter;
import com.veblr.android.veblrapp.datasource.ResponseTag;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.FOCUSABLE_AUTO;

public class SearchActivity extends AppCompatActivity   {

    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    public static final String CONTENT_AUTHORITY = "br.com.mauker.materialsearchview.searchhistorydatabase";
    ProgressBar progressBarSearch;
    SearchViewModel searchViewModel;
    private PagedList<VIdeoItem> vIdeoItemPagedList;
    SearchView search;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       // setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        search = (SearchView)findViewById(R.id.searchView);
        search.requestFocus();
        progressBarSearch = (ProgressBar)findViewById(R.id.pbSearch);
        search.setFocusable(true);
        final String[] from = new String[] {"Suggestions"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(SearchActivity.this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        search.setSuggestionsAdapter(mAdapter);
        search.setIconifiedByDefault(false);

        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        searchView.setHintTextColor(getResources().getColor(R.color.black_border));
        searchView.setHint("Search videos");

        recyclerView = (RecyclerView) findViewById(R.id.rvSearch);
        recyclerView.setLayoutManager(new  LinearLayoutManager(SearchActivity.this));
        String[] stringArray = getResources().getStringArray(R.array.CountryArray);
       // final List<String> strings = Arrays.asList(stringArray);
        final List<String> strings = new ArrayList<>();

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

   //     final SearchSuggestionListAdapter searchSuggestionListAdapter = new SearchSuggestionListAdapter(SearchActivity.this, strings, SearchSuggestionListAdapter.SEARCHTEXT_TYPE);
       // recyclerView.setAdapter(searchSuggestionListAdapter);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                recyclerView.setVisibility(View.VISIBLE);

                final Call<ResponseVideoList> responseVideoListCall = ApiService.getService(SearchActivity.this)
                        .create(ApiInterface.class).getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(query));

                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        if(response.isSuccessful()){
                            //search.setQuery(query, true);
                            List<VIdeoItem> vIdeoItemList =new ArrayList<>();
                            vIdeoItemList = response.body().getResonse().getResult();
                            ListOfVideosAdapter listOfVideosAdapter=  new ListOfVideosAdapter
                                    (getApplicationContext(),AppUtils.initGlide(getApplicationContext()),vIdeoItemList);
                            listOfVideosAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(listOfVideosAdapter);
                            progressBarSearch.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                        progressBarSearch.setVisibility(View.VISIBLE);

                    }
                });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                return false;
            }
        });

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                progressBarSearch.setVisibility(View.VISIBLE);
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("Suggestions"));
                search.setQuery(txt, true);
                recyclerView.setVisibility(View.VISIBLE);
                final Call<ResponseVideoList> responseVideoListCall = ApiService.getService(SearchActivity.this)
                        .create(ApiInterface.class).getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(txt));

                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        if(response.isSuccessful()){
                            List<VIdeoItem> vIdeoItemList =new ArrayList<>();
                            vIdeoItemList = response.body().getResonse().getResult();
                            ListOfVideosAdapter listOfVideosAdapter=  new ListOfVideosAdapter
                                    (getApplicationContext(),AppUtils.initGlide(getApplicationContext()),vIdeoItemList);
                            listOfVideosAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(listOfVideosAdapter);
                            progressBarSearch.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                        progressBarSearch.setVisibility(View.VISIBLE);

                    }
                });
                return true;
            }
        });

        searchView.setSuggestionIcon(R.drawable.ic_history_white);
        handleIntent(getIntent());
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                    //  searchViewModel.insertText( new SearchHistory(query));
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                Call<ResponseTag> responseCommentCall =
                        ApiService.getService(SearchActivity.this).create(ApiInterface.class)
                                .getTagList(AppUtils.getJSonOBJForTAgList(newText));
                responseCommentCall.enqueue(new Callback<ResponseTag>() {

                    @Override
                    public void onResponse(Call<ResponseTag> call, Response<ResponseTag> response) {

                        if( response.isSuccessful()){
                          /*  final SearchSuggestionListAdapter searchSuggestionListAdapter
                                    = new SearchSuggestionListAdapter(SearchActivity.this,
                                    response.body().getResponse().getResult(),
                                    SearchSuggestionListAdapter.SEARCHTEXT_TYPE);
                            recyclerView.setAdapter(searchSuggestionListAdapter);*/
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTag> call, Throwable t) {

                    }
                });
                return true;
              }

            });


        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
                // Do something once the view is open.
            }

            @Override
            public void onSearchViewClosed() {
                // Do something once the view is closed.
                SearchActivity.super.onBackPressed();
            }
        });
        searchView.adjustTintAlpha(0.8f);

       /* searchViewModel.getAllSearchText().observe(this,new Observer<List<SearchHistory>>(){

            @Override
            public void onChanged(List<SearchHistory> listLiveData) {
                if(listLiveData!=null)
                {
                    for(SearchHistory ss:listLiveData)
                    {
                        strings.add(ss.getQueryString());
                    }
                    searchView.addSuggestions(strings);
                }
            }
        });*/

        searchView.setOnVoiceClickedListener(new MaterialSearchView.OnVoiceClickedListener() {
            @Override
            public void onVoiceClicked() {
                Toast.makeText(getApplicationContext(), "Voice clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.post(new Runnable() {
            @Override
            public void run() {
                searchView.openSearch();
            }
        });
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  ArrayList<VIdeoItem> mediaObjects = new ArrayList<VIdeoItem>(Arrays.asList(AppUtils.MEDIA_OBJECTS));
                searchViewModel.getAllSearchedVideo().observe(SearchActivity.this,new Observer<PagedList<VIdeoItem>>(){
                    @Override
                    public void onChanged(PagedList<VIdeoItem> vIdeoItems) {
                   /*      vIdeoItemPagedList = vIdeoItems;
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(null);
                   //     recyclerView.setAdapter(new ListOfVideosAdapter(SearchActivity.this, VideoWatchActivity.initGlide(SearchActivity.this),vIdeoItems));
                     ListOfVideosAdapter listOfVideosAdapter=  new ListOfVideosAdapter(getApplicationContext(),AppUtils.initGlide(SearchActivity.this));
                      listOfVideosAdapter.submitList(vIdeoItemPagedList);
        recyclerView.setAdapter(listOfVideosAdapter);*/
        }
        });
        }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private void handleIntent(Intent invtent) {

        if (Intent.ACTION_SEARCH.equals(invtent.getAction())) {
            String query = invtent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        final SearchView search2 =  (SearchView) menu.findItem(R.id.btnSearch).getActionView();
       /* MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }*/

        search2.setFocusable(true);
        search2.setQueryHint("Search videos...");
        final String[] from = new String[] {"Suggestions"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(SearchActivity.this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        search2.setSuggestionsAdapter(mAdapter);
        search2.setIconifiedByDefault(false);
        search2.setIconified(false);
        search2.onActionViewExpanded();
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
   /*     search2.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //menuItemsVisibility(true);
                if(getSupportActionBar()!=null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                return false;
            }
        });*/
   search2.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
       @Override
       public void onFocusChange(View v, boolean hasFocus) {
           search2.setFocusable(true);
           //if (hasFocus)
               showInputMethod(v.findFocus());
       }
   });
        // Associate searchable configuration with the SearchView
        search2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                recyclerView.setVisibility(View.VISIBLE);
                View view = SearchActivity.this.getCurrentFocus();
                if (view != null) {
                    try{
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    catch (NullPointerException e){e.printStackTrace();}

                }
                final Call<ResponseVideoList> responseVideoListCall = ApiService.getService(SearchActivity.this)
                        .create(ApiInterface.class).getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(query));

                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        if(response.isSuccessful()){
                            //search.setQuery(query, true);
                            List<VIdeoItem> vIdeoItemList =new ArrayList<>();
                            vIdeoItemList = response.body().getResonse().getResult();
                            ListOfVideosAdapter listOfVideosAdapter=  new ListOfVideosAdapter
                                    (getApplicationContext(),AppUtils.initGlide(getApplicationContext()),vIdeoItemList);
                            listOfVideosAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(listOfVideosAdapter);
                            progressBarSearch.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                        progressBarSearch.setVisibility(View.VISIBLE);

                    }
                });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                return false;
            }
        });

        search2.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                progressBarSearch.setVisibility(View.VISIBLE);
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("Suggestions"));
                search2.setQuery(txt, true);
                recyclerView.setVisibility(View.VISIBLE);
                final Call<ResponseVideoList> responseVideoListCall = ApiService.getService(SearchActivity.this)
                        .create(ApiInterface.class).getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(txt));

                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        if(response.isSuccessful()){
                            List<VIdeoItem> vIdeoItemList =new ArrayList<>();
                            vIdeoItemList = response.body().getResonse().getResult();
                            ListOfVideosAdapter listOfVideosAdapter=  new ListOfVideosAdapter
                                    (getApplicationContext(),AppUtils.initGlide(getApplicationContext()),vIdeoItemList);
                            listOfVideosAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(listOfVideosAdapter);
                            progressBarSearch.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                        progressBarSearch.setVisibility(View.VISIBLE);

                    }
                });
                return true;
            }
        });



        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here. It'll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // Open the search view on the menu item click.
                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // searchView.clearSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
   //     searchView.activityResumed();
       /* String[] arr = getResources().getStringArray(R.array.suggestions);
        searchView.addSuggestions(arr);*/

    }

    private void clearHistory() {
        searchView.clearHistory();
    }

    private void clearSuggestions() {
        searchView.clearSuggestions();
    }

    private void clearAll() {
        searchView.clearAll();
    }

    @Override
    protected void onStart() {
       // AppUtils.getResponseValidation(this);
        super.onStart();
    }
    private void populateAdapter(final String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "Suggestions" });
        Call<ResponseTag> responseCommentCall =
                ApiService.getService(SearchActivity.this).create(ApiInterface.class).getTagList(AppUtils.getJSonOBJForTAgList(query));
        responseCommentCall.enqueue(new Callback<ResponseTag>() {

            @Override
            public void onResponse(Call<ResponseTag> call, Response<ResponseTag> response) {

                if( response.isSuccessful()){
                    for (int i=0; i<response.body().getResponse().getResult().size(); i++) {
                        if (response.body().getResponse().getResult().get(i).toLowerCase().startsWith(query.toLowerCase()))
                            c.addRow(new Object[] {i, response.body().getResponse().getResult().get(i)});
                        mAdapter.changeCursor(c);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseTag> call, Throwable t) {

            }
        });

    }
   private void showInputMethod(View view) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, 0);
          }
    }
}
