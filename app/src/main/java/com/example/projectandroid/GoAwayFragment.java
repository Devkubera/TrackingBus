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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoAwayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoAwayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // declare variable
    private ListView listView;
    private TextView empty;
    public static String token_id;
    View view;

    FirebaseAuth auth;

    public GoAwayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoAwayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoAwayFragment newInstance(String param1, String param2) {
        GoAwayFragment fragment = new GoAwayFragment();
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
        view = inflater.inflate(R.layout.fragment_go_away, container, false);

        // firebase authentication
        auth = FirebaseAuth.getInstance();

        // listView handler
        listView = (ListView) view.findViewById(R.id.list_follower);
        empty = (TextView) view.findViewById(R.id.empty);

        final ArrayList<String> list = new ArrayList<>();

        // set background different color in listview
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.list_go_away,list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Get current item for ListView
                View v = super.getView(position, convertView, parent);
                if (position %2 == 1) {
                    v.setBackgroundColor(getResources().getColor(
                            R.color.blue_dark
                    ));
                } else {
                    v.setBackgroundColor(getResources().getColor(
                            R.color.blue_darkBight
                    ));
                }
                return  v;
            }

        };
        listView.setAdapter(adapter);

        // get token id
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    token_id = task.getResult();
                    Log.e("Access Token", "onComplete: new Token got: "+ token_id );

                }
            }
        });

        // set on click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                //DatetimeData data = new DatetimeData();
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                String title = adapterView.getItemAtPosition(postion).toString();
                // cut nouns
                String[] separated = new String[1];
                separated = title.split("น");
                String word = separated[0];

                // get time
                String[] time_array = new String[1];
                time_array = title.split("\n");
                String time_firebase = time_array[0];

                TimeSchedule timeSchedule = new TimeSchedule();

                String desc = "ยกเลิกการติดตามเที่ยวรถรอบนี้หรือไม่ ?";
                //adb.setMessage(desc);
                adb.setTitle("เลิกติดตามเที่ยวรถ " + word + "นาฬิกา ?");
                adb.setNegativeButton("ไม่ใช่", null);
                // Set ITEM ID SELECTED BY USER TO DATABASE
                adb.setPositiveButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pt = postion + 1;
                        Query db;
                        db = FirebaseDatabase
                                .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Times/Follow/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/")
                                .child("Inside")
                                .orderByChild("time")
                                .equalTo(time_firebase);
                        Log.d("pt_number", String.valueOf(pt));
                        Log.d("title", title);
                        // remove data snapshot
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot deleteSnapshot : snapshot.getChildren()) {
                                    deleteSnapshot.getRef().removeValue();

                                    // delete token id follower
                                    Query token_query;
                                    token_query = FirebaseDatabase
                                            .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                            .getReference("NotifyCount/data/")
                                            .child(token_id)
                                            .orderByChild("time")
                                            .equalTo(time_firebase);
                                    token_query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot delSnap : snapshot.getChildren()){
                                                delSnap.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        // Toast success
                        Toast.makeText(getContext(), "เลิกติดตามรอบรถสำเร็จ", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.show();
            }
        });

        // Show data in firebase
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://tracking-car-48339-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Times/Follow/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Inside");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TimeSchedule timeSchedule = dataSnapshot.getValue(TimeSchedule.class);
                    // config here emoji here
                    String txt = timeSchedule.getTime() + "\n\uD83D\uDE89 " + timeSchedule.getPosition();
                    list.add(txt);
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