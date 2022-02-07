package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.IClickOpenMG;
import com.example.chat.Model.ListGroup;
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


public class AllMemberAdapter extends RecyclerView.Adapter<AllMemberAdapter.ViewHolder> {
    private Context context;
    private List<ListGroup> mListGroup;
    private String IDGROUP;
    private String GROUPNAME;
    private boolean QTV;
    private IClickOpenMG iClickOpenMG;


    public AllMemberAdapter(Context context, List<ListGroup> mListGroup, String IDGROUP, String GROUPNAME, boolean QTV, IClickOpenMG iClickOpenMG) {
        this.context = context;
        this.mListGroup = mListGroup;
        this.IDGROUP = IDGROUP;
        this.GROUPNAME = GROUPNAME;
        this.QTV = QTV;
        this.iClickOpenMG = iClickOpenMG;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AllMemberAdapter.ViewHolder holder, int position) {
       ListGroup listGroup = mListGroup.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(listGroup.getIdUser().equals(user.getId())){
                        if(user.getImageURL().equals("default")){
                            holder.profile.setImageResource(R.drawable.user_add);
                        }else {
                            if (context != null) {
                                Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(holder.profile);
                            }
                        }
                        holder.txtName.setText(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        holder.txtChucvu.setText(listGroup.getChucvu());
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickOpenMG.OpenBSDFragmentMemberGroup(listGroup.getIdUser());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView txtName,txtChucvu;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            txtName = itemView.findViewById(R.id.textviewName);
            txtChucvu = itemView.findViewById(R.id.textviewCV);
        }
    }
}
