package com.aviv_pos.olgats.avivinventory;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static final String ARG_PARAM = "paramCriteria";

    private Button btnSearch, btnClear;
    private EditText etSearchByName, etSearchByCode;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnClear = (Button) view.findViewById(R.id.btnClear);

        etSearchByCode = (EditText) view.findViewById(R.id.etSearchByCode);
        etSearchByName = (EditText) view.findViewById(R.id.etSearchByName);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(v);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v);
                    return true;
                }
                return false;
            }
        };

        etSearchByCode.setOnEditorActionListener(onEditorActionListener);
        etSearchByName.setOnEditorActionListener(onEditorActionListener);

        return view;
    }


    private void clear() {
        etSearchByCode.setText("");
        etSearchByName.setText("");
    }


    private void performSearch(View v) {
        Utilities.hideKeyboard(v);

        if (validate(etSearchByCode, 3) && validate(etSearchByName, 3)) {
            ItemSearchCriteria c = new ItemSearchCriteria();
            c.setCode(etSearchByCode.getText().toString());
            c.setName(etSearchByName.getText().toString());
            onSearchCodePressed(c);
        }
    }


    private boolean validate(EditText editText, int limit) {
        int l = editText.getText().toString().trim().length();
        boolean empty = l > 0 && l < limit;
        if (empty) {
            Snackbar.make(editText, getString(R.string.invalid_character_count), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            editText.requestFocus();
            editText.selectAll();
        }
        return !empty;
    }





    public void onSearchCodePressed(ItemSearchCriteria data) {
        if (mListener != null) {
            mListener.onSearchFragmentInteraction(data);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onSearchFragmentInteraction(ItemSearchCriteria criteria);
    }
}
