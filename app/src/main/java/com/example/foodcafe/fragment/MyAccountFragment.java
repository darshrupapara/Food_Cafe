package com.example.foodcafe.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.DeliveryActivity;
import com.example.foodcafe.LoginActivity;
import com.example.foodcafe.MyAddressesActivity;
import com.example.foodcafe.NavigationActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.UpdateUserInfoActivity;
import com.example.foodcafe.model.MyOrderItemModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private Button viewAllButton;
    private Button signOutBtn;
    public static final int MANAGE_ADDRESS = 1;
    private TextView name,email;
    private Dialog loadingDialog;
    private TextView yourRecentOrdersTitle;
    private LinearLayout recentOrdersContainer;
    private TextView addressname, address, pincode;
    private FloatingActionButton settingBtn;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        signOutBtn = view.findViewById(R.id.sign_out_btn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Dbqueries.clearData();
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });

        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        yourRecentOrdersTitle = view.findViewById(R.id.your_recent_orders_title);
        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);

        addressname = view.findViewById(R.id.address_fullname);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.address_pincode);
        settingBtn = view.findViewById(R.id.settings_btn);

        name.setText(Dbqueries.name);
        email.setText(Dbqueries.email);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                int i = 0;
                for (MyOrderItemModel myOrderItemModel : Dbqueries.myOrderItemModelList){
                    if (i < 4) {
                        Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.homeicon)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                        i++;
                    }else {
                        break;
                    }
                }
                if (i == 0){
                    yourRecentOrdersTitle.setText("No recent Orders.");
                }
                if (i < 3){
                    for (int x = i ; x < 4 ; x++){
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        loadingDialog.setOnDismissListener(null);
                        if (Dbqueries.addressesModelList.size() == 0){
                            addressname.setText("No Address");
                            address.setText("-");
                            pincode.setText("-");
                        }else {
                            setAddress();
                        }
                    }

                });
                Dbqueries.loadAddresses(getContext(),loadingDialog,false);
            }
        });
        Dbqueries.loadOrders(getContext(),null,loadingDialog);

        viewAllButton = view.findViewById(R.id.view_all_addresses_btn);
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",name.getText());
                updateUserInfo.putExtra("Email",email.getText());
                startActivity(updateUserInfo);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!loadingDialog.isShowing()){
            if (Dbqueries.addressesModelList.size() == 0){
                addressname.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            }else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nameText, mobileNo;
        nameText = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getName();
        mobileNo = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getMobileNo();
        if (Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getAlternateMobileNo().equals("")){
            addressname.setText(nameText + " - " + mobileNo);
        }else {
            addressname.setText(nameText + " - " + mobileNo + " or " + Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getAlternateMobileNo());
        }
        String flatNo = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getFlatNo();
        String locality = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getLocality();
        String landmark = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getLandmark();
        String city = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getCity();
        String state = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getState();

        if (landmark.equals("")){
            address.setText(flatNo + " " + locality + " " + city + " " + state);
        }else {
            address.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getPincode());
    }

}