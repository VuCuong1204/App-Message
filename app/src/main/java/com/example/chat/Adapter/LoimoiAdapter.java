package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.DialogSheetUser;
import com.example.chat.Model.Listketban;
import com.example.chat.Model.User;
import com.example.chat.Notifications.Data;
import com.example.chat.Notifications.MyFirebaseMessaging;
import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;
import com.example.chat.Notifications.Token;
import com.example.chat.R;
import com.example.chat.Service.APIService;
import com.example.chat.Service.DataService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoimoiAdapter extends RecyclerView.Adapter<LoimoiAdapter.ViewHolder>{

    Context context;
    List<User> arrayListketban;
    DialogSheetUser dialogSheetUser;
    String NAMEUSER;

    public LoimoiAdapter(Context context, List<User> arrayListketban, DialogSheetUser dialogSheetUser) {
        this.context = context;
        this.arrayListketban = arrayListketban;
        this.dialogSheetUser = dialogSheetUser;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loimoi_ketban,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LoimoiAdapter.ViewHolder holder, int position) {
        User user = arrayListketban.get(position);
            holder.txtUsername.setText(user.getUsername());
            if(user.getImageURL().equals("default")){
                holder.profile_image.setImageResource(R.drawable.user_add);
            }else {
                if(context != null){
                    Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
                }
            }

            holder.btnDongy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btnDongy.setText("Đã kết bạn");
                    holder.btnHuy.setVisibility(View.GONE);
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User user1 = dataSnapshot.getValue(User.class);
                                if(user1.getId().equals(fUser.getUid())){
                                    NAMEUSER = user1.getUsername();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("idUser",arrayListketban.get(position).getId());
                    hashMap.put("titleAdd","Bạn bè");
                    databaseReference.child(fUser.getUid()).child(arrayListketban.get(position).getId()).setValue(hashMap);

                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put("idUser",fUser.getUid());
                    hashMap1.put("titleAdd","Bạn bè");
                    databaseReference.child(arrayListketban.get(position).getId()).child(fUser.getUid()).setValue(hashMap1);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Listketban");
                    databaseReference1.child(fUser.getUid()).child(arrayListketban.get(position).getId()).removeValue();
                    databaseReference1.child(arrayListketban.get(position).getId()).child(fUser.getUid()).removeValue();

                    DatabaseReference tokens=  FirebaseDatabase.getInstance().getReference("Tokens");
                    Query query = tokens.orderByKey().equalTo(user.getId());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Token token = dataSnapshot.getValue(Token.class);
                                Data data = new Data(fUser.getUid(),R.drawable.addfriend, NAMEUSER+" đã chấp nhận lời mời kết bạn, bây giờ hai bạn đã trở thành bạn bè","Thông báo",
                                        user.getId(),"0");

                                Sender sender = new Sender(data,token.getToken());
                                DataService dataService = APIService.getService();
                                Call<MyResponse> callback = dataService.sendNotification(sender);
                                callback.enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200){
                                            if(response.body().success != 1){
                                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            });
            holder.btnHuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btnHuy.setText("Đã hủy");
                    holder.btnDongy.setVisibility(View.GONE);
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Listketban");
                    databaseReference1.child(fUser.getUid()).child(arrayListketban.get(position).getId()).removeValue();
                    databaseReference1.child(arrayListketban.get(position).getId()).removeValue();

                }
            });
            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   dialogSheetUser.OpenSheetDialogUser(user.getId());
                }
            });
    }

    @Override
    public int getItemCount() {
        return arrayListketban.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView txtUsername;
        Button btnDongy,btnHuy;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            txtUsername = itemView.findViewById(R.id.username);
            btnDongy = itemView.findViewById(R.id.xacnhan);
            btnHuy = itemView.findViewById(R.id.textviewHuy);

        }
    }
}
