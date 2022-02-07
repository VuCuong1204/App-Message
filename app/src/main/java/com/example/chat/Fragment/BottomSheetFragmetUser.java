package com.example.chat.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.MessengersActivity;
import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Model.InfoUser;
import com.example.chat.Model.Listketban;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetFragmetUser extends BottomSheetDialogFragment {

    CircleImageView imgProfile;
    ImageView imgNhantin;
    TextView txtUsername,txtGioitinh,txtNamsinh;
    TextView txtDiachi,txtHuy,txtGioithieu;
    BottomSheetDialog bottomSheetDialog ;
    View view;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_user_bottomdialog,null);
        bottomSheetDialog.setContentView(view);
        String IdUser =  getArguments().getString("IdUser");

        Anhxa();
        Init(IdUser);
        return bottomSheetDialog;
    }

    private void Init(String idUser) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getId().equals(idUser)) {
                            txtUsername.setText(user.getUsername());
                            if (user.getImageURL().equals("default")) {
                                imgProfile.setImageResource(R.drawable.user_add);
                            } else {
                                if (getActivity() != null) {
                                    Glide.with(getActivity()).load(user.getImageURL()).into(imgProfile);
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
                        if (infoUser.getIduser().equals(idUser)) {
                            txtGioitinh.setVisibility(View.VISIBLE);
                            txtNamsinh.setVisibility(View.VISIBLE);
                            txtDiachi.setVisibility(View.VISIBLE);
                            txtGioithieu.setVisibility(View.VISIBLE);


                            txtGioitinh.setText(infoUser.getGioitinh());
                            txtNamsinh.setText(infoUser.getNamsinh());
                            txtDiachi.setText(infoUser.getDiachi());
                            txtGioithieu.setText(infoUser.getGioithieu());
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
                Intent intent = new Intent(getContext(), MessengersActivity.class) ;
                intent.putExtra("IdUser",idUser);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if(listketban.getIdUser().equals(idUser)){
                        txtHuy.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
                    databaseReference.child(idUser).child(firebaseUser.getUid()).removeValue();
                    databaseReference.child(firebaseUser.getUid()).child(idUser).removeValue();
                Toast.makeText(getActivity(), "Hủy kết bạn thành công", Toast.LENGTH_SHORT).show();
                    dismiss();
            }
        });
    }

    private void Anhxa() {
        imgProfile = view.findViewById(R.id.profile_image);
        imgNhantin = view.findViewById(R.id.imgNhantin);
        txtUsername = view.findViewById(R.id.textviewName);
        txtGioitinh = view.findViewById(R.id.txtGioitinh);
        txtNamsinh = view.findViewById(R.id.txtNamsinh);
        txtDiachi = view.findViewById(R.id.txtDiachi);
        txtGioithieu = view.findViewById(R.id.txtGioithieu);
        txtHuy = view.findViewById(R.id.txtHuy);
    }
}
