package com.example.chat.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.chat.Activity.Doi_mkActivity;
import com.example.chat.Activity.EditIFActivity;
import com.example.chat.Activity.MainActivity;
import com.example.chat.Activity.StartActivity;
import com.example.chat.Model.InfoUser;
import com.example.chat.Model.User;
import com.example.chat.R;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView imgChinhsua;
    TextView txtGioitinh,txtNamsinh,txtDiachi,txtGioithieu,txtDoimk;
    View view;
    TextView txt_username;
    CircleImageView profile_image;
    FirebaseUser fUser;
    DatabaseReference reference;
    int REQUEST_CODE_FILE = 124;
    Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    Button btnDangxuat;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_fragment,container,false);
        Anhxa();
        Init();
        Editthongtin();
        Doimk();
        return view;
    }

    private void Doimk() {
        txtDoimk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext().getApplicationContext(), Doi_mkActivity.class));
            }
        });
    }

    private void Editthongtin() {
        imgChinhsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditIFActivity.class);
                startActivity(intent);
            }
        });
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("InfoUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    InfoUser infoUser = dataSnapshot.getValue(InfoUser.class);
                    if (infoUser.getIduser().equals(fUser.getUid())){
                        txtGioitinh.setVisibility(View.VISIBLE);
                        txtNamsinh.setVisibility(View.VISIBLE);
                        txtDiachi.setVisibility(View.VISIBLE);
                        txtGioithieu.setVisibility(View.VISIBLE);

                        txtGioitinh.setText(infoUser.getGioitinh());
                        txtNamsinh.setText(infoUser.getNamsinh());
                        txtDiachi.setText(infoUser.getDiachi());
                        txtGioithieu.setText(infoUser.getGioithieu());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void Init() {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    txt_username.setText(user.getUsername());
                    if(user.getImageURL().equals("default")){
                        profile_image.setImageResource(R.drawable.user_add);
                    }else {
                        if(getContext() != null) {
                            Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                        }
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFile();
            }
        });

        btnDangxuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có muốn đăng xuất không ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    private void Anhxa() {
        txt_username = view.findViewById(R.id.username);
        profile_image = view.findViewById(R.id.profile_image);
        txtGioitinh= view.findViewById(R.id.txtGioitinh);
        txtNamsinh= view.findViewById(R.id.txtNamsinh);
        txtDiachi= view.findViewById(R.id.txtDiachi);
        txtGioithieu= view.findViewById(R.id.txtGioithieu);
        imgChinhsua = view.findViewById(R.id.imgchinhsuathongtin);
        btnDangxuat = view.findViewById(R.id.btnDangxuat);
        txtDoimk = view .findViewById(R.id.txtDoimk);
    }

    private void OpenFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_CODE_FILE);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("uploading");
        pd.show();

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then( Task task) throws Exception {
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete( Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri =  downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();

                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode ==  REQUEST_CODE_FILE && resultCode == RESULT_OK && data !=null && data.getData() !=null){
            imageUri = data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
