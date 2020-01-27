package com.ta.slk.sistemlayanankegiatan.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;
import com.ta.slk.sistemlayanankegiatan.Adapter.*;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import com.ta.slk.sistemlayanankegiatan.Rest.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import com.ta.slk.sistemlayanankegiatan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment{
    SwipeRefreshLayout refreshLayout;
    SimpleDialog progressDialog;
    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    MembersAdapter adapter;
    List<Users> listUsers;
    Fragment fragment;
    SearchView searchView;

    public MembersFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_members, container, false);
        initComponents(view);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, final int position) {

            }

            @Override
            public void onLongClick(View v, final int position) {
                final List<Users> filtered = adapter.getUsersFiltered();
                CharSequence[] sequence = {"Ganti Role","Hapus Member"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Opsi Member");
                builder.setItems(sequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if(filtered.get(position).getLevel() != null){
                                    showSwitch(filtered.get(position).getIdMember(),filtered.get(position).getLevel());
                                }else{
                                    Toast.makeText(getContext(),"User Belum Mendaftar ",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                doDelete(filtered.get(position).getIdMember());
                                break;

                        }
                    }
                }).create().show();
            }
        }));
        this.setHasOptionsMenu(true);
        loadData(view);
        return view;
    }

    private void initComponents(final View view) {
        mRecyclerView = view.findViewById(R.id.recycler_content);
        progressBar = view.findViewById(R.id.progress_bar);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        listUsers = new ArrayList<>();
        adapter = new MembersAdapter(listUsers, getContext());
        mRecyclerView.setAdapter(adapter);
        fragment = this;
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        mRecyclerView.scheduleLayoutAnimation();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(view);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void showSwitch(final String idMember, final String level) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final Switch aSwitch = new Switch(getContext());
        aSwitch.setTextOn("1");
        aSwitch.setTextOff("2");
        if(level.equals("1")){
            aSwitch.setChecked(true);
        }
        builder.setTitle("Ganti Status ").setMessage("Akun Admin");
        builder.setView(aSwitch);
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level = null;
                if(aSwitch.isChecked()){
                    level = "1";
                }else{
                    level = "2";
                }
                ApiMembers members = ApiClient.getClient().create(ApiMembers.class);
                Call<PostData> call = members.changeRule(idMember,level);
                call.enqueue(new Callback<PostData>() {
                    @Override
                    public void onResponse(Call<PostData> call, Response<PostData> response) {
                        if(response.code()==200){
                            if(response.body().getStatus().equals("success")){
                                Toast.makeText(getContext(),"sukes update role",Toast.LENGTH_SHORT).show();
                                loadData(getView());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PostData> call, Throwable t) {
                    }
                });
            }
        });
        builder.create().show();
    }

    private void doDelete(String idUser) {
        progressDialog = Application.getProgress(getContext(), "Hapus Member").show();
        ApiMembers members = ApiClient.getClient().create(ApiMembers.class);
        Call<PostData> call = members.deleteNip(idUser);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        Toast.makeText(getContext(),"Sukses hapus member",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        loadData(getView());
                    }
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Toast.makeText(getContext(),"Cek koneksi Internet",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void loadData(final View view){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetUsers> usersCall = apiInterface.getUsers();
        usersCall.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code() == 200){
                    listUsers.clear();
                    listUsers.addAll(response.body().getResult());
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }else{
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(),"NO DATA",Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            loadData(view);
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getView(),"Cek koneksi Internet",Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        loadData(view);
                    }
                }).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
//                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
//                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
