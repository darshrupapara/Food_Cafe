package com.example.foodcafe;

import static com.example.foodcafe.NavigationActivity.showCart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodcafe.adapter.ProductImagesAdapter;
import com.example.foodcafe.adapter.RewardAdapter;
import com.example.foodcafe.fragment.MyCartFragment;
import com.example.foodcafe.model.CartItemModel;
import com.example.foodcafe.model.RewardModel;
import com.example.foodcafe.model.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_cart_query = false;

    public static boolean fromSearch = false;

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView productPrice;
    private String productOriginalPrice;
    private ImageView codIndicatorImage;
    private TextView codIndicator;
    private TabLayout viewpagerIndicator;
    private Button coupenRedeemBtn;

    private TextView rewardTitle;
    private TextView rewardBody;

    private ConstraintLayout productDetailsContainer;
    private TextView productDetailsBody;

    //rating layout
    public static LinearLayout rate_now_container;
    private TextView averageRating;
    //rating layout

    private Button buy_now_btn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addToWishlistBtn;

    private FirebaseFirestore firebaseFirestore;

    //coupen dialog
    private TextView coupenTitle;
    private TextView coupenExpiryDate;
    private TextView coupenBody;
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;
    private TextView discountedPrice;
    private TextView originalPrice;
    //coupen dialog

    private Dialog loadingDialog;
    private FirebaseUser currentUser;
    public static String productID;
    private TextView badgeCount;
    private boolean inStock = false;

    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishlistBtn = findViewById(R.id.add_to_wishlist_btn);
        buy_now_btn = findViewById(R.id.buy_now_btn);
        coupenRedeemBtn = findViewById(R.id.coupen_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.rating_miniview);
        productPrice = findViewById(R.id.product_price);
        codIndicatorImage = findViewById(R.id.cod_indicator_imageview);
        codIndicator = findViewById(R.id.cod_indicator);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsContainer = findViewById(R.id.product_details_container);
        productDetailsBody = findViewById(R.id.product_details_body);
        averageRating = findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);

        //loading dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        //coupen dialog

        final Dialog checkCoupenPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
        checkCoupenPriceDialog.setCancelable(true);
        checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggle_recyclerview);
        coupensRecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupens_recyclerview);
        selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);
        coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
        coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity);
        coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body);

        originalPrice = checkCoupenPriceDialog.findViewById(R.id.original_price);
        discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discounted_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        coupensRecyclerView.setLayoutManager(layoutManager);

        //List<RewardModel> rewardModelList = new ArrayList<>();
        //rewardModelList.add(new RewardModel("Cashback", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Discount", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Buy 1 Get 1 free", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Cashback", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Discount", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Buy 1 Get 1 free", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Cashback", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Discount", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));
        //rewardModelList.add(new RewardModel("Buy 1 Get 1 free", "till 5th April 2023", "GET 20% OFF on any food above Rs.500/- and below Rs.2500/-."));

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRecyclerView();
            }
        });

        //coupen dialog

        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("product_ID");

        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();

                            firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){

                                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                                }
                                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                                productImagesViewPager.setAdapter(productImagesAdapter);

                                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                                averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                                productPrice.setText("Rs." + documentSnapshot.get("product_price").toString() + "/-");

                                                //for coupen dialog
                                                originalPrice.setText(productPrice.getText());
                                                productOriginalPrice = documentSnapshot.get("product_price").toString();
                                                RewardAdapter rewardAdapter = new RewardAdapter(Dbqueries.rewardModelList, true,coupensRecyclerView,selectedCoupen,productOriginalPrice, coupenTitle, coupenExpiryDate, coupenBody, discountedPrice);
                                                coupensRecyclerView.setAdapter(rewardAdapter);
                                                rewardAdapter.notifyDataSetChanged();
                                                //for coupen dialog

                                                if ((boolean) documentSnapshot.get("COD")) {
                                                    codIndicatorImage.setVisibility(View.VISIBLE);
                                                    codIndicator.setVisibility(View.VISIBLE);
                                                } else {
                                                    codIndicatorImage.setVisibility(View.INVISIBLE);
                                                    codIndicator.setVisibility(View.INVISIBLE);
                                                }
                                                rewardTitle.setText((long) documentSnapshot.get("free_coupens") + documentSnapshot.get("free_coupen_title").toString());
                                                rewardBody.setText(documentSnapshot.get("free_coupen_body").toString());

                                                productDetailsContainer.setVisibility(View.VISIBLE);
                                                productDetailsBody.setText(documentSnapshot.get("product_details").toString());

                                                averageRating.setText(documentSnapshot.get("average_rating").toString());

                                                if (currentUser != null) {
                                                    if (Dbqueries.myRating.size() == 0) {
                                                        Dbqueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (Dbqueries.cartList.size() == 0) {
                                                        Dbqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                                    }
                                                    if (Dbqueries.wishList.size() == 0) {
                                                        Dbqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                                    }
                                                    if (Dbqueries.rewardModelList.size() == 0){
                                                        Dbqueries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
                                                    }
                                                    if (Dbqueries.cartList.size() != 0 && Dbqueries.wishList.size() != 0 &&Dbqueries.rewardModelList.size() != 0){
                                                        loadingDialog.dismiss();
                                                    }
                                                } else {
                                                    loadingDialog.dismiss();
                                                }

                                                if (Dbqueries.cartList.contains(productID)) {
                                                    ALREADY_ADDED_TO_CART = true;
                                                } else {
                                                    ALREADY_ADDED_TO_CART = false;
                                                }

                                                if (Dbqueries.wishList.contains(productID)) {
                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                                } else {
                                                    addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                    ALREADY_ADDED_TO_WISHLIST = false;
                                                }

                                                if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){

                                                    inStock = true;
                                                    buy_now_btn.setVisibility(View.VISIBLE);
                                                    addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (!running_cart_query) {
                                                                running_cart_query = true;
                                                                if (ALREADY_ADDED_TO_CART) {
                                                                    running_cart_query = false;
                                                                    Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Map<String, Object> addProduct = new HashMap<>();
                                                                    addProduct.put("product_ID_" + String.valueOf(Dbqueries.cartList.size()), productID);
                                                                    addProduct.put("list_size", (long) Dbqueries.cartList.size() + 1);

                                                                    firebaseFirestore.collection("USERS")
                                                                            .document(currentUser.getUid())
                                                                            .collection("USER_DATA")
                                                                            .document("MY_CART")
                                                                            .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                        if (Dbqueries.cartItemModelList.size() != 0) {
                                                                                            Dbqueries.cartItemModelList.add(0, new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                                                                    , documentSnapshot.get("product_title").toString()
                                                                                                    , (long) documentSnapshot.get("free_coupens")
                                                                                                    , documentSnapshot.get("product_price").toString()
                                                                                                    , (long) 1
                                                                                                    , (long) 0
                                                                                                    , inStock
                                                                                                    , (long) documentSnapshot.get("max_quantity")
                                                                                                    , (long) documentSnapshot.get("stock_quantity")));
                                                                                        }
                                                                                        ALREADY_ADDED_TO_CART = true;
                                                                                        Dbqueries.cartList.add(productID);
                                                                                        Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                                                                                        invalidateOptionsMenu();
                                                                                        //addToWishlistBtn.setEnabled(true);
                                                                                        running_cart_query = false;
                                                                                    } else {
                                                                                        //addToWishlistBtn.setEnabled(true);
                                                                                        running_cart_query = false;
                                                                                        String error = task.getException().getMessage();
                                                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    });

                                                }else {
                                                    inStock = false;
                                                    buy_now_btn.setVisibility(View.GONE);
                                                    TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                                    outOfStock.setText("out of stock");
                                                    outOfStock.setTextColor(getResources().getColor(R.color.red));
                                                    outOfStock.setCompoundDrawables(null, null, null, null);
                                                }

                                            }else{
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addToWishlistBtn.setEnabled(false);
                if (!running_wishlist_query) {
                    running_wishlist_query = true;
                    if (ALREADY_ADDED_TO_WISHLIST) {
                        int index = Dbqueries.wishList.indexOf(productID);
                        Dbqueries.removeFromWishlist(index, ProductDetailsActivity.this);
                        ALREADY_ADDED_TO_WISHLIST = false;
                        addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    } else {
                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                        Map<String, Object> addProduct = new HashMap<>();
                        addProduct.put("product_ID_" + String.valueOf(Dbqueries.wishList.size()), productID);
                        addProduct.put("list_size", (long) Dbqueries.wishList.size() + 1);

                        firebaseFirestore.collection("USERS")
                                .document(currentUser.getUid())
                                .collection("USER_DATA")
                                .document("MY_WISHLIST")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (Dbqueries.wishlistModelList.size() != 0) {
                                                Dbqueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString()
                                                        , documentSnapshot.get("product_title").toString()
                                                        , (long) documentSnapshot.get("free_coupens")
                                                        , documentSnapshot.get("average_rating").toString()
                                                        , documentSnapshot.get("product_price").toString()
                                                        , (boolean) documentSnapshot.get("COD")
                                                        , inStock));
                                            }

                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                            Dbqueries.wishList.add(productID);
                                            Toast.makeText(ProductDetailsActivity.this, "Added to wishlist successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                            //addToWishlistBtn.setEnabled(true);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                        running_wishlist_query = false;
                                    }
                                });
                    }
                }

            }
        });

        //rating layout
        rate_now_container = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rate_now_container.getChildCount(); x++) {
            final int starPosition = x;
            rate_now_container.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRating(starPosition);
                }

            });
        }
        //rating layout

        buy_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.fromCart = false;
                loadingDialog.show();
                //DeliveryActivity.cartItemModelList.clear();
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                        , documentSnapshot.get("product_title").toString()
                        , (long) documentSnapshot.get("free_coupens")
                        , documentSnapshot.get("product_price").toString()
                        , (long) 1
                        , (long) 0
                        , inStock
                        , (long) documentSnapshot.get("max_quantity")
                        , (long) documentSnapshot.get("stock_quantity")));
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                if (Dbqueries.addressesModelList.size() == 0) {
                    Dbqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                } else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        coupenRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkCoupenPriceDialog.show();

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (Dbqueries.myRating.size() == 0) {
                Dbqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (Dbqueries.wishList.size() == 0) {
                Dbqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (Dbqueries.rewardModelList.size() == 0){
                Dbqueries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
            }
            if (Dbqueries.cartList.size() != 0 && Dbqueries.wishList.size() != 0 && Dbqueries.rewardModelList.size() != 0){
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }

        if (Dbqueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (Dbqueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.red));
        } else {
            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

     private void showDialogRecyclerView() {
        if (coupensRecyclerView.getVisibility() == View.GONE) {
            coupensRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupen.setVisibility(View.GONE);
        } else {
            coupensRecyclerView.setVisibility(View.GONE);
            selectedCoupen.setVisibility(View.VISIBLE);
        }
    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rate_now_container.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rate_now_container.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#CAC6C6")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.cart);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.cart);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {
            if (Dbqueries.cartList.size() == 0) {
                Dbqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);
                if (Dbqueries.cartList.size() < 10) {
                    badgeCount.setText(String.valueOf(Dbqueries.cartList.size()));
                } else {
                    badgeCount.setText("9+");
                }
            }
        }
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, NavigationActivity.class);
                showCart = true;
                startActivity(cartIntent);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.search) {
            if (fromSearch){
                finish();
            }else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.cart) {
            Intent cartIntent = new Intent(ProductDetailsActivity.this, NavigationActivity.class);
            showCart = true;
            startActivity(cartIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }
}