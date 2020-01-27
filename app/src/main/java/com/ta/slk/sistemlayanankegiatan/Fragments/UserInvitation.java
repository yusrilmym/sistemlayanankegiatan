package com.ta.slk.sistemlayanankegiatan.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Adapter.InvitationAdapter;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Model.GetInvtActivities;
import com.ta.slk.sistemlayanankegiatan.Model.InvtActivities;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoView;

public class UserInvitation extends Fragment {
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    ImageView noData;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<InvtActivities> activitiesList;
    public static Fragment invitation;

    public UserInvitation(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_invitation,container,false);

        recyclerView = view.findViewById(R.id.recycler_content);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        noData = view.findViewById(R.id.no_data);
        invitation = this;

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        activitiesList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InvitationAdapter(activitiesList, view.getContext());
        recyclerView.setAdapter(adapter);

        refreshData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(), recyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                showDialog(activitiesList.get(position).getIdActivity(),activitiesList.get(position).getFile(),activitiesList.get(position).getPicture());
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));
        return view;
    }

    public void refreshData() {
        SharedPreferences sf = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        ApiInterface apiClient = ApiClient.getClient().create(ApiInterface.class);
        Call<GetInvtActivities> call = apiClient.getActivityStatus(sf.getString("id_member",""),"pending");
        call.enqueue(new Callback<GetInvtActivities>() {
            @Override
            public void onResponse(Call<GetInvtActivities> call, Response<GetInvtActivities> response) {
                if(response.code()==200){
                    if (response.body().getResult().size() == 0) {
                        activitiesList.clear();
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.GONE);
                        activitiesList.clear();
                        activitiesList.addAll(response.body().getResult());
                    }
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetInvtActivities> call, Throwable t) {
                Toast.makeText(getContext(), "Cek koneksi internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog(final String id_activity, final String name, final String picture){
        CharSequence[] charSequence = {"Lihat Undangan","Download Undangan","Terima","Tolak"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pilih Menu").setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        final Dialog dialog1=new Dialog(getContext(),R.style.ZoomImageDialog);
                        dialog1.setContentView(R.layout.zoom_image);
                        final PhotoView imageView = dialog1.findViewById(R.id.zoom_image);
                        try {
                            Glide.with(getContext()).load(ApiClient.BASE_URL+"uploads/"+picture).into(imageView);
                        }catch (Exception e){

                        }
                        dialog1.show();
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse( ApiClient.BASE_URL+"uploads/" + name), "text/html");
                        startActivity(intent);
                        break;
                    case 2:
                        confirmInvitation("join", id_activity, "");
//                        getMessageDialog("join",id_activity);
                        break;
                    case 3:
                        getMessageDialog("rejected",id_activity);
                        break;
                }
            }
        });
        builder.show();
        }
    private void confirmInvitation(String action, String id_activity, String message){
        SharedPreferences sf = getContext().getSharedPreferences("login",Context.MODE_PRIVATE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostData> call = apiInterface.putInvitationStatus(id_activity,sf.getString("id_member",""),action, message);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if (response.body().getStatus().equals("success")) {
                    refreshData();
                    ((UserInvitationAccept) UserInvitationAccept.userInvitation).refreshData();
                    ((UserInvitationRejected) UserInvitationRejected.userRejected).refreshData();
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Snackbar.make(getView(), "Cek Koneksi Internet",Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    private void getMessageDialog(final String action, final String id_activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Kirim Pesan");
        final EditText input = new EditText(getContext());
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
