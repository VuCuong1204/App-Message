package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.Model.User;
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

import org.jetbrains.annotations.NotNull;

public class Xacnhan_mk_Activity extends AppCompatActivity {

    EditText edtDoimk;
    Button btnXacnhan;
    String mkcu = "";
    Toolbar toolbar;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xacnhan_mk);

        Intent intent = getIntent();
        String Emailmoi = intent.getStringExtra("Emailmoi");
        edtDoimk = findViewById(R.id.edtMatkhauxn);
        btnXacnhan = findViewById(R.id.btnXacnhan);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Xác nhận mật khẩu");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnXacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mkcu = "";
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if(user.getId().equals(firebaseUser.getUid())){
                                mkcu = user.getPassword();
                            }
                        }

                        if(edtDoimk.getText().toString().trim().equals(mkcu)){
                            ProgressDialog progressDialog = new ProgressDialog(Xacnhan_mk_Activity.this);
                            progressDialog.setMessage("Đang cập nhật");
                            progressDialog.show();
                            firebaseUser.updateEmail(Emailmoi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(Xacnhan_mk_Activity.this,Doi_mkActivity.class));
                                        Toast.makeText(Xacnhan_mk_Activity.this, "Đổi tài khoản email thành công", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }else {
                                        Toast.makeText(Xacnhan_mk_Activity.this, "Lỗi email mới vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Xacnhan_mk_Activity.this, "Mật khẩu không chính xác vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                        }
                        databaseReference.removeEventListener(valueEventListener);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });
    }
}