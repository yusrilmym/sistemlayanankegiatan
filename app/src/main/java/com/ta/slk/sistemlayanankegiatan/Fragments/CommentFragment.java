package com.ta.slk.sistemlayanankegiatan.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ta.slk.sistemlayanankegiatan.Adapter.CommentAdapter;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Method.Session;
import com.ta.slk.sistemlayanankegiatan.Model.Comment;
import com.ta.slk.sistemlayanankegiatan.R;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    EditText commentText;
    Button btn_send;
    List<Comment> commentList;
    private DatabaseReference root;
    private String temp_key;
    private String name;
    private String photo;
    CircleImageView profile;

    public CommentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_comment,container,false);

        SharedPreferences sf = view.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        name = sf.getString("name","");
        photo = sf.getString("photo","");


        profile = view.findViewById(R.id.img_profile);
        commentText = view.findViewById(R.id.txt_comment);
        btn_send = view.findViewById(R.id.btn_submit);

        Glide.with(getContext()).load(ApiClient.BASE_URL+"uploads/members/"+photo)
                .into(profile);

        commentList = new ArrayList<>();
        Bundle bundle = getArguments();
        recyclerView = view.findViewById(R.id.recycler_content);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        root = FirebaseDatabase.getInstance().getReference().child(bundle.get("comment_key").toString());

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                Session session = Application.getSession();

                Map<String,Object> map = new HashMap<>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("id",session.getIdMember());
                map2.put("name",name);
                map2.put("date",currentDateandTime);
                map2.put("comment",commentText.getText().toString());
                map2.put("photo",photo);
                message_root.updateChildren(map2);
                commentText.setText("");
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                commentList = refreshComment(dataSnapshot);
                adapter = new CommentAdapter(commentList,view.getContext());
                recyclerView.setAdapter(adapter);
                if(commentList.size()==0){
                    Toast.makeText(getContext(),"NO COMMENT",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                commentList = refreshComment(dataSnapshot);
                adapter = new CommentAdapter(commentList,view.getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        commentList.add(new Comment("test 1","INI KOMENTAR 1","5 days ago",""));
//        commentList.add(new Comment("test 2","INI KOMENTAR 2","3 days ago",""));

//        adapter = new CommentAdapter(commentList,view.getContext());



        return view;
    }

    private List<Comment> refreshComment(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            String name = (String)((DataSnapshot)i.next()).getValue();
            String comment = (String)((DataSnapshot)i.next()).getValue();
            String id = (String)((DataSnapshot)i.next()).getValue();
            String date = (String)((DataSnapshot)i.next()).getValue();
            String photo = (String)((DataSnapshot)i.next()).getValue();
            commentList.add(new Comment(id,name,comment,date,photo));
        }
        return commentList;
    }
}
