package com.example.coronatracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DeleteFragment extends Fragment {
    EditText txtId;
    Button btnDelete;
    DatabaseHelper dbh;

    public DeleteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete,container, false);txtId = view.findViewById(R.id.txtDeletePatientById);
        btnDelete = view.findViewById(R.id.btnDelete);
        dbh = new DatabaseHelper(getActivity());

        dbh = new DatabaseHelper(getActivity());
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = dbh.deleteRecord(Integer.parseInt(txtId.getText().toString()));
                if (count > 0) {
                    Toast.makeText(getContext(), "Deleted Record Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error: Deleted Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
