package com.example.chat.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chat.Activity.DialogSheetUser;
import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Model.ChanUser;
import com.example.chat.Model.Chat;
import com.example.chat.Model.Listchat;
import com.example.chat.Model.User;
import com.example.chat.Notifications.Token;
import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> mUsers;
    List<String> mChanUsers;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<String> listID;
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.chats_fragment,container,false);
       recyclerView = view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

       listID = new ArrayList<>();
       mChanUsers = new ArrayList<>();

       databaseReference = FirebaseDatabase.getInstance().getReference("ListChats").child(firebaseUser.getUid());
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange( DataSnapshot snapshot) {
               listID.clear();
               for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  Listchat listchat = dataSnapshot.getValue(Listchat.class);

                  listID.add(listchat.getId());
               }
               readChats();
           }

           @Override
           public void onCancelled( DatabaseError error) {

           }
       });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChanUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if(chanUser.getIdChan().equals(firebaseUser.getUid())){
                        mChanUsers.add(chanUser.getIdBiChan());
                    }
                    if (chanUser.getIdBiChan().equals(firebaseUser.getUid())){
                        mChanUsers.add(chanUser.getIdChan());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

       FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
           @Override
           public void onComplete(Task<String> task) {
               if(task.isSuccessful()){
                   String token2 = task.getResult();
                   updateToken(token2);
               }
           }
       });
        swipeRefreshLayout = view.findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(userAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

    return view;
    }


    private void updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }
    private void readChats(){
        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(mChanUsers.size() != 0) {
                        for (String Id : listID) {
                            for (String chanUser : mChanUsers) {
                                if (user.getId().equals(Id) && !user.getId().equals(chanUser)) {
                                    mUsers.add(user);
                                }
                            }
                        }
                    }else {
                        for (String Id : listID) {
                            if (user.getId().equals(Id)) {
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, true, new DialogSheetUser() {
                        @Override
                        public void OpenSheetDialogUser(String IdUser) {
                          Bottom_Sheet_Userchat_Fragment bottom_sheet_userchat_fragment = new Bottom_Sheet_Userchat_Fragment();
                          Bundle bundle =  new Bundle();
                          bundle.putString("IdUser",IdUser);
                          bottom_sheet_userchat_fragment.setArguments(bundle);
                          bottom_sheet_userchat_fragment.show(getActivity().getSupportFragmentManager(),bottom_sheet_userchat_fragment.getTag());
                        }
                    });
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }
}
