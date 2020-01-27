package com.ta.slk.sistemlayanankegiatan.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.brouding.simpledialog.SimpleDialog;
import com.ta.slk.sistemlayanankegiatan.Adapter.*;
import com.ta.slk.sistemlayanankegiatan.AddInvitation;
import com.ta.slk.sistemlayanankegiatan.DetailActivity;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import com.ta.slk.sistemlayanankegiatan.Rest.*;
import com.ta.slk.sistemlayanankegiatan.Method.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import com.ta.slk.sistemlayanankegiatan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment {
    SimpleDialog progress;
    MultiSelectDialog multiSelectDialog;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    ImageView noData;
    private String id_activity;
    public static Fragment activityFragment;

    List<Groups> listGroups;
    List<Activities> listActivities;
    List<Users> listUsers;

    public ActivitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_activities, container, false);
        initComponents(view);

        loadData();
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                bundle.putString("id_activity",listActivities.get(position).getIdActivity());
                bundle.putString("comment_key",listActivities.get(position).getCommentKey());
                bundle.putString("admin",listActivities.get(position).getCreatedBy());
                bundle.putString("name",listActivities.get(position).getNameActivities());
                bundle.putString("file",listActivities.get(position).getFile());
                bundle.putString("date",listActivities.get(position).getDate());
                bundle.putString("picture",listActivities.get(position).getPicture());
                bundle.putString("place",listActivities.get(position).getPlace());
                bundle.putString("description",listActivities.get(position).getDescription());
                intent.putExtra("activity",bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View v, final int position) {
                id_activity = listActivities.get(position).getIdActivity();
                final CharSequence[] dialogitem = {"Buka","Kirim Grup","Kirim Pribadi","Edit","Hapus"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pilih Menu");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 1:
                                MultiSelectDialog dialog1 = multiSelectShow("groups");
                                dialog1.show(getFragmentManager(),"Testing");
                                break;
                            case 2:
                                MultiSelectDialog dialog2 = multiSelectShow("members");
                                dialog2.show(getFragmentManager(),"Testing");
                                break;
                            case 3:
                                Bundle activities = new Bundle();
                                activities.putString("action", "update");
                                activities.putString("date", listActivities.get(position).getDate());
                                activities.putString("id_activity", listActivities.get(position).getIdActivity());
                                activities.putString("picture", listActivities.get(position).getPicture());
                                activities.putString("name", listActivities.get(position).getNameActivities());
                                activities.putString("location", listActivities.get(position).getPlace());
                                activities.putString("description", listActivities.get(position).getDescription());
                                activities.putString("file", listActivities.get(position).getFile());
                                startActivity(new Intent(getContext(), AddInvitation.class).putExtras(activities));
                                break;
                            case 4:
                                deleteActivity(listActivities.get(position).getIdActivity());
                                break;
                        }
                    }
                }).create().show();
            }
        }));
        return view;
    }

    private void initComponents(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_content);
        progressBar = view.findViewById(R.id.progress_bar);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        noData = view.findViewById(R.id.no_data);
        activityFragment = this;
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        listActivities = new ArrayList<>();
        mAdapter = new ActivitiesAdapter(listActivities, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scheduleLayoutAnimation();
    }

    private void deleteActivity(final String idActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Konfirmasi").setMessage("Hapus kegiatan akan menghapus semua rekam jejak kegiatan");
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete(idActivity);
            }
        }).show();
    }

    private void doDelete(String idActivity) {
        progress = Application.getProgress(getContext(), "Sedanga menghapus data").show();
        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<PostData> call = service.deleteActivities(idActivity);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                        Toast.makeText(getContext(),"Berhasil di hapus",Toast.LENGTH_SHORT).show();
                        loadData();
                        mAdapter.notifyDataSetChanged();
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {

            }
        });
    }

    public void loadData(){
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetActivities> mGetActivity = mApiInterface.getActivities();
        mGetActivity.enqueue(new Callback<GetActivities>() {
            @Override
            public void onResponse(Call<GetActivities> call, Response<GetActivities> response) {
                if(response.code()==200){
                    if (response.body().getResult().size() == 0) {
                        listActivities.clear();
                        noData.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    } else {
                        noData.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        listActivities.clear();
                        listActivities.addAll(response.body().getResult());
                    }
                    mAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                    getDataGroups();
                    getDataUsers();
                }else{
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(),"NO DATA",Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            loadData();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<GetActivities> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                try {
                    Snackbar.make(activityFragment.getView(), "Cek koneksi Internet", Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            loadData();
                        }
                    }).show();
                } catch (Exception e) {

                }
            }
        });
    }

    private MultiSelectDialog multiSelectShow(final String table){
        ArrayList<MultiSelectModel> listOfSelect= new ArrayList<>();
        Session session = Application.getSession();
        if(table.equals("groups")){
            for(int i = 0; i < listGroups.size(); i++){
                int idGroups = Integer.parseInt(listGroups.get(i).getIdGroup());
                String nameGroups = listGroups.get(i).getName();
                listOfSelect.add(new MultiSelectModel(idGroups, nameGroups));
            }
        }else if (table.equals("members")){
            for(int i=0 ;i < listUsers.size();i++){
                int idEmployee = Integer.parseInt(listUsers.get(i).getIdMember());
                String nameEmployee = listUsers.get(i).getName();
                if(!listUsers.get(i).getIdMember().equals(session.getIdMember())) {
                    listOfSelect.add(new MultiSelectModel(idEmployee, nameEmployee));
                }
            }
        }

        multiSelectDialog = new MultiSelectDialog()
                .title("Pilih") //setting title for dialog
                .titleSize(25)
                .positiveText("Done")
                .negativeText("Cancel")
                .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                .setMaxSelectionLimit(listOfSelect.size()) //you can set maximum checkbox selection limit (Optional)
                .multiSelectList(listOfSelect) // the multi select model list with ids and name
                .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                    @Override
                    public void onSelected(ArrayList<Integer> arrayList, ArrayList<String> arrayList1, String s) {
                        if(table.equals("groups")){
                            sendGroup(arrayList);
                        }else{
                            sendInvitation(arrayList,"groups");
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        return multiSelectDialog;
    }

    private void getDataGroups(){
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetGroups> mGetGroups = mApiInterface.getGroups();
        mGetGroups.enqueue(new Callback<GetGroups>() {
            @Override
            public void onResponse(Call<GetGroups> call, Response<GetGroups> response) {
                if(response.code()==200){
                    listGroups = response.body().getResult();
                }
            }

            @Override
            public void onFailure(Call<GetGroups> call, Throwable t) {

            }
        });

    }

    private void getDataUsers(){
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetUsers> mGetUser = mApiInterface.getUsers();
        mGetUser.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code()==200){
                    listUsers = response.body().getResult();
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }

    private void sendInvitation(ArrayList<Integer> list, String action){
        progress = Application.getProgress(getContext(), "Sedang mengirim undangan").show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostData> postDataCall = apiInterface.sendInvitation(list,id_activity,action);
        postDataCall.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        Toast.makeText(getContext(),"Undangan Berhasil dikirimkan",Toast.LENGTH_SHORT).show();
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
//                Toast.makeText(getContext(),"Cek koneksi interner",Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    private void sendGroup(ArrayList<Integer> list){
        progress = Application.getProgress(getContext(), "Sedang mengirimkan undangan").show();
        ApiGroups apiGroups = ApiClient.getClient().create(ApiGroups.class);
        Call<PostData> call = apiGroups.sendGroup(list,id_activity);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        Toast.makeText(getContext(),"Undangan Berhasil dikirimkan",Toast.LENGTH_SHORT).show();
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
//                Toast.makeText(getContext(),"Cek koneksi internet",Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
}
