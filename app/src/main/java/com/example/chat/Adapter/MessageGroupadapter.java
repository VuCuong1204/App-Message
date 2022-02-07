package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.IMessageGroup;
import com.example.chat.Model.ListMessageGroup;
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

public class MessageGroupadapter extends RecyclerView.Adapter<MessageGroupadapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

     Context context;
     List<ListMessageGroup> arrayListmsgGroup;
     FirebaseUser fUser;
     boolean trangthai =true;
     IMessageGroup iMessageGroup;

    public MessageGroupadapter(Context context, List<ListMessageGroup> arrayListmsgGroup, IMessageGroup iMessageGroup) {
        this.context = context;
        this.arrayListmsgGroup = arrayListmsgGroup;
        this.iMessageGroup = iMessageGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_LEFT) {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
                return new ViewHolder(view);
            }
    }

    @Override
    public void onBindViewHolder( MessageGroupadapter.ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.imgTinnhancho.setVisibility(View.GONE);
            ListMessageGroup listMessageGroup = arrayListmsgGroup.get(position);
            holder.txt_show_message.setVisibility(View.VISIBLE);
        holder.txt_seen.setText(listMessageGroup.getThoigian());
            holder.txt_show_message.setText(listMessageGroup.getMessagegroup());
            holder.txtInfo.setText(listMessageGroup.getUsername());

            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("User");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getId().equals(listMessageGroup.getSenduser()) && !listMessageGroup.getSenduser().equals(fUser.getUid())) {
                            if (user.getStatus().equals("online")) {
                                holder.img_on.setVisibility(View.VISIBLE);
                                holder.img_off.setVisibility(View.GONE);
                                if (user.getImageURL().equals("default")) {
                                    holder.profile_image.setImageResource(R.drawable.user_add);
                                } else {
                                    if ( context != null) {
                                        Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(holder.profile_image);
                                    }
                                }
                            } else {
                                holder.img_on.setVisibility(View.GONE);
                                holder.img_off.setVisibility(View.VISIBLE);
                                if (user.getImageURL().equals("default")) {
                                    holder.profile_image.setImageResource(R.drawable.user_add);
                                } else {
                                    if ( context != null) {
                                        Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(holder.profile_image);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


            if(position == arrayListmsgGroup.size()-1){
                if(arrayListmsgGroup.get(position).getSenduser().equals(fUser.getUid())){
                    holder.txt_seen.setVisibility(View.VISIBLE);
                    trangthai = false;
                }
            }else {
                holder.txt_seen.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(trangthai == true){
                        holder.txt_seen.setVisibility(View.VISIBLE);
                        trangthai = false;
                    }else {
                        holder.txt_seen.setVisibility(View.GONE);
                        trangthai = true;
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    iMessageGroup.DeleteMSG(listMessageGroup);
                    return false;
                }
            });

        }


    @Override
    public int getItemCount() {
        return arrayListmsgGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image,img_on,img_off,imgTinnhancho;
        TextView txt_show_message,txtInfo;
        TextView txt_seen;
        public ViewHolder( View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_show_message = itemView.findViewById(R.id.show_message);
            txtInfo = itemView.findViewById(R.id.textviewinfo);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            imgTinnhancho = itemView.findViewById(R.id.imgTinnhancho);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (arrayListmsgGroup.get(position).getSenduser().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }


}
