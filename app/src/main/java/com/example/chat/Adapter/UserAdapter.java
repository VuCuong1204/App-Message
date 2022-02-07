package com.example.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.DialogSheetUser;
import com.example.chat.Activity.MessengersActivity;
import com.example.chat.Fragment.ChatsFragment;
import com.example.chat.Fragment.GroupChatFragment;
import com.example.chat.Fragment.ProfileFragment;
import com.example.chat.Fragment.UsersFragment;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User>  mUsers;
    private boolean ischat;
    FirebaseUser fUser;
    DialogSheetUser dialogSheetUser;
    String TheLastMessage;
    boolean seen = false;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat, DialogSheetUser dialogSheetUser) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
        this.dialogSheetUser = dialogSheetUser;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_user,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(  UserAdapter.ViewHolder holder, int position) {
        if(mUsers != null){
            User user = mUsers.get(position);



            holder.txt_user.setText(user.getUsername());
            if(user.getImageURL().equals("default")){
                holder.circleImageView.setImageResource(R.drawable.user_add);
            }else {
                Glide.with(mContext).load(user.getImageURL()).into(holder.circleImageView);
            }
            if (ischat){
                holder.imgTinnhancho.setVisibility(View.VISIBLE);
                LastMessensger(user.getId(),holder.txt_lastMessage,holder.txt_Tinnhancho,holder.imgTinnhancho);
            }else{
                holder.txt_lastMessage.setVisibility(View.GONE);
            }
                if (user.getStatus().equals("online")) {
                    holder.img_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.GONE);
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.VISIBLE);
                }

        }
        if (ischat){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessengersActivity.class);
                intent.putExtra("IdUser",mUsers.get(position).getId());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialogSheetUser.OpenSheetDialogUser(mUsers.get(position).getId());
                return false;
            }
        });
        }
        else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSheetUser.OpenSheetDialogUser(mUsers.get(position).getId());
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView,img_on,img_off,imgTinnhancho;
        TextView txt_user,txt_Tinnhancho;
        TextView txt_lastMessage;

        public ViewHolder( View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txt_user = itemView.findViewById(R.id.username);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            txt_lastMessage = itemView.findViewById(R.id.txt_lastMessage);
            imgTinnhancho = itemView.findViewById(R.id.imgTinnhancho);
            txt_Tinnhancho = itemView.findViewById(R.id.txtTinnhancho);
        }
    }

    public void LastMessensger(String IdUser, TextView lastmessage,TextView tinnhancho,CircleImageView imgTinnhancho){
        TheLastMessage = "default";
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(fUser != null){
                    if(chat.getReceiver().equals(IdUser) && chat.getSender().equals(fUser.getUid())) {
                        if (chat.isIsseen()) {
                            seen = true;
                        } else {
                            seen = false;
                        }
                        TheLastMessage = chat.getMessage();
                    }
                        if(chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(IdUser)) {
                            TheLastMessage = chat.getMessage();
                            seen = false;
                        }
                  }
                }

                switch (TheLastMessage){
                    case "default":
                   ;lastmessage.append(TheLastMessage);

                        break;
                    default:
                        if(seen) {
                            lastmessage.setText(TheLastMessage);

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User");
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user.getId().equals(IdUser)) {
                                            if (user.getImageURL().equals("default")) {
                                                imgTinnhancho.setImageResource(R.drawable.iconadd);
                                            } else {
                                                if (mContext != null) {
                                                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(imgTinnhancho);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                        }else {
                            lastmessage.setText(TheLastMessage);
                        }
                        break;
                }


                TheLastMessage = "default";
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });


                  FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot snapshot) {
                            int unread = 0;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Chat chat1 = dataSnapshot.getValue(Chat.class);
                                if (chat1 != null && firebaseUser != null) {
                                    if (chat1.getReceiver().equals(firebaseUser.getUid()) &&
                                    chat1.getSender().equals(IdUser)&& !chat1.isIsseen()) {
                                        unread++;
                                    }
                                }
                            }
                                if (unread != 0) {
                                    tinnhancho.setVisibility(View.VISIBLE);
                                    tinnhancho.setText("(" + unread + ")");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        lastmessage.setTextColor(mContext.getColor(R.color.black));
                                        lastmessage.setTypeface(Typeface.DEFAULT_BOLD);
                                    }
                                } else {
                                    tinnhancho.setVisibility(View.GONE);
                                }
                        }

                        @Override
                        public void onCancelled( DatabaseError error) {

                        }
                    });
    }
}
