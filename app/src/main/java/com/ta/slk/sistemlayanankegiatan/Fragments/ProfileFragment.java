package com.ta.slk.sistemlayanankegiatan.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Activity.Register;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiMembers;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    TextView docs, act,inv,name,pr_fname,pr_name,pr_telp,pr_level,pr_username;
    CircleImageView img_profile;
    Button edit;
    String[] description;
    ApiMembers service;
    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        docs = view.findViewById(R.id.pr_docs);
        act = view.findViewById(R.id.pr_act);
        inv =view.findViewById(R.id.pr_pending);
        pr_fname = view.findViewById(R.id.pr_fname);
        pr_name = view.findViewById(R.id.pr_name);
        pr_telp = view.findViewById(R.id.pr_telp);
        pr_level = view.findViewById(R.id.pr_level);
        pr_username = view.findViewById(R.id.pr_username);
        img_profile = view.findViewById(R.id.pr_photo);
        edit = view.findViewById(R.id.btn_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Register.class);
                intent.putExtra("id_member",preferences.getString("id_member",null));
                intent.putExtra("action","Update");
                startActivity(intent);
            }
        });

        initComponents();
        return view;
    }

    private void initComponents() {
        preferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        pr_fname.setText(preferences.getString("name",null));
        pr_name.setText(preferences.getString("email",null));
        pr_telp.setText(preferences.getString("telp",null));
        if(preferences.getString("level",null).equals("1")){
            pr_level.setText("Admin");
        }else{
            pr_level.setText("User");
        }
        pr_username.setText(preferences.getString("username",null));
        Glide.with(getContext())
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
                Toast.makeText(getContext(),"Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
