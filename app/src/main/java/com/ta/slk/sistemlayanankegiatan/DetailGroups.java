package com.ta.slk.sistemlayanankegiatan;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Activity.MyGroups;
import com.ta.slk.sistemlayanankegiatan.Adapter.MembersAdapter;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.Model.Users;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiGroups;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailGroups extends AppCompatActivity {
    TextView description, title, admin;
    Button action;
    CircleImageView imgGroup;
    CardView cardOption;
    RecyclerView recyclerView;
    ImageButton back;
    ImageView main, iconMember, iconDesc;
    Session session;
    int newColor;
    Drawable oldColor;
    Runnable runnable;
    ApiGroups apiGroups;
    List<Users> usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_groups);
        main = findViewById(R.id.back_detail);
        iconMember = findViewById(R.id.icon_name);
        iconDesc = findViewById(R.id.icon_desc);

        apiGroups = ApiClient.getClient().create(ApiGroups.class);
        title = findViewById(R.id.group_title);
        admin = findViewById(R.id.group_admin);
        imgGroup  = findViewById(R.id.img_group);
        description = findViewById(R.id.text_description);
        action  = findViewById(R.id.action_group);
        back = findViewById(R.id.btn_back);
        cardOption = findViewById(R.id.card_option);
        session = Application.getSession();

        if(session.isAdmin()){
            cardOption.setVisibility(View.GONE);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        admin.setText("Admin :"+getIntent().getStringExtra("admin"));
        title.setText(getIntent().getStringExtra("name"));
        description.setText(getIntent().getStringExtra("description"));
        recyclerView = findViewById(R.id.recycler_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Glide.with(getApplicationContext())
                .load(ApiClient.BASE_URL+"uploads/groups/"+getIntent().getStringExtra("picture"))
                .into(imgGroup);
        loadData();
        imgGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(v.getContext(),R.style.ZoomImageDialog);
                dialog.setContentView(R.layout.zoom_image);
                final ImageView imageView = dialog.findViewById(R.id.zoom_image);
                try {
                    Glide.with(v.getContext()).load(ApiClient.BASE_URL+"uploads/groups/"+getIntent().getStringExtra("picture"))
                            .into(imageView);
                }catch (Exception e){

                }

                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Peringatan").setMessage("Ingin keluar dari grup "+getIntent().getStringExtra("name")+" ?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExit();
                        finish();
                    }
                }).setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        oldColor = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        final Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                newColor = ((int) (Math.random() * 16777215)) | (0xFF << 24);
                Drawable colorDrawable = new ColorDrawable(newColor);
                Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldColor, layerDrawable});
                main.setBackground(transitionDrawable);
                iconMember.setImageTintList(ColorStateList.valueOf(newColor));
                iconDesc.setImageTintList(ColorStateList.valueOf(newColor));
                main.invalidate();
                iconMember.invalidate();
                iconDesc.invalidate();
                transitionDrawable.startTransition(500);
                oldColor = new ColorDrawable(newColor);
                handler.postDelayed(runnable, 8000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void loadData() {
        Call<GetUsers> call = apiGroups.getMemberGroup(getIntent().getStringExtra("id_group"));
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                usersList = response.body().getResult();
                RecyclerView.Adapter adapter = new MembersAdapter(usersList,getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }

    private void doExit(){
        Call<PostData> call = apiGroups.exitGroup(getIntent().getStringExtra("id_group"));
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code() == 200){
                    if(response.body().getStatus().equals("success")){
                        Toast.makeText(getApplicationContext(),"Berhasil Keluar grup",Toast.LENGTH_SHORT).show();
                        finish();
                        MyGroups.myGroups.loadData();
                    }
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {

            }
        });
    }
}
