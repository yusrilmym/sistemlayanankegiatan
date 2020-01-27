package com.ta.slk.sistemlayanankegiatan.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.ta.slk.sistemlayanankegiatan.AdminContent;
import com.ta.slk.sistemlayanankegiatan.MainActivity;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.R;

public class SplashScreen extends AppCompatActivity {
    Session session;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                session = Application.getSession();
                if(preferences.getString("token",null)!=null){
                    if (session.isAdmin()){
                        startActivity(new Intent(SplashScreen.this, AdminContent.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                }else{
                    SharedPreferences sf = getSharedPreferences("guide",MODE_PRIVATE);
                    if(sf.getString("user_guide",null) == null){
                        startActivity(new Intent(SplashScreen.this, Guide.class));
                    }else{
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    }

                }
            }
        },1000);
    }
}
