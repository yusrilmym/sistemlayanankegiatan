package com.ta.slk.sistemlayanankegiatan.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.ta.slk.sistemlayanankegiatan.Activity.UserActivity;
import com.ta.slk.sistemlayanankegiatan.Adapter.MembersAdapter;
import com.ta.slk.sistemlayanankegiatan.DetailActivity;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.Model.Users;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiGroups;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiMembers;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {
    CardView file, location, layAccept, layPending, layRejected;
    TextView detail, c1, c2, c3;
    Bundle bundle;
    CardView cardOption;
    Button exit;
    Session session;
    RecyclerView recyclerView, recyclerView2, recyclerView3;
    RecyclerView.Adapter adapter, adapter2, adapter3;
    List<Users> usersList, usersList2, usersList3;
    ApiMembers service;
    public DetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_detail,container,false);
        bundle = getArguments();
        initComponents(view);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
        detail.setText(bundle.getString("description"));
        service = ApiClient.getClient().create(ApiMembers.class);
        loadDataMembers();
        loadDataPending();
        loadDataRejected();
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+bundle.getString("place"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bundle.getString("file").equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(ApiClient.BASE_URL + "uploads/" + bundle.getString("file")), "text/html");
                    startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "File tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView3.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pesan").setMessage(usersList3.get(position).getMessage());
                builder.setIcon(R.drawable.round_announcement_black_18dp).create().show();
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));
        if(session.isAdmin()){
            cardOption.setVisibility(View.GONE);
            layPending.setVisibility(View.VISIBLE);
            layRejected.setVisibility(View.VISIBLE);
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Peringatan").setMessage("Yaking ingin keluar dari kegiatan ? ").setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMessageDialog("rejected",bundle.getString("id_activity"), v);
                        dialog.dismiss();
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
        return view;
    }

    private void initComponents(View view) {
        recyclerView = view.findViewById(R.id.recycler_member);
        recyclerView2 = view.findViewById(R.id.recycler_pending);
        recyclerView3 = view.findViewById(R.id.recycler_rejected);
        layAccept = view.findViewById(R.id.lay_accept);
        layPending = view.findViewById(R.id.lay_pending);
        layRejected = view.findViewById(R.id.lay_rejected);
        detail = view.findViewById(R.id.dt_detail);
        location = view.findViewById(R.id.locDocs);
        file = view.findViewById(R.id.fileDocs);
        c1 = view.findViewById(R.id.c_accept);
        c2 = view.findViewById(R.id.c_pending);
        c3 = view.findViewById(R.id.c_rejected);
        cardOption = view.findViewById(R.id.card_option);
        exit = view.findViewById(R.id.action_activity);
        session = Application.getSession();
    }

    private void loadDataPending() {
        Call<GetUsers> call = service.getUserActivities(bundle.getString("id_activity", null),"pending");
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code()==200){
                    if(response.body().getResult().size()!=0){
                        usersList2 = response.body().getResult();
                        adapter2 = new MembersAdapter(usersList2,getContext());
                        recyclerView2.setAdapter(adapter2);
                        c2.setText(usersList2.size() + " Anggota");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }

    private void loadDataMembers() {
        Call<GetUsers> call = service.getUserActivities(bundle.getString("id_activity",null),"join");
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        usersList = response.body().getResult();
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new MembersAdapter(usersList,getContext());
                        recyclerView.setAdapter(adapter);
                        c1.setText(usersList.size() + " Anggota");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }

    private void loadDataRejected() {
        Call<GetUsers> call = service.getUserActivities(bundle.getString("id_activity", null), "rejected");
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if (response.code() == 200) {
                    if (response.body().getResult().size() != 0) {
                        usersList3 = response.body().getResult();
                        adapter3 = new MembersAdapter(usersList3, getContext());
                        recyclerView3.setAdapter(adapter3);
                        c3.setText(usersList3.size() + " Anggota");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }
    private void confirmInvitation(String action, String id_activity, String message){
        SharedPreferences sf = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostData> call = apiInterface.putInvitationStatus(id_activity,sf.getString("id_member",""),action, message);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    DetailActivity.detail.finish();
                    UserActivity.userActivity.loadData();
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Snackbar.make(getView(), "Cek Koneksi Internet",Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    private void getMessageDialog(final String action, final String id_activity,final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Kirim Pesan");
        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmInvitation(action,id_activity,input.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
