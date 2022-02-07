package com.example.chat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

   TextInputLayout textInputLayout1,textInputLayout2;
   TextInputEditText textInputEditTextTk,textInputEditTextMk;
   TextView txtQuenmatkhau;
   CheckBox cbNho;
   Button btnDangnhap,btnDangki;
   FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Anhxa();

        sharedPreferences = getSharedPreferences("Dangnhap",MODE_PRIVATE);
        String email = sharedPreferences.getString("Email","");
        String password = sharedPreferences.getString("Password","");
        Boolean checkbox = sharedPreferences.getBoolean("Checkbox",false);

        textInputEditTextTk.setText(email);
        textInputEditTextMk.setText(password);
        cbNho.setChecked(checkbox);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_emailLogin = textInputEditTextTk.getText().toString().trim();
                String txt_passwordLogin = textInputEditTextMk.getText().toString().trim();

                if(!txt_emailLogin.equals("") && !txt_passwordLogin.equals("") && txt_passwordLogin.length()>5){
                    auth.signInWithEmailAndPassword(txt_emailLogin,txt_passwordLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(cbNho.isChecked()){
                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                   editor.putString("Email",txt_emailLogin);
                                   editor.putString("Password",txt_passwordLogin);
                                   editor.putBoolean("Checkbox",true);
                                   editor.commit();
                                }else {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("Email","");
                                    editor.putString("Password","");
                                    editor.putBoolean("Checkbox",false);
                                    editor.commit();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Thông tin tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    if(txt_emailLogin.equals("")){
                        textInputLayout1.setError("Nhập đủ thông tin");
                    }else {
                        if(txt_passwordLogin.equals("")){
                            textInputLayout2.setError("Vui lòng nhập đủ thông tin");
                        }else {
                            if(txt_passwordLogin.length()<6){
                                textInputLayout2.setError("Mật khẩu ít nhất 6 kí tự bao gồm cả chữ và số");
                            }
                        }
                    }
                }
            }
        });
        txtQuenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnDangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

       textInputEditTextTk.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(!hasFocus){
                   hideKeybroad(v);
               }
           }
       });

       textInputEditTextMk.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(!hasFocus){
                   hideKeybroad(v);
               }
           }
       });
    }

    public void hideKeybroad(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
        private void Anhxa() {
            textInputLayout1 = findViewById(R.id.textinput1);
            textInputLayout2 = findViewById(R.id.textinput2);
            textInputEditTextTk = findViewById(R.id.edtUsername);
            textInputEditTextMk = findViewById(R.id.edtPassword);
            txtQuenmatkhau = findViewById(R.id.textQuenmatkhau);
            cbNho = findViewById(R.id.checkbox);
            btnDangnhap = findViewById(R.id.btnDangnhap);
            btnDangki = findViewById(R.id.btnDangki);

        }
}
