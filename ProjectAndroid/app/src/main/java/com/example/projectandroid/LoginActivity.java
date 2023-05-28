package com.example.projectandroid;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class LoginActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigator_login);

        // Add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_drive_eta_24));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // Initialize fragment
                Fragment fragment = null;
                // Check Condition
                switch (item.getId()) {
                    case 1:
                        // Bus fragment
                        fragment = new UserLoginFragment();
                        break;
                    case 2:
                        // SUT fragment
                        fragment = new DriverLoginFragment();
                        break;
                }
                // load fragment
                loadFragment(fragment);
            }
        });

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
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    public void gotoRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void gotoBus(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}