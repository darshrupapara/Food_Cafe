package com.example.foodcafe.ui.reward;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcafe.Dbqueries;
import com.example.foodcafe.R;
import com.example.foodcafe.adapter.RewardAdapter;
import com.example.foodcafe.databinding.FragmentMyRewardBinding;
import com.example.foodcafe.model.RewardModel;

import java.util.ArrayList;
import java.util.List;

public class MyRewardFragment extends Fragment {

    private RecyclerView rewardRecyclerView;
    private Dialog loadingDialog;
    public static RewardAdapter rewardAdapter;

    private MyRewardFragment binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyRewardViewModel slideshowViewModel =
                new ViewModelProvider(this).get(MyRewardViewModel.class);

        View view = inflater.inflate(R.layout.fragment_my_reward, container, false);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        rewardRecyclerView = view.findViewById(R.id.my_reward_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rewardRecyclerView.setLayoutManager(layoutManager);

        rewardAdapter = new RewardAdapter(Dbqueries.rewardModelList,false);
        rewardRecyclerView.setAdapter(rewardAdapter);

        if (Dbqueries.rewardModelList.size() == 0){
            Dbqueries.loadRewards(getContext(),loadingDialog,true);
        }else {
            loadingDialog.dismiss();
        }

        rewardAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}