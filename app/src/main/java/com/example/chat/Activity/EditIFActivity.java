package com.example.chat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class EditIFActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtDiachi,edtGioithieu;
    RadioButton rdnNam, rdnNu;
    DatePicker datePicker;
    Button btnXacnhan;
    String gioitinh = "";
    String namsinh ="";
    String diachi ="";
    String gioithieu="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ifactivity);

        Anhxa();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                namsinh = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
            }
        });


         diachi = edtDiachi.getText().toString().trim();
         gioithieu = edtGioithieu.getText().toString().trim();

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
        EditInfo();

    }

    private void EditInfo() {

        btnXacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("InfoUser");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Iduser",fUser.getUid());
        hashMap.put("Gioitinh",gioitinh);
        hashMap.put("Namsinh",namsinh);
        hashMap.put("Diachi",diachi);
        hashMap.put("Gioithieu",gioithieu);

        databaseReference.child(fUser.getUid()).updateChildren(hashMap);
        Toast.makeText(EditIFActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        rdnNam = findViewById(R.id.radiobtnNam);
        rdnNu = findViewById(R.id.radiobtnNu);
        datePicker = findViewById(R.id.edtNamsinh);
        edtDiachi = findViewById(R.id.edtDiachi);
        edtGioithieu = findViewById(R.id.edtGioithieu);
        btnXacnhan = findViewById(R.id.btnXacnhan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sửa thông tin cá nhân");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}