package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageGroupadapter;
import com.example.chat.Model.Groupchat;
import com.example.chat.Model.ListGroup;
import com.example.chat.Model.ListMessageGroup;
import com.example.chat.Model.Listchat;
import com.example.chat.Model.User;
import com.example.chat.Notifications.Data;
import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;
import com.example.chat.Notifications.Token;
import com.example.chat.R;
import com.example.chat.Service.APIService;
import com.example.chat.Service.DataService;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView profilegroup;
    TextView txtGroupname,txtIdphong,txtSLThanhvien;
    EditText edtSendgroup;
    ImageButton btnSendgroup;
    DatabaseReference databaseReference;
    FirebaseUser fUser;
    RecyclerView recyclerViewMsgGroup;
    MessageGroupadapter messageGroupadapter;
    List<ListMessageGroup> listMessageGroups;
    String c,d;
    int sl;
    String idgroup;
    List<String> mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        Anhxa();
        mUser = new ArrayList<>();
        Intent intent = getIntent();
         idgroup = intent.getStringExtra("Idgroup");
        Init();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupUser");
        databaseReference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                    Groupchat groupchat = dataSnapshot2.getValue(Groupchat.class);
                    if (groupchat.getIdphong().equals(idgroup)) {
                            if (groupchat.getProfilegroup().equals("default")) {
                                profilegroup.setImageResource(R.drawable.viewpager1group2);
                            } else {
                                if (getApplicationContext() != null) {
                                    Glide.with(getApplicationContext()).load(groupchat.getProfilegroup()).into(profilegroup);
                                }
                            }
                            txtGroupname.setText(groupchat.getGroupname());
                            txtIdphong.setText(groupchat.getIdphong());
                    }
                }
            }
            @Override
            public void onCancelled( DatabaseError error) {

            }
        });


                    fUser = FirebaseAuth.getInstance().getCurrentUser();
                    databaseReference = FirebaseDatabase.getInstance().getReference("User");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (fUser != null) {
                                    if (user.getId().equals(fUser.getUid())) {
                                        c = user.getUsername();
                                        d = user.getImageURL();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
              btnSendgroup.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      String msg = edtSendgroup.getText().toString();
                      if(!msg.equals("")){
                          SendMessageGroup(msg,c,d);
                      }
                      edtSendgroup.setText("");
                  }
              });


        recyclerViewMsgGroup.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMsgGroup.setLayoutManager(linearLayoutManager);
        listMessageGroups = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(fUser != null) {
                        if (user.getId().equals(fUser.getUid())) {
                            if (!txtIdphong.getText().toString().equals("")) {
                                ReadMessageGroup();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewMsgGroup.setAdapter(messageGroupadapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

    }

    private void ReadMessageGroup(){
        databaseReference = FirebaseDatabase.getInstance().getReference("ListGroupChat");
        databaseReference.child(txtIdphong.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                listMessageGroups.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListMessageGroup listMessageGroup = dataSnapshot.getValue(ListMessageGroup.class);
                    listMessageGroups.add(listMessageGroup);
                }
                messageGroupadapter = new MessageGroupadapter(GroupMessageActivity.this, listMessageGroups, new IMessageGroup() {
                    @Override
                    public void DeleteMSG(ListMessageGroup listMessageGroup) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
                        String[] arrayMSG = {"Xóa, Gỡ bỏ"};
                        builder.setItems(arrayMSG, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupMessageActivity.this);
                                        builder1.setTitle("Thông báo");
                                        builder1.setMessage("Bạn có chắc muốn xóa chứ ?");
                                        builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroupChat");
                                                databaseReference.child(txtIdphong.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            ListMessageGroup listMessageGroup1 = dataSnapshot.getValue(ListMessageGroup.class);
                                                            if (listMessageGroup1.getSenduser().equals(listMessageGroup.getSenduser())
                                                                    && listMessageGroup1.getMessagegroup().equals(listMessageGroup.getMessagegroup())
                                                                    && listMessageGroup1.getThoigian().equals(listMessageGroup.getThoigian())) {
                                                                dataSnapshot.getRef().removeValue();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder1.show();
                                        break;
                                }
                            }
                        });
                        if (listMessageGroup.getSenduser().equals(fUser.getUid())) {
                            builder.show();
                        }
                    }
                });
                recyclerViewMsgGroup.setAdapter(messageGroupadapter);
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }
    private void SendMessageGroup(String msgGroup,String username, String profileUser) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dinhdangngay = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dinhdanggio = new SimpleDateFormat("hh:mm:ss a");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("ListGroupChat");
        if (fUser != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("senduser", fUser.getUid());
            hashMap.put("messagegroup", msgGroup);
            hashMap.put("username", username);
            hashMap.put("profileUser", profileUser);
            hashMap.put("Thoigian","Đã gửi vào "+ dinhdanggio.format(calendar.getTime())+","+dinhdangngay.format(calendar.getTime()));
            databaseReference.child(txtIdphong.getText().toString().trim()).push().setValue(hashMap);
//            SendNotification(fUser.getUid(),msgGroup,txtGroupname.getText().toString().trim(),txtIdphong.getText().toString().trim(),profileUser);
        }
    }

    private void Init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");
        databaseReference.child(idgroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                sl = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
                    if(listGroup != null){
                       sl++;
                    }
                }
                txtSLThanhvien.setText(sl+" Thành viên");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        profilegroup = findViewById(R.id.profile_group_chat);
        txtGroupname = findViewById(R.id.groupname);
        txtSLThanhvien = findViewById(R.id.txtSLthanhvien);
        edtSendgroup = findViewById(R.id.edittext_sendgroup);
        btnSendgroup = findViewById(R.id.btn_sendgroup);
        recyclerViewMsgGroup = findViewById(R.id.recyclerview_msgGroup);
        txtIdphong = findViewById(R.id.txtIdphong);
    }
//    private void SendNotification(String GroupName,String msgGroup, String Profile, String Username, String Idphong){
//
//        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ListGroup");
//        databaseReference1.child(idgroup).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//               mUser.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    ListGroup listGroup = dataSnapshot.getValue(ListGroup.class);
//                    mUser.add(listGroup.getIdUser());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//
//        });
//        for (String receiver : mUser) {
//            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//            Query query = tokens.orderByKey().equalTo(receiver);
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        Token token = dataSnapshot.getValue(Token.class);
//                        Data data = new Data(fUser.getUid(), R.mipmap.ic_launcher, Username+": "+msgGroup,GroupName ,
//                                Idphong,"2");
//
//                        Sender sender = new Sender(data, token.getToken());
//                        DataService dataService = APIService.getService();
//                        Call<MyResponse> callback = dataService.sendNotification(sender);
//                        callback.enqueue(new Callback<MyResponse>() {
//                            @Override
//                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                if (response.code() == 200) {
//                                    if (response.body().success != 1) {
//                                        Toast.makeText(GroupMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//
//                }
//            });
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_group,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iconGroup:
                Intent intent = new Intent(GroupMessageActivity.this,InfoGroupActivity.class);
                intent.putExtra("IDGROUP",txtIdphong.getText().toString());
                intent.putExtra("NAMEGROUP",txtGroupname.getText().toString());
                if(!(txtIdphong.getText().toString().equals("") && txtGroupname.getText().toString().equals(""))){
                    startActivity(intent);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void status(String status){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}