package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.AllMemberAdapter;
import com.example.chat.Fragment.BDMemberGroupFragment;
import com.example.chat.Model.ListGroup;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class AllMemberActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    String IDGROUP;
    String GROUPNAME;
    List<ListGroup> mListGroup;
    AllMemberAdapter allMemberAdapter;
    boolean QTV;
    int Dem ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_member);

        Intent intent = getIntent();
        IDGROUP = intent.getStringExtra("Idgroup");
        GROUPNAME = intent.getStringExtra("Groupname");
        Anhxa();
        mListGroup = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Init();
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(allMemberAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

    }

    private void Init() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ListGroup");
        databaseReference1.child(IDGROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                QTV = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if(listGroup.getIdUser().equals(fUser.getUid()) && listGroup.getChucvu().equals("Quản trị viên")){
                        QTV = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");
        databaseReference.child(IDGROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mListGroup.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    mListGroup.add(listGroup);
                }
                allMemberAdapter = new AllMemberAdapter(AllMemberActivity.this, mListGroup, IDGROUP, GROUPNAME, QTV, new IClickOpenMG() {
                    @Override
                    public void OpenBSDFragmentMemberGroup(String IdUser) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");

                        databaseReference.child(IDGROUP).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Dem = 0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                                    if( listGroup.getChucvu().equals("Quản trị viên")){
                                        Dem++;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                       BDMemberGroupFragment bdMemberGroupFragment = new BDMemberGroupFragment();
                       Bundle bundle = new Bundle();
                       bundle.putString("IdUser",IdUser);
                       bundle.putString("IdGroup",IDGROUP);
                       bundle.putInt("QTV",Dem);
                       bdMemberGroupFragment.setArguments(bundle);
                       bdMemberGroupFragment.show(getSupportFragmentManager(),bdMemberGroupFragment.getTag());
                    }
                });
                recyclerView.setAdapter(allMemberAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thành Viên");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}