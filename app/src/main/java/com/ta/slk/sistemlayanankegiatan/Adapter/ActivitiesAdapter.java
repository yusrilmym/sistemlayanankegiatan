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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.MyViewHolder> {
    private Context context;
    private List<Activities> myActivity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title,admin,date,location;
        private ImageView image;
        private Button status;
        public MyViewHolder(View v) {
            super(v);
            title = itemView.findViewById(R.id.main_title);
            admin = itemView.findViewById(R.id.main_admin);
            date = itemView.findViewById(R.id.main_tgl);
            image = itemView.findViewById(R.id.main_img_card);
            location = itemView.findViewById(R.id.main_location);
            status = itemView.findViewById(R.id.btn_status);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActivitiesAdapter(List<Activities> myActivity, Context context) {
        this.myActivity = myActivity;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_activities, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(myActivity.get(position).getNameActivities());
        holder.admin.setText(myActivity.get(position).getCreatedBy());
        holder.date.setText(myActivity.get(position).getDate());
        holder.location.setText(myActivity.get(position).getPlace());
        if (myActivity.get(position).getPicture() != null) {
            Glide.with(holder.itemView.getContext()).load(ApiClient.BASE_URL+"uploads/"+myActivity.get
                    (position).getPicture())
                    .into(holder.image);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStart = sdf.format(new Date());
        String dateStop = myActivity.get(position).getDate();
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(dateStart);
            d2 = sdf.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        long datediff = TimeUnit.MILLISECONDS.toDays(d2.getTime() - d1.getTime());
        if (datediff == 0) {
            holder.status.setBackgroundResource(R.drawable.shape_orange);
            holder.status.setText("SEKARANG");
        } else if (datediff <= 7 && datediff > 0) {
            holder.status.setBackgroundResource(R.drawable.shape_orange);
            holder.status.setText(datediff + " HARI");
        }else if(diffHours < 0){
            if(diffHours <= -24){
                holder.status.setBackgroundResource(R.drawable.shape_green);
                holder.status.setText("SELESAI");
            }
        }else{
            holder.status.setText(datediff+" DAYS");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myActivity.size();
    }
}