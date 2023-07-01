package com.example.MedicalClinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.ThrowOnExtraProperties;

import java.util.List;

public class DoctorAdapter extends ArrayAdapter<Doctor> {
    private Context context;
    private int resource;

    public DoctorAdapter(Context context, int resource, List<Doctor> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    private class ViewHolder {
        TextView name;
        TextView specialty;
        TextView gender;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();
        String specialty = getItem(position).getSpecialty();
        String gender = getItem(position).getGender();

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.specialty = (TextView) convertView.findViewById(R.id.specialty);
            holder.gender = (TextView) convertView.findViewById(R.id.gender);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(name);
        holder.specialty.setText(specialty);
        holder.gender.setText(gender);

        return convertView;
    }
}