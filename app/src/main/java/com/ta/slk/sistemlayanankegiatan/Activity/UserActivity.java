package com.ta.slk.sistemlayanankegiatan.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ta.slk.sistemlayanankegiatan.Adapter.ActivitiesAdapter;
import com.ta.slk.sistemlayanankegiatan.DetailActivity;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Model.Activities;
import com.ta.slk.sistemlayanankegiatan.Model.GetActivities;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    ApiInterface service;
    public static UserActivity userActivity;
    ProgressBar progressBar;
    WaveSwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ImageView noData;
    List<Activities> activitiesList;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userActivity = this;

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorLight));
        refreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));
        noData = findViewById(R.id.no_data);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        service = ApiClient.getClient().create(ApiInterface.class);
        loadData();
        refreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                bundle.putString("id_activity",activitiesList.get(position).getIdActivity());
                bundle.putString("comment_key",activitiesList.get(position).getCommentKey());
                bundle.putString("name",activitiesList.get(position).getNameActivities());
                bundle.putString("admin",activitiesList.get(position).getCreatedBy());
                bundle.putString("file",activitiesList.get(position).getFile());
                bundle.putString("date",activitiesList.get(position).getDate());
                bundle.putString("picture",activitiesList.get(position).getPicture());
                bundle.putString("place",activitiesList.get(position).getPlace());
                bundle.putString("description",activitiesList.get(position).getDescription());
                intent.putExtra("activity",bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));

    }

    public void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        Call<GetActivities> call = service.getActiviesById("0","my_activity");
        call.enqueue(new Callback<GetActivities>() {
            @Override
            public void onResponse(Call<GetActivities> call, Response<GetActivities> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        if (response.body().getResult().size() == 0) {
                            refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            activitiesList = response.body().getResult();
                            adapter = new ActivitiesAdapter(activitiesList, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    }else if (response.body().getResult().size()==0){
                        Toast.makeText(getApplicationContext(),"NO DATA",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetActivities> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_refresh:
                loadData();
                break;
        }
        return false;
    }
}
