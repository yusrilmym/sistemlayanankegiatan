package com.ta.slk.sistemlayanankegiatan.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiMembers;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUser extends AppCompatActivity {
    TextView docs, act,inv,name,pr_fname,pr_name,pr_telp,pr_level,pr_username;
    CircleImageView img_profile;
    Button edit;
    String[] description;
    ApiMembers service;
    SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        docs = findViewById(R.id.pr_docs);
        act = findViewById(R.id.pr_act);
        inv =findViewById(R.id.pr_pending);
        pr_fname = findViewById(R.id.pr_fname);
        pr_name = findViewById(R.id.pr_name);
        pr_telp = findViewById(R.id.pr_telp);
        pr_level = findViewById(R.id.pr_level);
        pr_username = findViewById(R.id.pr_username);
        img_profile = findViewById(R.id.pr_photo);
        edit = findViewById(R.id.btn_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                intent.putExtra("id_member",preferences.getString("id_member",null));
                intent.putExtra("action","Update");
                startActivity(intent);
            }
        });
        initComponents();
    }

    private void initComponents() {
        preferences = getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        pr_fname.setText(preferences.getString("name",null));
        pr_name.setText(preferences.getString("email",null));
        pr_telp.setText(preferences.getString("telp",null));
        if(preferences.getString("level",null).equals("1")){
            pr_level.setText("Admin");
        }else{
            pr_level.setText("User");
        }
        pr_username.setText(preferences.getString("username",null));
        Glide.with(getApplicationContext())
                .load(ApiClient.BASE_URL+"/uploads/members/"+preferences.getString("photo",null))
                .into(img_profile);

        service = ApiClient.getClient().create(ApiMembers.class);
        Call<PostData> call = service.description();
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    description = response.body().getMessage().split("_");
                    docs.setText(description[2]);
                    inv.setText(description[0]);
                    act.setText(description[1]);
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
