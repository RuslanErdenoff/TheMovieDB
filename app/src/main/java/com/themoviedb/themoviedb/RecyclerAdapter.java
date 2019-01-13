package com.themoviedb.themoviedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.themoviedb.themoviedb.db.DBHelper;
import com.themoviedb.themoviedb.db.DBReaderContract;
import com.themoviedb.themoviedb.models.movies.Movie;
import com.themoviedb.themoviedb.models.movies.MoviesResponce;
import com.themoviedb.themoviedb.utils.ServerConnection;

import java.util.Map;
import java.util.Random;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static com.themoviedb.themoviedb.MovieActivity.MOVIE_ID_TAG;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private MoviesResponce moviesResponce;
    private Context context;
    private int[] favorites;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }
    }

    public RecyclerAdapter(MoviesResponce moviesResponce,Context context){
        this.context = context;
        this.moviesResponce = moviesResponce;
        favorites = loadFavorites(this.moviesResponce);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        TextView textView = viewHolder.cardView.findViewById(R.id.text_view_title);
        final Movie movie = moviesResponce.getResults().get(i);
        textView.setText(movie.getTitle());
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        ImageView backdropImage = viewHolder.cardView.findViewById(R.id.image_view_backdrop);
        ImageView poster = viewHolder.cardView.findViewById(R.id.image_view_poster);

        Picasso.get().load(ServerConnection.IMAGE_URL+movie.getBackdropPath()).fit().into(backdropImage);
        Picasso.get().load(ServerConnection.IMAGE_URL+movie.getPosterPath()).into(poster);
        viewHolder.cardView.setCardBackgroundColor(color);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.cardView.getContext(),MovieActivity.class);
                intent.putExtra(MOVIE_ID_TAG,movie.getId());
                viewHolder.cardView.getContext().startActivity(intent);
            }
        });

        final AppCompatCheckBox checkBox = viewHolder.cardView.findViewById(R.id.checkbox_favorite);
        checkBox.setChecked(false);
        for(int y = 0 ; y < favorites.length;y++){
            if(favorites[y] == movie.getId()){
                checkBox.setChecked(true);
                Log.wtf("Match",favorites[y]+" = "+movie.getId());
            }
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    new AddFavorite().executeOnExecutor(THREAD_POOL_EXECUTOR, ((int) movie.getId()),0);
                }else{
                    new AddFavorite().executeOnExecutor(THREAD_POOL_EXECUTOR, ((int) movie.getId()),1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesResponce.getResults().size();
    }

    private int[] loadFavorites(MoviesResponce moviesResponce){
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.query(DBReaderContract.DBFavoriteEntry.TABLE_NAME,new String[]{DBReaderContract.DBFavoriteEntry.COLUMN_NAME_ID},
                null,null,null,null,null);
        int[] favorites = new int[cursor.getCount()];
        if(cursor.moveToFirst()){
            for(int i = 0; i < cursor.getCount();i++){
                favorites[i] = cursor.getInt(cursor.getColumnIndexOrThrow(DBReaderContract.DBFavoriteEntry.COLUMN_NAME_ID));
                cursor.moveToNext();
                Log.wtf("Favorite",""+favorites[i]);
            }
        }
       return favorites;
    }

    private class AddFavorite extends AsyncTask<Integer,Void,Boolean> {


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Log.wtf("Insert","true");
                //Toast.makeText(context,R.string.db_insert,Toast.LENGTH_SHORT).show();
            }else{
                Log.wtf("NotUpdated","true");
                //Toast.makeText(context,R.string.db_delete,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            int movie_id = integers[0];
            int saveInt = integers[1];
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            boolean updated = false;
            boolean currentUpdate = false;
            switch (saveInt){
                case 0:
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBReaderContract.DBFavoriteEntry.COLUMN_NAME_ID,movie_id);
                    if(db.insert(DBReaderContract.DBFavoriteEntry.TABLE_NAME,null,contentValues)>0){
                        updated = true;
                    }
                    break;
                case 1:
                    if(db.delete(DBReaderContract.DBFavoriteEntry.TABLE_NAME,DBReaderContract.DBFavoriteEntry.COLUMN_NAME_ID+"=?",new String[]{String.valueOf(movie_id)})>0){
                        updated = true;
                    }
                    break;
            }
            return updated;
        }
    }
}
