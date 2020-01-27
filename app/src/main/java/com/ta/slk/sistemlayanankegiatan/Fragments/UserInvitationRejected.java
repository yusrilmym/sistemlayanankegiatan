package com.ta.slk.sistemlayanankegiatan.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ta.slk.sistemlayanankegiatan.Adapter.InvitationAdapter;
import com.ta.slk.sistemlayanankegiatan.Model.GetInvtActivities;
import com.ta.slk.sistemlayanankegiatan.Model.InvtActivities;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInvitationRejected extends Fragment{
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    SwipeRefreshLayout refreshLayout;
    ImageView noData;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    List<InvtActivities> activitiesList;
    public static Fragment userRejected;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_invitation, container, false);
        recyclerView = view.findViewById(R.id.recycler_content);
        layoutManager = new LinearLayoutManager(view.getContext());
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        noData = view.findViewById(R.id.no_data);
        recyclerView.setLayoutManager(layoutManager);
        userRejected = this;
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        refreshData();
        return view;
    }

    public void refreshData() {
        SharedPreferences sf = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
        Call<GetInvtActivities> call = apiClient.getActivityStatus(sf.getString("id_member",""),"rejected");
        call.enqueue(new Callback<GetInvtActivities>() {
            @Override
            public void onResponse(Call<GetInvtActivities> call, Response<GetInvtActivities> response) {
                if(response.code()==200){
                    if(response.body().getResult().size()!=0){
                        activitiesList = response.body().getResult();
                        adapter = new InvitationAdapter(activitiesList, getContext());
                        recyclerView.setAdapter(adapter);
                        refreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }else{
                        refreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<GetInvtActivities> call, Throwable t) {
                Toast.makeText(getContext(), "Cek koneksi internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
