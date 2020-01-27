package com.ta.slk.sistemlayanankegiatan.Rest;
import com.ta.slk.sistemlayanankegiatan.Model.*;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("Rest_slkg/activities")
    Call<GetActivities> getActivities();

    @GET("Rest_groups/groups")
    Call<GetGroups> getGroups();

    @GET("Rest_slkg/users")
    Call<GetUsers> getUsers();

    @FormUrlEncoded
    @POST("Rest_slkg/sendinvitation")
    Call<PostData> sendInvitation(
            @Field("id[]") ArrayList<Integer> id,
            @Field("id_activity") String id_activity,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("Rest_slkg/allbyid")
    Call<GetInvtActivities> getInvitationActivity(
            @Field("user_id") String user_id,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("Rest_slkg/allbyid")
    Call<GetGroups> getGroupsById(
            @Field("user_id") String user_id,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("Rest_slkg/allbyid")
    Call<GetActivities> getActiviesById(
            @Field("user_id") String user_id,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<GetUsers> getUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("device_token") String token
    );

    @FormUrlEncoded
    @POST("users/loginnip")
    Call<GetUsers> getLoginNip(
            @Field("nip") String nip
    );

    @FormUrlEncoded
    @POST("Rest_slkg/invitation")
    Call<GetInvtActivities> getActivityStatus(
            @Field("id_member") String id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("Rest_slkg/invitationupdate")
    Call<PostData> putInvitationStatus(
            @Field("id_activity") String id_activity,
            @Field("id_member") String id_member,
            @Field("status") String status,
            @Field("message") String message
    );

    @Multipart
    @POST("Rest_slkg/activity")
    Call<PostData> postActivity(
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part docs,
            @Part("name") RequestBody name,
            @Part("location") RequestBody location,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part("comment_key") RequestBody key
    );

    @Multipart
    @POST("Rest_activities/update")
    Call<PostData> updateActivity(
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part docs,
            @Part("id_activity") RequestBody id,
            @Part("name") RequestBody name,
            @Part("location") RequestBody location,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description
    );

    @FormUrlEncoded
    @POST("Rest_activities/delete")
    Call<PostData> deleteActivities(
            @Field("id_activity") String id
    );
}
