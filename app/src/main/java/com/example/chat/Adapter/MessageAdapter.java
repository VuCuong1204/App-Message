package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.IDialogList;
import com.example.chat.Model.ChanUser;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser fuser;
    Context mContext;
    List<Chat> mChat;
    String imageurl;
    boolean seen = true;
    boolean chan = false;
    IDialogList iDialogList;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, IDialogList iDialogList) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
        this.iDialogList = iDialogList;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder( MessageAdapter.ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
            Chat chat = mChat.get(position);
            holder.txt_info.setVisibility(View.GONE);
        holder.txt_seen.setText(chat.getSeen());
        DatabaseReference databaseReference5 = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if((chanUser.getIdChan().equals(chat.getSender()) && chanUser.getIdBiChan().equals(chat.getReceiver()))
                    || (chanUser.getIdChan().equals(chat.getReceiver()) && chanUser.getIdBiChan().equals(chat.getSender()))){
                        if (chat.getReceiver().equals(fuser.getUid())) {
                            holder.img_off.setVisibility(View.GONE);
                            holder.img_on.setVisibility(View.GONE);
                            holder.profile_image.setImageResource(R.drawable.user_add);
                            chan = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


            holder.txt_show_message.setText(chat.getMessage());
            if(imageurl.equals("default")){
                holder.profile_image.setImageResource(R.drawable.user_add);
            }else {
                Glide.with(mContext).load(imageurl).into(holder.profile_image);
            }

        if(position == mChat.size() - 1) {
            if (mChat.get(position).getSender().equals(fuser.getUid()) && chat.isIsseen()) {
                if(imageurl.equals("default")){
                    holder.img_tinnhancho.setImageResource(R.drawable.user_add);
                }else {
                    Glide.with(mContext).load(imageurl).into(holder.img_tinnhancho);
                }
            }
        }else {
                if(chat.isIsseen()){
                    holder.img_tinnhancho.setVisibility(View.GONE);
                }
            }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat.getSender().equals(fuser.getUid())) {
                    if (seen == true) {
                        holder.txt_seen.setVisibility(View.VISIBLE);
                        seen = false;
                    } else {
                        holder.txt_seen.setVisibility(View.GONE);
                        seen = true;
                    }
                }
            }
        });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user.getId().equals(chat.getSender()) && !chat.getSender().equals(fuser.getUid())) {
                            if (user.getStatus().equals("online")) {
                                holder.img_on.setVisibility(View.VISIBLE);
                                holder.img_off.setVisibility(View.GONE);
                            } else {
                                holder.img_on.setVisibility(View.GONE);
                                holder.img_off.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!chan) {
                    iDialogList.OpenDialogList(chat, holder.txt_show_message);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image,img_on,img_off,img_tinnhancho;
        TextView txt_show_message,txt_info;
        TextView txt_seen;
        public ViewHolder( View itemView) {
            super(itemView);

            img_tinnhancho = itemView.findViewById(R.id.imgTinnhancho);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            txt_info = itemView.findViewById(R.id.textviewinfo);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

}
