package com.fireblaze.evento.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.EventListFragmentAdapter;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EventFragment extends Fragment {
    public static final String TAG = EventFragment.class.getSimpleName();
    private DatabaseReference mDatabase;
    private RecyclerView mEventsRecycler;
    private EventListFragmentAdapter mFirebaseAdapter;
    private String UID = null;
    private String category = null;
    public static final String ORGANIZER_ID_KEYWORD = "ORGANIZER_ID";
    public static final String CATEGORY_KEYWORD = "Category";
    private RecyclerView.AdapterDataObserver mObserver;
    private View mNoEventsView;
    private View mListLayout;

    public EventFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(getArguments()!= null){
            UID = getArguments().getString(ORGANIZER_ID_KEYWORD);
            category = (String) getArguments().get(CATEGORY_KEYWORD);
            //Log.d(TAG, "onCreate: UID="+UID+"category = "+category);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(UID!=null) {
            View rootView = inflater.inflate(R.layout.fragment_test, container, false);
            mEventsRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_events);
            mNoEventsView = rootView.findViewById(R.id.no_events);
            mListLayout = rootView.findViewById(R.id.events_list);
            Query query;

            //Check if category is all events and if true, display all events
            if(getString(R.string.all_events).equals(category))
                query = mDatabase.child(Constants.EVENTS_KEYWORD).child(UID).limitToFirst(10);
            else
                query = mDatabase.child(Constants.EVENTS_KEYWORD).child(UID).orderByChild("category").equalTo(category);


            mFirebaseAdapter = new EventListFragmentAdapter(Event.class, R.layout.event_card,
                    EventViewHolder.class, query,getContext());

            mObserver = new RecyclerView.AdapterDataObserver() {
                int items = 0;
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    Log.d(TAG, "onItemRangeInserted: itemCount:"+itemCount);
                    items += itemCount;
                    if(items >= 0){
                        showEvents(true);
                    }
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    items -= itemCount;
                    if(itemCount <= 0)
                        showEvents(false);
                }
            };
            mFirebaseAdapter.registerAdapterDataObserver(mObserver);
            LinearLayoutManager verticalLayoutManager =
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mEventsRecycler.setLayoutManager(verticalLayoutManager);
            mEventsRecycler.setAdapter(mFirebaseAdapter);
            return rootView;
        }

        return inflater.inflate(R.layout.content_no_event,container,false);
    }

    private void showEvents(boolean show){
        if(show){
            mNoEventsView.setVisibility(View.GONE);
            mListLayout.setVisibility(View.VISIBLE);
        }else {
            mNoEventsView.setVisibility(View.VISIBLE);
            mListLayout.setVisibility(View.GONE);
        }
    }
}
