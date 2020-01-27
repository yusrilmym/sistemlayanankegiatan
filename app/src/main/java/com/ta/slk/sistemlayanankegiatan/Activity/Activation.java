package com.ta.slk.sistemlayanankegiatan.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ta.slk.sistemlayanankegiatan.Adapter.CommentAdapter;
import com.ta.slk.sistemlayanankegiatan.Method.ClickListenner;
import com.ta.slk.sistemlayanankegiatan.Method.RecyclerTouchListener;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiMembers;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activation extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<String> myNumbers;
    String currentText;
    TextView codeText,email;
    ProgressBar progressBar;
    ApiMembers service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        recyclerView = findViewById(R.id.recycler_content);
        codeText = findViewById(R.id.code_text);
        email = findViewById(R.id.lb_email);
        email.setText("Dikirim ke email "+getIntent().getStringExtra("email"));
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        service = ApiClient.getAuth().create(ApiMembers.class);

        myNumbers = new ArrayList<>();
        currentText = " ";

        for(int i = 1; i < 10; i++){
            myNumbers.add(Integer.toString(i));
        }
        myNumbers.add("<");
        myNumbers.add("0");
        myNumbers.add(">");
        adapter = new ActivationAdapter(myNumbers);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListenner() {
            @Override
            public void onClick(View v, int position) {
                if(myNumbers.get(position).equals("<")){
                    String subs = currentText;
                    int count = subs.length();
                    subs = subs.substring(0,(count-1));
                    currentText = subs;
                }else if(myNumbers.get(position).equals(">")){
                    codeText.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    checkCode();
                }else{
                    currentText += myNumbers.get(position);
                }
                codeText.setText(currentText);
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        }));
    }

    private void checkCode(){
         service.checkCode(getIntent().getStringExtra("id_user"),currentText).enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if(response.body().getStatus().equals("success")){
                    Toast.makeText(getApplicationContext(),"Aktivasi Sukses, silahkan login kembali", Toast.LENGTH_SHORT).show();
                    codeText.setText("");
                    currentText = " ";
                    codeText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(),"GAGAL", Toast.LENGTH_SHORT).show();
                    codeText.setText("");
                    currentText = " ";
                    codeText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
}

class ActivationAdapter extends RecyclerView.Adapter<ActivationAdapter.MyViewHolder>{
    private List<String> number;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView number;
        public MyViewHolder(View v) {
            super(v);
            number = itemView.findViewById(R.id.txt_num);
        }
    }

    public ActivationAdapter(List<String> number) {
        this.number = number;
    }

    @NonNull
    @Override
    public ActivationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_number, viewGroup, false);
        return new ActivationAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivationAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.number.setText(number.get(i));
    }

    @Override
    public int getItemCount() {
        return number.size();
    }
}
