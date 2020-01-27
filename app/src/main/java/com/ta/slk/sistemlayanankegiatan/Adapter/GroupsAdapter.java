package com.ta.slk.sistemlayanankegiatan.Adapter;
import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import android.content.Context;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {
    private Context context;
    private List<Groups> myList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView name,count;
        private CircleImageView image;
        public MyViewHolder(View v) {
            super(v);
            name = itemView.findViewById(R.id.name_group);
            image = itemView.findViewById(R.id.img_group);
            count = itemView.findViewById(R.id.count_group);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupsAdapter(List<Groups> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_group, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupsAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(myList.get(position).getName());
        if(myList.get(position).getCount()!=null){
            holder.count.setText("Anggota : "+myList.get(position).getCount());
        }else{
            holder.count.setText("Admin : "+myList.get(position).getCreatedBy());
        }

        if (myList.get(position).getPhotoGroup() != null) {
            Glide.with(holder.itemView.getContext()).load(ApiClient.BASE_URL+"uploads/groups/"+myList.get
                    (position).getPhotoGroup())
                    .into(holder.image);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myList.size();
    }
}
