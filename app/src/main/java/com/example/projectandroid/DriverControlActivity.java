package com.example.projectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DriverControlActivity extends AppCompatActivity implements IBaseGpsListener {
    TextView Lat, Lng, Country, Locality, Address_Detail;
    Switch SwitchLocation;
    Button bt_location;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static String url = "https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/";

    public static double latitude = 0;
    public static double longitude = 0;
    public static final int CHANNEL_ID = 5;
    public static int i = 0;
    public static double timeFinal = 0;
    public static String[] token_id;
    public static final int PERMISSION_LOCATION = 1000;
    public static int j = 0;
    LatLngBounds SUT;
    public static LatLngBounds SUT_TRANSPORT;
    public static List<String> arrayList = new ArrayList<>();

    public static FcmNotificationsSender sender;

    // auth firebase
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_control);

        auth = FirebaseAuth.getInstance();

        //Assign Variable
        Lat = findViewById(R.id.latitude);
        Lng = findViewById(R.id.longtitude);
        Country = findViewById(R.id.country);
        Locality = findViewById(R.id.address);
        Address_Detail = findViewById(R.id.address_detail);
        SwitchLocation = findViewById(R.id.switch_gps);
        // Initialize location
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);

        // Check Permission AND REQUIRE THIS
        if (ActivityCompat.checkSelfPermission(DriverControlActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // When permission granted nothing to do
        } else {
            // When permission denied
            ActivityCompat.requestPermissions(DriverControlActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(url);

        // Firebase Message
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        SUT = new LatLngBounds(
                // south east 14.884537, 102.026603
                new LatLng(14.884537, 102.026603),
                // north east 14.912547, 102.068162
                new LatLng(14.912043, 102.067933)
        );


        SUT_TRANSPORT = new LatLngBounds(
                // south east 14.883039, 102.022676
                new LatLng(14.875059, 102.018704),
                // north east 14.875059, 102.018704
                new LatLng(14.883039, 102.022676)
        );

        checkingTime("test","test");
    }

    @Override
    protected void onDestroy() {
        disconnectGPS();
        super.onDestroy();
    }

    /*
    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverControlActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient
                .getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        //Initialize Location
                        Location location = task.getResult();
                        if (location != null) {
                            try {
                                // Initialize geoCoder
                                Geocoder geocoder = new Geocoder(DriverControlActivity.this
                                        , Locale.getDefault());
                                // Initialize Address List
                                List<android.location.Address> addressList = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );

                                // Set latitude on Textview
                                Lat.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b>Latitude :<br></b></font>" + addressList.get(0).getLatitude())
                                );
                                // Set longitude on Textview
                                Lng.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b>Longitude :<br></b></font>" + addressList.get(0).getLongitude())
                                );
                                // Set Country Name on Textview
                                Country.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b>Country Name :<br></b></font>" + addressList.get(0).getCountryName())
                                );
                                // Set Locality on Textview
                                Locality.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b>Locality :<br></b></font>" + addressList.get(0).getLocality())
                                );
                                // Set Locality on Textview
                                Address_Detail.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b>Address :<br></b></font>" + addressList.get(0).getAddressLine(0))
                                );
                                Log.d("Location:", location.getLatitude() + " " + location.getLongitude());

                                // Set data to firebase
                                String Locality, Address;
                                Locality = addressList.get(0).getLocality();
                                Address = addressList.get(0).getAddressLine(0);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                // Declare Class Data gps
                                DriverGPS driverGPS = new DriverGPS(latitude, longitude, Locality, Address);
                                database.getReference("driverGPS/data")
                                        .setValue(driverGPS)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "update location", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "update location failed !!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
    */

    public void getLocationNews() {
        // check location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            showLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLocation();
            } else {
                Toast.makeText(this, "ต้องอนุญาติสิทธิ์ในการใช้งานแอปพลิเคชัน", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // check if gps enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // start location
            Lat.setText("Loading...");
            Lng.setText("Loading...");

            // this code auto generates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            // enabled gps
            Toast.makeText(this, "เปิดใช้งานสิทธิ์ในการเข้าถึงตำแหน่ง", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void disconnectGPS() {
        database.getReference("driverGPS")
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "ปิด GPS", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "ปิด GPS ไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // remove update gps on application
        // I CAN DO IT VERY WELL
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);

        Lat.setText("ละติจูด");
        Lng.setText("ลองจิจูด");
        Country.setText("Country");
        Locality.setText("Locality");
        Address_Detail.setText("Address");
    }

    public void driverLogout(View view) {
        auth.signOut();
        startActivity(new Intent(DriverControlActivity.this, LoginActivity.class));
        Toast.makeText(DriverControlActivity.this, "ออกจากระบบสำเร็จแล้ว", Toast.LENGTH_LONG).show();
        disconnectGPS();
    }

    private void disableSwitch(int delay_ms) {
        final Switch SwitchView = findViewById(R.id.switch_gps);
        SwitchView.setEnabled(false);
        SwitchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                SwitchView.setEnabled(true);
            }
        }, delay_ms);
    }


    /* public void notification() {
    //this is notification inside one devices
        String title = "รถกำลังล้อหมุน !!";
        String message = "ขณะนี้รถกำลังออกจาก บขส. นครราชสีมา";

        Intent intent = new Intent(DriverControlActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(DriverControlActivity.this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(DriverControlActivity.this, "notify_01");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(title);
        bigText.setBigContentTitle("แจ้งเตือนรถออก");
        bigText.setSummaryText("แจ้งเตือนรถออก");

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(bigText);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_01";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(0, builder.build());
    }
    */

    public void notifyAllDevices(String title, String msg) {
        FcmNotificationsSender sender = new FcmNotificationsSender("/topics/all",
                title, msg, getApplicationContext(), DriverControlActivity.this);
        sender.SendNotifications();
    }

    public void checkingTime(String _title, String _details) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
        Date date = new Date(System.currentTimeMillis());
        String _date = formatter.format(date);

        // cut : out date form
        String[] separated = new String[1];
        separated = _date.split(":");
        double hours = Double.parseDouble(separated[0]);
        double seconds = Double.parseDouble(separated[1]);

        timeFinal = (hours * 3600) + (seconds * 60);
        Log.d("time final", String.valueOf(timeFinal));

        timeFinal = 1;
        if (timeFinal <= 23400)
        {
            _details = _details + " สำหรับรอบ 06:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 27000)
        {
            _details = _details + " สำหรับรอบ 07:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 30600)
        {
            _details = _details + " สำหรับรอบ 08:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 34200)
        {
            _details = _details + " สำหรับรอบ 09:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 37800)
        {
            _details = _details + " สำหรับรอบ 10:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 41400)
        {
            _details = _details + " สำหรับรอบ 11:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 45000)
        {
            _details = _details + " สำหรับรอบ 12:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 48600)
        {
            _details = _details + " สำหรับรอบ 13:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 52200)
        {
            _details = _details + " สำหรับรอบ 14:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 55800)
        {
            _details = _details + " สำหรับรอบ 15:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 59400)
        {
            _details = _details + " สำหรับรอบ 16:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        }
        else if (timeFinal <= 63000)
        {
            _details = _details + " สำหรับรอบ 17:30 นาฬิกา";
            notifySpecificDevices(_title, _details);
        } else if (timeFinal > 63000) {
            Toast.makeText(getApplicationContext(), "ขณะนี้เป็นเวลานอกทำการแล้ว\nท่านไม่สามารถเดินรถได้ในขณะนี้", Toast.LENGTH_LONG).show();
        }


    }

    public void notifySpecificDevices(String title, String msg) {


        // Declare ref to database store token phone id
        DatabaseReference ref = FirebaseDatabase
                .getInstance(url).getReference("NotifyCount")
                .child("data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // this code work get token id
                    String key = dataSnapshot.getKey();
                    if (!key.isEmpty()) {
                            arrayList.add(key);
                            Log.d("tokenID", dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (!title.equals("test")) {
            for (int i=0; i < arrayList.size() ; i++) {
                Log.d("arrayList", arrayList.get(i));
                sender = new FcmNotificationsSender(arrayList.get(i),
                        title, msg, getApplicationContext(), DriverControlActivity.this);
                sender.SendNotifications();
            }
        }

        arrayList.clear();
    }

    public void onSwitch(View view) {
        boolean on = ((Switch) view).isChecked();
        switch (view.getId()) {
            case R.id.switch_gps:
                if (on == true) {
                        //disableSwitch(5000);
                        String title = "ล้อกำลังหมุนแล้ว !!!!!";
                        String msg = " ขณะนี้รถกำลังออกจาก บขส. นครราชสีมา";
                        getLocationNews();
                        checkingTime(title, msg);
                        Toast.makeText(getApplicationContext(), "เปิด GPS", Toast.LENGTH_SHORT).show();
                } else {
                    disconnectGPS();
                    disableSwitch(5000);
                }
        }
    }

    // show location as string
    private String lngLocation(Location location) {
        return "ลองจิจูด:\n " + location.getLongitude();
    }

    // show location as string
    private String latLocation(Location location) {
        return "ละติจูด:\n " + location.getLatitude();
    }

    @Override
    public void onLocationChanged(Location location) {
        SUT = new LatLngBounds(
                // south east 14.884537, 102.026603
                new LatLng(14.884537, 102.026603),
                // north east 14.912547, 102.068162
                new LatLng(14.912043, 102.067933)
        );

        // Declare Class Data gps
        DriverGPS driverGPS = new DriverGPS(location.getLatitude(), location.getLongitude());
        database.getReference("driverGPS/data")
                .setValue(driverGPS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d("update:", "success");
                        } else {
                            //Log.d("update:", "failed");
                        }
                    }
                });

        // update gps
        Lat.setText(latLocation(location));
        Lng.setText(lngLocation(location));

        LatLng PIKUD = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.d("PIKUD", String.valueOf(PIKUD));
        //Log.d("SUT", String.valueOf(SUT));

        // Check location into SUT
        if(SUT.contains(PIKUD) && j!=1) {
            String title = "รถใกล้ถึงจุดหมายแล้ว !!!!!";
            String msg = " ขณะนี้รถกำลังเข้าสู่มหาวิทยาลัย";
            notifySpecificDevices(title, msg);
            j = 1;
            Log.d("titleCheck", title);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // empty
    }

    @Override
    public void onProviderEnabled(String provider) {
        // empty
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // empty
    }

    @Override
    public void onGpsStatusChanged(int event) {
        // empty
    }

    public void Checkin(View view) {
        String title = "รถโดยสารถึงจุดหมายปลายทางแล้ว !!";
        String msg = "รถโดยสารเดินทางถึงสถานีขนส่งภายใน มทส เรียบร้อย";
        notifySpecificDevices(title, msg);
    }
}