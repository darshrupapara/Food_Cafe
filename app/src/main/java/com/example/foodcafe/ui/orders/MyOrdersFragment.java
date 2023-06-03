package com.example.foodcafe.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.DeliveryActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.adapter.MyOrderAdapter;
import com.example.foodcafe.databinding.FragmentHomeBinding;
import com.example.foodcafe.model.MyOrderItemModel;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView myOrdersRecyclerView;
    public static MyOrderAdapter myOrderAdapter;
    private Dialog loadingDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyOrdersViewModel myOrdersViewModel =
                new ViewModelProvider(this).get(MyOrdersViewModel.class);

        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        myOrdersRecyclerView = view.findViewById(R.id.my_orders_recycleView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrdersRecyclerView.setLayoutManager(layoutManager);

        // List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
        // myOrderItemModelList.add(new MyOrderItemModel(R.drawable.burger,"Burger (Cheese)",3));
        // myOrderItemModelList.add(new MyOrderItemModel(R.drawable.wishlist,"Burger (Cheese)",2));
        // myOrderItemModelList.add(new MyOrderItemModel(R.drawable.orders,"Burger (Cheese)",4));

        myOrderAdapter = new MyOrderAdapter(Dbqueries.myOrderItemModelList,loadingDialog);
        myOrdersRecyclerView.setAdapter(myOrderAdapter);

        Dbqueries.loadOrders(getContext(), myOrderAdapter,loadingDialog);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}