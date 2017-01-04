package com.fireblaze.evento;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chait on 7/24/2016.
 */

public class TrendingFragment extends Fragment {
    private static final String TAG = "TrendingFragment";

    private DatabaseReference mDatabase;

    private RecyclerView mRecycler;

    public TrendingFragment(){
        //Empty constructor required
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_blank,container,false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.scrollTrendingEvents);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }
}
