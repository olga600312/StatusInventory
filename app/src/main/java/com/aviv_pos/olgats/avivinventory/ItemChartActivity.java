package com.aviv_pos.olgats.avivinventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.aviv_pos.olgats.avivinventory.async.ItemSaveTask;
import com.aviv_pos.olgats.avivinventory.async.TransactionDataTask;
import com.aviv_pos.olgats.avivinventory.beans.Result;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ItemChartActivity extends AppCompatActivity implements TransactionDataTask.Listener {
    private LineChartView chart;
    private ProgressDialog progress;
    private TextView tvFromDate;
    private TextView tvToDate;
    private String currentCode;
    private long fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.itemChartToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        TextView lblFromDate=(TextView) findViewById(R.id.lblFromDate);
        TextView lblToDate=(TextView) findViewById(R.id.lblToDate);


        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar c=new GregorianCalendar();
                c.setTimeInMillis(fromDate);
                DialogFragment newFragment = new DatePickerFragment(c) {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        GregorianCalendar gc = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        fromDate = gc.getTimeInMillis();
                        tvFromDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        };
        tvFromDate.setOnClickListener(l);
        lblFromDate.setOnClickListener(l);

        View.OnClickListener l1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar c=new GregorianCalendar();
                c.setTimeInMillis(toDate);
                DialogFragment newFragment = new DatePickerFragment(c) {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        GregorianCalendar gc = new GregorianCalendar(year, monthOfYear, dayOfMonth, 23, 59, 59);
                        toDate = gc.getTimeInMillis();
                        tvToDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        };
        tvToDate.setOnClickListener(l1);
        lblToDate.setOnClickListener(l1);

        chart = (LineChartView) findViewById(R.id.salesChart);


        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setZoomEnabled(true);
        chart.setZoomType(ZoomType.HORIZONTAL);
        // For build-up animation you have to disable viewport recalculation.
        // chart.setViewportCalculationEnabled(false);
        chart.setValueSelectionEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabChart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(view);
                if (currentCode != null) {
                    showChart(currentCode);
                }
            }
        });
    }

    private LineChartData createChartData(Result result) {
        List<PointValue> arrPoints = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (Iterator it = result.getData().iterator(); it.hasNext(); ) {
            LinkedTreeMap p = (LinkedTreeMap) it.next();
            int x = (int) Float.parseFloat(p.get("x").toString());
            PointValue pv = new PointValue(x, Float.parseFloat(p.get("y").toString()));
            String str = (String) p.get("lable");
            axisValues.add(new AxisValue(x).setLabel(str != null ? str : "z " + (int) x));

            arrPoints.add(pv);
        }

        List<Line> arr = new ArrayList<>();
        Line line = new Line(arrPoints)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setPointColor(ContextCompat.getColor(this, R.color.accent))
                .setPointRadius(4)
                .setCubic(false)
                .setFilled(true);


        line.setShape(ValueShape.CIRCLE);
        line.setFilled(false);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setHasLabelsOnlyForSelected(true);

        arr.add(line);
        LineChartData data = new LineChartData(arr);
        Axis axisX = new Axis(axisValues).setHasLines(true).setHasTiltedLabels(true);
        ;
        Axis axisY = new Axis().setHasLines(true);

        axisX.setName("Date");
        axisY.setName("Sales");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        // data.setBaseValue(Float.NEGATIVE_INFINITY);
        return data;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        currentCode = intent.getStringExtra("itemCode");
        GregorianCalendar gc = new GregorianCalendar();
        if (toDate > 0) {
            gc.setTimeInMillis(toDate);
        }
        tvToDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()));
        toDate = gc.getTime().getTime();
        if (fromDate > 0) {
            gc.setTimeInMillis(fromDate);
        } else {
            int month = gc.get(Calendar.MONTH);
            int day = gc.get(Calendar.DAY_OF_MONTH);
            if (day < 10) {
                month = Math.max(0, month - 1);
            }
            gc.set(Calendar.MONTH, month);
            gc.set(Calendar.DAY_OF_MONTH, 1);
            fromDate = gc.getTime().getTime();
        }
        tvFromDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime()));

        showChart(currentCode);
    }

    private void showChart(String code) {
        if (fromDate > 0 && toDate > 0 && code != null && !code.isEmpty()) {
            final TransactionDataTask task = new TransactionDataTask(this, this);
            task.execute(new String[]{code, String.valueOf(Math.min(fromDate, toDate)), String.valueOf(Math.max(fromDate, toDate)), "" + WSConstants.TR_SALE});
            progress = new ProgressDialog(this);
            progress.setTitle(getString(R.string.saving));
            progress.setIndeterminate(true);
            progress.setMessage(getString(R.string.waiting_for_result));
            progress.setCancelable(false);
            progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    task.cancel(true);
                }
            });
            progress.show();
        }
    }

    @Override
    public void onResult(Result result, boolean isCanceled) {
        chart.setLineChartData(isCanceled ? new LineChartData(new ArrayList<Line>()) : createChartData(result));

        progress.dismiss();
    }


    private abstract class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Calendar calendar;

        public DatePickerFragment(Calendar calendar) {
            this.calendar = calendar;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


    }
}
