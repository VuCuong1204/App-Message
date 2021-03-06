package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import java.util.HashMap;

public class Doi_mkActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtMatkhaucu,edtMatkhaumoi,edtMatkhauxn;
    EditText edtTaikhoancu,edtTaikhoanmoi,edtTaikhoanxn,edtUsernamemoi;
    Button btnXacnhanMatkhau,btnXacnhanTaikhoan,btnXacnhanUsername;
    String xnmkcu = "";
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mk);
        Anhxa();
        Doimk();
        Doitk();
        Doitentk();
    }

    private void Doitentk() {
        btnXacnhanUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernamemoi = edtUsernamemoi.getText().toString().trim();
                if (!usernamemoi.equals("")){
                    ProgressDialog progressDialog = new ProgressDialog(Doi_mkActivity.this);
                    progressDialog.setMessage("??ang c???p nh???t");
                    progressDialog.show();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put("search",usernamemoi);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap1);
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("username",usernamemoi);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(Doi_mkActivity.this, "C???p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();
                            }else {
                                Toast.makeText(Doi_mkActivity.this, "L???i c???p nh???t vui l??ng tho??t ra ????? x??c nh???n l???i", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            }
                        }
                    });
                }else
                {
                    edtUsernamemoi.setError("Vui l??ng nh???p ????? th??ng tin");
                }
            }
        });
    }

    private void Doitk() {
        btnXacnhanTaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Emailcu = edtTaikhoancu.getText().toString().trim();
                String Emailmoi = edtTaikhoanmoi.getText().toString().trim();
                String Emailxn = edtTaikhoanxn.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(Emailcu.equals("") || Emailmoi.equals("") || Emailxn.equals("")){
                    Toast.makeText(Doi_mkActivity.this, "Vui l??ng nh???p ????? th??ng tin Email", Toast.LENGTH_SHORT).show();
                }else {
                    if(Emailcu.equals(user.getEmail())){
                      if(Emailmoi.equals(Emailxn)){
                          AlertDialog.Builder builder = new AlertDialog.Builder(Doi_mkActivity.this);
                          builder.setTitle("X??c nh???n");
                          builder.setMessage("B???n c?? ch???c mu???n ?????i Email ch???, email m???i c???a b???n s??? l??: " + Emailmoi);
                          builder.setPositiveButton("Ch???c", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                   Intent intent = new Intent(Doi_mkActivity.this,Xacnhan_mk_Activity.class);
                                   intent.putExtra("Emailmoi",Emailmoi);
                                   startActivity(intent);
                              }
                          });

                          builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.cancel();
                              }
                          });

                          builder.show();
                      }else {
                          edtTaikhoanxn.setError("Email nh???p ch??a ????ng");
                      }
                    }else {
                        edtTaikhoancu.setError("Email nh???p ch??a ????ng");
                    }
                }
            }
        });
    }

    private void Doimk() {
        btnXacnhanMatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xnmkcu = "";
                String mkcu = edtMatkhaucu.getText().toString().trim();
                String mkmoi = edtMatkhaumoi.getText().toString().trim();
                String mkmoixn = edtMatkhauxn.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("User");
              valueEventListener =  databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user1 = dataSnapshot.getValue(User.class);
                            if(user1.getId().equals(user.getUid()) ){
                                xnmkcu = user1.getPassword();
                            }
                        }

                        if (xnmkcu.equals(mkcu) ) {
                            if (user != null && !edtMatkhaumoi.getText().toString().trim().equals("")) {
                                if (mkmoi.length() < 6) {
                                    edtMatkhaumoi.setError("M???t kh???u ph???i l???n h??n ho???c b???ng 6 k?? t???");
                                } else {
                                    if (mkmoi.equals(mkmoixn)) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Doi_mkActivity.this);
                                        builder.setTitle("X??c nh???n");
                                        builder.setMessage("B???n c?? ch???c mu???n ?????i m???t kh???u ch???, m???t kh???u m???i c???a b???n s??? l??: " + mkmoi);
                                        builder.setPositiveButton("Ch???c", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ProgressDialog pd = new ProgressDialog(Doi_mkActivity.this);
                                                pd.setMessage("??ang c???p nh???t");
                                                pd.show();
                                                user.updatePassword(mkmoi)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Doi_mkActivity.this,
                                                                            "B???n ???? ?????i m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT).show();
                                                                    edtMatkhaucu.setText("");
                                                                    edtMatkhaumoi.setText("");
                                                                    edtMatkhauxn.setText("");
                                                                    HashMap<String,Object> hashMap = new HashMap<>();
                                                                    hashMap.put("password",mkmoi);
                                                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User");
                                                                    databaseReference1.child(user.getUid()).updateChildren(hashMap);
                                                                    pd.dismiss();

                                                                } else {
                                                                    Toast.makeText(Doi_mkActivity.this, "L???i ?????i m???t kh???u vui l??ng ki???m tra l???i", Toast.LENGTH_SHORT).show();
                                                                    pd.dismiss();
                                                                }
                                                            }
                                                        });

                                            }
                                        });

                                        builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder.show();
                                    } else {
                                        edtMatkhauxn.setError("M???t kh???u ph???i gi???ng m???t kh???u m???i");
                                    }
                                }
                            }
                        }else{
                            if (mkcu.equals("") || mkmoi.equals("") ||  mkmoixn.equals("")) {
                                Toast.makeText(Doi_mkActivity.this, "Vui l??ng ??i???n ?????y ????? th??ng tin", Toast.LENGTH_SHORT).show();
                            }else {
                                if(!xnmkcu.equals(mkcu)){
                                    edtMatkhaucu.setError("M???t kh???u ch??a ch??nh x??c");
                                }
                            }
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

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        edtMatkhaucu = findViewById(R.id.edtMatkhaucu);
        edtMatkhaumoi = findViewById(R.id.edtMatkhaumoi);
        edtMatkhauxn = findViewById(R.id.edtMatkhauxn);
        btnXacnhanMatkhau = findViewById(R.id.btnXacnhan);
        edtTaikhoancu = findViewById(R.id.edtTaikhoancu);
        edtTaikhoanmoi = findViewById(R.id.edtTaikhoanmoi);
        edtTaikhoanxn = findViewById(R.id.edtTaikhoanxn);
        btnXacnhanTaikhoan = findViewById(R.id.btnXacnhan1);
        edtUsernamemoi = findViewById(R.id.edtUsername);
        btnXacnhanUsername = findViewById(R.id.btnXacnhan2);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("C??i ?????t");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Doi_mkActivity.this,MainActivity.class));
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}