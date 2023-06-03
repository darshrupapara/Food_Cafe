package com.example.foodcafe;

import static com.example.foodcafe.Dbqueries.lists;
import static com.example.foodcafe.Dbqueries.loadFragmentData;
import static com.example.foodcafe.Dbqueries.loadedCategoriesNames;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcafe.adapter.CategoryAdapter;
import com.example.foodcafe.adapter.HomePageAdapter;
import com.example.foodcafe.model.CategoryModel;
import com.example.foodcafe.model.HomePageModel;
import com.example.foodcafe.model.HorizontalProductScrollModel;
import com.example.foodcafe.model.SliderModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView category_recyclerView;
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category_recyclerView = findViewById(R.id.category_recyclerView);

        //banner slider
        //List<SliderModel> sliderModelList = new ArrayList<SliderModel>();

        //sliderModelList.add(new SliderModel(R.drawable.wishlist,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.account,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.home,"#077AE4"));

        //sliderModelList.add(new SliderModel(R.drawable.cart,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.orders,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.rewards,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.notification,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.logout,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.wishlist,"#077AE4"));

        //sliderModelList.add(new SliderModel(R.drawable.account,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.home,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.cart,"#077AE4"));
        //banner slider

        //horizontal product layout
        //List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.burger,"Burger","1 piece","Rs.110/-"));
        //horizontal product layout

        /////

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        category_recyclerView.setLayoutManager(testingLayoutManager);

        //List<HomePageModel> homePageModelList = new ArrayList<>();

        //homePageModelList.add(new HomePageModel(0,sliderModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.banner,"#000000"));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.wishlist,"#ffff00"));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.banner,"#000000"));

        int listPosition = 0;
        for (int x = 0; x < loadedCategoriesNames.size(); x++){
            if (loadedCategoriesNames.get(x).equals(title)){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadedCategoriesNames.add(title);
            lists.add(new ArrayList<HomePageModel>());
            adapter = new HomePageAdapter(lists.get(loadedCategoriesNames.size() - 1));
            loadFragmentData(adapter,this,loadedCategoriesNames.size() - 1,title);
        }else{
            adapter = new HomePageAdapter(lists.get(listPosition));
        }

        category_recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}