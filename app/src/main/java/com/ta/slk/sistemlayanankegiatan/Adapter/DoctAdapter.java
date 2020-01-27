package com.ta.slk.sistemlayanankegiatan.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Model.Documentation;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class DoctAdapter extends RecyclerView.Adapter<DoctAdapter.MyViewHolder>{
    private Context context;
    private List<Documentation> documentationList;

    public DoctAdapter(Context context, List<Documentation> documentationList) {
        this.context = context;
        this.documentationList = documentationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_documentation, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position){
        if (documentationList.get(position).getPicture() != "") {
            Glide.with(holder.itemView.getContext()).load(ApiClient.BASE_URL+"uploads/documentation/"+documentationList.get
                    (position).getPicture())
                    .into(holder.imageDoc);
        }

        holder.imageDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext(), R.style.ZoomImageDialog);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.ZoomImageDialog;
                dialog.setContentView(R.layout.zoom_image);
                final PhotoView imageView = dialog.findViewById(R.id.zoom_image);
//                ImageButton btnSave = dialog.findViewById(R.id.btn_save_image);
                try {
                    Glide.with(v.getContext()).load(ApiClient.BASE_URL+"uploads/documentation/"+documentationList.get
                            (position).getPicture())
                            .into(imageView);
                }catch (Exception e){

                }
                /*
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        try {
                            saveImage(documentationList.get(position).getPicture());
                            Toast.makeText(v.getContext(),"Gambar berhasil di simpan",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(v.getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                */
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
    }

    @Override
    public int getItemCount() {
        return documentationList.size();
    }

    private static void saveImage(String filename) throws IOException {
        URL url = new URL(ApiClient.BASE_URL+"uploads/documentation/"+filename);
        InputStream inputStream = url.openStream();
        OutputStream outputStream = new FileOutputStream(filename);
        byte[] bytes = new byte[2048];
        int i;
        while ((i = inputStream.read(bytes))!= 1){
            outputStream.write(bytes,0, i);
        }
        inputStream.close();
        outputStream.close();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageDoc;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageDoc = itemView.findViewById(R.id.image_doc);
        }
    }
}
