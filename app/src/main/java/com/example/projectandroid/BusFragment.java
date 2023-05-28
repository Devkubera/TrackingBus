package com.example.projectandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String access_token;
    private static int follower_count;

    private String mParam1;
    private String mParam2;
    View view;
    private ListView listView;
    private TextView textView;
    private TextView gone;

    // auth firebase
    private FirebaseAuth mAuth;
    // real time database
    private FirebaseStorage firebaseDatabase;

    public BusFragment() {
        // Required empty public constructor
    }

    public static BusFragment newInstance(String param1, String param2) {
        BusFragment fragment = new BusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bus, container, false);

        // auth firebase
        mAuth = FirebaseAuth.getInstance();

        // firebase storage
        firebaseDatabase = FirebaseStorage.getInstance();

        // listView handler
        listView = (ListView) view.findViewById(R.id.list_time);
        textView = (TextView) view.findViewById(R.id.time_topic);
        gone = (TextView) view.findViewById(R.id.ghost);
        final ArrayList<String> list = new ArrayList<>();

        // get token id
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    access_token = task.getResult();
                    Log.e("Access Token", "onComplete: new Token got: "+access_token );

                }
            }
        });

        // set background different color in listview
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.list_go_away,list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Get current item for ListView
               View v = super.getView(position, convertView, parent);
               if (position == 0) {
                   v.setBackgroundColor(getResources().getColor(
                           R.color.blue_shine
                   ));
               } else {
                   if (position %2 == 1) {
                       v.setBackgroundColor(getResources().getColor(
                               R.color.blue_dark
                       ));
                   } else {
                       v.setBackgroundColor(getResources().getColor(
                               R.color.blue_darkBight
                       ));
                   }
               }
               return  v;
            }

        };
        listView.setAdapter(adapter);
        // set on click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //DatetimeData data = new DatetimeData();
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                // title is summary string in list view
                String title = adapterView.getItemAtPosition(position).toString();
                // cut nouns
                String[] separated = new String[1];
                separated = title.split("น");
                String word = separated[0];

                // get time
                String[] times = new String[1];
                times = title.split("\n\uD83D\uDE89");
                String str_time = times[0];
                String str_position = times[1].trim();

                String desc = "ต้องการติดตามเที่ยวรถรอบนี้หรือไม่ ?";
                adb.setTitle("ติดตามรอบรถ " + word + "นาฬิกา ?");
                adb.setNegativeButton("ยกเลิก", null);

                // Set ITEM ID SELECTED BY USER TO DATABASE
                adb.setPositiveButton("ติดตาม", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pt = position + 1;
                        DatabaseReference db =
                                FirebaseDatabase.getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Times/Follow/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Inside/"+pt);
                        db.child("id").setValue(pt);
                        db.child("position").setValue(str_position);
                        db.child("time").setValue(str_time)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "ติดตามรอบรถสำเร็จ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "เกิดข้อผิดพลาดในการติดตามรอบรถ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        // write phone token id follower to database
                        DatabaseReference notifyDB =
                                FirebaseDatabase.getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("NotifyCount/data/"+access_token).child(word);
                        notifyDB.child("id").setValue(access_token);
                        notifyDB.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        notifyDB.child("time").setValue(str_time)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.e("notifyDB", "Complete" );
                                        } else {
                                            Log.e("notifyDB", "Failed" );
                                        }
                                    }
                                });
                    }
                });
                adb.show();
                Log.d("titleTAG", title);
            }
        });

        // fetch car data from firebase to display in listview
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Times/Inside");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TimeSchedule timeSchedule = dataSnapshot.getValue(TimeSchedule.class);
                    // config here emoji here
                    String txt = timeSchedule.getTime() + "\n\uD83D\uDE89 " + "(ขาเข้า) มทส.";
                    // end of config
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
                    Date date = new Date(System.currentTimeMillis());
                    String _date = formatter.format(date);

                    // cut : out date form
                    String[] separated = new String[1];
                    separated = _date.split(":",3);
                    double hours = Double.parseDouble(separated[0]);
                    double seconds = Double.parseDouble(separated[1]);

                    // calculate hours to second 3600 * hours
                     //และเอา นาทีมาคูณวินาที + ด้วยผลลัพธ์ชั่วโมงจะได้เวลาจริงที่เป็นวินาที
                    hours = 6;
                    seconds = 1;

                    seconds = (hours * 3600) + (seconds * 60);

                    if (hours < 5) {
                        // if the time until 00:00 to 05:00 AM not show anything
                        // show txt
                        gone.setText("ขออภัยเที่ยวเดินรถยังไม่เปิดให้บริการ");
                        gone.setVisibility(View.VISIBLE);
                    }
                    else {
                        gone.setVisibility(View.GONE);
                        if (timeSchedule.getId() == 1 && seconds <= 23400)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 2 && seconds <= 27000)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 3 && seconds <= 30600)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 4 && seconds <= 34200)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 5 && seconds <= 37800)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 6 && seconds <= 41400)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 7 && seconds <= 45000)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 8 && seconds <= 48600)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 9 && seconds <= 52200)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 10 && seconds <= 55800)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 11 && seconds <= 59400)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 12 && seconds <= 63000)
                        {
                            list.add(txt);
                        }
                        if (seconds > 63000) {
                            // time out for bus rounds
                            gone.setVisibility(View.VISIBLE);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}