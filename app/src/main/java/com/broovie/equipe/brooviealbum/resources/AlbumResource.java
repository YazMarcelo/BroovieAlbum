package com.broovie.equipe.brooviealbum.resources;

import com.broovie.equipe.brooviealbum.model.Album;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by bruno on 28/09/18.
 */


public interface AlbumResource {

    @GET("albums")
    Call<List<Album>> get();

    @GET("albums/{id}")
    Call<Album> getAlbum(@Path("id") String id);

    @POST("albums")
    Call<Album> post(@Body Album album);

    @PUT("albums/{id}")
    Call<Album> put(@Path("id") String id,@Body Album album);

    @DELETE("albums/{id}")
    Call<Void> delete(@Path("id") String id);
}
