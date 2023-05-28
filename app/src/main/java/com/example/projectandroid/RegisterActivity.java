package com.example.projectandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;
    TextView PersonCard, PublicCard, CarPicture;
    EditText dvName, dvEmail, dvPhone, dvCarNumber, dvPwd, dvConPwd;
    EditText Email, Username, Pwd, ConPwd;
    // part of upload images to firebase
    Uri imageUri;
    ImageView imageView;
    public static int UPLOAD_IMAGE_PERSON_CARD = 10;
    public static int UPLOAD_IMAGE_PUBLIC_CARD = 11;
    public static int UPLOAD_IMAGE_CAR_PICTURE = 12;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    // auth firebase
    private FirebaseAuth mAuth;
    // FirebaseAuth.getInstance().signOut(); this is code to sign out

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // auth firebase
        mAuth = FirebaseAuth.getInstance();

        // firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        PersonCard  = (TextView) findViewById(R.id.regisDriverBtn_card_person_txt);
        PublicCard = (TextView) findViewById(R.id.regisDriverBtn_card_public_txt);
        CarPicture = (TextView) findViewById(R.id.regisDriverBtn_car_picture_txt);
        dvName = (EditText) findViewById(R.id.regisDriverName);
        dvEmail = (EditText) findViewById(R.id.regisDriverEmail);
        dvPhone = (EditText) findViewById(R.id.regisDriverPhone);
        dvCarNumber = (EditText) findViewById(R.id.regisDriverCar_number);
        dvPwd = (EditText) findViewById(R.id.regisDriverPassword);
        dvConPwd = (EditText) findViewById(R.id.regisDriverCCPWD);

    }

    public void CardPerson(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_IMAGE_PERSON_CARD);
    }

    public void CardPublic(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_IMAGE_PUBLIC_CARD);
    }

    public void CarPicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_IMAGE_CAR_PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageView = findViewById(R.id.regisDriverPreview_person);
        if (requestCode == UPLOAD_IMAGE_PERSON_CARD && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            String name = "person_card";
            File file = new File(imageUri.getPath());
            String file_name = file.getName();
            Log.d("pictureName", file_name);

            PersonCard.setBackgroundColor(Color.rgb(255,227,79));
            PersonCard.setTextColor(Color.rgb(0,48,105));
            PersonCard.setText("อัปโหลดบัตรประชาชนสำเร็จ");

            uploadImage(name);
        }
        else if (requestCode == UPLOAD_IMAGE_PUBLIC_CARD && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            String name = "public_card";
            File file = new File(imageUri.getPath());
            String file_name = file.getName();
            Log.d("pictureName", file_name);

            PublicCard.setBackgroundColor(Color.rgb(255,227,79));
            PublicCard.setTextColor(Color.rgb(0,48,105));
            PublicCard.setText("อัปโหลดใบขับขี่สำเร็จ");

            uploadImage(name);
        }
        else if (requestCode == UPLOAD_IMAGE_CAR_PICTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            String name = "car_picture";
            File file = new File(imageUri.getPath());
            String file_name = file.getName();
            Log.d("pictureName", file_name);


            CarPicture.setBackgroundColor(Color.rgb(255,227,79));
            CarPicture.setTextColor(Color.rgb(0,48,105));
            CarPicture.setText("อัปโหลดรูปภาพรถสำเร็จ");

            uploadImage(name);
        }
    }

    private void uploadImage(String name) {
        // get email from Driver register
        String path = "";
        String[] separated = new String[1];
        if (dvEmail.getText().toString() != null || dvEmail.getText().toString() != "" || !dvEmail.getText().toString().isEmpty()) {
            //path = dvEmail.getText().toString();
            path = dvEmail.getText().toString();
            separated = path.split("@");
        } else {
            path = "bukkumphan";
            separated[0] = path;
        }
        //final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRefs = storageReference.child("images/" + separated[0] + "/" + separated[0] + "_" + name);
        riversRefs.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void regisDriverRegister(View view) {

        String name = dvName.getText().toString().trim();
        String email = dvEmail.getText().toString().trim();
        String phone = dvPhone.getText().toString().trim();
        String car = dvCarNumber.getText().toString().trim();
        String pwd = dvPwd.getText().toString().trim();
        String ccPwd = dvConPwd.getText().toString().trim();
        String field_person = PersonCard.getText().toString().trim();
        String field_public = PublicCard.getText().toString().trim();
        String field_car = CarPicture.getText().toString().trim();

        if (name.isEmpty()) {
            dvName.setError(getString(R.string.fill_name));
            dvName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            dvEmail.setError(getString(R.string.fill_email));
            dvEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dvEmail.setError(getString(R.string.fill_email_right));
            dvEmail.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            dvPhone.setError(getString(R.string.fill_phone));
            dvPhone.requestFocus();
            return;
        }
        if (car.isEmpty()) {
            dvCarNumber.setError(getString(R.string.fill_car_number));
            dvCarNumber.requestFocus();
            return;
        }
        if (pwd.isEmpty()) {
            dvPwd.setError(getString(R.string.fill_pwd));
            dvPwd.requestFocus();
            return;
        }
        if (pwd.length() < 8) {
            dvPwd.setError(getString(R.string.fill_pwd_lenght));
            dvPwd.requestFocus();
            return;
        }
        if (ccPwd.isEmpty()) {
            dvConPwd.setError(getString(R.string.fill_pwd_con));
            dvConPwd.requestFocus();
            return;
        }
        if (!ccPwd.equals(pwd)) {
            dvConPwd.setError(getString(R.string.fill_pwd_match));
            dvConPwd.requestFocus();
            return;
        }
        if (!field_person.equals(getString(R.string.fill_person))){
            Toast.makeText(RegisterActivity.this, R.string.fill_pic, Toast.LENGTH_SHORT).show();
            PersonCard.requestFocus();
            return;
        }
        if (!field_public.equals(getString(R.string.fill_public))){
            Toast.makeText(RegisterActivity.this, R.string.fill_pic, Toast.LENGTH_SHORT).show();
            PublicCard.requestFocus();
            return;
        }
        if (!field_car.equals(getString(R.string.fill_pic_car))){
            Toast.makeText(RegisterActivity.this, R.string.fill_pic, Toast.LENGTH_SHORT).show();
            CarPicture.requestFocus();
            return;
        }

        // create Authentication
        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String role = "1";
                            RegisterDriver user = new RegisterDriver(name, email, phone, car, uid, role);
                            // assign data to real-time database
                            Log.d("sasageyo", user.name);
                            Log.d("sasageyo", user.email);
                            Log.d("sasageyo", user.phone_number);
                            Log.d("sasageyo", user.car_number);
                            Log.d("sasageyo", user.uid);
                            FirebaseDatabase.getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users/Driver")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, R.string.fill_dv_success, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(RegisterActivity.this, DriverControlActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Can't connect to database", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Can't connect to authentication", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}