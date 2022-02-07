package com.example.chat.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
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
import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;
import com.example.chat.Notifications.Token;
import com.example.chat.R;
import com.example.chat.Service.APIService;
import com.example.chat.Service.DataService;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Repeatable;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    Context context;
    List<User> arrayUser;
    ProgressDialog pd;
    DialogSheetUser dialogSheetUser;
    String NAMEUSER;

    public SearchAdapter(Context context, List<User> arrayUser, DialogSheetUser dialogSheetUser) {
        this.context = context;
        this.arrayUser = arrayUser;
        this.dialogSheetUser = dialogSheetUser;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_friends,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchAdapter.ViewHolder holder, int position) {
         User user = arrayUser.get(position);
         holder.txtUsername.setText(user.getUsername());
        pd = new ProgressDialog(context);
        pd.setMessage("vui lòng chờ");
         if(user.getImageURL().equals("default")){
             holder.profileImage.setImageResource(R.drawable.user_add);
         }else {
             if(context != null){
                 Glide.with(context).load(user.getImageURL()).into(holder.profileImage);
             }
         }

         holder.btnThembanbe.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
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
                 if(holder.btnThembanbe.getText().toString().equals("Thêm bạn bè")) {
                     DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
                     HashMap<String,Object> hashMap = new HashMap<>();
                     hashMap.put("idUser",fUser.getUid());
                     hashMap.put("titleAdd","Vui lòng chờ");
                     databaseReference.child(arrayUser.get(position).getId()).child(fUser.getUid()).setValue(hashMap);
                     holder.btnThembanbe.setText("Vui lòng chờ");
                     HashMap<String,Object> hashMap1 = new HashMap<>();
                     hashMap1.put("idUser",arrayUser.get(position).getId());
                     hashMap1.put("titleAdd","Xác nhận");
                     databaseReference.child(fUser.getUid()).child(arrayUser.get(position).getId()).setValue(hashMap1);

                     DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                     Query query = tokens.orderByKey().equalTo(user.getId());
                     query.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                             for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                 Token token = dataSnapshot.getValue(Token.class);
                                 Data data = new Data(fUser.getUid(),R.drawable.addfriend,NAMEUSER+" đã gửi cho bạn một lời mời kết bạn",
                                         "Thông báo",user.getId(),"0");

                                 Sender sender = new Sender(data,token.getToken());
                                 DataService dataService = APIService.getService();
                                 Call<MyResponse> callback = dataService.sendNotification(sender);
                                 callback.enqueue(new Callback<MyResponse>() {
                                     @Override
                                     public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                         if(response.code() == 200){
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

                     pd.show();
                 }else{
                     if(holder.btnThembanbe.getText().equals("Vui lòng chờ")) {
                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
                         databaseReference.child(arrayUser.get(position).getId()).child(fUser.getUid()).removeValue();
                         databaseReference.child(fUser.getUid()).child(arrayUser.get(position).getId()).removeValue();
                         holder.btnThembanbe.setText("Thêm bạn bè");
                     }
                     else{
                         if(holder.btnThembanbe.getText().equals("Xác nhận")){
                             HashMap<String,Object> hashMap2 = new HashMap<>();
                             hashMap2.put("idUser",arrayUser.get(position).getId());
                             hashMap2.put("titleAdd","Bạn bè");
                             DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListFriends");
                             databaseReference.child(fUser.getUid()).child(arrayUser.get(position).getId()).setValue(hashMap2);
                             HashMap<String,Object> hashMap3 = new HashMap<>();
                             hashMap3.put("idUser",fUser.getUid());
                             hashMap3.put("titleAdd","Bạn bè");
                             databaseReference.child(arrayUser.get(position).getId()).child(fUser.getUid()).setValue(hashMap3);
                             DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Listketban");
                             databaseReference1.child(fUser.getUid()).child(arrayUser.get(position).getId()).removeValue();
                             databaseReference1.child(arrayUser.get(position).getId()).child(fUser.getUid()).removeValue();
                             holder.btnThembanbe.setText("Bạn bè");
                         }
                     }
                 }
             }
         });
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Listketban");
        databaseReference.child(arrayUser.get(position).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if(listketban.getIdUser().equals(fUser.getUid())){
                        holder.btnThembanbe.setText(listketban.getTitleAdd());
                        pd.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("ListFriends");
        databaseReference1.child(arrayUser.get(position).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Listketban listketban = dataSnapshot.getValue(Listketban.class);
                    if(listketban.getIdUser().equals(fUser.getUid())){
                        holder.btnThembanbe.setText(listketban.getTitleAdd());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSheetUser.OpenSheetDialogUser(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView txtUsername;
        Button btnThembanbe;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            txtUsername = itemView.findViewById(R.id.username);
            btnThembanbe = itemView.findViewById(R.id.thembanbe);

        }
    }
}
