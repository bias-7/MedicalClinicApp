package com.example.MedicalClinic;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class Model {

    public Model() {

    }

    public void authenticate(String email, String password, Consumer<User> callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // get current log-in User ID
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // get the userType (Doctor or Patient)
                    FirebaseDatabase.getInstance().getReference("UserTypes")
                            .child(userID).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userType = snapshot.getValue(String.class);

                            if (userType == null) {
                                Log.d("Model", "Failed to authenticate, cannot find userType");
                                callback.accept(null);
                            }

                            else if (userType.equals("Doctor")) {
                                Doctor doctor = new Doctor();
                                doctor.setID(userID);
                                callback.accept(doctor);
                            }

                            else if (userType.equals("Patient")) {
                                Patient patient = new Patient();
                                patient.setID(userID);
                                callback.accept(patient);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Log.d("Model", "Failed to authenticate.");
                    callback.accept(null);
                }
            }
        });
    }
}