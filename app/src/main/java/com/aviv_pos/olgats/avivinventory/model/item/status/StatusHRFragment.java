package com.aviv_pos.olgats.avivinventory.model.item.status;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aviv_pos.olgats.avivinventory.R;
import com.aviv_pos.olgats.avivinventory.StatusActivity;
import com.aviv_pos.olgats.avivinventory.Utilities;
import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusHRFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusHRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusHRFragment extends AbstractStatusFragment {
    private static final String TAG = "StatusHR";
    private TextView tvHRAvg;
    private TextView tvHRTotal;
    private TextView tvHREntry;

    //////////////Using MPAndroidChart library
    protected BarChart statusHRAvgChart;
    private BarChart statusHRTotalChart;
    private BarChart statusHREntryChart;

    private OnFragmentInteractionListener mListener;

    public StatusHRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatusHRFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusHRFragment newInstance() {
        StatusHRFragment fragment = new StatusHRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_status_hr, container, false);

        tvHRAvg = (TextView) rootView.findViewById(R.id.lblHRAvg);
        tvHRTotal = (TextView) rootView.findViewById(R.id.lblHRTotal);
        tvHREntry = (TextView) rootView.findViewById(R.id.lblHREntry);


        createHRAvgChart(rootView);
        createHRTotalChart(rootView);
        createtvHREntryChart(rootView);
        updateChart(((StatusActivity) getActivity()).getStatusInfoList());
        return rootView;
    }

    private void createtvHREntryChart(View rootView) {
        statusHREntryChart = (BarChart) rootView.findViewById(R.id.statusHREntryChart);
        setupChart(statusHREntryChart, "");
        setupLegend(statusHREntryChart);

        statusHREntryChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    public class TimeValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            int h = (int) (value / 60);
            int m = (int) (value % 60);
            return h == 0 && m == 0 ? "-" : String.format("%02d:%02d", h, m); // e.g.
        }
    }

    public class TimeYAxisValueFormatter implements YAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            int h = (int) (value / 60);
            int m = (int) (value % 60);
            return String.format("%02d", h); // e.g.
        }
    }

    private void createHRTotalChart(View rootView) {
        statusHRTotalChart = (BarChart) rootView.findViewById(R.id.statusHRTotalChart);
        setupChart(statusHRTotalChart, "");

        statusHRTotalChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setupLegend(statusHRTotalChart);
    }


    private void createHRAvgChart(View rootView) {
        statusHRAvgChart = (BarChart) rootView.findViewById(R.id.statusHRAvgChart);
        setupChart(statusHRAvgChart, "");
        statusHRAvgChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setupLegend(statusHRAvgChart);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updateChart(List<StatusInfo> list) {
        setHRAvgChartData(list);
        setHRTotalChartData(list);
        setHREntryChartData(list);
    }

    private long toMinutes(long v) {
        if (v > 0) {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(v);
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);
            v = h * 60 + m; // in the minutes
        }
        return v;
    }

    private void setHREntryChartData(List<StatusInfo> list) {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        int i = 0;

        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            yVals1.add(new BarEntry(toMinutes(info.getFirstShiftEnter()), i)); // in the minutes
            yVals2.add(new BarEntry(toMinutes(info.getFirstDealTime()), i));
            i++;
        }

        String header1 = getContext().getString(R.string.legendHRFirstShift);
        String header2 = getContext().getString(R.string.legendHRFirstDeal);
        BarDataSet set1 = new BarDataSet(yVals1, header1);
        set1.setBarSpacePercent(35f);

        BarDataSet set2 = new BarDataSet(yVals2, header2);
        set1.setBarSpacePercent(35f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        // data.setValueTypeface(mTf);

        int hrTotalColor = Utilities.getColor(getActivity(), R.color.hrFirstShiftChart);
        int hrCurrentsColor = Utilities.getColor(getActivity(), R.color.hrFirstDeatChart);
        set1.setColor(hrTotalColor);
        set2.setColor(hrCurrentsColor);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(50f);
        data.setValueFormatter(new TimeValueFormatter());
        YAxis leftAxis = statusHREntryChart.getAxisLeft();
        leftAxis.setValueFormatter(new TimeYAxisValueFormatter());
        statusHREntryChart.setData(data);
        statusHREntryChart.invalidate();
    }

    private void setHRTotalChartData(List<StatusInfo> list) {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        int i = 0;
        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            yVals1.add(new BarEntry(info.getHrTotal(), i));
            yVals2.add(new BarEntry(info.getHrCurrent(), i));
            i++;
        }

        String header1 = getContext().getString(R.string.legendHRTotal);
        String header2 = getContext().getString(R.string.legendHRCurrent);
        BarDataSet set1 = new BarDataSet(yVals1, header1);
        set1.setBarSpacePercent(35f);

        BarDataSet set2 = new BarDataSet(yVals2, header2);
        set1.setBarSpacePercent(35f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        // data.setValueTypeface(mTf);

        int hrTotalColor = Utilities.getColor(getActivity(), R.color.hrTotalChart);
        int hrCurrentsColor = Utilities.getColor(getActivity(), R.color.hrCurrentChart);
        set1.setColor(hrTotalColor);
        set2.setColor(hrCurrentsColor);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(50f);
        data.setValueFormatter(new ValueFormatter() {
                                   @Override
                                   public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                       return value == 0 ? "-" : String.format("%d", (int) value); // e.g.
                                   }
                               }
        );

        statusHRTotalChart.setData(data);
        statusHRTotalChart.invalidate();
    }

    private void setHRAvgChartData(List<StatusInfo> list) {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        int i = 0;
        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            long total = info.getWhTotal() / 1000 / 60 / 60; // hours
            yVals1.add(new BarEntry(total, i));
            yVals2.add(new BarEntry(total != 0 ? info.getSalesSum() / total : 0, i)); // per hour
            i++;
        }

        String header1 = getContext().getString(R.string.legendHRTotal);
        String header2 = getContext().getString(R.string.legendHRSales);
        BarDataSet set1 = new BarDataSet(yVals1, header1);
        set1.setBarSpacePercent(35f);

        BarDataSet set2 = new BarDataSet(yVals2, header2);
        set1.setBarSpacePercent(35f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        // data.setValueTypeface(mTf);

        int hrTotalColor = Utilities.getColor(getActivity(), R.color.salesChart);
        int hrSalesColor = Utilities.getColor(getActivity(), R.color.zChart);
        set1.setColor(hrSalesColor);
        set2.setColor(hrTotalColor);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(50f);
        set2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                // write your logic here
                return value == 0 ? "-" : String.format("%2d", (int) value); // e.g.
            }
        });
        statusHRAvgChart.setData(data);
        statusHRAvgChart.invalidate();


    }
}
