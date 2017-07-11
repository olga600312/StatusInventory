package com.aviv_pos.olgats.avivinventory.model.item.status;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviv_pos.olgats.avivinventory.R;
import com.aviv_pos.olgats.avivinventory.StatusActivity;
import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusTerminalsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusTerminalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusTerminalsFragment extends AbstractStatusFragment {
    private HorizontalBarChart mChart;
    private OnFragmentInteractionListener mListener;

    public StatusTerminalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
         * @return A new instance of fragment StatusTerminalsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusTerminalsFragment newInstance() {
       StatusTerminalsFragment fragment= new StatusTerminalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_status_terminals, container, false);
        mChart= (HorizontalBarChart) rootView.findViewById(R.id.statusDealsChart);
        mChart.getAxisLeft().setAxisMinValue(0f);
        mChart.setDrawValueAboveBar(false);
        mChart.animateY(1500);
        setupChart(mChart, "");
        mChart.getXAxis().setLabelRotationAngle(0f);
        setupLegend(mChart);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
      //  mChart.setDrawGridBackground(false);
        updateChart(((StatusActivity) getActivity()).getStatusInfoList());
        return rootView;
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

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        int i=0;
        for (StatusInfo info:list) {
            xVals.add(getXValue(info));
            yVals1.add(new BarEntry(info.getOpenDealSum(), i++));
        }

        BarDataSet set1 = new BarDataSet(yVals1, getContext().getString(R.string.openDeals));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        mChart.setData(data);
        mChart.invalidate();
    }

}
