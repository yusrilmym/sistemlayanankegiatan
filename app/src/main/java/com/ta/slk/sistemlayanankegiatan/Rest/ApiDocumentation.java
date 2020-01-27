package com.ta.slk.sistemlayanankegiatan.Rest;

import com.ta.slk.sistemlayanankegiatan.Model.GetDocumentation;
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

public interface ApiDocumentation {
    @FormUrlEncoded
    @POST("Rest_documentation/byid")
    Call<GetDocumentation> getDocumentation(
            @Field("id_activity") String id_activity
    );

    @GET("Rest_documentation/getDocumentationByMember")
    Call<GetDocumentation> getDocumentationByMember();

    @Multipart
    @POST("Rest_documentation/insert")
    Call<PostData> new_documentaton(
            @Part MultipartBody.Part file,
            @Part("id_activity") RequestBody id
    );

    @FormUrlEncoded
    @POST("Rest_documentation/delete")
    Call<PostData> del_docs(
            @Field("id_documentation") String id
    );
}
