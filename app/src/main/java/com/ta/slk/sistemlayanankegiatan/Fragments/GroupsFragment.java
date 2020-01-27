package com.ta.slk.sistemlayanankegiatan.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.ValueIterator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.brouding.simpledialog.SimpleDialog;
import com.ta.slk.sistemlayanankegiatan.Adapter.GroupsAdapter;
import com.ta.slk.sistemlayanankegiatan.AdminContent;
import com.ta.slk.sistemlayanankegiatan.DetailGroups;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.FileUtil;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Model.GetGroups;
import com.ta.slk.sistemlayanankegiatan.Model.GetUsers;
import com.ta.slk.sistemlayanankegiatan.Model.Groups;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.Model.Users;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiGroups;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment{
    RecyclerView recyclerView;
    SimpleDialog progress;
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    RecyclerView.Adapter adapter;
    MultiSelectDialog multiSelectDialog;
    ImageView noData;

    ApiGroups service;
    private String imagePath;
    Fragment fragment;
    TextInputEditText title, description, image;

    List<Groups> groupsList;
    List<Users> usersList;

    File originalFile, fileCompressed;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups,container,false);
        initComponents(view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadData();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getContext(), DetailGroups.class);
                intent.putExtra("id_group",groupsList.get(position).getIdGroup());
                intent.putExtra("admin",groupsList.get(position).getCreatedBy());
                intent.putExtra("name",groupsList.get(position).getName());
                intent.putExtra("description",groupsList.get(position).getDescription());
                intent.putExtra("picture",groupsList.get(position).getPhotoGroup());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View v, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final CharSequence[] sequence = {"Buka","Tambah Anggota","Edit","Delete"};
                builder.setTitle("Pilihan Group").setItems(sequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 1:
                                MultiSelectDialog dialog1 = inviteGroup(position);
                                dialog1.show(getFragmentManager(),"Group");
                                break;
                            case 2:
                                updateData(position);
                                break;
                            case 3:
                                deleteData(groupsList.get(position).getIdGroup());
                                break;
                        }
                    }
                }).create().show();
            }
        }));
        return view;
    }

    private void initComponents(View view) {
        fragment = this;
        recyclerView = view.findViewById(R.id.recycler_content);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        noData = view.findViewById(R.id.no_data);

        groupsList = new ArrayList<>();
        service = ApiClient.getClient().create(ApiGroups.class);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.scheduleLayoutAnimation();
        adapter = new GroupsAdapter(groupsList, getContext());
        recyclerView.setAdapter(adapter);
    }

    private MultiSelectDialog inviteGroup(final int position) {
        ArrayList<MultiSelectModel> listOfSelect= new ArrayList<>();
        for(int i=0 ;i < usersList.size();i++) {
            int idEmployee = Integer.parseInt(usersList.get(i).getIdMember());
            String nameEmployee = usersList.get(i).getName();
            listOfSelect.add(new MultiSelectModel(idEmployee, nameEmployee));
        }
            multiSelectDialog = new MultiSelectDialog()
                    .title("Pilih") //setting title for dialog
                    .titleSize(25)
                    .positiveText("Done")
                    .negativeText("Cancel")
                    .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                    .setMaxSelectionLimit(listOfSelect.size()) //you can set maximum checkbox selection limit (Optional)
                    .multiSelectList(listOfSelect)// the multi select model list with ids and name
                    .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                        @Override
                        public void onSelected(ArrayList<Integer> arrayList, ArrayList<String> arrayList1, String s) {
                            sendInvitationGroup(arrayList,groupsList.get(position).getIdGroup());
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        return multiSelectDialog;
    }

    private void sendInvitationGroup(ArrayList<Integer> arrayList, String idGroup) {
        progress = Application.getProgress(getContext(), "Menambahka anggota").show();
        Call<PostData> call = service.inviteGroup(arrayList,idGroup);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        progressBar.setVisibility(View.VISIBLE);
                        loadData();
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Snackbar.make(getView(), "Cek Koneksi internet", Snackbar.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    private void loadData(){
        loadUsers();
        progressBar.setVisibility(View.VISIBLE);
        Call<GetGroups> call = service.getGroups();
        call.enqueue(new Callback<GetGroups>() {
            @Override
            public void onResponse(Call<GetGroups> call, Response<GetGroups> response) {
                if(response.code()==200){
                    if (response.body().getResult().size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                        groupsList.clear();
                    } else {
                        noData.setVisibility(View.GONE);
                        groupsList.clear();
                        groupsList.addAll(response.body().getResult());
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                }else{
                    Snackbar.make(getView(), "Cek Koneksi Internet ", Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadData();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<GetGroups> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                Snackbar.make(getView(), "Cek Koneksi Internet ",Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData();
                    }
                }).show();
            }
        });
    }

    private void loadUsers() {
        ApiInterface userService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetUsers> call = userService.getUsers();
        call.enqueue(new Callback<GetUsers>() {
            @Override
            public void onResponse(Call<GetUsers> call, Response<GetUsers> response) {
                if(response.code()==200){
                    usersList = response.body().getResult();
                }
            }

            @Override
            public void onFailure(Call<GetUsers> call, Throwable t) {

            }
        });
    }

    private void deleteData(String id){
        progress = Application.getProgress(getContext(), "Sedanga menghapus data").show();
        Call<PostData> call = service.del_group(id);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equals("success")){
                        progressBar.setVisibility(View.VISIBLE);
                        loadData();
                        adapter.notifyDataSetChanged();
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    private void doUpdate(String id){
        MultipartBody.Part body = null;
        if (!imagePath.isEmpty()){
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileCompressed);
            body = MultipartBody.Part.createFormData("picture", fileCompressed.getName(),
                    requestFile);
        }
        RequestBody reqId = MultipartBody.create(MediaType.parse("multipart/form-data"),
                (id.isEmpty())?"":id);
        RequestBody reqName = MultipartBody.create(MediaType.parse("multipart/form-data"),
                (title.getText().toString().isEmpty())?"":title.getText().toString());
        final RequestBody reqDes = MultipartBody.create(MediaType.parse("multipart/form-data"),
                (description.getText().toString().isEmpty())?"":description.getText().toString());

        Call<PostData> call= service.update_group(body,reqId,reqName,reqDes);
        call.enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.code()==200){
                    Toast.makeText(getContext(),"SUKSES",Toast.LENGTH_SHORT).show();
                    loadData();
                    adapter.notifyDataSetChanged();
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                progress.dismiss();
            }
        });

    }

    private void updateData(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.manage_groups,null);
        builder.setView(dialog).setTitle("Edit Group").setIcon(R.drawable.group);
        title       = dialog.findViewById(R.id.mg_title_text);
        description = dialog.findViewById(R.id.mg_desc_text);
        image       = dialog.findViewById(R.id.mg_img_text);
        title.setText(groupsList.get(position).getName());
        description.setText(groupsList.get(position).getDescription());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent gallery = new Intent();
                gallery.setType("image/*").setAction(Intent.ACTION_PICK);
                Intent intentChoice = Intent.createChooser(gallery,"Pilih Gambar untuk di upload");
//                getActivity().startActivityForResult(intentChoice,1);
                fragment.startActivityForResult(intentChoice,1);
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doUpdate(groupsList.get(position).getIdGroup());
                progress = Application.getProgress(getContext(), "Sedang memperbarui data").show();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            if (data==null){
                Toast.makeText(getContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath =cursor.getString(columnIndex);
                try {
                    originalFile = FileUtil.from(fragment.getContext(),data.getData());
                    fileCompressed = new Compressor(fragment.getContext())
                            .setMaxHeight(480).setMaxWidth(480).setQuality(75)
                            .compressToFile(originalFile);
                }catch (Exception e){

                }

//                Picasso.with(getApplicationContext()).load(new File(imagePath)).fit().into(mImageView);
//                Glide.with(getApplicationContext()).load(new File(imagePath)).into(mImageView);
                image.setText(imagePath);
                cursor.close();
            }else{
                Toast.makeText(getContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
        }
    }
}
