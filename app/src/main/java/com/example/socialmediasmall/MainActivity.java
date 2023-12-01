package com.example.socialmediasmall;

import static com.example.socialmediasmall.utils.Constants.PREF_DIRECTORY;
import static com.example.socialmediasmall.utils.Constants.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.example.socialmediasmall.adapter.ViewPagerAdapter;
import com.example.socialmediasmall.fragment.SearchFragment;
import com.example.socialmediasmall.interfaceListener.IOnDataPass;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity implements IOnDataPass {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private Toolbar toolbar;
    private ViewPagerAdapter viewPagerAdapter;
    public static boolean isGalleryOpened = false;

    public static String USER_ID;
    public static Boolean IS_SEARCHED_USER = false;



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
                isGalleryOpened = false;


            }


        });

        // Thêm tabs cho TabLayout
        addTabs();


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);

        // Bắt sự kiện khi người dùng chọn một tab cụ thể
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Di chuyển đến trang tương ứng khi người dùng chọn tab
                viewPager2.setCurrentItem(tab.getPosition()); //
                isGalleryOpened = false;


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
//                    case 4:
//                        tab.setIcon(android.R.drawable.ic_menu_help);
//                        break;
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
//                    case 4:
//                        //tab.setIcon(R.drawable.ic_heart_fill);
//                        break;
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

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = sharedPreferences.getString(PREF_DIRECTORY, "");

        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));


        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
    }



    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 4) {
            viewPager2.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onChange(String uid) {
        USER_ID = uid; // uid cua user khac: la id cua user minh nhan vao
        IS_SEARCHED_USER = true;// khi có sự kiện click vào một user trong SearchFragment
        viewPager2.setCurrentItem(4);
    }

    private Bitmap loadProfileImage(String directory) {
        File file = new File(directory, "profile.png");
        try {
                return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}