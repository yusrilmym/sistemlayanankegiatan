package com.ta.slk.sistemlayanankegiatan.Adapter;
import com.bumptech.glide.Glide;
import com.ta.slk.sistemlayanankegiatan.Model.*;

import android.app.AlertDialog;
import android.content.Context;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.MyViewHolder> {
    private Context context;
    private List<InvtActivities> myList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title,admin,date,day,clock;
        private View line;
        public MyViewHolder(View v) {
            super(v);
            title = itemView.findViewById(R.id.inv_title);
            admin = itemView.findViewById(R.id.inv_admin);
            date = itemView.findViewById(R.id.inv_date);
            day = itemView.findViewById(R.id.inv_day);
            clock = itemView.findViewById(R.id.inv_clock);
            line = itemView.findViewById(R.id.line_indicator);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InvitationAdapter(List<InvtActivities> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_invitation, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.admin.setText(myList.get(position).getCreatedBy());
        holder.title.setText(myList.get(position).getNameActivities());
        String st_date = myList.get(0).getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = sdf.format(new Date());
        Date date = null;
        Date date2 = null;

        try {
            date = sdf.parse(st_date);
            date2 = sdf.parse(dateNow);
        }catch (Exception e){}

        long diff = date.getDate() - date2.getDate();
        if(diff<0){
            holder.line.setBackgroundResource(R.color.colorAccent);
        }else if(diff <= 7 && diff >=0){
            holder.line.setBackgroundResource(R.color.mateOrange);
        }else if(diff > 7){
            holder.line.setBackgroundResource(R.color.colorPrimary);
        }

        SimpleDateFormat new_sdf = new SimpleDateFormat("E");
            try {
                date = new_sdf.parse(st_date);
            }catch (Exception e){}
        String[] splitDay = date.toString().split(" ");
        holder.date.setText(splitDay[2]);
        holder.day.setText(splitDay[1]);
        holder.clock.setText(splitDay[0]+" "+splitDay[3]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myList.size();
    }
}