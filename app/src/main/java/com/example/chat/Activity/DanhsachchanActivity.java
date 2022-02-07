package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.chat.Adapter.DSChanAdapter;
import com.example.chat.Model.ChanUser;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DanhsachchanActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    List<String> mIdChan;
    List<User> mUser;
    DSChanAdapter dsChanAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhsachchan);
        Init();
        mIdChan = new ArrayList<>();
        mUser = new ArrayList<>();

        ReadAdapter();
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dsChanAdapter = new DSChanAdapter(DanhsachchanActivity.this,mUser);
                        recyclerView.setAdapter(dsChanAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

    }

    private void ReadAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mIdChan.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if(chanUser.getIdChan().equals(firebaseUser.getUid())){
                        mIdChan.add(chanUser.getIdBiChan());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                     for (String Id : mIdChan){
                         if(user.getId().equals(Id)){
                             mUser.add(user);
                         }
                     }
                }
                dsChanAdapter = new DSChanAdapter(DanhsachchanActivity.this,mUser);
                recyclerView.setAdapter(dsChanAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.relativelayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Danh sách chặn");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}