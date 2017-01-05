package com.fireblaze.evento.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityOrganizerListBinding;
import com.fireblaze.evento.models.ImageItem;
import com.fireblaze.evento.viewholders.ImageItemHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrganizerListActivity extends BaseActivity {
    public static final String TAG = OrganizerListActivity.class.getSimpleName();
    ActivityOrganizerListBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<ImageItem, ImageItemHolder> mAdapter;
    @Override
    public View getContainer() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_organizer_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupView();


    }
    private void setupView(){
        Query q = mDatabase.child(Constants.ORGANIZER_IMAGE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mAdapter = new FirebaseRecyclerAdapter<ImageItem, ImageItemHolder>(ImageItem.class,
                R.layout.organizer_list_item1,ImageItemHolder.class,q) {
            @Override
            protected void populateViewHolder(ImageItemHolder viewHolder, final ImageItem model, int position) {
                viewHolder.bindToPost(OrganizerListActivity.this, model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,Constants.ORGANIZER_KEYWORD+"/"+model.getName());
                        launchEventList(model.getId());
                    }
                });
            }
        };
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(mAdapter);
    }
    private void launchEventList(String id){
        EventListActivity.navigate(this,id);

    }
}
