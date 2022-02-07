package com.example.chat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.chat.Adapter.SearchAdapter;
import com.example.chat.Fragment.BottomSheetFragmetUser;
import com.example.chat.Model.ChanUser;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseUser fUser;
    Toolbar toolbar;
    RecyclerView recyclerViewSearch;
    SearchAdapter searchAdapter;
    List<User> mUser;
    List<String> mChanUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Anhxa();
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewSearch.setHasFixedSize(true);
        mUser = new ArrayList<>();
        mChanUsers = new ArrayList<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChanUser");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChanUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChanUser chanUser = dataSnapshot.getValue(ChanUser.class);
                    if(chanUser.getIdChan().equals(firebaseUser.getUid())){
                        mChanUsers.add(chanUser.getIdBiChan());
                    }
                    if (chanUser.getIdBiChan().equals(firebaseUser.getUid())){
                        mChanUsers.add(chanUser.getIdChan());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewSearch.setAdapter(searchAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewSearch = findViewById(R.id.recyclerviewTimkiem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tìm kiếm bạn bè ... ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view,menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getData(newText.toLowerCase());
                return false;
            }
        });
        return true;
    }

    private void getData(String newText) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!newText.equals("")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("User");
            Query query = databaseReference.orderByChild("search")
                    .startAt(newText)
                    .endAt(newText + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    mUser.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (fUser != null) {
                            if (mChanUsers.size() != 0) {
                                for (String chanUser : mChanUsers) {
                                    if (!user.getId().equals(fUser.getUid()) && !user.getId().equals(chanUser)) {
                                        mUser.add(user);
                                    }
                                }
                            }else {
                                    if (!user.getId().equals(fUser.getUid())) {
                                        mUser.add(user);
                                    }
                            }
                        }
                    }
                    searchAdapter = new SearchAdapter(SearchActivity.this, mUser, new DialogSheetUser() {
                        @Override
                        public void OpenSheetDialogUser(String IdUser) {
                            BottomSheetFragmetUser bottomSheetFragmetUser = new BottomSheetFragmetUser();
                            Bundle bundle = new Bundle();
                            bundle.putString("IdUser",IdUser);
                            bottomSheetFragmetUser.setArguments(bundle);
                            bottomSheetFragmetUser.show(getSupportFragmentManager(),bottomSheetFragmetUser.getTag());
                        }
                    });
                    recyclerViewSearch.setAdapter(searchAdapter);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}