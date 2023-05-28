package com.example.projectandroid;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

public class RegisterActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;
    TextView PersonCard;
    TextView PublicCard;
    TextView CarPicture;
    EditText Email;
    Uri uri;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // find view
        PublicCard = (TextView) findViewById(R.id.regisDriverBtn_card_public_txt);
        CarPicture = (TextView) findViewById(R.id.regisDriverBtn_car_picture_txt);
        Email = (EditText) findViewById(R.id.regisDriverEmail);

        // find bottom navigation
        // RENAME A ID OF NAVIGATION BAR !!!
        bottomNavigation = findViewById(R.id.bottom_navigator_register);

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
                        fragment = new UserRegFragment();
                        break;
                    case 2:
                        // SUT fragment
                        fragment = new DriverRegFragment();
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
                .replace(R.id.frame_layout_register, fragment)
                .commit();
    }

    public void CardPerson(View view) {
        PersonCard = (TextView) findViewById(R.id.regisDriverBtn_card_person_txt);
        CropImage.startPickImageActivity(RegisterActivity.this);
    }

    public void resetEverything(View view) {
        // set anything to default value
        imageView.setImageBitmap(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // declare imageView
        imageView = findViewById(R.id.regisDriverPreview_person);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                , 0);
            } else {
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (requestCode == RESULT_OK) {
                imageView.setImageURI(result.getUri());
                Toast.makeText(this, "Image Update Successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setActivityMenuIconColor(000000)
                .start(this);
    }
}