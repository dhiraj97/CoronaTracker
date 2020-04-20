package com.example.coronatracker;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewDataFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Patient> mPatients = new ArrayList<>();
    private PatientListAdapter mAdapter;
    private DatabaseHelper dbh;
    public ViewDataFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_data,container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        dbh = new DatabaseHelper(getActivity());
        Cursor cursor = dbh.viewData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No record Found", Toast.LENGTH_SHORT).show();
            return v;
        } else {
            if (cursor.moveToFirst()) {
                do {
                    //Creates a student object and passes that object in the Arraylist
                    Patient patient = new Patient();
                    patient.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    patient.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                    patient.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                    patient.setAge(cursor.getInt(cursor.getColumnIndex("age")));
                    patient.setCity(cursor.getString(cursor.getColumnIndex("city")));
                    patient.setCountry(cursor.getString(cursor.getColumnIndex("country")));
                    patient.setDateOfInfection(cursor.getString(cursor.getColumnIndex("dateOfInfection")));
                    patient.setAlive(cursor.getInt(cursor.getColumnIndex("alive")));
                    patient.setRecovered(cursor.getInt(cursor.getColumnIndex("recovered")));

                    mPatients.add(patient);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        dbh.close();
        bindAdapter();
        return v;
    }
    private void bindAdapter() {
        //Specifies that the layout for recycler view is vertical linear layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PatientListAdapter(mPatients, getContext());
        mRecyclerView.setAdapter(mAdapter);
        //tells the recycler view to update every single item
        mAdapter.notifyDataSetChanged();
    }
}
