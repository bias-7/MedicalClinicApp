package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.DataCollectionDefaultChange;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DisplayAppointmentActivity extends AppCompatActivity {

    private User loggedInUser;

    private List<Appointment> appointments;
    private ListView appointmentsView;

    private boolean isUpcoming;

    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_appointment);

        loggedInUser = (User) getIntent().getSerializableExtra(getString(R.string.loggedInUser));
        isUpcoming = (boolean) getIntent().getBooleanExtra(getString(R.string.upcoming_appointment_key), true);

        appointments = new ArrayList<Appointment>();
        appointmentsView = (ListView) findViewById(R.id.appointmentView);

        db = FirebaseDatabase.getInstance();

        getAppointments();
    }

    private void getAppointments() {

        db.getReference("Appointments")
                .orderByChild(loggedInUser instanceof Patient ? "patientID" : "doctorID")
                .equalTo(loggedInUser.getID())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointments.clear();

                        Calendar now = Calendar.getInstance();

                        for (DataSnapshot appSnapshot: snapshot.getChildren()) {

                            Appointment app = appSnapshot.getValue(Appointment.class);

                            // parse the dateTime to a calendar
                            Calendar appCal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat(Appointment.DATETIME_FORMAT, Locale.ENGLISH);
                            try {
                                appCal.setTime(sdf.parse(app.getDateTime()));
                            } catch (ParseException e) {
                                Toast.makeText(DisplayAppointmentActivity.this, "Failed to get retrieve data!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // showing upcoming appointments (Patient & Doctor), and appointment time not yet passed
                            if (isUpcoming && appCal.compareTo(now) != -1) {
                                appointments.add(app);
                            }

                            // showing previous appointments (only Patient), and appointment time already passed
                            else if (!isUpcoming && appCal.compareTo(now) == -1) {
                                appointments.add(app);
                            }


                            // always check to update Doctor's timeslot if appointment time already passed
                            if (appCal.compareTo(now) == -1) {

                                // get day and hour exactly 1 week after the current appointment, to check if theres another appointment
                                Calendar nextCal = Calendar.getInstance();

                                int appDay = appCal.get(Calendar.DAY_OF_WEEK);
                                int todayDay = nextCal.get(Calendar.DAY_OF_WEEK);
                                int diff = ((appDay - todayDay) + 7) % 7;

                                if (diff == 0) {
                                    int appHour = appCal.get(Calendar.HOUR_OF_DAY);
                                    int todayHour = nextCal.get(Calendar.HOUR_OF_DAY);
                                    if (appHour < todayHour) diff += 7;
                                }

                                nextCal.add(Calendar.DATE, diff);
                                nextCal.set(Calendar.HOUR_OF_DAY, appCal.get(Calendar.HOUR_OF_DAY));
                                nextCal.set(Calendar.MINUTE, appCal.get(Calendar.MINUTE));
                                nextCal.set(Calendar.SECOND, appCal.get(Calendar.SECOND));

                                // now nextDate (nextCal) is the date 1 week after the current appointment time
                                String nextDate = DateFormat.format(Appointment.DATETIME_FORMAT, nextCal).toString();


                                // search for the appointment
                                db.getReference("Appointments")
                                        .orderByChild("dateTime").equalTo(nextDate)
                                        .addValueEventListener(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // if there is another appointment 1 week after, do nothing...
                                                if (snapshot.exists()) return;

                                                // otherwise, we need to update the Doctor's timeslot
                                                db.getReference("Doctors")
                                                        .child(app.getDoctorID())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {

                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                Doctor doc = (Doctor) snapshot.getValue(Doctor.class);

                                                                // update Doctor's timeslot
                                                                doc.resetTimeslot(appDay, appCal.get(Calendar.HOUR_OF_DAY));

                                                                // also update firebase
                                                                db.getReference("Doctors").child(app.getDoctorID()).setValue(doc);

                                                                // add this doctor to the patient's doctor list
                                                                if (loggedInUser instanceof Patient) {
                                                                    ((Patient) loggedInUser).addDoctor(doc.getName());
                                                                    db.getReference("Patients").child(loggedInUser.getID()).setValue(loggedInUser);
                                                                } else {

                                                                    db.getReference("Patients")
                                                                            .child(app.getPatientID())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    Patient patient = (Patient) snapshot.getValue(Patient.class);
                                                                                    patient.addDoctor(loggedInUser.getName());
                                                                                    db.getReference("Patients").child(snapshot.getKey()).setValue(patient);
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {}
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });
                            }
                            AppointmentAdapter a = new AppointmentAdapter(DisplayAppointmentActivity.this, R.layout.appointment_item_layout, appointments);
                            appointmentsView.setAdapter(a);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

    }
}