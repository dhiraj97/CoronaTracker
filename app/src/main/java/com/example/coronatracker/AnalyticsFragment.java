package com.example.coronatracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AnalyticsFragment extends Fragment {
    BarChart barChart;
    DatabaseHelper dbh;
    PieChart pieChart;
    private int[] yData = new int[4];
    private String[] xData = {"1-20", "20-45", "45-80"};

    public AnalyticsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);
        barChart = v.findViewById(R.id.barChart);
        pieChart = v.findViewById(R.id.pieChart);
        dbh = new DatabaseHelper(getActivity());
        //Code to Add Bar Chart
        //Bar chart that represents number of patients per city
        barChart.setDrawBarShadow(true);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(100);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        ArrayList<String> cityNames = new ArrayList<>(); //to store all cities in ArrayList
        float i = 0f;
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        Cursor cursor = dbh.getInfectedPatientsByCity();
        if (cursor.getCount() == 0)  //if there are empty records
        {
            Toast.makeText(getActivity(), "No data Available", Toast.LENGTH_SHORT).show();
        } else {
            if (cursor.moveToFirst()) {
                do { //Log.i("info", cursor.getString(cursor.getColumnIndex("city")));
                    // Log.i("info", cursor.getInt(cursor.getColumnIndex("patientCount")) + "");
                    int count = (cursor.getInt(cursor.getColumnIndex("patientCount")));//getting count of number of patients affected per city
                    cityNames.add(cursor.getString(cursor.getColumnIndex("city"))); //getting city name
                    barEntries.add(new BarEntry(i, count)); //adding count and city name to barchart
                    i = i + 1f;
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        BarDataSet barDataSet = new BarDataSet(barEntries, "City Wise Corona Patients Count");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //Formatting value to Int
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

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
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextSize(12f); // set the text size
        yAxis.setAxisMinimum(0f); // start at zero
        yAxis.setAxisMaximum(10f); // the axis maximum is 100
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1f); // interval 1

        //----------------------------------
        //Code to represent Piechart
        pieChart.setRotationEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Effect of COVID-19 over age groups");
        pieChart.setCenterTextSize(13f);
        pieChart.setHoleRadius(30f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(20f);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        addDataSet();
        return v;
    }

    //Calling addDataSet function
    private void addDataSet() {
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();
        yData = dbh.getCount();
        for (int i = 0; i < yData.length; i++) {
            yEntry.add(i, new PieEntry(yData[i]));
        }
        for (int i = 0; i < xData.length; i++) {
            xEntry.add(xData[i]);
        }
        //Create Data Set
        PieDataSet pieDataSet = new PieDataSet(yEntry, "Effect of COVID-19 over age groups");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(14);

        //add Colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.LTGRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        pieDataSet.setColors(colors);
        //add legend
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        //Formatting value to Int
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

           
        //Pie Data
        PieData pieData = new PieData(pieDataSet);


        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(25f);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int position = e.toString().indexOf("y: ");
                String affectedNum = e.toString().substring(position + 3);
                Log.d("OnValueSelected", "onValueSelected: " + affectedNum);
                for (int i = 0; i < yData.length; i++) {
                    if (yData[i] == Float.parseFloat(affectedNum)) {
                        position = i;
                        break;
                    }
                }
                Toast.makeText(getActivity(), "Age Group: " + xData[position] + "\n" + "Count: " + affectedNum, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

    }

}



