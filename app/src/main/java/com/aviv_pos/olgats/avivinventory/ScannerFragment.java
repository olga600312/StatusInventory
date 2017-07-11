package com.aviv_pos.olgats.avivinventory;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScannerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ScannerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "avivinventory.ScannerFragment";

    private Button btnScannerBar;
    private Button btnScannerQR;

    private static final String ARG_PARAM_QR = "paramQR";
    private static final String ARG_PARAM_BAR = "paramBAR";
    private int paramQR;
    private int paramBAR;


    public ScannerFragment() {
        // Required empty public constructor

    }

    public static ScannerFragment newInstance(int paramBar, int paramQr) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_QR, paramQr);
        args.putInt(ARG_PARAM_BAR, paramBar);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramQR = getArguments().getInt(ARG_PARAM_QR);
            paramBAR = getArguments().getInt(ARG_PARAM_BAR);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        btnScannerBar = (Button) view.findViewById(R.id.scannerbar1);
        btnScannerQR = (Button) view.findViewById(R.id.scannerqr1);
        btnScannerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBARButtonPressed();
            }
        });

        btnScannerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQRButtonPressed();
            }
        });
        return view;
    }


    public void onQRButtonPressed() {
        if (mListener != null) {
            mListener.onScanerFragmentInteraction(getView(),paramQR);
        }
    }

    public void onBARButtonPressed() {
        if (mListener != null) {
            mListener.onScanerFragmentInteraction(getView(),paramBAR);
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
        void onScanerFragmentInteraction(View view,int type);
    }


}
