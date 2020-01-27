package com.ta.slk.sistemlayanankegiatan.Rest;

import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiMembers {
    @FormUrlEncoded
    @POST("Rest_members/addnip")
    Call<PostData> addNip(
            @Field("nip") String nip,
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("Rest_members/delete")
    Call<PostData> deleteNip(
            @Field("id_member") String id_member
    );

    @Multipart
    @POST("Rest_members/update")
    Call<PostData> editMember(
            @Part MultipartBody.Part file,
            @Part("id_user") RequestBody id_user,
            @Part("id_member") RequestBody id_member,
            @Part("name") RequestBody name,
            @Part("password") RequestBody password,
            @Part("address") RequestBody address,
            @Part("telp") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("action") RequestBody action
    );

    @Multipart
    @POST("Rest_members/update")
    Call<PostData> registerMember(
            @Part MultipartBody.Part file,
            @Part("id_member") RequestBody id_member,
            @Part("name") RequestBody name,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("address") RequestBody address,
            @Part("telp") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("action") RequestBody action
    );

    @FormUrlEncoded
    @POST("Rest_members/rules")
    Call<PostData> changeRule(
            @Field("id_member") String id_member,
            @Field("level") String level
    );

    @GET("Rest_members/description")
    Call<PostData> description();

    @GET("Rest_members/profile")
    Call<GetUsers> profile();

    @FormUrlEncoded
    @POST("Rest_members/byactivities")
    Call<GetUsers> getUserActivities(
            @Field("id_activity") String id,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("Users/checkcode")
    Call<PostData> checkCode(
            @Field("id_user") String id,
            @Field("code") String code
    );
}
