package com.themoviedb.themoviedb;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.themoviedb.themoviedb.db.DBHelper;
import com.themoviedb.themoviedb.db.DBReaderContract;
import com.themoviedb.themoviedb.models.movies.MoviesResponce;
import com.themoviedb.themoviedb.utils.DataService;
import com.themoviedb.themoviedb.utils.ServerConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private android.support.v7.widget.SearchView searchView;
    private DataService dataService;
    private static final String LIST_POSITION = "list_position";
    private static final String PAGE_POSITION = "page_position";
    private RecyclerAdapter recyclerAdapter;
    boolean isScrolling;
    int currentItems,totalItems,scrollout,currentPage,firstScroll;
    private LinearLayoutManager manager;
    private MoviesResponce moviesResponce;
    private ProgressBar progressBar;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper.createInstance(this);
        progressBar = findViewById(R.id.progress_circular);
        currentPage = 1;
        if(savedInstanceState!=null){
            currentPage = savedInstanceState.getInt(PAGE_POSITION);
        }
        recyclerView = findViewById(R.id.recycler);
        searchView = findViewById(R.id.searchView);
        manager = new LinearLayoutManager(this);
        emptyText = findViewById(R.id.emptyText);
        emptyText.setVisibility(View.GONE);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollout = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollout == totalItems)){
                    isScrolling = false;
                    createNewData(false);
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        dataService = ServerConnection.getDataServiceInstance();
        initMovies();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
                Map<String,String> map = ServerConnection.getDefaultQueryOptions();
                map.put("page",String.valueOf(currentPage));
                map.put("query",query);
                Call<MoviesResponce> searchCall = dataService.getFoundedMovies(map);
                searchCall.enqueue(new Callback<MoviesResponce>() {
                    @Override
                    public void onResponse(Call<MoviesResponce> call, Response<MoviesResponce> response) {
                        if(response.isSuccessful()){
                            if(response.body().getResults().size()!=0){
                                moviesResponce = response.body();
                                recyclerAdapter = new RecyclerAdapter(moviesResponce,MainActivity.this);
                                recyclerView.swapAdapter(recyclerAdapter,true);
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyText.setVisibility(View.GONE);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesResponce> call, Throwable t) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.trim().isEmpty()){
                    initMovies();
                }
                return false;
            }
        });
    }
    private void initMovies(){
        Map<String,String> queryOptions = ServerConnection.getDefaultQueryOptions();
        queryOptions.put("page",String.valueOf(currentPage));
        Call<MoviesResponce> call = dataService.getMovies(queryOptions);

        call.enqueue(new Callback<MoviesResponce>() {
            @Override
            public void onResponse(Call<MoviesResponce> call, Response<MoviesResponce> response) {
                if(response.isSuccessful()){
                    emptyText.setVisibility(View.GONE);
                    moviesResponce = response.body();
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerAdapter = new RecyclerAdapter(moviesResponce,MainActivity.this);
                    recyclerView.swapAdapter(recyclerAdapter,true);
                    progressBar.setVisibility(View.GONE);
                    firstScroll = manager.findFirstVisibleItemPosition();
                }else{
                    try {
                        Log.wtf("OnResponce",response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponce> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(manager);
        }else{
            manager = new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(manager);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // Log.wtf("GETCHILDID",recyclerView.getLayoutManager().findFi.onSaveInstanceState()+"");
        outState.putInt(PAGE_POSITION,currentPage);
    }
    private void createNewData(final Boolean isUp){
        if(isUp&& currentPage!=1){
            currentPage--;
        }else{
            currentPage++;
        }
        Map<String,String> queryOptions = ServerConnection.getDefaultQueryOptions();
        queryOptions.put("page",String.valueOf(currentPage));
        Call<MoviesResponce> call = dataService.getMovies(queryOptions);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MoviesResponce>() {
            @Override
            public void onResponse(Call<MoviesResponce> call, Response<MoviesResponce> response) {
                if(response.isSuccessful()){
                    //moviesResponce = response.body();
                    //recyclerAdapter.notifyItemChanged(moviesResponce.getResults().size()-1);
                    //recyclerAdapter.notifyDataSetChanged();
                    //progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerAdapter = new RecyclerAdapter(response.body(),MainActivity.this);
                    recyclerView.swapAdapter(recyclerAdapter,true);
                    progressBar.setVisibility(View.GONE);
                }else{
                    try {
                        Log.wtf("OnResponce",response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponce> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
