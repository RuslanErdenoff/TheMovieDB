package com.themoviedb.themoviedb.utils;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerConnection {
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static DataService dataService;
    private static Retrofit retrofitInstance;

    public static DataService getDataServiceInstance(){
        if(dataService!=null){
            return dataService;
        }else{
            retrofitInstance  = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.client(getUnsafeOkHttpClient().build())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                    .build();
            dataService = retrofitInstance.create(DataService.class);
            return dataService;
        }
    }

    public static Map<String,String> getDefaultQueryOptions(){
        Map<String,String> defaultOptions = new HashMap<>();
        defaultOptions.put("api_key","c12c8147ba8c7af8152e906b9d25707f");
        defaultOptions.put("language","ru");
        return defaultOptions;
    }

}
