package com.example.coronatracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Calendar;

public class EnterDataFragment extends Fragment {
    EditText txtFirstName, txtLastName, txtAge, txtStreetAddress, txtCity, txtProvince,
            txtCountry, txtPostalCode, txtLatitude, txtLongitude,
            txtDateOfInfection;
    RadioGroup aliveGroup, recoveredGroup, genderGroup;
    int isAlive = 1;
    int isRecovered = 1;
    String gender;
    Boolean insertStat;
    Button btnSubmit;
    private static final String apiKey = "AIzaSyC1D_ZnFID-vHkPir4F8WI23qjeFCNg2pc";

    DatabaseHelper dbh;
    LatLng mLatLong;
    public EnterDataFragment() {
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            txtDateOfInfection.setText(dayOfMonth + "/" + (monthOfYear + 1)
                    + "/" + year);
        }
    };

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtDateOfInfection.setInputType(InputType.TYPE_NULL);
        txtDateOfInfection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_data,container, false);

        initialize(view);

        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);

        // Initialize the AutocompleteSupportFragment and setting listener.
        setAutoCompleteFragment();

        aliveGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.alivePositive:
                        isAlive = 1;
                        break;
                    case R.id.aliveNegative:
                        isAlive = 0;
                        break;
                }
            }
        });
        recoveredGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.recoveredPositive:
                        isRecovered = 1;
                        break;
                    case R.id.recoveredNegative:
                        isRecovered = 0;
                        break;
                }
            }
        });
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.male:
                        gender = "male";
                        break;
                    case R.id.female:
                        gender = "female";
                        break;
                    case R.id.other:
                        gender = "other";
                        break;
                }
            }
        });

        dbh = new DatabaseHelper(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int patientId = 0;
                Patient patient = new Patient(patientId, txtFirstName.getText().toString(), txtLastName.getText().toString(), Integer.parseInt(txtAge.getText().toString()), txtStreetAddress.getText().toString(), txtCity.getText().toString(), txtProvince.getText().toString(), txtCountry.getText().toString(), txtPostalCode.getText().toString(), mLatLong.latitude, mLatLong.longitude, txtDateOfInfection.getText().toString(), isAlive, isRecovered, gender);

                insertStat = dbh.insertPatient(patient);
                if (insertStat) {
                    Toast.makeText(getActivity(), "Record Added Successfully", Toast.LENGTH_SHORT).show();
                } else if (!insertStat) {
                    Toast.makeText(getActivity(), "Record Not Added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void initialize(View view) {
        txtFirstName = view.findViewById(R.id.txtFullName);
        txtLastName = view.findViewById(R.id.txtLastName);
        txtAge = view.findViewById(R.id.txtAgeGender);
        txtStreetAddress = view.findViewById(R.id.txtStreetAddress);
        txtCity = view.findViewById(R.id.txtFullAddress);
        txtProvince = view.findViewById(R.id.txtProvince);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtPostalCode = view.findViewById(R.id.txtPostalCode);
        txtLatitude = view.findViewById(R.id.txtLatitude);
        txtLongitude = view.findViewById(R.id.txtLongitude);
        txtDateOfInfection = view.findViewById(R.id.txtDateOfInfection);

        btnSubmit = view.findViewById(R.id.btnSubmit);

        aliveGroup = view.findViewById(R.id.aliveRadioGroup);
        recoveredGroup = view.findViewById(R.id.recoveredRadioGroup);
        genderGroup = view.findViewById(R.id.genderRadioGroup);
    }

    private void setAutoCompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mLatLong = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }
}
