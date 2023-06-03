package com.example.foodcafe.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.ProductDetailsActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.model.WishlistModel;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private boolean fromSearch;
    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCoupens = wishlistModelList.get(position).getFreeCoupens();
        String rating = wishlistModelList.get(position).getRating();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();

        holder.setData(productId,resource,title,freeCoupens,rating,productPrice,paymentMethod,position,inStock);
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView product_image;
        private TextView product_title;
        private TextView free_coupen;
        private ImageView coupen_icon;
        private TextView rating;
        private TextView product_price;
        private TextView payment_method;
        private ImageView delete_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_title = itemView.findViewById(R.id.product_title);
            free_coupen = itemView.findViewById(R.id.free_coupen);
            coupen_icon = itemView.findViewById(R.id.coupen_icon);
            rating = itemView.findViewById(R.id.rating_miniview);
            product_price = itemView.findViewById(R.id.product_price);
            payment_method = itemView.findViewById(R.id.payment_method);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
        private void setData(String productId, String resource, String title, long freeCoupensNo, String averageRate, String price, boolean COD, int index,boolean inStock){
            //product_image.setImageResource(resource);
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.homeicon)).into(product_image);
            product_title.setText(title);
            if (freeCoupensNo != 0 && inStock){
                coupen_icon.setVisibility(View.VISIBLE);
                if (freeCoupensNo == 1) {
                    free_coupen.setText("free " + freeCoupensNo + " coupen");
                }else {
                    free_coupen.setText("free " + freeCoupensNo + " coupens");
                }
            }else {
                coupen_icon.setVisibility(View.INVISIBLE);
                free_coupen.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock) {
                rating.setVisibility(View.VISIBLE);
                product_price.setTextColor(Color.parseColor("#000000"));
                linearLayout.setVisibility(View.VISIBLE);

                rating.setText(averageRate);
                product_price.setText("Rs." + price + "/-");
                if (COD) {
                    payment_method.setVisibility(View.VISIBLE);
                } else {
                    payment_method.setVisibility(View.INVISIBLE);
                }
            }else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                product_price.setText("out of Stock");
                product_price.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                payment_method.setVisibility(View.INVISIBLE);
            }
            //payment_method.setText(payMethod);

            if (wishlist){
                delete_btn.setVisibility(View.VISIBLE);
            }else{
                delete_btn.setVisibility(View.GONE);
            }
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete_btn.setEnabled(false);
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        Dbqueries.removeFromWishlist(index, itemView.getContext());
                    }
                    //Toast.makeText(itemView.getContext(),"delete",Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fromSearch){
                        ProductDetailsActivity.fromSearch = true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("product_ID",productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
