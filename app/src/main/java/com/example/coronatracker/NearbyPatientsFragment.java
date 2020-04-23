package com.example.coronatracker;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbyPatientsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "NearbyPatientsFragment";
    private static final String apiKey = "AIzaSyC1D_ZnFID-vHkPir4F8WI23qjeFCNg2pc";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final float DEFAULT_ZOOM = 15.0f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DatabaseHelper dbh;
    private List<Patient> mPatients = new ArrayList<>();

    public NearbyPatientsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_patients,container, false);

        //Fetching patient data from SQLite Database and setting in Arraylist.
        fetchPatientData();

        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);

        // Initialize the AutocompleteSupportFragment and setting listener.
        setAutoCompleteFragment();

        //Validating location permission, If not granted, Request to user
        ValidateLocationPermission();

        //Checking if play services is working
        isServicesOK();

        return view;
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
                moveCamera(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), DEFAULT_ZOOM, place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void fetchPatientData() {

        dbh = new DatabaseHelper(getActivity());
        Cursor cursor = dbh.viewData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No record Found", Toast.LENGTH_SHORT).show();

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
                    patient.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                    patient.setCountry(cursor.getString(cursor.getColumnIndex("country")));
                    patient.setDateOfInfection(cursor.getString(cursor.getColumnIndex("dateOfInfection")));
                    patient.setAlive(cursor.getInt(cursor.getColumnIndex("alive")));
                    patient.setRecovered(cursor.getInt(cursor.getColumnIndex("recovered")));
                    patient.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                    patient.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                    patient.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                    mPatients.add(patient);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        dbh.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Log.e(TAG, "onViewCreated: View created");
        initMap();
        Log.e(TAG, "onViewCreated: After Init created");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do this
                    mLocationPermissionGranted = true;
                    Log.e(TAG, "onRequestPermissionsResult: Granted");
                    setMapLocationFunctionality(true);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    setMapLocationFunctionality(false);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapStyle(googleMap);
        Log.e(TAG, "onMapReady: Map ready, mLocationPermissionGranted: " + mLocationPermissionGranted);
        setMapLocationFunctionality(mLocationPermissionGranted);
        Log.e(TAG, "onMapReady: After setMapLocationFunctionality, mLocationPermissionGranted: " + mLocationPermissionGranted);

        for (int i = 0; i < mPatients.size(); i++) {
            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mPatients.get(i).getLatitude(), mPatients.get(i).getLongitude()))
                    .title(mPatients.get(i).getAge() + ", " + mPatients.get(i).getGender().substring(0, 1).toUpperCase() + mPatients.get(i).getGender().substring(1))
                    .snippet("(" + mPatients.get(i).getDateOfInfection() + ")")
                    .icon(BitmapDescriptorFactory.fromResource(Helper.getMapIcon(mPatients.get(i)))));
            mMarker.setTag(0);
            Log.e(TAG, "onMapReady: " + mPatients.get(i).getLatitude() + ", " + mPatients.get(i).getLongitude() + "" + Helper.getMapIcon(mPatients.get(i)));
        }

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }


    //checking google services Availability
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            //everything is ok
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but resolvable
            Log.d(TAG, "isServicesOK: an error occurred, but is fixable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(),"You cannot make Map Request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void ValidateLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            else{
                requestPermissions(permissions, 1);
            }
        } else {
            requestPermissions(permissions, 1);
        }

    }


    private void initMap() {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }


    private void setMapStyle(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void setMapLocationFunctionality(boolean value) {
        if (value) {
            setDeviceCurrentLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            //Adding listener to Search box
            //  addSearchTextActionListener();
        } else {
            Toast.makeText(getActivity(), "Unable to use Location, Functionality disabled", Toast.LENGTH_SHORT).show();
        }
    }

    //Setting Device current location
    private void setDeviceCurrentLocation() {
        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "Home");
                        } else{
                            Toast.makeText(getActivity(),"Unable to find location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch(SecurityException e){
            Log.e(TAG,""+e.getMessage());
        }
    }


    //Method to reuse for setting up Camera.
    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }

}
