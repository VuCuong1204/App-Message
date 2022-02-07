package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder>{

    private Context context;
    private List<User> mUser;
    private String IDGROUP;
    private String GROUPNAME;

    public AddMemberAdapter(Context context, List<User> mUser, String IDGROUP, String GROUPNAME) {
        this.context = context;
        this.mUser = mUser;
        this.IDGROUP = IDGROUP;
        this.GROUPNAME = GROUPNAME;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AddMemberAdapter.ViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.txtTen.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profile.setImageResource(R.drawable.user_add);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile);
        }

        holder.btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnThem.getText().toString().equals("Thêm")) {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("IdUser", mUser.get(position).getId());
                    hashMap.put("Chucvu", "Thành viên");
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ListGroup");
                    databaseReference.child(IDGROUP).child(mUser.get(position).getId()).setValue(hashMap);
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("groupname", GROUPNAME);
                    hashMap1.put("profilegroup", "default");
                    hashMap1.put("Idphong", IDGROUP);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("GroupUser");
                    databaseReference1.child(mUser.get(position).getId()).child(IDGROUP).setValue(hashMap1);
                    holder.btnThem.setText("Đã thêm");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button btnThem;
        CircleImageView profile;
        TextView txtTen;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
           btnThem = itemView.findViewById(R.id.buttonThem);
            profile = itemView.findViewById(R.id.profile_image);
            txtTen = itemView.findViewById(R.id.textviewTen);
        }
    }
}
