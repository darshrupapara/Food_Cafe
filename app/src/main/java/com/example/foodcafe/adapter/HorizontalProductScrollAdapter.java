package com.example.foodcafe.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodcafe.ProductDetailsActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.model.HorizontalProductScrollModel;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout ,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();

        holder.setH_s_product_image(resource);
        holder.setH_s_product_title(title);
        holder.setH_s_product_description(description);
        holder.setH_s_product_price(price);
    }

    @Override
    public int getItemCount() {
        if(horizontalProductScrollModelList.size() > 8){
            return 8;
        }else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView h_s_product_image;
        private TextView h_s_product_title;
        private TextView h_s_product_description;
        private TextView h_s_product_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            h_s_product_image = itemView.findViewById(R.id.h_s_product_image);
            h_s_product_title = itemView.findViewById(R.id.h_s_product_title);
            h_s_product_description = itemView.findViewById(R.id.h_s_product_description);
            h_s_product_price = itemView.findViewById(R.id.h_s_product_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }

        private void setH_s_product_image(String resource){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.homeicon)).into(h_s_product_image);
        }

        private void setH_s_product_title(String title){
            h_s_product_title.setText(title);
        }

        private void setH_s_product_description(String description){
            h_s_product_description.setText(description);
        }

        private void setH_s_product_price(String price){
            h_s_product_price.setText("Rs."+price+"/-");
        }
    }
}
