package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeslotActivity extends AppCompatActivity {

    private Spinner spinner;

    private ListView timeslotListView;

    private String selectedDay;

    private User loggedInUser;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslot);

        timeslotListView = (ListView) findViewById(R.id.timeslotListView);


        spinner = (Spinner) findViewById(R.id.spinner);

        userID = getIntent().getStringExtra(getString(R.string.user_key));

        FirebaseDatabase.getInstance().getReference("Doctors")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loggedInUser = snapshot.getValue(Doctor.class);
                loggedInUser.setID(snapshot.getKey());

                if (loggedInUser == null) {
                    Toast.makeText(TimeslotActivity.this, "Failed retrieve User Info!", Toast.LENGTH_LONG).show();
                }

                ArrayAdapter<Object> a = new ArrayAdapter<>(TimeslotActivity.this, android.R.layout.simple_spinner_item, Doctor.days);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDay = (String) parent.getItemAtPosition(pos);

                List<String> availableHours = new ArrayList<String>();

                Map<String, String> timeslot = ((Doctor) loggedInUser).getTimeslots().get(selectedDay);
                for (String hour : Doctor.hours) {
                    if (timeslot.get(hour).equals(""))
                        availableHours.add(hour);
                    else
                        availableHours.add(hour + ": " + timeslot.get(hour));
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(TimeslotActivity.this, android.R.layout.simple_list_item_1, availableHours);
                timeslotListView.setAdapter(aa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}