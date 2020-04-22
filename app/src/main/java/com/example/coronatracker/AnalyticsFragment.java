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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AnalyticsFragment extends Fragment {
    BarChart barChart;
    DatabaseHelper dbh;

    public AnalyticsFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);
        barChart = (BarChart) v.findViewById(R.id.barChart);
        dbh = new DatabaseHelper(getActivity());
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(100);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        ArrayList<String> cityNames = new ArrayList<>(); //to store all cities in ArrayList
        float i=0f;
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Cursor cursor = dbh.getInfectedPatientsByCity();
        if (cursor.getCount() == 0)  //if there are empty records
        {
            Toast.makeText(getActivity(), "No data Available", Toast.LENGTH_SHORT).show();
        } else {
            if (cursor.moveToFirst()) {
                do
                { //Log.i("info", cursor.getString(cursor.getColumnIndex("city")));
                   // Log.i("info", cursor.getInt(cursor.getColumnIndex("patientCount")) + "");
                    int count = (cursor.getInt(cursor.getColumnIndex("patientCount")));//getting count of number of patients effected per city
                    cityNames.add(cursor.getString(cursor.getColumnIndex("city"))); //getting city name
                    barEntries.add(new BarEntry(i, count)); //adding count and city name to barchart
                    i=(float)i+1f;
               }while (cursor.moveToNext());
            }
        }
            cursor.close();

            BarDataSet barDataSet = new BarDataSet(barEntries, "Patients Data");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            BarData barData = new BarData(barDataSet);

            barData.setBarWidth(0.45f);
            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.animateY(1500);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(cityNames));
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setTextSize(15f);
            xAxis.setCenterAxisLabels(false);
            xAxis.setLabelCount(cityNames.size());
            return v;
    }
}


