package com.example.chat.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.AllMemberActivity;
import com.example.chat.Activity.MainActivity;
import com.example.chat.Activity.MessengersActivity;
import com.example.chat.Model.Groupchat;
import com.example.chat.Model.InfoUser;
import com.example.chat.Model.ListGroup;
import com.example.chat.Model.Listketban;
import com.example.chat.Model.User;
import com.example.chat.Notifications.Data;
import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;
import com.example.chat.Notifications.Token;
import com.example.chat.R;
import com.example.chat.Service.APIService;
import com.example.chat.Service.DataService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.encoders.ObjectEncoder;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BDMemberGroupFragment extends BottomSheetDialogFragment {

   public static CircleImageView imgProfile;
   ImageView imgKetban,imgNhantin;
   TextView txtUsername,txtGioitinh,txtNamsinh;
   TextView txtDiachi,txtChidinh,txtXoaTV;
   TextView txtKetban,txtThanhvien;
   ImageView iconXoaTV,iconChucvu;
    boolean QTV = false;
    View view;
    String NAMEUSER;
    BottomSheetDialog bottomSheetDialog;
    String IdUser;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);
        view =LayoutInflater.from(getContext()).inflate(R.layout.layout_membergroup_fragment,null);
        bottomSheetDialog.setContentView(view);
        Anhxa();

        IdUser = getArguments().getString("IdUser");
        String IdGroup = getArguments().getString("IdGroup");
        int Dem = getArguments().getInt("QTV");
        Init(IdUser,IdGroup,Dem);
        BDMemberGroupFragment(IdUser);
        return bottomSheetDialog;
    }



    private void Init(String idUser, String IdGroup, int Dem) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");

        databaseReference.child(IdGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if(listGroup.getIdUser().equals(firebaseUser.getUid())&& listGroup.getChucvu().equals("Qu???n tr??? vi??n")){
                        QTV = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ListGroup");

        databaseReference1.child(IdGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if(QTV == true &&listGroup.getIdUser().equals(idUser)&& listGroup.getChucvu().equals("Qu???n tr??? vi??n")){
                        txtThanhvien.setVisibility(View.VISIBLE);
                    }else{
                        if (QTV == true &&listGroup.getIdUser().equals(idUser)&& listGroup.getChucvu().equals("Th??nh vi??n" )){
                            txtChidinh.setVisibility(View.VISIBLE);
                            txtXoaTV.setVisibility(View.VISIBLE);
                            iconChucvu.setVisibility(View.VISIBLE);
                            iconXoaTV.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        txtThanhvien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dem > 1) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("IdUser", idUser);
                    hashMap.put("Chucvu", "Th??nh vi??n");
                    databaseReference.child(IdGroup).child(idUser).updateChildren(hashMap);
                    Toast.makeText(getActivity(), "Ch??? ?????nh th??nh c??ng", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "Trong nh??m ph???i c?? ??t nh???t m???t qu???n tr??? vi??n", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtChidinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("IdUser",idUser);
                hashMap.put("Chucvu","Qu???n tr??? vi??n");
                databaseReference.child(IdGroup).child(idUser).updateChildren(hashMap);
                Toast.makeText(getActivity(), "Ch??? ?????nh th??nh c??ng", Toast.LENGTH_SHORT).show();
                txtChidinh.setVisibility(View.GONE);
                txtXoaTV.setVisibility(View.GONE);
                iconChucvu.setVisibility(View.GONE);
                iconXoaTV.setVisibility(View.GONE);
            }
        });

        txtXoaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("GroupUser");
                databaseReference1.child(idUser).child(IdGroup).removeValue();
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ListGroup");
                databaseReference2.child(IdGroup).child(idUser).removeValue();
                Toast.makeText(getActivity(), "X??a th??nh c??ng", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }


    private void Anhxa() {
        imgProfile = view.findViewById(R.id.profile_image);
        imgKetban = view.findViewById(R.id.imgKetban);
        imgNhantin = view.findViewById(R.id.imgNhantin);
        txtUsername = view.findViewById(R.id.textviewName);
        txtGioitinh = view.findViewById(R.id.txtGioitinh);
        txtNamsinh = view.findViewById(R.id.txtNamsinh);
        txtDiachi = view.findViewById(R.id.txtDiachi);
        txtChidinh = view.findViewById(R.id.Chucvu);
        txtXoaTV = view.findViewById(R.id.Xoathanhvien);
        txtKetban = view.findViewById(R.id.ketban);
        txtThanhvien = view.findViewById(R.id.ChucvuTV);
        iconChucvu = view.findViewById(R.id.iconchucvu);
        iconXoaTV = view.findViewById(R.id.iconXoaTV);
    }



    private void BDMemberGroupFragment(String IdUser) {


        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fUser.getUid().equals(IdUser)){
            txtKetban.setText("B???n b??");
            imgKetban.setImageResource(R.drawable.friends);
        }

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getId().equals(IdUser)) {
                            txtUsername.setText(user.getUsername());
                            if (user.getImageURL().equals("default")) {
                                imgProfile.setImageResource(R.drawable.user_add);
                            } else {
                                if (getContext() != null) {
                                    Glide.with(getContext()).load(user.getImageURL()).into(imgProfile);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("InfoUser");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InfoUser infoUser = dataSnapshot.getValue(InfoUser.class);
                    if (infoUser != null) {
                        if (infoUser.getIduser().equals(IdUser)) {
                            txtGioitinh.setVisibility(View.VISIBLE);
                            txtNamsinh.setVisibility(View.VISIBLE);
                            txtDiachi.setVisibility(View.VISIBLE);

                            txtGioitinh.setText(infoUser.getGioitinh());
                            txtNamsinh.setText(infoUser.getNamsinh());
                            txtDiachi.setText(infoUser.getDiachi());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("ListFriends");
        databaseReference3.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if (listketban != null) {
                        if (listketban.getIdUser().equals(IdUser)) {
                            imgKetban.setImageResource(R.drawable.friends);
                            txtKetban.setText(listketban.getTitleAdd());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
        databaseReference.child(IdUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if(listketban != null){
                        if(listketban.getIdUser().equals(fUser.getUid())){
                            imgKetban.setImageResource(R.drawable.loading);
                            txtKetban.setText(listketban.getTitleAdd());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


            imgNhantin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MessengersActivity.class);
                    intent.putExtra("IdUser", IdUser);
                    startActivity(intent);
                }
            });

            imgKetban.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User user1 = dataSnapshot.getValue(User.class);
                                if(user1.getId().equals(fUser.getUid())){
                                    NAMEUSER = user1.getUsername();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                    if (txtKetban.getText().equals("k???t b???n")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("idUser", fUser.getUid());
                        hashMap.put("titleAdd", "Vui l??ng ch???");
                        txtKetban.setText("Vui l??ng ch???");
                        databaseReference.child(IdUser).child(fUser.getUid()).setValue(hashMap);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("idUser", IdUser);
                        hashMap1.put("titleAdd", "X??c nh???n");
                        databaseReference.child(fUser.getUid()).child(IdUser).setValue(hashMap1);
                        imgKetban.setImageResource(R.drawable.loading);

                        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                        Query query = tokens.orderByKey().equalTo(IdUser);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Token token = dataSnapshot.getValue(Token.class);
                                    Data data = new Data(fUser.getUid(),R.drawable.addfriend,NAMEUSER+" ???? g???i cho b???n m???t l???i m???i k???t b???n","Th??ng b??o",IdUser,"0");

                                    Sender sender = new Sender(data,token.getToken());
                                    DataService dataService = APIService.getService();
                                    Call<MyResponse> callback = dataService.sendNotification(sender);
                                    callback.enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if(response.code() == 200){
                                                if(response.body().success != 1){
                                                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    } else {
                        if (txtKetban.getText().equals("Vui l??ng ch???")) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
                            databaseReference.child(IdUser).child(fUser.getUid()).removeValue();
                            databaseReference.child(fUser.getUid()).child(IdUser).removeValue();
                            txtKetban.setText("K???t b???n");
                            imgKetban.setImageResource(R.drawable.user);
                        } else {
                            if (txtKetban.getText().equals("X??c nh???n")) {
                                txtKetban.setText("B???n b??");
                                HashMap<String, Object> hashMap2 = new HashMap<>();
                                hashMap2.put("idUser", IdUser);
                                hashMap2.put("titleAdd", "B???n b??");
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
                                databaseReference.child(fUser.getUid()).child(IdUser).setValue(hashMap2);
                                HashMap<String, Object> hashMap3 = new HashMap<>();
                                hashMap3.put("idUser", fUser.getUid());
                                hashMap3.put("titleAdd", "B???n b??");
                                databaseReference.child(IdUser).child(fUser.getUid()).setValue(hashMap3);
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Listketban");
                                databaseReference1.child(fUser.getUid()).child(IdUser).removeValue();
                                databaseReference1.child(IdUser).child(fUser.getUid()).removeValue();
                                imgKetban.setImageResource(R.drawable.friends);

                                DatabaseReference tokens=  FirebaseDatabase.getInstance().getReference("Tokens");
                                Query query = tokens.orderByKey().equalTo(IdUser);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Token token = dataSnapshot.getValue(Token.class);
                                            Data data = new Data(fUser.getUid(),R.drawable.addfriend, NAMEUSER+" ???? ch???p nh???n l???i m???i k???t b???n, b??y gi??? hai b???n ???? tr??? th??nh b???n b??","Th??ng b??o",
                                                    IdUser,"0");

                                            Sender sender = new Sender(data,token.getToken());
                                            DataService dataService = APIService.getService();
                                            Call<MyResponse> callback = dataService.sendNotification(sender);
                                            callback.enqueue(new Callback<MyResponse>() {
                                                @Override
                                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                    if (response.code() == 200){
                                                        if(response.body().success != 1){
                                                            Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            });
    }

}
