package com.example.chat.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edt_sendemail;
    Button btn_reset;
     FirebaseAuth fAuth;
     TextView txtThongbao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Anhxa();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_sendemail.getText().toString().trim();
                if (!email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                    builder.setTitle("Xác nhận");
                    builder.setMessage("Bạn có chắc muốn lấy lại mật khẩu không?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        txtThongbao.setText("Vui lòng vào email đăng kí làm theo hướng dẫn để lấy lại mật khẩu");

                                    } else {
                                        txtThongbao.setText("Email chưa đúng hoặc chưa đăng kí tài khoản");
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
            }
        });
    }
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    if(getCurrentFocus() != null){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
    return super.dispatchTouchEvent(ev);
}
    private void Anhxa() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lấy mật khẩu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edt_sendemail = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);
        fAuth = FirebaseAuth.getInstance();
        txtThongbao = findViewById(R.id.txtThongbao);
    }
}