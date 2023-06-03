package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodcafe.adapter.CartAdapter;
import com.example.foodcafe.fragment.MyCartFragment;
import com.example.foodcafe.model.CartItemModel;
import com.example.foodcafe.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity implements PaymentResultListener {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    public static CartAdapter cartAdapter;
    private Button change_or_add_address_btn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullname,mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageButton codBtn;
    private ImageButton razorPay;
    private String paymentMethod = "RAZORPAY";
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private boolean successResponse = false;
    public static boolean fromCart;
    private String order_id;
    private Dialog loadingDialog1;

    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIDs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        change_or_add_address_btn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_amount);
        fullname = findViewById(R.id.fullname);
        mobileNo = findViewById(R.id.mobile_no);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        //loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //payment dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //payment dialog

        order_id = UUID.randomUUID().toString().substring(0,20);

        loadingDialog1 = new Dialog(DeliveryActivity.this);
        loadingDialog1.setContentView(R.layout.get_payment_dialog);
        loadingDialog1.setCancelable(false);
        loadingDialog1.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        //List<CartItemModel> cartItemModelList = new ArrayList<>();
        //cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",4,"Rs.99/-",1,0));
        //cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",0,"Rs.99/-",1,0));
        //cartItemModelList.add(new CartItemModel(0,R.drawable.burger,"Burger",4,"Rs.99/-",1,0));
        //cartItemModelList.add(new CartItemModel(1,"Price (2 items)","Rs. 198/-","Free","Rs. 198/-"));

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();


        change_or_add_address_btn.setVisibility(View.VISIBLE);
        change_or_add_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean allProductsAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvailable = false;
                    }
                }
                if (allProductsAvailable) {
                    paymentMethodDialog.show();
                }
            }
        });

        codBtn = paymentMethodDialog.findViewById(R.id.cod_btn);
        razorPay = paymentMethodDialog.findViewById(R.id.razorPay);

        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymentMethod = "COD";
                placeOrderDetails();

            }
        });

        razorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymentMethod = "RAZORPAY";
                placeOrderDetails();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //accessing qty
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;

                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {
                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }

                                                                long availableQty = 0;
                                                                boolean noLongerAvailable = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if (!serverQuantity.contains(qtyId)) {

                                                                        if (noLongerAvailable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry ! all products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvailable = false;
                                                                    }
                                                                }
                                                                cartAdapter.notifyDataSetChanged();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        } else {
            getQtyIDs = true;
        }
        //accessing qty

        fullname.setText(Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getName());

        String flatNo = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getFlatNo();
        String locality = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getLocality();
        String landmark = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getLandmark();
        String city = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getCity();
        String state = Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getState();

        if (landmark.equals("")){
            fullAddress.setText(flatNo + " " + locality + " " + city + " " + state);
        }else {
            fullAddress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(Dbqueries.addressesModelList.get(Dbqueries.selectAddress).getPincode());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPaymentSuccess(String s) {
        //Toast.makeText(this, "sucess", Toast.LENGTH_SHORT).show();
        loadingDialog1.dismiss();
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        successResponse = true;

        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < Dbqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    Dbqueries.cartList.remove(indexList.get(x).intValue());
                                    Dbqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    Dbqueries.cartItemModelList.remove(Dbqueries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }
        }
        orderId.setText("order ID " + order_id);

        firebaseFirestore.collection("ORDERS").document(order_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Map<String,Object> userOrders = new HashMap<>();
                            userOrders.put("order_id",order_id);
                            userOrders.put("time",FieldValue.serverTimestamp());
                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id).set(userOrders)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                orderConfirmationLayout.setVisibility(View.VISIBLE);
                                            }else {
                                                Toast.makeText(DeliveryActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(DeliveryActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }
                                    }
                                });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }

        //super.onBackPressed();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeliveryActivity.this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit app?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void placeOrderDetails() {

        String userID = FirebaseAuth.getInstance().getUid();
        //loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM){

                Map<String ,Object> orderDetails = new HashMap<>();
                orderDetails.put("Order Id", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductID());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User Id", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCoupenId() != null) {
                    orderDetails.put("Coupen Id", cartItemModel.getSelectedCoupenId());
                }else {
                    orderDetails.put("Coupen Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                }else {
                    orderDetails.put("Discounted Price","");
                }
                orderDetails.put("Date", FieldValue.serverTimestamp());
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("FullName", fullname.getText());
                orderDetails.put("Pincode", pincode.getText());
                orderDetails.put("Free Coupens", cartItemModel.getFreeCoupens());
                orderDetails.put("Delivery Price", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation requested",false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()){
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Map<String ,Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (paymentMethod.equals("RAZORPAY")){
                                        razorPay();
                                    }else {
                                        cod();
                                    }
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void razorPay(){
        loadingDialog1.show();

        EditText amountEdit = loadingDialog1.findViewById(R.id.amountEdt);
        Button dialog_paynow = loadingDialog1.findViewById(R.id.dialog_paynow);
        Button cancel_btn = loadingDialog1.findViewById(R.id.cancel_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog1.dismiss();
            }
        });


        dialog_paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = amountEdit.getText().toString();
                //Log.e("aaaa111", "===111111====" + aaa + " " + cartAdapter.aa11);

                int aInt = cartAdapter.amountMain;
                String aString = Integer.toString(aInt);

                if (amount.equals(aString)) {

                    int amount1 = Math.round(Float.parseFloat(amount) * 100);

                    getQtyIDs = false;
                    Checkout checkout = new Checkout();
                    Checkout.preload(getApplicationContext());
                    checkout.setKeyID("rzp_test_izhabiTXDxMcWE");

                    checkout.setImage(R.drawable.logo);

                    try {
                        JSONObject options = new JSONObject();

                        options.put("name", "RazorPay");
                        options.put("description", "Reference No. #123456");
                        //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
                        //options.put("order_id", order_id);//from response of step 3.
                        options.put("theme.color", "#3399cc");
                        options.put("currency", "INR");
                        options.put("amount", amount1);

                        //options.put("amount",  Integer.parseInt(totalAmount.getText().toString()));//pass amount in currency subunits
                        options.put("prefill.email", "gaurav.kumar@example.com");
                        options.put("prefill.contact", "9265964912");

                        JSONObject retryObj = new JSONObject();
                        retryObj.put("enabled", true);
                        retryObj.put("max_count", 4);
                        options.put("retry", retryObj);

                        checkout.open(DeliveryActivity.this, options);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    paymentMethodDialog.dismiss();
//                            Log.e("aaaa", "======dialog_paynow========11===========" + cartAdapter.aa11);
                } else {
//                            Log.e("aaaa", "======dialog_paynow========22===========" + cartAdapter.aa11);
                    Toast.makeText(DeliveryActivity.this, "Enter Valid Price", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void cod(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        Toast.makeText(DeliveryActivity.this, "Your order is confirm!", Toast.LENGTH_SHORT).show();

        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < Dbqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    Dbqueries.cartList.remove(indexList.get(x).intValue());
                                    Dbqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    Dbqueries.cartItemModelList.remove(Dbqueries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }
        }
        orderId.setText("order ID " + order_id);

        firebaseFirestore.collection("ORDERS").document(order_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Map<String,Object> userOrders = new HashMap<>();
                            userOrders.put("order_id",order_id);
                            userOrders.put("time",FieldValue.serverTimestamp());
                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id).set(userOrders)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                orderConfirmationLayout.setVisibility(View.VISIBLE);
                                            }else {
                                                Toast.makeText(DeliveryActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(DeliveryActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}