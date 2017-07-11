package com.aviv_pos.olgats.avivinventory.model.item.status;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aviv_pos.olgats.avivinventory.R;
import com.aviv_pos.olgats.avivinventory.StatusActivity;
import com.aviv_pos.olgats.avivinventory.Utilities;
import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.aviv_pos.olgats.avivinventory.beans.ZReport;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SubcolumnValue;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusSalesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusSalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusSalesFragment extends AbstractStatusFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "StatusSales";
    private TextView tvThisMonth;

    //////////////Using MPAndroidChart library
    protected BarChart thisMonthTotalChart;
    private BarChart salesChart;
    private BarChart dealsAvgChart;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public StatusSalesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusSalesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusSalesFragment newInstance(String param1, String param2) {
        StatusSalesFragment fragment = new StatusSalesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public static StatusSalesFragment newInstance() {
        return new StatusSalesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_status_sales, container, false);
        tvThisMonth = (TextView) rootView.findViewById(R.id.lblSalesMonth);
        TextView tvSalesTitle = (TextView) rootView.findViewById(R.id.lblTitle);
        Calendar g = Calendar.getInstance();
        String header = getContext().getString(R.string.sales_chart, new DateFormatSymbols().getMonths()[g.get(Calendar.MONTH)] + " " + g.get(Calendar.YEAR));
        tvSalesTitle.setText(header);

        createSalesChart(rootView);
        createDealsAvgChart(rootView);
        createMonthTotalChart(rootView);
        updateChart(((StatusActivity)getActivity()).getStatusInfoList());
        return rootView;
    }

    private void createDealsAvgChart(View rootView) {
        dealsAvgChart = (BarChart) rootView.findViewById(R.id.statusSalesAvgChart);
        setupChart(dealsAvgChart, "");
        dealsAvgChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setupLegend(dealsAvgChart);
    }
    private void createSalesChart(View rootView) {
        salesChart = (BarChart) rootView.findViewById(R.id.statusSalesChart);
        setupChart(salesChart, "");
        salesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setupLegend(salesChart);
    }



    private void createMonthTotalChart(View rootView) {
        thisMonthTotalChart = (BarChart) rootView.findViewById(R.id.mChart);
        setupChart(thisMonthTotalChart, "");
        thisMonthTotalChart.getLegend().setEnabled(false);
        thisMonthTotalChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;

                RectF bounds = thisMonthTotalChart.getBarBounds((BarEntry) e);
                PointF position = thisMonthTotalChart.getPosition(e, YAxis.AxisDependency.LEFT);
                Log.d(TAG, "bounds:" + bounds.toString());
                Log.d(TAG, "position:" + position.toString());

                Log.d(TAG, "x-index:" +
                        "low: " + thisMonthTotalChart.getLowestVisibleXIndex() + ", high: "
                        + thisMonthTotalChart.getHighestVisibleXIndex());
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }




    private void setSalesThisMonth(List<StatusInfo> list) {
        Calendar g = Calendar.getInstance();
        String header = getContext().getString(R.string.sales_month_chart, new DateFormatSymbols().getMonths()[g.get(Calendar.MONTH)] + " " + g.get(Calendar.YEAR));
        tvThisMonth.setText(header);
        DatabaseHandler.ZReports zReports = new DatabaseHandler.ZReports(this.getActivity());
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        int i = 0;
        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            ZReport z = zReports.retrieveThisMonthTotal(info.getAvivId());
            yVals1.add(new BarEntry(info.getSalesSum() + z.getSum(), i++));
        }


        BarDataSet set1 = new BarDataSet(yVals1, header);
        set1.setBarSpacePercent(35f);
        int zColor= Utilities.getColor(getActivity(), R.color.zChart);
        set1.setColor(zColor);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        // data.setValueTypeface(mTf);

        thisMonthTotalChart.setData(data);
        thisMonthTotalChart.invalidate();
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

    public void updateChart(List<StatusInfo> list){
        setSalesChartData(list);
        setSalesAvgChartData(list);
        setSalesThisMonth(list);
    }
    private void setSalesChartData(List<StatusInfo> list) {
        DatabaseHandler.ZReports zReports = new DatabaseHandler.ZReports(this.getActivity());
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        int i = 0;
        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            ZReport z = zReports.retrieveThisMonthTotal(info.getAvivId());
            yVals1.add(new BarEntry(z.getSum(), i));
            yVals2.add(new BarEntry(info.getSalesSum(), i));
            i++;
        }
        Calendar g = Calendar.getInstance();
        String header1 = getContext().getString(R.string.legendThisMonth, new DateFormatSymbols().getMonths()[g.get(Calendar.MONTH)] + " " + g.get(Calendar.YEAR));
        String header2 = getContext().getString(R.string.legendThisDay, g.get(Calendar.DAY_OF_MONTH)+" "+new DateFormatSymbols().getMonths()[g.get(Calendar.MONTH)] + " " + g.get(Calendar.YEAR));
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

        int salesColor= Utilities.getColor(getActivity(), R.color.salesChart);
        int zColor= Utilities.getColor(getActivity(), R.color.zChart);
        set1.setColor(zColor);
        set2.setColor(salesColor);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(80f);
        salesChart.setData(data);
        salesChart.invalidate();

    }

    private void setSalesAvgChartData(List<StatusInfo> list) {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        int i = 0;
        for (StatusInfo info : list) {
            xVals.add(getXValue(info));
            yVals1.add(new BarEntry(info.getClientCount(), i));
            yVals2.add(new BarEntry(info.getClientCount() != 0 ? info.getSalesSum() / info.getClientCount() : 0, i));
            i++;
        }

        Calendar g = Calendar.getInstance();

        String header1 = getContext().getString(R.string.legendClients);
        String header2 = getContext().getString(R.string.legendDealsAvg);
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

        int avgColor= Utilities.getColor(getActivity(), R.color.avgChart);
        int clientsColor= Utilities.getColor(getActivity(), R.color.clientsChart);
        set1.setColor(clientsColor);
        set2.setColor(avgColor);
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(80f);
        dealsAvgChart.setData(data);
        dealsAvgChart.invalidate();
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

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }


}
