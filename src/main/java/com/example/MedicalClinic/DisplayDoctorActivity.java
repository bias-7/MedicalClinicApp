package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DisplayDoctorActivity extends AppCompatActivity implements View.OnClickListener {

    private Patient loggedInUser;

    private List<Doctor> doctors;

    private ListView listViewDoctor;

    private Button filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_doctor);

        loggedInUser = (Patient) getIntent().getSerializableExtra(getString(R.string.loggedInUser));

        filter = (Button) findViewById(R.id.filterDoctor);
        filter.setOnClickListener(this);

        doctors = new ArrayList<Doctor>();

        listViewDoctor = (ListView) findViewById(R.id.listViewDoctor);

        listViewDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doc = doctors.get(position);

                Intent intent = new Intent(DisplayDoctorActivity.this, DoctorInfoActivity.class);
                intent.putExtra(getString(R.string.loggedInUser), loggedInUser);
                intent.putExtra(getString(R.string.userInfo), doc);

                startActivity(intent);
            }

        });


        getDoctors(null);
    }

    private void getDoctors(String specialty) {

        Query query = FirebaseDatabase.getInstance().getReference("Doctors")
                .orderByChild("specialty");

        if (specialty != null && !specialty.equals(""))
            query = query.equalTo(specialty);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctors.clear();

                for (DataSnapshot docSnapshot: snapshot.getChildren()) {

                    Doctor doctor = docSnapshot.getValue(Doctor.class);
                    doctor.setID(docSnapshot.getKey());

                    doctors.add(doctor);

                }

                // update UI (List view)
                DoctorAdapter adapter = new DoctorAdapter(DisplayDoctorActivity.this, R.layout.doctor_item_layout, doctors);
                listViewDoctor.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }


    private void getDoctorGender(String specialty) {

        Query query = FirebaseDatabase.getInstance().getReference("Doctors")
                .orderByChild("gender");

        if (specialty != null && !specialty.equals(""))
            query = query.equalTo(specialty);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctors.clear();

                for (DataSnapshot docSnapshot: snapshot.getChildren()) {

                    Doctor doctor = docSnapshot.getValue(Doctor.class);
                    doctor.setID(docSnapshot.getKey());

                    doctors.add(doctor);

                }

                // update UI (List view)
                DoctorAdapter adapter = new DoctorAdapter(DisplayDoctorActivity.this, R.layout.doctor_item_layout, doctors);
                listViewDoctor.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filterDoctor:

                String specialty = ((EditText) findViewById(R.id.editTextSpecialty)).getText().
                        toString().trim().toLowerCase();

                if (specialty.equals("male")|| specialty.equals("female") )
                {
                    getDoctorGender(specialty);
                }
                else {
                    getDoctors(specialty);
                }
        }
    }
}