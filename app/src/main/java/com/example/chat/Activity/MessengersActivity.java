package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.Fragment.BottomSheetFragmentCT;
import com.example.chat.Model.ChanUser;
import com.example.chat.Model.Listchat;
import com.example.chat.R;
import com.example.chat.Service.APIService;
import com.example.chat.Service.DataService;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.Notifications.Data;
import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;
import com.example.chat.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  MessengersActivity extends AppCompatActivity{

    Toolbar toolbar;
    CircleImageView circleImageView, img_on, img_off;
    TextView txt_username, txtHoatdong, txtChan;
    EditText edt_send;
    DatabaseReference databaseReference;
    FirebaseUser fUser;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    ImageButton btn_send;
    String IdUser;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    ValueEventListener seenListener;

    boolean notifi = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messengers);

        Init();
        Intent intent = getIntent();
        IdUser = intent.getStringExtra("IdUser");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        ChanTrochuyen();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(IdUser);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    txt_username.setText(user.getUsername());
                    if (user.getStatus().equals("online")) {
                        img_on.setVisibility(View.VISIBLE);
                        img_off.setVisibility(View.GONE);
                        txtHoatdong.setText("Đang hoạt động");
                    } else {
                        img_on.setVisibility(View.GONE);
                        img_off.setVisibility(View.VISIBLE);
                        txtHoatdong.setText("Đang offline");
                    }
                    if (user.getImageURL().equals("default")) {
                        circleImageView.setImageResource(R.drawable.user_add);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(circleImageView);
                    }
                    readMessenger(fUser.getUid(), IdUser, user.getImageURL());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), IdUser, msg);
                    notifi = true;
                } else {
                    Toast.makeText(MessengersActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                edt_send.setText("");
            }
        });


    }


    private void ChanTrochuyen() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if ((chanUser.getIdChan().equals(fUser.getUid()) && chanUser.getIdBiChan().equals(IdUser))
                            || chanUser.getIdChan().equals(IdUser) && chanUser.getIdBiChan().equals(fUser.getUid())) {
                        txtChan.setText("Cuộc trò chuyện này đã bị chặn ");
                        txtChan.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        img_off.setVisibility(View.GONE);
                        img_on.setVisibility(View.GONE);
                        circleImageView.setImageResource(R.drawable.user_add);
                        txtHoatdong.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
                        recyclerView.setAdapter(messageAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });

    }


    private void sendMessage(String sender, String receiver, String message) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dinhdangngay = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dinhdanggio = new SimpleDateFormat("hh:mm a");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("soluong", 0);
        hashMap.put("infor", "Đã gửi vào " + dinhdanggio.format(calendar.getTime()) + "," + dinhdangngay.format(calendar.getTime()));
        hashMap.put("seen", "Đã nhận " + dinhdanggio.format(calendar.getTime()) + "," + dinhdangngay.format(calendar.getTime()));
        databaseReference.child("Chats").push().setValue(hashMap);


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ListChats")
                .child(fUser.getUid())
                .child(IdUser);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    reference.child("Id").setValue(IdUser);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ListChats")
                .child(IdUser)
                .child(fUser.getUid());
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    reference1.child("Id").setValue(fUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        String msg1 = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notifi) {
                    sendNotification(receiver, user.getUsername(), msg1);
                }
                notifi = false;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String username, String msg1) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(fUser.getUid(), R.mipmap.ic_launcher, "" + msg1, username,
                            IdUser,"1");

                    Sender sender = new Sender(data, token.getToken());
                    DataService dataService = APIService.getService();
                    Call<MyResponse> callback = dataService.sendNotification(sender);
                    callback.enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
                                    Toast.makeText(MessengersActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void Init() {
        recyclerView = findViewById(R.id.recyclerviewChat);
        edt_send = findViewById(R.id.text_send);
        btn_send = findViewById(R.id.btn_send);
        toolbar = findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        txtHoatdong = findViewById(R.id.txtHoatdong);
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        txtChan = findViewById(R.id.txtChan);
        linearLayout = findViewById(R.id.linearlayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessengersActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private void readMessenger(String myid, String userid, String imgurl) {
        mchat = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if ((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))) {
                        mchat.add(chat);
                    }
                }

                    messageAdapter = new MessageAdapter(MessengersActivity.this, mchat, imgurl, new IDialogList() {
                        @Override
                        public void OpenDialogList(Chat chat, TextView txt_show_message) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MessengersActivity.this);
                            String[] animals = {"Chi tiết", "Xóa"};
                            builder.setItems(animals, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            BottomSheetFragmentCT bottomSheetFragmentCT = new BottomSheetFragmentCT();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("Chat", chat);
                                            bottomSheetFragmentCT.setArguments(bundle);
                                            bottomSheetFragmentCT.show(getSupportFragmentManager(), bottomSheetFragmentCT.getTag());
                                            break;
                                        case 1:
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MessengersActivity.this);
                                            builder1.setTitle("Thông báo");
                                            builder1.setMessage("bạn có chắc muốn xóa chứ ?");
                                            builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Chats");
                                                    seenListener = databaseReference1.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                                Chat chat1 = dataSnapshot1.getValue(Chat.class);
                                                                if (chat1.getSender().equals(chat.getSender()) && chat1.getSender().equals(chat.getSender()) &&
                                                                        chat1.getReceiver().equals(chat.getReceiver()) &&
                                                                        chat1.getInfor().equals(chat.getInfor()) &&
                                                                        chat1.getMessage().equals(chat.getMessage()) &&
                                                                        chat1.getSoluong() == chat.getSoluong() &&
                                                                        chat1.getSeen().equals(chat.getSeen())) {
                                                                    dataSnapshot1.getRef().removeValue();
                                                                    txt_show_message.setText("Tin nhắn đã được xóa ");
                                                                    txt_show_message.setTextColor(getResources().getColor(R.color.Xam));
                                                                    txt_show_message.setBackgroundColor(getResources().getColor(R.color.Xam1));
                                                                }
                                                            }
                                                            databaseReference1.removeEventListener(seenListener);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                        }
                                                    });
                                                    Toast.makeText(MessengersActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
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
                            builder.show();
                        }
                    });
                    recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void currentUser(String IdUser) {
        SharedPreferences.Editor editor = getSharedPreferences("PREPS", MODE_PRIVATE).edit();
        editor.putString("currentuser", IdUser);
        editor.apply();
    }

    private void status(String status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iconGroup:
                Intent intent = new Intent(MessengersActivity.this, InforUserActivity.class);
                intent.putExtra("IdUser", IdUser);
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(IdUser);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dinhdangngay = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dinhdanggio = new SimpleDateFormat("hh:mm a");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat1 = dataSnapshot.getValue(Chat.class);
                    if( chat1.getSender().equals(IdUser) && chat1.getReceiver().equals(fUser.getUid()) && chat1.getSoluong() == 0){
                        HashMap<String,Object> hashMap =  new HashMap<>();
                        hashMap.put("seen","Đã xem "+ dinhdanggio.format(calendar.getTime())+","+dinhdangngay.format(calendar.getTime()));
                        hashMap.put("soluong",1);
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        currentUser("none");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).resumeRequests();
    }
}
