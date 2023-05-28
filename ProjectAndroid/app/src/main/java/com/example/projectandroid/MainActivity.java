package com.example.projectandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigator);

        // Add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_bus));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_school));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_notifications_active_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_baseline_favorite_24));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // Initialize fragment
                Fragment fragment = null;
                // Check Condition
                switch (item.getId()) {
                    case 1:
                        // Bus fragment
                        fragment = new BusFragment();
                        break;
                    case 2:
                        // SUT fragment
                        fragment = new SutFragment();
                        break;
                    case 3:
                        // Home fragment
                        fragment = new HomeFragment();
                        break;
                    case 4:
                        // notification fragment
                        fragment = new NotificationFragment();
                        break;
                    case 5:
                        // favorite fragment
                        fragment = new FavoriteFragment();
                        break;
                }
                // load fragment
                loadFragment(fragment);
            }
        });

        //Set notification count
        bottomNavigation.setCount(1, "10");
        //Set home fragment initially selected
        bottomNavigation.show(1, true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                //Toast.makeText(getApplicationContext(), item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                //Toast.makeText(getApplicationContext(), item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}