package com.ta.slk.sistemlayanankegiatan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ta.slk.sistemlayanankegiatan.Activity.Guide;
import com.ta.slk.sistemlayanankegiatan.Activity.MyGroups;
import com.ta.slk.sistemlayanankegiatan.Activity.UserActivity;
import com.ta.slk.sistemlayanankegiatan.Adapter.DoctAdapter;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import com.ta.slk.sistemlayanankegiatan.Rest.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Menu implements View.OnClickListener {
    Session session;
    ApiMembers service;
    TextView invitation, activities, documentation, groups;
    LinearLayout invitation_ly, activity_ly, group_ly, gallery_ly, help_ly;
    String[] dataServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = Application.getSession();
        if (session.checkSavedCredetential()) {
            if (session.isAdmin()) {
                finish();
                session.logout(0);
            } else {
                getMenu();
                initComponents();
                getData();
            }
        } else {
            finish();
            session.logout(0);
        }
    }

    private void getData() {
        Call<PostData> call = service.description();
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        dataServer = response.body().getMessage().split("_");
                        invitation.setText(dataServer[0]+" Undangan belum diterima");
                        activities.setText(dataServer[1]+" Kegiatan");
                        documentation.setText(dataServer[2]+" upload dokumentasi");
                        groups.setText(dataServer[3]+" Group");
                    }
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {

            }
        });
    }

    private void initComponents() {
        // Instansiasi View
        invitation = findViewById(R.id.main_inv);
        activities = findViewById(R.id.main_act);
        groups     = findViewById(R.id.main_groups);
        documentation =findViewById(R.id.main_docs);
        activity_ly = findViewById(R.id.activity_main);
        gallery_ly = findViewById(R.id.gallery_main);
        group_ly = findViewById(R.id.group_main);
        help_ly = findViewById(R.id.help_main);
        invitation_ly = findViewById(R.id.invitation_main);
        // Instansiasi Service
        service = ApiClient.getClient().create(ApiMembers.class);
        // Instansiasi Click Listener
        activity_ly.setOnClickListener(this);
        gallery_ly.setOnClickListener(this);
        group_ly.setOnClickListener(this);
        invitation_ly.setOnClickListener(this);
        help_ly.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_main:
                showGallery();
                break;
            case R.id.group_main:
                startActivity(new Intent(v.getContext(), MyGroups.class));
                break;
            case R.id.activity_main:
                startActivity(new Intent(v.getContext(), UserActivity.class));
                break;
            case R.id.invitation_main:
                startActivity(new Intent(v.getContext(), UserInvitation.class));
                break;
            case R.id.help_main:
                startActivity(new Intent(v.getContext(), Guide.class).putExtra("user_guide", "user"));
                break;
        }
    }

    private void showGallery() {
        LayoutInflater inflater = getLayoutInflater();
        final View gallery = inflater.inflate(R.layout.fragment_documentation, null);
        final SwipeRefreshLayout refreshLayout = gallery.findViewById(R.id.swipe_refresh);
        final TextView status = gallery.findViewById(R.id.txt_status);
        final ProgressBar progressBar = gallery.findViewById(R.id.progress_bar);
        final RecyclerView recyclerView = gallery.findViewById(R.id.recycler_content);
        status.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new GridLayoutManager(gallery.getContext(), 3));
        ApiDocumentation apiDocumentation = ApiClient.getClient().create(ApiDocumentation.class);
        apiDocumentation.getDocumentationByMember().enqueue(new Callback<GetDocumentation>() {
            @Override
            public void onResponse(Call<GetDocumentation> call, Response<GetDocumentation> response) {
                if (response.code() == 200) {
                    recyclerView.setAdapter(new DoctAdapter(gallery.getContext(), response.body().getResult()));
                }
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GetDocumentation> call, Throwable t) {

            }
        });
        doShow(gallery);
    }

    private void doShow(View gallery) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gallery");
        builder.setMessage("Daftar gambar yang telah di upload");
        builder.setView(gallery);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_downUp;
        dialog.show();
    }
}
