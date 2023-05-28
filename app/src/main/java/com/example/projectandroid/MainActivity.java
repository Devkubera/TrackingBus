package com.example.projectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            // if want to make profile
            // do it here
//            Glide.with(yourProfileActivity.this)
//                    .load(firebaseUser.getPhotoUrl())
//                    .into(imageView);
//            // set name in text view
//            textView.setText(firebaseUser.getDisplayName());
        }

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);


        // find bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigator);

        // Add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_bus));
        //bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_school));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_baseline_map_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_notifications_active_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_baseline_account_circle_24));

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
                        // Maps fragment
                        fragment = new MapsFragment();
                        break;
                    case 4:
                        // notification fragment
                        // GoAway fragment
                        fragment = new GoAwayFragment();
                        break;
                    case 5:
                        // favorite fragment
                        fragment = new ProfileUserFragment();
                        break;
                }
                // load fragment
                loadFragment(fragment);
            }
        });

        //Set notification count
        //bottomNavigation.setCount(1, "10");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadFragment(Fragment fragment) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    public void userLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        Toast.makeText(MainActivity.this, "ออกจากระบบสำเร็จแล้ว", Toast.LENGTH_LONG).show();
    }

    public void btn_goAway(View view) {
        Fragment fragment;
        fragment = new GoAwayFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    public void btn_returnWay(View view) {
        Fragment fragment;
        fragment = new ReturnWayFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}