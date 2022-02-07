package com.example.chat.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Activity.DanhsachchanActivity;
import com.example.chat.Activity.DialogSheetUser;
import com.example.chat.Adapter.LoimoiAdapter;
import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Model.ChanUser;
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
import java.util.List;

public class UsersFragment extends Fragment {
    View view;
    RecyclerView recyclerView,recyclerViewLoimoi;
    TextView txtLoimoi,txtDSHuy;
    UserAdapter userAdapter;
    FirebaseUser firebaseUser;
    List<User> mUsers;
    List<String> mId,mId1,mChanUser;
    List<User> mAddUsers;
    LoimoiAdapter loimoiAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_fragment, container, false);
        txtLoimoi = view.findViewById(R.id.textviewLoimoi);
        recyclerViewLoimoi = view.findViewById(R.id.recyclerviewLoimoi);
        recyclerViewLoimoi.setHasFixedSize(true);
        recyclerViewLoimoi.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView = view.findViewById(R.id.recyclerviewUser);
        txtDSHuy = view.findViewById(R.id.txtXoaChan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        mId =  new ArrayList<>();
        mId1 = new ArrayList<>();
        mChanUser = new ArrayList<>();

        mAddUsers = new ArrayList<>();
        readLoimoi();
        readUsers();
        readXoaChan();
         txtLoimoi.setVisibility(View.GONE);
        return view;
    }

    private void readXoaChan() {
        txtDSHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DanhsachchanActivity.class));
            }
        });
    }

    private void readLoimoi() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mId.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if(!listketban.getTitleAdd().equals("Xác nhận")) {
                        mId.add(listketban.getIdUser());
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
                mAddUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user =  dataSnapshot.getValue(User.class);
                    for ( String idUser : mId) {
                            if (user.getId().equals(idUser)) {
                                mAddUsers.add(user);
                                txtLoimoi.setVisibility(View.VISIBLE);
                            }
                    }
                }
                loimoiAdapter = new LoimoiAdapter(getActivity(), mAddUsers, new DialogSheetUser() {
                    @Override
                    public void OpenSheetDialogUser(String IdUser) {
                        BottomSheetFragmetUser bottomSheetFragmetUser = new BottomSheetFragmetUser();
                        Bundle bundle = new Bundle();
                        bundle.putString("IdUser",IdUser);
                        bottomSheetFragmetUser.setArguments(bundle);
                        bottomSheetFragmetUser.show(getActivity().getSupportFragmentManager(), bottomSheetFragmetUser.getTag());
                    }
                });
                recyclerViewLoimoi.setAdapter(loimoiAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChanUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if(chanUser.getIdChan().equals(firebaseUser.getUid())){
                        mChanUser.add(chanUser.getIdBiChan());
                    }
                    if (chanUser.getIdBiChan().equals(firebaseUser.getUid())){
                        mChanUser.add(chanUser.getIdChan());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    mId1.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Listketban listketban = snapshot1.getValue(Listketban.class);
                        mId1.add(listketban.getIdUser());
                    }
                }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                   User user = snapshot1.getValue(User.class);
                    if(mChanUser.size() != 0) {
                        for (String Id : mId1) {
                            for (String chanUser : mChanUser) {
                                if (user.getId().equals(Id) && !user.getId().equals(chanUser)) {
                                    mUsers.add(user);
                                }
                            }
                        }
                    }else {
                        for (String Id : mId1) {
                            if (user.getId().equals(Id)) {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, false, new DialogSheetUser() {
                    @Override
                    public void OpenSheetDialogUser(String IdUser) {
                        BottomSheetFragmetUser bottomSheetFragmetUser = new BottomSheetFragmetUser();
                        Bundle bundle = new Bundle();
                        bundle.putString("IdUser",IdUser);
                        bottomSheetFragmetUser.setArguments(bundle);
                        bottomSheetFragmetUser.show(getActivity().getSupportFragmentManager(), bottomSheetFragmetUser.getTag());
                    }
                });
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
}
