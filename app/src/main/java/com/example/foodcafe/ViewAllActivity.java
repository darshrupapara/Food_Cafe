package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.example.foodcafe.adapter.GridProductLayoutAdapter;
import com.example.foodcafe.adapter.WishlistAdapter;
import com.example.foodcafe.model.HorizontalProductScrollModel;
import com.example.foodcafe.model.WishlistModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridView;
    public static List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    public static List<WishlistModel> wishlistModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        gridView = findViewById(R.id.grid_view);

        int layout_code = getIntent().getIntExtra("layout_code",-1);

        if (layout_code == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            //List<WishlistModel> wishlistModelList = new ArrayList<>();
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "3", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 0, "2", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 2, "4", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "1", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "1", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "3", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 0, "2", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 2, "4", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "1", "Rs. 99/-", "Cash on delivery"));
            //wishlistModelList.add(new WishlistModel(R.drawable.burger, "Burger", 1, "1", "Rs. 99/-", "Cash on delivery"));

            WishlistAdapter adapter = new WishlistAdapter(wishlistModelList, false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }else if (layout_code == 1) {

            gridView.setVisibility(View.VISIBLE);

            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));
            //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger, "Burger", "1 piece", "Rs.110/-"));

            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList);
            gridView.setAdapter(gridProductLayoutAdapter);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}