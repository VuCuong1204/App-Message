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
import com.bumptech.glide.GlideBuilder;
import com.example.chat.Model.ChanUser;
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

public class DSChanAdapter extends RecyclerView.Adapter<DSChanAdapter.ViewHolder>{

    Context context;
    List<User> mUser;
    ValueEventListener DeleteDSChan;

    public DSChanAdapter(Context context, List<User> mUser) {
        this.context = context;
        this.mUser = mUser;
    }

    @NonNull
    @NotNull
    @Override
    public DSChanAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chan_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DSChanAdapter.ViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.txtUsername.setText(user.getUsername());
        if(user.getImageURL().equals("defaule")){
           holder.profile_image.setImageResource(R.drawable.user_add);
        }else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }

        holder.btnHuyChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btnHuyChan.getText().equals("Bỏ chặn")) {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
                   DeleteDSChan = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                                if (chanUser.getIdChan().equals(fUser.getUid()) && chanUser.getIdBiChan().equals(user.getId())) {
                                    dataSnapshot.getRef().removeValue();
                                }
                            }
                            databaseReference.removeEventListener(DeleteDSChan);
                            holder.btnHuyChan.setText("Đã bỏ chặn");
                            Toast.makeText(context, "Đã xóa " + user.getUsername() + " khỏi danh sách chặn ", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }



    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView txtUsername;
        Button btnHuyChan;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            btnHuyChan = itemView.findViewById(R.id.btnBochan);
        }
    }
}
