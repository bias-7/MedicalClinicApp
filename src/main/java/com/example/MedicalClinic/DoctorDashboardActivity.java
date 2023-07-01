package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorDashboardActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnUpcomingAppointments, btnViewPatients, btnViewTimeslot, btnLogout;


    private User loggedInUser;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);


        btnUpcomingAppointments = (Button) findViewById(R.id.btnUpcomingAppointments);
        btnUpcomingAppointments.setOnClickListener(this);

        btnViewPatients = (Button) findViewById(R.id.btnViewPatients);
        btnViewPatients.setOnClickListener(this);

        btnViewTimeslot = (Button) findViewById(R.id.btnViewTimeslot);
        btnViewTimeslot.setOnClickListener(this);

        btnLogout = (Button) findViewById(R.id.logOut);
        btnLogout.setOnClickListener(this);

        userID = getIntent().getStringExtra(getString(R.string.user_key));

        FirebaseDatabase.getInstance().getReference("Doctors")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loggedInUser = snapshot.getValue(Doctor.class);
                loggedInUser.setID(snapshot.getKey());

                if (loggedInUser == null) {
                    Toast.makeText(DoctorDashboardActivity.this, "Failed retrieve User Info!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onClick(View view) {

        Intent intent;
        switch (view.getId()) {
            case R.id.btnViewPatients:
                intent = new Intent(this, DisplayPatientActivity.class);
                intent.putExtra(getString(R.string.loggedInUser), loggedInUser);
                startActivity(intent);
                break;

            case R.id.btnViewTimeslot:
                intent = new Intent(this, TimeslotActivity.class);
                intent.putExtra(getString(R.string.user_key), userID);
                startActivity(intent);
                break;

            case R.id.btnUpcomingAppointments:
                intent = new Intent(this, DisplayAppointmentActivity.class);
                intent.putExtra(getString(R.string.loggedInUser), loggedInUser);
                intent.putExtra(getString(R.string.upcoming_appointment_key), true);
                startActivity(intent);
                break;
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DoctorDashboardActivity.this, MainActivity.class));
                break;
        }
    }
}