package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Model.Groupchat;
import com.example.chat.Model.ListGroup;
import com.example.chat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class InfoGroupActivity extends AppCompatActivity {

    CircleImageView profilegroup;
    String IDGROUP;
    String NAMEGROUP;
    Toolbar toolbar;
    int Dem = 0;
    int Dem1 = 0;
    TextView txtThemTV,txtTatcaTV,txtRoikhoinhom,txtNAMEGROUP;
    int REQUEST_CODE_FILE = 124;
    Uri imgURL;
    StorageTask UploadTask;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group);
        Intent intent = getIntent();
        IDGROUP = intent.getStringExtra("IDGROUP");
        NAMEGROUP = intent.getStringExtra("NAMEGROUP");
        Anhxa();
        Init();
        InfoGroup();
    }

    private void InfoGroup() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GroupUser");
        databaseReference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                    Groupchat groupchat = dataSnapshot2.getValue(Groupchat.class);
                    if (groupchat.getIdphong().equals(IDGROUP)) {
                        if (groupchat.getProfilegroup().equals("default")) {
                            profilegroup.setImageResource(R.drawable.viewpager1group2);
                        } else {
                            if (getApplicationContext() != null) {
                                Glide.with(getApplicationContext()).load(groupchat.getProfilegroup()).into(profilegroup);
                            }
                        }
                        txtNAMEGROUP.setText(groupchat.getGroupname());
                    }
                }
            }
            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

    private void Init() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");

        databaseReference.child(IDGROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if( listGroup.getChucvu().equals("Quản trị viên")){
                        Dem++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ListGroup");

        databaseReference1.child(IDGROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if (listGroup != null) {
                        Dem1++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



        txtThemTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoGroupActivity.this,AddMemberGroupActivity.class);
                     intent.putExtra("Idgroup",IDGROUP);
                     intent.putExtra("Groupname",NAMEGROUP);
                     if(!(IDGROUP.equals("") && NAMEGROUP.equals(""))) {
                         startActivity(intent);
                     }
            }
        });

        txtTatcaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(InfoGroupActivity.this, AllMemberActivity.class);
                intent1.putExtra("Idgroup",IDGROUP);
                intent1.putExtra("Groupname", NAMEGROUP);
                if (!(IDGROUP.equals("") && NAMEGROUP.equals(""))) {
                    startActivity(intent1);
                }
            }
        });

        txtRoikhoinhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dem1 == 1) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("GroupUser");
                    databaseReference1.child(firebaseUser.getUid()).child(IDGROUP).removeValue();
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ListGroup");
                    databaseReference2.child(IDGROUP).child(firebaseUser.getUid()).removeValue();
                    Intent intent = new Intent(InfoGroupActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (Dem < 2) {
                        Toast.makeText(InfoGroupActivity.this, "Vui lòng chuyển quản trị viên cho thành viên khác", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("GroupUser");
                        databaseReference1.child(firebaseUser.getUid()).child(IDGROUP).removeValue();
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("ListGroup");
                        databaseReference2.child(IDGROUP).child(firebaseUser.getUid()).removeValue();
                        Intent intent = new Intent(InfoGroupActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });


    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        txtThemTV = findViewById(R.id.txtThemthanhvien);
        txtTatcaTV = findViewById(R.id.txtTatcathanhvien);
        txtRoikhoinhom = findViewById(R.id.txtRoikhoinhom);
        profilegroup = findViewById(R.id.profile_image);
        txtNAMEGROUP = findViewById(R.id.textviewTenGroup);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.idphong:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông tin mã phòng");
                builder.setMessage(IDGROUP);
                builder.show();
                break;
            case R.id.doiidten:
                if(!IDGROUP.equals("")) {
                    openFeedbackDialogDoiten();
                }
                break;
            case R.id.doianhnhom:
                if(!IDGROUP.equals("")) {
                    openFile();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFeedbackDialogDoiten(){
        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_doitennhom);
        Window window = dialog1.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog1.setCancelable(true);
        Button btnDongy = dialog1.findViewById(R.id.buttondongy);
        EditText edtDoiten = dialog1.findViewById(R.id.doitennhom);
         edtDoiten.setText(NAMEGROUP);
        btnDongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtDoiten.getText().toString().trim().equals("")){
                    txtNAMEGROUP.setText(edtDoiten.getText().toString().trim());
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("groupname",edtDoiten.getText().toString().trim());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GroupUser");
                    databaseReference.child(fUser.getUid()).child(IDGROUP).updateChildren(hashMap);
                    dialog1.dismiss();
                }
                dialog1.dismiss();
            }
        });

        dialog1.show();

    }

    private void openFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_CODE_FILE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgURL = data.getData();
            if(UploadTask != null && UploadTask.isInProgress()){
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else
            {
                UploadImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadImage() {
        final ProgressDialog pb =  new ProgressDialog(this);
        pb.setMessage("Uploading");
        pb.show();

        if(imgURL != null){
            storageReference = FirebaseStorage.getInstance().getReference("uploads");
            storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imgURL));
            UploadTask = storageReference.putFile(imgURL);
            UploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then( Task task) throws Exception {
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete( Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("profilegroup",mUri);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                        databaseReference.child("GroupUser").child(fUser.getUid()).child(IDGROUP).updateChildren(hashMap);
                        pb.dismiss();
                    }else {
                        Toast.makeText(InfoGroupActivity.this, "Fail!!!", Toast.LENGTH_SHORT).show();
                        pb.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    Toast.makeText(InfoGroupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    pb.dismiss();
                }
            });

        }else{
            Toast.makeText(this,"Dữ liệu lỗi không nhận đưuọc ảnh",Toast.LENGTH_SHORT).show();
        }
    }
}