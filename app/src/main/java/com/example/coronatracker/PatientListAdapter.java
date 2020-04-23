package com.example.coronatracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Patient> mList;

    public PatientListAdapter(List<Patient> list, Context ctx) {
        super();
        mList = list;
    }

    @Override
    //Involves populating data into the item through holder
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Patient patient = mList.get(position);
        ((ViewHolder) holder).txtId.setText("(" + patient.getId() + ")");
        ((ViewHolder) holder).txtFullName.setText(patient.getLastName() + " " + patient.getFirstName());
        ((ViewHolder) holder).txtAgeGender.setText(patient.getAge() + ", " + patient.getGender().substring(0, 1).toUpperCase() + patient.getGender().substring(1));
        ((ViewHolder) holder).txtFullAddress.setText(patient.getStreetAddress() + ", " + patient.getCity() + ", " + patient.getProvince() + ", " + patient.getCountry() + ", " + patient.getPostalCode());
        ((ViewHolder) holder).txtDateOfInfection.setText("Date of Infection: " + patient.getDateOfInfection());
        ((ViewHolder) holder).imgProfile.setImageResource(getMapIcon(patient));
        String mCurrentStatus = "";
        if (patient.getAlive() == 1) {
            if (patient.getRecovered() == 1) {
                mCurrentStatus = "Recovered";
            } else {
                mCurrentStatus = "Still Infected";
            }
        } else {
            mCurrentStatus = "Death";
        }
        ((ViewHolder) holder).txtCurrentStatus.setText("Current Status: " + mCurrentStatus);

    }

    @NonNull
    @Override
    //Usually involves inflating a layout from XML and returning the holder
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public int getMapIcon(Patient patient) {
        Log.d("VIVEK", "isServicesOK: checking google services version" + patient.getGender());
        if (patient.getAlive() == 0) {
            return R.drawable.death;
        } else if (patient.getRecovered() == 1) {
            return R.drawable.discharged;
        } else if (patient.getGender().equals("male")) {
            return R.drawable.activemale;
        } else {
            return R.drawable.activefemale;
        }
    }

    //Provide a direct reference to each of the views within a data item
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtId, txtFullName, txtAgeGender, txtFullAddress, txtDateOfInfection, txtCurrentStatus;
        public ImageView imgProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtAgeGender = itemView.findViewById(R.id.txtAgeGender);
            txtFullAddress = itemView.findViewById(R.id.txtFullAddress);
            txtDateOfInfection = itemView.findViewById(R.id.txtDateOfInfection);
            txtCurrentStatus = itemView.findViewById(R.id.txtCurrentStatus);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }

    }

    @Override
    //Returns the total count of items in the list
    public int getItemCount() {
        return mList.size();
    }
}
