package com.example.woof.WoofBackend;

import com.example.woof.Model.NewOwner;
import com.example.woof.Model.Owner;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OwnerApi {

    @POST("owner")
    Call<Owner> createOwner(@Body NewOwner newOwner);

    @GET("owner/{email}")
    Call<Owner> findOwner(
            @Path("email") String mail
    );

    @PUT("owner/{email}/addDog")
    Call<Void> addDog(
            @Path("email") String email,
            @Body String dogName
    );

 /*   @GET("owner/getOwnerDogs/{ownerEmail}")
    Call<List<Dog>>getAllDogsByOwner(
            @Path("ownerEmail") String ownerEmail
    );*/

    @GET("owner/search/{ownerMail}")
    Call<List<Owner>> searchOwner(
            @Path("ownerMail") String ownerMail
    );

}
