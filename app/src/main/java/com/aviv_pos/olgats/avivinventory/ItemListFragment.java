package com.aviv_pos.olgats.avivinventory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv_pos.olgats.avivinventory.beans.Item;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ItemListAdapter adapter;

    public ItemListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ItemListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvResult);
        setupRecyclerView(rv);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new ItemListAdapter();
        recyclerView.setAdapter(adapter);
        updateList();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onItemPressed(String code) {
        if (mListener != null) {
            mListener.onItemListFragmentInteraction(code);
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

    public void updateList() {
        Activity a = getActivity();
        if (a != null) {
            DatabaseHandler.Items items = new DatabaseHandler.Items(getActivity());
            List<Item> list = items.retrieve(false);
            adapter.updateData(list);
            adapter.notifyDataSetChanged();
        }
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
        // TODO: Update argument type and name
        void onItemListFragmentInteraction(String code);
    }

    private class ItemListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<Item> data = new ArrayList<>();

        public void updateData(List<Item> data) {
            this.data = data;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View vDefault = inflater.inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(vDefault);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Item item = getValueAt(position);
            holder.mBoundString = item.getCode();
            holder.tvName.setText(item.getName());
            holder.tvBarcode.setText(item.getCode());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPressed(holder.mBoundString);
                }
            });
            File photo = Utilities.getImageFile(holder.mImageView.getContext(), item.getCode());
            if (photo != null) {
            /*Bitmap b=BitmapFactory.decodeFile(photo.getAbsolutePath());
            imageView.setImageBitmap(b);
            // Now change ImageView's dimensions to match the scaled image
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) imageView.getLayoutParams();
            params.width = b.getWidth();
            params.height = b.getHeight();
            imageView.setLayoutParams(params);*/

                Glide.with(holder.mImageView.getContext()).load(photo).fitCenter().into(holder.mImageView);
            } else
                Glide.with(holder.mImageView.getContext())
                        .load(R.drawable.catalogs)
                        .fitCenter()
                        .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public Item getValueAt(int position) {
            return data.get(position);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        public final ImageView mImageView;
        public final TextView tvName;
        public final TextView tvBarcode;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvBarcode = (TextView) view.findViewById(R.id.tvBarcode);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvName.getText();
        }
    }
}
