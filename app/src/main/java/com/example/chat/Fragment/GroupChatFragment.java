package com.example.chat.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chat.Adapter.GroupAdapter;
import com.example.chat.Model.Groupchat;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupChatFragment extends Fragment {

    View view;
    RecyclerView recyclerviewGroup;
    GroupAdapter groupAdapter;
    List<Groupchat> mGroup;


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.group_chat_fragment,container,false);
        recyclerviewGroup = view.findViewById(R.id.recyclerviewGroup);
        recyclerviewGroup.setHasFixedSize(true);
        recyclerviewGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroup =  new ArrayList<>();
      ReadGroup();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        groupAdapter = new GroupAdapter(getContext(),mGroup);
                        recyclerviewGroup.setAdapter(groupAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

        return view;
    }

    private void ReadGroup() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GroupUser");
        databaseReference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mGroup.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Groupchat groupchat = dataSnapshot.getValue(Groupchat.class);
                    mGroup.add(groupchat);
                }
                groupAdapter = new GroupAdapter(getContext(),mGroup);
                recyclerviewGroup.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}

