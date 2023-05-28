package com.example.projectandroid;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;
    protected FirebaseAuth mAuth;
    EditText userEmail, userPassword;
    DatabaseReference ref;
    GoogleSignInClient googleSignInClient;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        // firebase user
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // ล็อคอินค้างไว้จะข้ามหน้าอัตโนมัติ มันอยู่ตรงนี้
        /*
        if (firebaseUser != null) {
            String sum = firebaseUser.getProviderId();
            Log.d("checkUSer", sum);
            // When user already sign in
            // Redirect to profile activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        */
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadFragment(Fragment fragment) {
        //Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    ////////////////////////////////////// START DRIVER LOGIN AREA /////////////////////////////////////////////////////////////////////

    public void gotoRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void resetBack(View view) {
        Fragment fragment;
        fragment = new DriverLoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    public void gotoResetPassword(View view) {
        Fragment fragment;
        fragment = new DriverResetPasswordFragment();
        getSupportFragmentManager()
                .beginTransaction()
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    public void resetPassword(View view) {
        TextView message = (TextView) findViewById(R.id.driver_reset_confirm_text);
        EditText editText_email = (EditText) findViewById(R.id.driver_email_reset);

        String email = editText_email.getText().toString().trim();

        if (email.isEmpty()) {
            editText_email.setError(getString(R.string.fill_email));
            editText_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText_email.setError(getString(R.string.fill_email_right));
            editText_email.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message.setText(R.string.reset_request_confirm);
                    Toast.makeText(LoginActivity.this, R.string.reset_request_confirm, Toast.LENGTH_LONG).show();
                } else {
                    message.setText(R.string.reset_error);
                    Toast.makeText(LoginActivity.this, R.string.reset_error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void gotoDriverLogin(View view) {
        Fragment fragment;
        fragment = new DriverLoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    public void goBack(View view) {
        Fragment fragment;
        fragment = new UserLoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                // frame_layout you must be research your file again
                .replace(R.id.frame_layout_login, fragment)
                .commit();
    }

    public void gotoControl(View view) {
        userEmail = (EditText) findViewById(R.id.loginDriverEmail);
        userPassword = (EditText) findViewById(R.id.loginDriverPassword);
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (email.isEmpty()) {
            userEmail.setError(getString(R.string.fill_email));
            userEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError(getString(R.string.fill_email_right));
            userEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            userPassword.setError(getString(R.string.fill_pwd));
            userPassword.requestFocus();
            return;
        }
        if (password.length() < 8) {
            userPassword.setError(getString(R.string.fill_pwd_lenght));
            userPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.fill_login_success), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), DriverControlActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.fill_login_error), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    ////////////////////////////////////// END OF DRIVER LOGIN AREA /////////////////////////////////////////////////////////////////////

    ////////////////////////////////////// START USER LOGIN AREA /////////////////////////////////////////////////////////////////////

    public void userSignin(View view) {
        // sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("614360204332-o34c7ij5ohfheuod83otumsh9gb3lrfk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        //Initialize sign in intent
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // Initialize Task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            // Check condition
            if(signInAccountTask.isSuccessful()){
                // Display Toast

                // initialize sign in account
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);

                    if (googleSignInAccount != null) {
                        // Initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                , null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            // Create data in database
                                            String role = "0";
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                            RegisterUser user = new RegisterUser(email, uid, role);
                                            // assign data to real-time database
                                            Log.d("sasageyo", user.email);
                                            Log.d("sasageyo", user.uid);
                                            Log.d("sasageyo", user.role);
                                            FirebaseDatabase.getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users/general")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //displayToast(getString(R.string.fill_user_success));
                                                    } else {
                                                        displayToast("Can't connect to database");
                                                    }
                                                }
                                            });

                                            // Redirect to mainActivity
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            displayToast(getString(R.string.fill_login_success));
                                        } else {
                                            displayToast(getString(R.string.fill_login_error) + " " + task.getException()
                                            .getMessage());
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    ////////////////////////////////////// END OF USER LOGIN AREA /////////////////////////////////////////////////////////////////////

    private void displayToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
}