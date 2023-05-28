package com.example.projectandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    private ListView listView;
    private TextView textView;
    private TextView ghost;

    // auth firebase
    private FirebaseAuth auth;
    // real time database
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public SutFragment() {
        // Required empty public constructor
    }

    public static SutFragment newInstance(String param1, String param2) {
        SutFragment fragment = new SutFragment();
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
        view = inflater.inflate(R.layout.fragment_sut, container, false);

        // auth firebase
        auth = FirebaseAuth.getInstance();

        // firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // ListView handler
        listView = (ListView) view.findViewById(R.id.list_time_sut);
        textView = (TextView) view.findViewById(R.id.return_ways);
        ghost = (TextView) view.findViewById(R.id.ghost_text);

        final ArrayList<String> list = new ArrayList<>();

        // set background different color in listview
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.list_return_away,list) {
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
                AlertDialog.Builder box = new AlertDialog.Builder(getContext());
                // get data string from list view
                String title = adapterView.getItemAtPosition(position).toString();

                // cut time string from title
                String[] cut_time = new String[1];
                cut_time = title.split("น");
                String strTime = cut_time[0];

                // get data from cut_time to store in firebase
                // menu Follow Car
                String[] firebase_time = new String[1];
                firebase_time = title.split("\n\uD83D\uDE89");
                String str_time = firebase_time[0];
                String str_position = firebase_time[1].trim();

                String desc = "ต้องการติดตามเที่ยวรถรอบนี้หรือไม่ ?";
                box.setTitle("ติดตามรอบรถ " + strTime + "นาฬิกา ?");
                box.setNegativeButton("ยกเลิก", null);

                // Set ITEM ID SELECTED BY USER TO DATABASE
                box.setPositiveButton("ติดตาม", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pt = position + 1;
                        DatabaseReference db =
                                FirebaseDatabase.getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("Times/Follow/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ReturnWay/"+pt);
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
                    }
                });
                box.show();
                Log.d("titleTAG", title);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Times/ReturnWay");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TimeScheduleReturnWays timeSchedule = dataSnapshot.getValue(TimeScheduleReturnWays.class);
                    // config here emoji here
                    String txt = timeSchedule.getTime() + "\n\uD83D\uDE89 " + timeSchedule.getPosition();
                    // end of config
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
                    Date date = new Date(System.currentTimeMillis());
                    String _date = formatter.format(date);
                    Log.d("txt HERE", txt);

                    // set title and des to data
//                    ReturnWayTime data = new ReturnWayTime();
//                    data.title = timeSchedule.getTime().toString();
//                    data.des = timeSchedule.getPosition().toString();

                    // cut : out date form
                    String[] separated = new String[1];
                    separated = _date.split(":",3);
                    double hours = Double.parseDouble(separated[0]);
                    double seconds = Double.parseDouble(separated[1]);
                    String string_time = separated[2];

                    Log.d("times", String.valueOf(hours) + " " + String.valueOf(seconds) + " " + string_time);

                    // calculate hours to second 3600 * hours
                    // และเอา นาทีมาคูณวินาที + ด้วยผลลัพธ์ชั่วโมงจะได้เวลาจริงที่เป็นวินาที
                    hours = 6;
                    seconds = 0;
                    seconds = (hours * 3600) + (seconds * 60);

                    if (hours < 5) {
                        // if the time until 00:00 to 05:00 AM not show anything
                        // show txt
                        ghost.setText("ขออภัยเที่ยวเดินรถยังไม่เปิดให้บริการ");
                        ghost.setVisibility(View.VISIBLE);
                    }
                    else {
                        ghost.setVisibility(View.GONE);
                        if (timeSchedule.getId() == 1 && seconds <= 21600)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 2 && seconds <= 24000)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 3 && seconds <= 28800)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 4 && seconds <= 31200)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 5 && seconds <= 36000)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 6 && seconds <= 38400)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 7 && seconds <= 43200)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 8 && seconds <= 45600)
                        {
                            txt = "12:40 นาฬิกา" + "\n\uD83D\uDE89 " + timeSchedule.getPosition();
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 9 && seconds <= 50400)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 10 && seconds <= 52800)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 11 && seconds <= 57600)
                        {
                            list.add(txt);
                        }
                        if (timeSchedule.getId() == 12 && seconds <= 60000)
                        {
                            list.add(txt);
                        }
                        if (seconds > 60000) {
                            // time out for bus rounds
                            ghost.setVisibility(View.VISIBLE);
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