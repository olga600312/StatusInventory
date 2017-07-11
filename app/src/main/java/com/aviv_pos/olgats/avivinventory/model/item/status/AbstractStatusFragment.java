package com.aviv_pos.olgats.avivinventory.model.item.status;

import android.support.v4.app.Fragment;

import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by olgats on 07/04/2016.
 */
public abstract class AbstractStatusFragment extends Fragment {
    protected void setupChart(BarChart chart, String description) {

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.setDescription(description);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        chart.setVisibleXRangeMaximum(4);// allow 20 values to be displayed at once on the x-axis, not more

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);


        chart.setDrawGridBackground(false);
        // thisMonthTotalChart.setDrawYLabels(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        // xAxis.setSpaceBetweenLabels(2);
        xAxis.setLabelRotationAngle(-90f);
        xAxis.setLabelsToSkip(0);


        YAxis leftAxis = chart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        YAxisValueFormatter yFormat = new YAxisValueFormatter() {

            private DecimalFormat mFormat = new DecimalFormat("0");

            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return mFormat.format(value);
            }
        };
        leftAxis.setValueFormatter(yFormat);
               /* XAxisValueFormatter xFormat = new XAxisValueFormatter() {

            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                return String.format("%7.7s", original);
            }


        };
        xAxis.setValueFormatter(xFormat);*/


        chart.getAxisRight().setEnabled(false); // no right axis

        chart.setKeepPositionOnRotation(true);
    }

    protected void setupLegend(BarChart chart) {
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        l.setYOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
    }

    protected String getXValue(StatusInfo info) {
        String city = info.getAddress();
        String name = city == null || city.trim().isEmpty() ? info.getName() : city;
        return name == null || name.trim().isEmpty() ? info.getAvivId() : city;
    }
}
