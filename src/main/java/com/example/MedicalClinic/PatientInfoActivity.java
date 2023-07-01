package com.example.MedicalClinic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PatientInfoActivity extends AppCompatActivity {


    private Patient selectedPatient;

    private TextView textViewPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);


        selectedPatient = (Patient) getIntent().getSerializableExtra(getString(R.string.userInfo));

        textViewPatient = (TextView) findViewById(R.id.textViewPatient);
        textViewPatient.setText(selectedPatient.toString());
    }
}