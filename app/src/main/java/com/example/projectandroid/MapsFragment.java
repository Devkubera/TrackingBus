package com.example.projectandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MapsFragment extends Fragment {
    public static double _lat;
    public static double _lng;
    public static int i = 0;
    public static String _locality = "";
    public static String _address = "";
    TextView textView, tv_loading;
    public static Marker marker = null;
    View view;
    Button btn_maps;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        @Override
        public void onMapReady(GoogleMap googleMap) {
            // declare variable

                DatabaseReference databaseReference = FirebaseDatabase
                        .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference()
                        .child("driverGPS/data");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String key = dataSnapshot.getKey();

                                    if (key.isEmpty()) {
                                        tv_loading.setVisibility(View.VISIBLE);
                                    } else {
                                        if (key.equals("address")) {
                                            _address = dataSnapshot.getValue().toString();
                                        }
                                        if (key.equals("locality")) {
                                            _locality = dataSnapshot.getValue().toString();
                                        }
                                        if (key.equals("lat")) {
                                            _lat = (double) dataSnapshot.getValue();
                                        }
                                        if (key.equals("lng")) {
                                            _lng = (double) dataSnapshot.getValue();
                                        }


                                        //String txt = "ตำแหน่งปัจจุบัน : " + _locality + "\n" + _address;
                                        //textView.setText(txt);
                                        //Log.d("locality", _locality + " " + _address);

                                        // this code is work about remove previous marker
                                        // is mean map have ONLY 1 MARKER BUT not working in post delayed I'm not understand it
                                        if (!(marker == null)) {
                                            marker.remove();
                                        }

                                        // set loading page map is gone
                                        tv_loading.setVisibility(View.GONE);

                                        LatLng car = new LatLng(_lat, _lng);
                                        marker = googleMap.addMarker(new MarkerOptions().position(car).title("รถอยู่ตรงนี้!"));
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(car, 14));
                                        Log.d("location", _lat + " " + _lng);

                                    }
                                }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tv_loading.setVisibility(View.VISIBLE);
                    }
                });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        //textView = (TextView) view.findViewById(R.id.tv_locality);
        tv_loading = (TextView) view.findViewById(R.id.tv_loading);
        btn_maps = (Button) view.findViewById(R.id.btn_maps);

        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this navigator view
                //Uri gmmIntentUri = Uri.parse("google.navigation:q="+_lat+","+_lng);
                // this is street view *not working
                Uri gmmIntentUri = Uri.parse("geo:"+_lat+","+_lng+"?q="+_lat+","+_lng+("A car is here"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}