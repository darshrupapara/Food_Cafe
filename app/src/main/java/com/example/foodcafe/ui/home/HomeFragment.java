package com.example.foodcafe.ui.home;

import static com.example.foodcafe.Dbqueries.categoryModelList;
import static com.example.foodcafe.Dbqueries.firebaseFirestore;
//import static com.example.foodcafe.Dbqueries.homePageModelList;
import static com.example.foodcafe.Dbqueries.lists;
import static com.example.foodcafe.Dbqueries.loadCategories;
import static com.example.foodcafe.Dbqueries.loadFragmentData;
import static com.example.foodcafe.Dbqueries.loadedCategoriesNames;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.foodcafe.R;
import com.example.foodcafe.adapter.CategoryAdapter;
import com.example.foodcafe.adapter.GridProductLayoutAdapter;
import com.example.foodcafe.adapter.HomePageAdapter;
import com.example.foodcafe.adapter.HorizontalProductScrollAdapter;
import com.example.foodcafe.adapter.SliderAdapter;
import com.example.foodcafe.databinding.FragmentHomeBinding;
import com.example.foodcafe.model.CategoryModel;
import com.example.foodcafe.model.HomePageModel;
import com.example.foodcafe.model.HorizontalProductScrollModel;
import com.example.foodcafe.model.SliderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView category_recyclerview;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private HomePageAdapter adapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.black),getContext().getResources().getColor(R.color.black),getContext().getResources().getColor(R.color.black));

        category_recyclerview = view.findViewById(R.id.category_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        category_recyclerview.setLayoutManager(layoutManager);



        categoryAdapter = new CategoryAdapter(categoryModelList);
        category_recyclerview.setAdapter(categoryAdapter);

        if (categoryModelList.size() == 0){
            loadCategories(categoryAdapter,getContext());
        }else {
            categoryAdapter.notifyDataSetChanged();
        }



        //banner slider
        //List<SliderModel> sliderModelList = new ArrayList<SliderModel>();

        //sliderModelList.add(new SliderModel(R.drawable.home,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.cart,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.orders,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.rewards,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.notification,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.logout,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.wishlist,"#077AE4"));
        //sliderModelList.add(new SliderModel(R.drawable.account,"#077AE4"));

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
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerView);
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        if (lists.size() == 0){
            loadedCategoriesNames.add("Home");
            lists.add(new ArrayList<HomePageModel>());
            adapter = new HomePageAdapter(lists.get(0));
            loadFragmentData(adapter,getContext(),0,"Home");
        }else {
            adapter = new HomePageAdapter(lists.get(0));
            adapter.notifyDataSetChanged();
        }

        //homePmn0ageModelList.add(new HomePageModel(0,sliderModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.banner,"#000000"));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.wishlist,"#ffff00"));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.banner,"#000000"));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.wishlist,"#ffff00"));
        //homePageModelList.add(new HomePageModel(3,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(2,"Deals of the day!",horizontalProductScrollModelList));
        //homePageModelList.add(new HomePageModel(1,R.drawable.banner,"#000000"));

        homePageRecyclerView.setAdapter(adapter);




        // adapter.notifyDataSetChanged();

        //refresh layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                categoryModelList.clear();
                lists.clear();
                loadedCategoriesNames.clear();

                loadCategories(categoryAdapter,getContext());

                loadedCategoriesNames.add("Home");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(adapter,getContext(),0,"Home");
            }
        });

        //refresh layout

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}