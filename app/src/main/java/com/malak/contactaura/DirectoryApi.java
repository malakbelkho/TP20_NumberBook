package com.malak.contactaura;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DirectoryApi {

    @POST("insertContact.php")
    Call<ServerReply> uploadPerson(@Body PhonePerson person);

    @GET("getAllContacts.php")
    Call<List<PhonePerson>> loadRemoteDirectory();

    @GET("searchContact.php")
    Call<List<PhonePerson>> searchRemotePeople(@Query("keyword") String keyword);
}
