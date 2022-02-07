package com.example.chat.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class BottomSheetFragmentCT extends BottomSheetDialogFragment {
    BottomSheetDialog bottomSheetDialog;
    ImageView img_profile;
    TextView txt_Thongtin,txt_Tinnhan,txt_User;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheetfragment_ct,null);
        bottomSheetDialog.setContentView(view);
        Chat chat = (Chat) getArguments().getSerializable("Chat");
        Anhxa();
        Init(chat);
        return bottomSheetDialog;
    }

    private void Anhxa() {
        img_profile = bottomSheetDialog.findViewById(R.id.profile_image);
        txt_Tinnhan = bottomSheetDialog.findViewById(R.id.txtTinnhan);
        txt_Thongtin = bottomSheetDialog.findViewById(R.id.txtThongtin);
        txt_User = bottomSheetDialog.findViewById(R.id.txtUsername);
    }

    private void Init(Chat chat) {
        txt_Tinnhan.setText(chat.getMessage());
        txt_Thongtin.setText(chat.getInfor());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getId().equals(chat.getSender())){
                        txt_User.setText(user.getUsername());
                        if(user.getImageURL().equals("default")){
                            img_profile.setImageResource(R.drawable.user_add);
                        }else {
                            if (getActivity() != null) {
                                Glide.with(getActivity()).load(user.getImageURL()).into(img_profile);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
