package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.chat.Adapter.AddMemberAdapter;
import com.example.chat.Model.ListGroup;
import com.example.chat.Model.Listketban;
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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

public class AddMemberGroupActivity extends AppCompatActivity {
  Toolbar toolbar;
  RecyclerView recyclerView;
  List<String> ID;
  List<String> ID1;
  AddMemberAdapter addMemberAdapter;
  List<User> arrayUser;
  String IDGROUP;
  String GROUPNAME;
  int Dem = 0;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_group);

      Intent intent = getIntent();
      IDGROUP = intent.getStringExtra("Idgroup");
      GROUPNAME = intent.getStringExtra("Groupname");
        Anhxa();

       arrayUser = new ArrayList<>();
       ID = new ArrayList<>();
       ID1 = new ArrayList<>();
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(AddMemberGroupActivity.this));
       Init();
      SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sRefresh);
      swipeRefreshLayout.setColorSchemeResources(R.color.black);
      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      recyclerView.setAdapter(addMemberAdapter);
                      swipeRefreshLayout.setRefreshing(false);
                  }
              },2500);
          }
      });
    }

    private void Init(){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ListGroup");
        databaseReference2.child(IDGROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ID1.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    ID1.add(listGroup.getIdUser());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
        databaseReference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ID.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    for (String id1 : ID1) {
                        if (listketban.getIdUser().equals(id1)) {
                            Dem = 1;
                        }
                    }
                    if(Dem==0){
                        ID.add(listketban.getIdUser());
                    }
                    Dem = 0;
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
                arrayUser.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for(String id1 : ID){
                        if(user.getId().equals(id1)){
                            arrayUser.add(user);
                        }
                    }
                }
                addMemberAdapter = new AddMemberAdapter(AddMemberGroupActivity.this,arrayUser,IDGROUP,GROUPNAME);
                recyclerView.setAdapter(addMemberAdapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    private void Anhxa() {
        recyclerView= findViewById(R.id.recyclerviewAddMember);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}