package com.example.socialmediasmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.example.socialmediasmall.adapter.ViewPagerAdapter;
import com.example.socialmediasmall.fragment.CreateAccountFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        // Tạo và đặt adapter cho ViewPager2
       int count = tabLayout.getTabCount();
        Log.e("COunt", "" + count);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        // Thêm listener cho sự kiện thay đổi trang trên ViewPager2

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Thay đổi tab được chọn khi trang ViewPager2 thay đổi
                tabLayout.selectTab(tabLayout.getTabAt(position));
                Log.e("ViewPager2", "Page selected: " + position);

            }
        });

        // Thêm tabs cho TabLayout
        addTabs();
//        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                switch (position) {
//                    case 0:
//                        tab.setText("Home");
//                        tab.setIcon(R.drawable.ic_home);
//                        break;
//                    case 1:
//                        tab.setText("Search");
//                        tab.setIcon(R.drawable.ic_search);
//                        break;
//                    case 2:
//                        tab.setText("Add");
//                        tab.setIcon(R.drawable.icons8_add);
//                        break;
//                    case 3:
//                        tab.setText("Heart");
//                        tab.setIcon(R.drawable.ic_heart_fill);
//                        break;
//                    case 4:
//                        tab.setText("Help");
//                        tab.setIcon(android.R.drawable.ic_menu_help);
//                        break;
//                }
//
//            }
//        });

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);

        // Bắt sự kiện khi người dùng chọn một tab cụ thể
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Di chuyển đến trang tương ứng khi người dùng chọn tab
                viewPager2.setCurrentItem(tab.getPosition()); //
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.ic_home_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.icons8_search_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.icons8_add);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;
                    case 4:
                        tab.setIcon(android.R.drawable.ic_menu_help);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.ic_home_outline);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.icons8_search_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.icons8_add);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_heart);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }


    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icons8_search_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icons8_add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart_fill));


        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
    }
}