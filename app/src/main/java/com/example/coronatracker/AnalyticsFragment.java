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
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AnalyticsFragment extends Fragment {
    BarChart barChart;
    DatabaseHelper dbh;
    PieChart pieChart;
    private int[] yData =new int[4];
    private String[] xData = {"1-20","20-45","45-80"};
    public AnalyticsFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);
        barChart = (BarChart) v.findViewById(R.id.barChart);
        pieChart=(PieChart)v.findViewById(R.id.pieChart);
        dbh = new DatabaseHelper(getActivity());
        //Code to Add Bar Chart
        //Bar chart that represents number of patients per city
        barChart.setDrawBarShadow(true);
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

            BarDataSet barDataSet = new BarDataSet(barEntries, "City Wise Corona Patients Count");
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
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.setTextSize(12f); // set the text size
            yAxis.setAxisMinimum(0f); // start at zero
            yAxis.setAxisMaximum(10f); // the axis maximum is 100
            yAxis.setTextColor(Color.BLACK);
           yAxis.setGranularity(1f); // interval 1

            //----------------------------------
            //Code to represent Piechart
            pieChart.setRotationEnabled(true);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.setCenterText("Affect in different Age Groups");
            pieChart.setCenterTextSize(13f);
            pieChart.setCenterTextSize(10);
            pieChart.setHoleRadius(30f);
            addDataSet();
            return v;
    }
    //Calling addDataSet function
    private void addDataSet() {
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();
        yData=dbh.getCount();
        for(int i = 0;i < yData.length; i++){
            yEntry.add(i, new PieEntry(yData[i]));
        }
        for(int i = 0;i < xData.length; i++){
            xEntry.add(xData[i]);
        }
        //Create Data Set
        PieDataSet pieDataSet = new PieDataSet(yEntry, "Affect of Corona in Age Groups");
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
        //Pie Data
        PieData pieData = new PieData(pieDataSet);
       //pieData.setValueFormatter(new DecimalRemover());
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int position = e.toString().indexOf("y: ");
                String affectedNum = e.toString().substring(position + 3);
                Log.d("OnValueSelected", "onValueSelected: "+affectedNum);
                for(int i=0;i<yData.length;i++){
                    if(yData[i] == Float.parseFloat(affectedNum)){
                        position = i;
                        break;
                    }
                }
                Toast.makeText(getActivity(),"Age Group: "+xData[position ]+"\n"+"Count: "+affectedNum,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
            }
        });

    }
   /* public class DecimalRemover extends DefaultValueFormatter {
        public int digits;
        public DecimalRemover(int digits) {
            super(digits);
            this.digits = digits;
        }
        @Override
        public int getDecimalDigits() {
            return digits;
        }
    }*/
}



