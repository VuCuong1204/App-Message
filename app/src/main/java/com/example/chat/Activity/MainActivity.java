package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.ViewPagerAdapter;
import com.example.chat.Fragment.ChatsFragment;
import com.example.chat.Fragment.GroupChatFragment;
import com.example.chat.Fragment.ProfileFragment;
import com.example.chat.Fragment.UsersFragment;
import com.example.chat.Model.Chat;
import com.example.chat.Model.Listketban;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class  MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    int unread;
    int Dem ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.viewpager0:
                        viewPager.setCurrentItem(0);
                        break;

                    case R.id.viewpager1:
                        viewPager.setCurrentItem(1);
                        break;

                    case R.id.viewpager2:
                        viewPager.setCurrentItem(2);
                        break;

                    case R.id.viewpager3:
                        viewPager.setCurrentItem(3);
                        break;

                }
                return true;
            }
        });
        SetupViewpager();
    }

    private  void SetupViewpager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.viewpager0).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.viewpager1).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.viewpager2).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.viewpager3).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void Anhxa() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        toolbar = findViewById(R.id.toolbar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        viewPager = findViewById(R.id.view_pager);
//        tabLayout = findViewById(R.id.tab_layout);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    username.setText(user.getUsername());
                    if (user.getImageURL().equals("default")) {
                        profile_image.setImageResource(R.drawable.user_add);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.timkiem:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
        }
        return false;
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}