package com.fireblaze.evento.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.CategoryListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoriesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoriesRecycler = (RecyclerView) findViewById(R.id.category_recycler);
        setupCategoriesRecycler();
    }

    private void setupCategoriesRecycler(){
        if(categoriesRecycler == null)
            throw new RuntimeException("Categories Recycler is unexpectedly null");

        int[] img = {
                R.drawable.ic_coding,
                R.drawable.ic_arts,
                R.drawable.ic_adventure
        };
        List<String> names = new ArrayList<>();
        names.add("Coding");
        names.add("Arts");
        names.add("Adventure");
        GridLayoutManager layoutManager = new GridLayoutManager(CategoryActivity.this,2);
        CategoryListAdapter adapter = new CategoryListAdapter(CategoryActivity.this,names,img);

        categoriesRecycler.setLayoutManager(layoutManager);
        categoriesRecycler.setAdapter(adapter);

    }
}
