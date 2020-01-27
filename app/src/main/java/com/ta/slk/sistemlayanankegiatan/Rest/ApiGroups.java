package com.ta.slk.sistemlayanankegiatan.Rest;

import com.ta.slk.sistemlayanankegiatan.Model.*;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiGroups {
    @FormUrlEncoded
    @POST("Rest_groups/groupmember")
    Call<GetUsers> getMemberGroup(
            @Field("id_group") String id_group
    );

    @FormUrlEncoded
    @POST("Rest_groups/extigroups")
    Call<PostData> exitGroup(
            @Field("id_group") String id_group
    );

    @FormUrlEncoded
    @POST("Rest_groups/invitegroup")
    Call<PostData> inviteGroup(
            @Field("id_member[]") ArrayList<Integer> id,
            @Field("id_group") String id_group
    );

    @FormUrlEncoded
    @POST("Rest_groups/invitation")
    Call<PostData> sendGroup(
            @Field("id_group[]") ArrayList<Integer> id,
            @Field("id_activity") String id_activity
    );

    @GET("Rest_groups/groups")
    Call<GetGroups> getGroups();

    @Multipart
    @POST("Rest_groups/newgroup")
    Call<PostData> new_group(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description
    );

    @Multipart
    @POST("Rest_groups/editgroup")
    Call<PostData> update_group(
            @Part MultipartBody.Part file,
            @Part("id_group") RequestBody id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description
    );

    @FormUrlEncoded
    @POST("Rest_groups/delgroup")
    Call<PostData> del_group(
            @Field("id_group") String id
    );
}
