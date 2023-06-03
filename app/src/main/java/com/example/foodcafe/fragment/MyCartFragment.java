package com.example.foodcafe.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodcafe.AddAddressActivity;
import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.DeliveryActivity;
import com.example.foodcafe.ProductDetailsActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.adapter.CartAdapter;
import com.example.foodcafe.adapter.WishlistAdapter;
import com.example.foodcafe.model.CartItemModel;
import com.example.foodcafe.model.RewardModel;
import com.example.foodcafe.ui.reward.MyRewardFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCartFragment newInstance(String param1, String param2) {
        MyCartFragment fragment = new MyCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView cartItemsRecyclerView;
    private Button cart_continue_btn;

    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private TextView totalAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recycleView);
        cart_continue_btn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_amount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);



        //List<CartItemModel> cartItemModelList = new ArrayList<>();
       // cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",4,"Rs.99/-",1,0));
       // cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",0,"Rs.99/-",1,0));
       // cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",4,"Rs.99/-",1,0));
        //cartItemModelList.add(new CartItemModel(1,"Price (2 items)","Rs. 198/-","Free","Rs. 198/-"));

        cartAdapter = new CartAdapter(Dbqueries.cartItemModelList,totalAmount,true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        cart_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;
                for (int x = 0 ; x < Dbqueries.cartItemModelList.size() ; x++){
                    CartItemModel cartItemModel = Dbqueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if (Dbqueries.addressesModelList.size() == 0) {
                    Dbqueries.loadAddresses(getContext(), loadingDialog,true);
                }else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();

        if (Dbqueries.rewardModelList.size() == 0){
            loadingDialog.show();
            Dbqueries.loadRewards(getContext(),loadingDialog,false);
        }

        if (Dbqueries.cartItemModelList.size() == 0){
            Dbqueries.cartList.clear();
            Dbqueries.loadCartList(getContext(),loadingDialog,true, new TextView(getContext()),totalAmount);
        }else {
            if (Dbqueries.cartItemModelList.get(Dbqueries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT){
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (CartItemModel cartItemModel : Dbqueries.cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCoupenId())){
                for (RewardModel rewardModel : Dbqueries.rewardModelList) {
                    if (rewardModel.getCoupenId().equals(cartItemModel.getSelectedCoupenId())) {
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                cartItemModel.setSelectedCoupenId(null);
                if (MyRewardFragment.rewardAdapter != null) {
                    MyRewardFragment.rewardAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}