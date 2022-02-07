package com.example.chat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.chat.Fragment.ChatsFragment;
import com.example.chat.Fragment.GroupChatFragment;
import com.example.chat.Fragment.ProfileFragment;
import com.example.chat.Fragment.UsersFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    public ViewPagerAdapter(FragmentManager fm,int behavior) {
        super(fm,behavior);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new GroupChatFragment();
            case 2:
                return new UsersFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
