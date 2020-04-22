package com.example.coronatracker;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AnalyticsFragment extends Fragment
{
    BarChart barChart;
    public AnalyticsFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics,container, false);
        barChart=(BarChart)v.findViewById(R.id.barChart);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries=new ArrayList<>();
        barEntries.add(new BarEntry(1,46f));
        barEntries.add(new BarEntry(2,20f));
        barEntries.add(new BarEntry(3,24f));
        barEntries.add(new BarEntry(3,36f));

        BarDataSet barDataSet=new BarDataSet(barEntries,"Data set1");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData=new BarData(barDataSet);
        barData.setBarWidth(0.7f);

        barChart.setData(barData);

        String[] cities = new String[]{"Waterloo","Kitchener","Cambridge","Guelph","Toronto"};
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(cities));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
       // xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);

        return v;
    }
   public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter
    {
        public String[] mValues;
        public MyXAxisValueFormatter(String[] mValues)
        {
            this.mValues=mValues;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }

}


