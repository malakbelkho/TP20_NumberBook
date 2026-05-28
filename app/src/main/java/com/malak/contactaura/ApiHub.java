package com.malak.contactaura;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHub {

    private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";
    private static Retrofit retrofitInstance;

    public static Retrofit getRetrofit() {
        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofitInstance;
    }
}
