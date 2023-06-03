package com.example.foodcafe.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.ProductDetailsActivity;
import com.example.foodcafe.R;
import com.example.foodcafe.model.CartItemModel;
import com.example.foodcafe.model.RewardModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private List<RewardModel> rewardModelList;
    private Boolean useMiniLayout = false;
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;
    private String productOriginalPrice;
    private TextView selectedCoupenTitle;
    private TextView selectedCoupenExpiryDate;
    private TextView selectedCoupenBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;
    private List<CartItemModel> cartItemModelList;

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView coupensRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView selectedCoupenTitle, TextView selectedCoupenExpiryDate, TextView selectedCoupenBody, TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.coupensRecyclerView = coupensRecyclerView;
        this.selectedCoupen = selectedCoupen;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCoupenTitle = selectedCoupenTitle;
        this.selectedCoupenExpiryDate = selectedCoupenExpiryDate;
        this.selectedCoupenBody = selectedCoupenBody;
        this.discountedPrice = discountedPrice;
    }

    public RewardAdapter(int cartItemPosition, List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView coupensRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView selectedCoupenTitle, TextView selectedCoupenExpiryDate, TextView selectedCoupenBody, TextView discountedPrice, List<CartItemModel> cartItemModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.coupensRecyclerView = coupensRecyclerView;
        this.selectedCoupen = selectedCoupen;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCoupenTitle = selectedCoupenTitle;
        this.selectedCoupenExpiryDate = selectedCoupenExpiryDate;
        this.selectedCoupenBody = selectedCoupenBody;
        this.discountedPrice = discountedPrice;
        this.cartItemPosition = cartItemPosition;
        this.cartItemModelList = cartItemModelList;
    }

    @NonNull
    @Override
    public RewardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useMiniLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_rewards_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardAdapter.ViewHolder holder, int position) {
        String coupenId = rewardModelList.get(position).getCoupenId();
        String type = rewardModelList.get(position).getType();
        Date validity = rewardModelList.get(position).getTimestamp();
        String body = rewardModelList.get(position).getCoupenBody();
        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discORamt = rewardModelList.get(position).getDiscORamt();
        Boolean alreadyUsed = rewardModelList.get(position).getAlreadyUsed();

        holder.setData(coupenId, type, validity, body, lowerLimit, upperLimit, discORamt, alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView coupen_title;
        private TextView coupen_validity;
        private TextView coupen_body;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coupen_title = itemView.findViewById(R.id.coupen_title);
            coupen_validity = itemView.findViewById(R.id.coupen_validity);
            coupen_body = itemView.findViewById(R.id.coupen_body);
        }

        private void setData(final String coupenId, final String type, final Date validity, final String body, final String upperLimit, final String loweLimit, final String discORamt, boolean alreadyUsed) {

            if (type.equals("Discount")) {
                coupen_title.setText(type);
            } else {
                coupen_title.setText("Flat Rs." + discORamt + " OFF");
            }

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (alreadyUsed) {
                coupen_validity.setText("Already used");
                coupen_validity.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                coupen_body.setTextColor(Color.parseColor("#50ffffff"));
                coupen_title.setTextColor(Color.parseColor("#50ffffff"));
            } else {
                coupen_body.setTextColor(Color.parseColor("#ffffff"));
                coupen_title.setTextColor(Color.parseColor("#ffffff"));
                coupen_validity.setTextColor(itemView.getContext().getResources().getColor(R.color.coupenPurple));
                coupen_validity.setText("till " + simpleDateFormat.format(validity));
            }

            coupen_body.setText(body);

            if (useMiniLayout){
                itemView.setOnClickListener(view -> {

                    if (!alreadyUsed){
                        selectedCoupenTitle.setText(type);
                        selectedCoupenExpiryDate.setText(simpleDateFormat.format(validity));
                        selectedCoupenBody.setText(body);

                        if (Long.valueOf(productOriginalPrice) < Long.valueOf(loweLimit) && Long.valueOf(productOriginalPrice) > Long.valueOf(upperLimit)){
                            if (type.equals("Discount")){
                                Long discountAmount = Long.valueOf(productOriginalPrice)*Long.valueOf(discORamt)/100;
                                discountedPrice.setText("Rs."+String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount)+"/-");
                            }else {
                                discountedPrice.setText("Rs."+String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discORamt))+"/-");
                            }
                            if (cartItemPosition != -1) {
                                cartItemModelList.get(cartItemPosition).setSelectedCoupenId(coupenId);
                            }
                        }else {
                            if (cartItemPosition != -1) {
                                cartItemModelList.get(cartItemPosition).setSelectedCoupenId(null);
                            }
                            discountedPrice.setText("Invalid");
                            Toast.makeText(itemView.getContext(), "Sorry ! Food does not matches the coupen terms.", Toast.LENGTH_SHORT).show();
                        }
                        if (coupensRecyclerView.getVisibility() == View.GONE) {
                            coupensRecyclerView.setVisibility(View.VISIBLE);
                            selectedCoupen.setVisibility(View.GONE);
                        } else {
                            coupensRecyclerView.setVisibility(View.GONE);
                            selectedCoupen.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

        }
    }
}
