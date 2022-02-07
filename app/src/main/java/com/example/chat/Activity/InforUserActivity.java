package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Model.ChanUser;
import com.example.chat.Model.Chat;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class InforUserActivity extends AppCompatActivity {

     Toolbar toolbar;
     CircleImageView img_profile,img_on,img_off;
     TextView txtUsername,txtChan;
     String IdUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_user);

        Intent intent = getIntent();
        IdUser = intent.getStringExtra("IdUser");
        Anhxa();

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if ((chanUser.getIdChan().equals(fUser.getUid()) && chanUser.getIdBiChan().equals(IdUser)) ||
                            (chanUser.getIdChan().equals(IdUser) && chanUser.getIdBiChan().equals(fUser.getUid()))) {
                        txtChan.setVisibility(View.GONE);
                        img_off.setVisibility(View.GONE);
                        img_on.setVisibility(View.GONE);
                        img_profile.setImageResource(R.drawable.user_add);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        Init();

    }

    private void Init() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getId().equals(IdUser)){
                        txtUsername.setText(user.getUsername());
                        if(user.getImageURL().equals("default")){
                            if(user.getStatus().equals("online")){
                                img_on.setVisibility(View.VISIBLE);
                                img_off.setVisibility(View.GONE);
                            }else {
                                img_on.setVisibility(View.GONE);
                                img_off.setVisibility(View.VISIBLE);
                            }
                            img_profile.setImageResource(R.drawable.user_add);
                        } else {
                            Glide.with(getApplicationContext()).load(user.getImageURL()).into(img_profile);
                            }
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        txtChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Hopthoai();
            }
        });
    }
    private void Hopthoai(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn có chắc muốn chặn cuộc trò chuyện này chứ ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("IdChan",firebaseUser.getUid());
                hashMap.put("IdBiChan",IdUser);
               databaseReference.push().setValue(hashMap);
               Toast.makeText(InforUserActivity.this,"Cuộc trò chuyện đã bị chặn",Toast.LENGTH_SHORT).show();
            txtChan.setVisibility(View.GONE);
            img_on.setVisibility(View.GONE);
            img_off.setVisibility(View.GONE);
            img_profile.setImageResource(R.drawable.user_add);
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

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        img_profile = findViewById(R.id.profile_image);
        txtChan = findViewById(R.id.txtChan);
        txtUsername = findViewById(R.id.username);
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thông tin");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}