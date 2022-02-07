package com.example.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.GroupMessageActivity;
import com.example.chat.Model.Groupchat;
import com.example.chat.Model.ListMessageGroup;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context context;
    private List<Groupchat> ListGroupchat;
    String Lastmessage;

    public GroupAdapter(Context context, List<Groupchat> ListGroupchat) {
        this.context = context;
        this.ListGroupchat = ListGroupchat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        Groupchat groupchat = ListGroupchat.get(position);
        holder.txtGroup.setText(groupchat.getGroupname());
        if (groupchat.getProfilegroup().equals("default")) {
            holder.circleImageView.setImageResource(R.drawable.viewpager1group2);
        } else {
            Glide.with(context).load(groupchat.getProfilegroup()).into(holder.circleImageView);
            holder.txtGroup.setText(groupchat.getGroupname());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupMessageActivity.class);
                intent.putExtra("Idgroup", ListGroupchat.get(position).getIdphong());
                context.startActivity(intent);
            }
        });
        Lastmessagegroup(groupchat.getIdphong(), holder.txtLastmessage);
    }

    @Override
    public int getItemCount() {
        return ListGroupchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtGroup, txtLastmessage;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_group);
            txtGroup = itemView.findViewById(R.id.textviewGroup);
            txtLastmessage = itemView.findViewById(R.id.txt_lastMessage);
        }
    }

    public void Lastmessagegroup(String IdGroup, TextView lastmessage) {
        Lastmessage = "default";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroupChat");
        databaseReference.child(IdGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListMessageGroup listMessageGroup = dataSnapshot.getValue(ListMessageGroup.class);
                    Lastmessage = listMessageGroup.getMessagegroup();
                }
                switch (Lastmessage) {
                    case "default":
                        lastmessage.setText("No message");
                        break;
                    default:
                        lastmessage.setText(Lastmessage);
                        break;
                }
                Lastmessage = "default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
}
