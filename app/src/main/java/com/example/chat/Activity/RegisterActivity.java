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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmail,edtPassword,edtUsername,edtDiachi,edtGioithieu;
    Button btnXacnhan;
    RadioButton rdnNam,rdnNu;
    DatePicker datePicker;
    Toolbar toolbar;
    FirebaseAuth auth;
    DatabaseReference reference;
    TextInputLayout inputLayout1, inputLayout2;
    String email,password,username,diachi,gioithieu;
    String gioitinh = "";
    String namsinh = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Anhxa();
        Init();
        auth = FirebaseAuth.getInstance();
        //Giới tính
        rdnNam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gioitinh = "Nam";
                }
            }
        });

        rdnNu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gioitinh ="Nữ";
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Năm sinh
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                namsinh = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
            }
        });


        btnXacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                diachi = edtDiachi.getText().toString().trim();
                gioithieu = edtGioithieu.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                username = edtUsername.getText().toString().trim();

                if (!email.equals("")) {
                        if (!password.equals("")) {
                            if (password.length() > 5) {
                                if (!username.equals("")) {
                                    if (!rdnNam.isChecked() && !rdnNu.isChecked()) {
                                        Toast.makeText(RegisterActivity.this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Dangki();
                                    }
                                }else {
                                        inputLayout2.setError("Vui lòng nhập đủ tên tài khoản");
                                    }
                            }else {
                                    inputLayout1.setError("Mật khẩu tối thiểu 6 kí tự");
                                }
                        }else {
                                inputLayout1.setError("Vui lòng nhập đủ mật khẩu");
                            }
                    }else {
                       edtEmail.setError("Vui lòng nhập email");
                    }
            }
        });

    }

    private void Init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Đăng kí");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
//
    private void Anhxa() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtDiachi = findViewById(R.id.edtDiachi);
        edtGioithieu = findViewById(R.id.edtGioithieu);
        rdnNam = findViewById(R.id.radiobtnNam);
        rdnNu = findViewById(R.id.radiobtnNu);
        datePicker  = findViewById(R.id.edtNamsinh);
        btnXacnhan = findViewById(R.id.btnXacnhan);
        toolbar = findViewById(R.id.toolbar);
        inputLayout1 = findViewById(R.id.textinputlayout1);
        inputLayout2 = findViewById(R.id.textinputlayout2);
    }

    private void Dangki(){

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc muốn đăng kí tài khoản chứ");
        builder.setPositiveButton("Chắc", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Đang cập nhật");
                pd.show();
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                   @Override
                                                   public void onComplete(Task<AuthResult> task) {
                                                       if(task.isSuccessful()){
                                                           FirebaseUser firebaseUser = auth.getCurrentUser();
                                                           assert firebaseUser != null;
                                                           String userid = firebaseUser.getUid();

                                                           reference = FirebaseDatabase.getInstance().getReference("User").child(userid);
                                                           HashMap<String,String> hashMap = new HashMap<>();
                                                           hashMap.put("id",userid);
                                                           hashMap.put("username",username);
                                                           hashMap.put("imageURL","default");
                                                           hashMap.put("status","offline");
                                                           hashMap.put("search",username.toLowerCase());
                                                           hashMap.put("password",password);
                                                           reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                   if(task.isSuccessful()){
                                                                       HashMap<String,Object> hashMap1 = new HashMap<>();
                                                                       hashMap1.put("Iduser",userid);
                                                                       hashMap1.put("Gioitinh",gioitinh);
                                                                       hashMap1.put("Namsinh",namsinh);
                                                                       hashMap1.put("Diachi",diachi);
                                                                       hashMap1.put("Gioithieu",gioithieu);
                                                                       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("InfoUser");
                                                                       databaseReference.child(userid).setValue(hashMap1);
                                                                       startActivity(new Intent(RegisterActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                                                   pd.dismiss();
                                                                   }
                                                               }
                                                           });
                                                       }else{
                                                        edtEmail.setError("Email không đúng hoặc đã tồn tại");
                                                        pd.dismiss();
                                                       }
                                                   }
                                               });
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
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}