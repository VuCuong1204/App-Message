package com.example.chat.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.chat.Activity.GroupMessageActivity;
import com.example.chat.Model.Chat;
import com.example.chat.Model.Listchat;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.example.chat.Service.DataService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;

public class Bottom_Sheet_Userchat_Fragment extends BottomSheetDialogFragment {
    BottomSheetDialog bottomSheetDialog ;
    RelativeLayout relativeLayout1,relativeLayout2;
    TextView txtTaonhom;
    ValueEventListener DeleteMessage;
    DatabaseReference databaseReference,databaseReference1;
    String IdUser;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        IdUser = getArguments().getString("IdUser");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_fragment_userchat,null);
        bottomSheetDialog.setContentView(view);
        Anhxa();
        Init();
        CreateGroup();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getId().equals(IdUser)){
                        txtTaonhom.setText("Tạo nhóm với "+user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return bottomSheetDialog;
    }

    private void CreateGroup() {
        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int Idnhom = random.nextInt(10000);
                int Idnhom1 = random.nextInt(10000);
                int Idnhom2 = random.nextInt(10000);

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("groupname", "Vui lòng đặt tên nhóm");
                hashMap.put("profilegroup", "default");
                hashMap.put("Idphong","VQC"+Idnhom+Idnhom1+Idnhom2);
                databaseReference.child("GroupUser").child(fUser.getUid()).child("VQC"+Idnhom+Idnhom1+Idnhom2).setValue(hashMap);
                databaseReference.child("GroupUser").child(IdUser).child("VQC"+Idnhom+Idnhom1+Idnhom2).setValue(hashMap);
                HashMap<String,Object> hashMap1 = new HashMap<>();
                hashMap1.put("IdUser",fUser.getUid());
                hashMap1.put("Chucvu","Quản trị viên");
                databaseReference.child("ListGroup").child("VQC"+Idnhom+Idnhom1+Idnhom2).child(fUser.getUid()).setValue(hashMap1);
                HashMap<String,Object> hashMap2 = new HashMap<>();
                hashMap2.put("IdUser",IdUser);
                hashMap2.put("Chucvu","Thành viên");
                databaseReference.child("ListGroup").child("VQC"+Idnhom+Idnhom1+Idnhom2).child(IdUser).setValue(hashMap2);
                Intent intent = new Intent(getActivity(), GroupMessageActivity.class);
                intent.putExtra("Idgroup","VQC"+Idnhom+Idnhom1+Idnhom2);
                Toast.makeText(getActivity(), "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    private void Init() {
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có chắc muốn ẩn cuộc trò chuyện chứ");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListChats");
                            databaseReference.child(firebaseUser.getUid()).child(IdUser).removeValue();
                            databaseReference.child(IdUser).child(firebaseUser.getUid()).removeValue();
                        bottomSheetDialog.dismiss();
                    }
                });

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void Anhxa() {
        relativeLayout1 = bottomSheetDialog.findViewById(R.id.relativelayout1);
        relativeLayout2 = bottomSheetDialog.findViewById(R.id.relativelayout2);
        txtTaonhom = bottomSheetDialog.findViewById(R.id.textview2);
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
