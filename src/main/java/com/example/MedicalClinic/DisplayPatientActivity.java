package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayPatientActivity extends AppCompatActivity {

    private User loggedInUser;

    private List<Patient> patients;

    private ListView listViewPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_patient);


        loggedInUser = (User) getIntent().getSerializableExtra(getString(R.string.loggedInUser));

        patients = new ArrayList<Patient>();

        listViewPatient = (ListView) findViewById(R.id.listViewPatient);

        listViewPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Patient patient = patients.get(position);

                Intent intent = new Intent(DisplayPatientActivity.this, PatientInfoActivity.class);
                intent.putExtra(getString(R.string.userInfo), patient);
                startActivity(intent);
            }

        });

        getPatients("");
    }

    private void getPatients(String name) {

        Query query = FirebaseDatabase.getInstance().getReference("Patients")
                .orderByChild("name");

        if (name != null && !name.equals(""))
            query = query.equalTo(name);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patients.clear();

                for (DataSnapshot docSnapshot: snapshot.getChildren()) {

                    Patient patient = docSnapshot.getValue(Patient.class);
                    patient.setID(docSnapshot.getKey());

                    patients.add(patient);

                }

                // update UI (List view)
                PatientAdapter adapter = new PatientAdapter(DisplayPatientActivity.this, R.layout.patient_item_layout, patients);
                listViewPatient.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}