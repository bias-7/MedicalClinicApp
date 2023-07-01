package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView banner;

    private EditText editTextName, editTextBirthOrSpecialty, editTextEmail, editTextPassword;
    private CheckBox checkBoxDoctor;
    private Button registerUser;
    private ProgressBar progressBar;

    private RadioGroup radioGroup;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.name);
        editTextBirthOrSpecialty = (EditText) findViewById(R.id.birthOrSpecialty);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        checkBoxDoctor = (CheckBox) findViewById(R.id.doctor);
        checkBoxDoctor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editTextBirthOrSpecialty.setText("");
                if(checkBoxDoctor.isChecked())
                    editTextBirthOrSpecialty.setHint("Specialty");
                else editTextBirthOrSpecialty.setHint("Date of Birth");

            }
        });

        checkBoxDoctor.setChecked(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        String birthOrSpecialty = editTextBirthOrSpecialty.getText().toString().trim().toLowerCase();

        String gender = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).
                getText().toString().trim().toLowerCase();

        if (name.isEmpty()) {
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

        if (birthOrSpecialty.isEmpty()) {
            editTextBirthOrSpecialty.setError("This field is required!");
            editTextBirthOrSpecialty.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{

                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                            FirebaseDatabase.getInstance()
                                    .getReference("UserTypes").child(userID)
                                    .child("userType")
                                    .setValue(checkBoxDoctor.isChecked() ? "Doctor" : "Patient");

                            User user;
                            String ref;


                            if (!checkBoxDoctor.isChecked()){
                                ref = "Patients";
                                user = new Patient(email, name, gender, birthOrSpecialty);

                            }
                            else{
                                ref = "Doctors";
                                user = new Doctor(email, name, gender, birthOrSpecialty);

                            }


                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                    .getReference(ref).child(userID);

                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else {

                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                    }
                                }
                            });

                        }
                    }
                });

    }
}