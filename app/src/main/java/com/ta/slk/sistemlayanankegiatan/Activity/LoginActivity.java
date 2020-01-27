package com.ta.slk.sistemlayanankegiatan.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;
import com.dd.CircularProgressButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ta.slk.sistemlayanankegiatan.AdminContent;
import com.ta.slk.sistemlayanankegiatan.MainActivity;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.Users;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
    SimpleDialog progressDialog;
    TextInputEditText username,password;
    CircularProgressButton btn_login;
    Button btn_register;
    private static final int READ_STORAGE_PERMISSIONS_REQUEST = 0;
    private String TAG = "Pesan Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        galleryPermition();
        String msg = getIntent().getStringExtra("message");
        if(msg != null){
            if(msg.equals("logout")){
                SharedPreferences sf =  getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.clear().apply();
            }
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_login.setText("Login");
        btn_login.setIdleText("Login");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.setIndeterminateProgressMode(true);
                btn_login.setProgress(1);
                Login();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCheck();
            }
        });
    }

    private void registerCheck(){
        final ApiInterface service = ApiClient.getAuth().create(ApiInterface.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.popup_register,null);
        final TextInputEditText input = dialog.findViewById(R.id.register_nip);
        final Button button = dialog.findViewById(R.id.btn_check_nip);
        builder.setView(dialog).setTitle("Daftar Anggota").setMessage("Masukan Identitas");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Application.getProgress(v.getContext(), "Cek User").show();
                Call<GetUsers> call  = service.getLoginNip(input.getText().toString());
                call.enqueue(new Callback<GetUsers>() {
                    @Override
                    public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                        if(response.code()==200){
                            if(response.isSuccessful()){
                                if(response.body().getResult().isEmpty()){
                                    Toast.makeText(getApplicationContext(),"Data tidak ditemukan",Toast.LENGTH_LONG).show();
                                }else{
                                    if(response.body().getResult().get(0).getIdUser() != null){
                                        Toast.makeText(getApplicationContext(),"User Sudah Terdaftar",Toast.LENGTH_LONG).show();
                                    }else{
                                        Intent intent = new Intent(getApplicationContext(),Register.class);
                                        intent.putExtra("id_member",response.body().getResult().get(0).getIdMember());
                                        intent.putExtra("name",response.body().getResult().get(0).getName());
                                        intent.putExtra("action","Register");
                                        startActivity(intent);
                                    }
                                }

                            }
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetUsers> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Cek koneksi Internet", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void Login(){
        ApiInterface mApiInterface = ApiClient.getAuth().create(ApiInterface.class);
        final SharedPreferences sf = getSharedPreferences("device_token",MODE_PRIVATE);
//        Log.d(TAG, "Login: Login berjalan");
        Call<GetUsers> mLoginCall = mApiInterface.getUser(username.getText().toString(),
                password.getText().toString(),
                sf.getString("device_token",null));

        mLoginCall.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, retrofit2.Response<GetUsers> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        btn_login.setProgress(100);
                        btn_login.setCompleteText("Success");
                        btn_login.setProgress(0);
                        Session session = Application.getSession();
                        String id_user = response.body().getResult().get(0).getIdUser();
                        String username = response.body().getResult().get(0).getUsername();
                        String name = response.body().getResult().get(0).getName();
                        String photo = response.body().getResult().get(0).getPhotoProfile();
                        String id_member = response.body().getResult().get(0).getIdMember();
                        String email = response.body().getResult().get(0).getEmail();
                        String telp = response.body().getResult().get(0).getPhoneNumber();
                        String token = response.body().getToken();
                        String level = response.body().getResult().get(0).getLevel();

                        if(response.body().getResult().get(0).getActive().equals("0")){
                            finish();
                            Intent intent = new Intent(getApplicationContext(),Activation.class);
                            intent.putExtra("email",email);
                            intent.putExtra("id_user",id_user);
                            startActivity(intent);
                        }else {
                            session.saveCredentials(id_user, name, username, photo, id_member, email, telp, level, token);
                            if (session.isAdmin()) {
                                startActivity(new Intent(getApplicationContext(), AdminContent.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    }else{
                        btn_login.setProgress(-1);
                        btn_login.setProgress(0);
                        username.setError("invalid");
                        password.setError("invalid");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Cek Koneksi Internet", Toast.LENGTH_SHORT).show();
                btn_login.setProgress(-1);
                btn_login.setProgress(0);
            }
        });


    }
    private void galleryPermition(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSIONS_REQUEST);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_STORAGE_PERMISSIONS_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }
}
