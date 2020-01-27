package com.ta.slk.sistemlayanankegiatan.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dd.CircularProgressButton;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.FileUtil;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.Model.Users;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiMembers;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    String id_member, name, action, imagePath;
    TextInputEditText txt_name,txt_username,txt_password,txt_address,txt_email,txt_phone;
    ImageButton btn_back;
    CircleImageView upload;
    CircularProgressButton button;
    File originalFile,fileCompressed;
    public Register register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();
        if(action.equals("Update")){
            componentUpdate();
        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                Intent intentChoice = Intent.createChooser(
                        intent,"Pilih Gambar untuk di upload");
                startActivityForResult(intentChoice,1);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setIndeterminateProgressMode(true);
                button.setProgress(1);
                doUpdate();
            }
        });
    }

    private void initComponents() {
        id_member = getIntent().getStringExtra("id_member");
        name = getIntent().getStringExtra("name");
        action = getIntent().getStringExtra("action");
        btn_back = findViewById(R.id.btn_back);
        txt_name = findViewById(R.id.re_name);
        txt_username = findViewById(R.id.re_username);
        txt_password = findViewById(R.id.re_password);
        txt_address = findViewById(R.id.re_address);
        txt_email= findViewById(R.id.re_email);
        txt_phone = findViewById(R.id.re_call);
        upload = findViewById(R.id.btn_upload);
        button = findViewById(R.id.btn_save);
        register = this;

        if(action.equals("Register")){
            txt_name.setText(name);
        }

        button.setText("SUBMIT");
        button.setIdleText("SUBMIT");
    }

    private void doUpdate(){
        if(TextUtils.isEmpty(txt_name.getText().toString())){
            txt_name.setError("nama tidak valid");
            errorButton();
        }else if(TextUtils.isEmpty(txt_address.getText().toString())){
            txt_address.setError("alamat tidak valid");
            errorButton();
        }else if(TextUtils.isEmpty(txt_email.getText().toString())){
            txt_email.setError("email tidak valid");
            errorButton();
        }else if(TextUtils.isEmpty(txt_phone.getText().toString())){
            txt_phone.setError("no telp tidak valid");
            errorButton();
        }else{
            if(action.equals("Register")){
                if (TextUtils.isEmpty(txt_username.getText().toString())) {
                    txt_username.setError("username tidak valid");
                    errorButton();
                } else if (!txt_username.toString().matches("[a-zA-Z]*")) {
                    txt_username.setError("username tidak valid");
                    errorButton();
                } else if (TextUtils.isEmpty(txt_password.getText().toString())) {
                    txt_password.setError("");
                    errorButton();
                }else{
                    if(imagePath == null){
                        Toast.makeText(getApplicationContext(),"Gambar belum dipilih",Toast.LENGTH_SHORT).show();
                        errorButton();
                    }
                }
            }

            MultipartBody.Part body = null;
                if (imagePath  != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileCompressed);
                    body = MultipartBody.Part.createFormData("picture", fileCompressed.getName(),
                            requestFile);
                }
                RequestBody reqName = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_name.getText().toString())) ? "" :txt_name.getText().toString());
                RequestBody reqId = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        id_member);
                RequestBody reqUsername = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_username.getText().toString())) ? "" : txt_username.getText().toString().toLowerCase());
                RequestBody reqPassword = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_password.getText().toString())) ? "" :txt_password.getText().toString());
                RequestBody reqAddress = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_address.getText().toString())) ? "" :txt_address.getText().toString());
                RequestBody reqContact = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_phone.getText().toString())) ? "" :txt_phone.getText().toString());
                RequestBody reqEmail = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (TextUtils.isEmpty(txt_email.getText().toString())) ? "" :txt_email.getText().toString());
                RequestBody Reqaction = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        action);

                ApiMembers members = ApiClient.getAuth().create(ApiMembers.class);
            Call<PostData> call;
            call = members.registerMember(body, reqId, reqName, reqUsername, reqPassword, reqAddress, reqContact, reqEmail, Reqaction);
            if (action.equals("Update")) {
                Session session = Application.getSession();
                RequestBody idUser = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        session.getidUser());
                call = members.editMember(body, idUser, reqId, reqName, reqPassword, reqAddress, reqContact, reqEmail, Reqaction);
            }

            call.enqueue(new Callback<PostData>() {
                    @Override
                    public void onResponse(Call<PostData> call, Response<PostData> response) {
                        if (response.body().getStatus().equals("success")) {
                                button.setProgress(100);
                                finish();
                            if (action.equals("Update")) {
                                Toast.makeText(getApplicationContext(), "Update sukses,Silahkan login kembali", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.putExtra("message", "logout");
                                startActivity(intent);
                            }
                            }else{
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                errorButton();
                            }
                    }

                    @Override
                    public void onFailure(Call<PostData> call, Throwable t) {
                        errorButton();
                    }
                });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                try {
                    originalFile = FileUtil.from(this,data.getData());
                    fileCompressed = new Compressor(this)
                            .setMaxHeight(480).setMaxWidth(480).setQuality(75)
                            .compressToFile(originalFile);
                }catch (Exception e){

                }
//                Picasso.with(getApplicationContext()).load(new File(imagePath)).fit().into(mImageView);
                Glide.with(getApplicationContext()).load(new File(imagePath)).into(upload);
                cursor.close();
            } else {
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void errorButton(){
        button.setProgress(100);
        button.setProgress(-1);
        button.setErrorText("Invalid");
        button.setProgress(0);
        button.setIndeterminateProgressMode(false);
    }

    private void componentUpdate(){
        ApiMembers members = ApiClient.getClient().create(ApiMembers.class);
        Call<GetUsers> call = members.profile();
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        Session session = Application.getSession();
                        txt_username.setText(session.getUsername());
                        txt_username.setEnabled(false);
                        txt_name.setText(response.body().getResult().get(0).getName());
                        txt_address.setText(response.body().getResult().get(0).getAddress());
                        txt_email.setText(response.body().getResult().get(0).getEmail());
                        txt_phone.setText(response.body().getResult().get(0).getPhoneNumber());
                        Glide.with(getApplicationContext()).load(ApiClient.BASE_URL+"/uploads/members/"+
                                response.body().getResult().get(0).getPhotoProfile()).into(upload);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });

    }
}
