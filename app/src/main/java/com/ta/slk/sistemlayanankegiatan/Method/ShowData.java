package com.ta.slk.sistemlayanankegiatan.Method;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ta.slk.sistemlayanankegiatan.Adapter.*;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowData {
    private RecyclerView.Adapter adapter;
    List<Groups> listGroups;
    Context context;


    public ShowData(Context context){
        this.context = context;
    }

    public RecyclerView.Adapter groupsById(){
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetGroups> mGroupCall =  mApiInterface.getGroupsById("1","groups");

        mGroupCall.enqueue(new Callback<GetGroups>() {
            @Override
            public void onResponse(Call<GetGroups> call, Response<GetGroups> response) {
                listGroups = response.body().getResult();
                adapter = new GroupsAdapter(listGroups, context);
            }

            @Override
            public void onFailure(Call<GetGroups> call, Throwable t) {

            }
        });
        return adapter;
    }
}
