package com.ta.slk.sistemlayanankegiatan.Adapter;
import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.*;
import android.content.Context;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context context;
    private List<Comment> myList;
    private Session session;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView name,comment,date,name2,comment2,date2;
        private CircleImageView image,image2;
        private RelativeLayout lay_in, lay_out;
        public MyViewHolder(View v) {
            super(v);
            comment = itemView.findViewById(R.id.comment_member);
            date = itemView.findViewById(R.id.date_time);
            name = itemView.findViewById(R.id.name_member);
            image = itemView.findViewById(R.id.img_member);
            comment2 = itemView.findViewById(R.id.comment_member2);
            date2 = itemView.findViewById(R.id.date_time2);
            name2 = itemView.findViewById(R.id.name_member2);
            image2 = itemView.findViewById(R.id.img_member2);
            lay_in = itemView.findViewById(R.id.lay_in);
            lay_out = itemView.findViewById(R.id.lay_out);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentAdapter(List<Comment> myList, Context context) {
        this.myList = myList;
        this.context = context;
        session = Application.getSession();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_comment, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, final int position) {
        if(myList.get(position).getId().equals(session.getIdMember())){
            holder.lay_in.setVisibility(View.GONE);
            holder.lay_out.setVisibility(View.VISIBLE);
            holder.comment2.setText(Application.getTimeAgo(myList.get(position).getComment()));
            holder.date2.setText(myList.get(position).getDate());
            holder.name2.setText(myList.get(position).getName());
            Glide.with(holder.itemView.getContext()).load(ApiClient.BASE_URL+"uploads/members/"+myList.get
                    (position).getPhoto())
                    .into(holder.image2);
        }else{
            holder.lay_in.setVisibility(View.VISIBLE);
            holder.lay_out.setVisibility(View.INVISIBLE);
            holder.comment.setText(Application.getTimeAgo(myList.get(position).getComment()));
            holder.date.setText(myList.get(position).getDate());
            holder.name.setText(myList.get(position).getName());
            Glide.with(holder.itemView.getContext()).load(ApiClient.BASE_URL+"uploads/members/"+myList.get
                    (position).getPhoto())
                    .into(holder.image);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myList.size();
    }
}
