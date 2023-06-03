package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodcafe.adapter.WishlistAdapter;
import com.example.foodcafe.model.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private SearchView productSearch;
    private TextView textView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        productSearch = findViewById(R.id.productSearch);
        textView = findViewById(R.id.textview);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        final List<WishlistModel> list = new ArrayList<>();
        final List<String> ids = new ArrayList<>();

        final Adapter adapter = new Adapter(list , false);
        adapter.setFromSearch(true);
        recyclerView.setAdapter(adapter);

        productSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                list.clear();
                ids.clear();

                s.toLowerCase();

                final String[] tags = s.toLowerCase().split(" ");
                for (String tag : tags){
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags",tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                            WishlistModel model = new WishlistModel(documentSnapshot.getId(), documentSnapshot.get("product_image_1").toString()
                                                    , documentSnapshot.get("product_title").toString()
                                                    , (long) documentSnapshot.get("free_coupens")
                                                    , documentSnapshot.get("average_rating").toString()
                                                    , documentSnapshot.get("product_price").toString()
                                                    , (boolean) documentSnapshot.get("COD")
                                                    , true);

                                            model.setTags((ArrayList<String>) documentSnapshot.get("tags"));

                                            if (!ids.contains(model.getProductId())){
                                                list.add(model);
                                                ids.add(model.getProductId());
                                            }
                                        }
                                        if (tag.equals(tags[tags.length - 1])){
                                            if (list.size() == 0){
                                                textView.setVisibility(View.VISIBLE);
                                                recyclerView.setVisibility(View.GONE);
                                            }else {
                                                textView.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                adapter.getFilter().filter(s);
                                            }
                                        }
                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    class Adapter extends WishlistAdapter implements Filterable{

        private List<WishlistModel> originalList;
        public Adapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
            super(wishlistModelList, wishlist);
            originalList = wishlistModelList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    FilterResults results = new FilterResults();
                    List<WishlistModel> filterList = new ArrayList<>();

                    final String[] tags = charSequence.toString().toLowerCase().split(" ");

                    for (WishlistModel model : originalList){
                        ArrayList<String> presentTags = new ArrayList<>();
                        for (String tag : tags){
                            if (model.getTags().contains(tag)){
                                presentTags.add(tag);
                            }
                        }
                        model.setTags(presentTags);
                    }

                    for (int i = tags.length ; i > 0 ; i--){
                        for (WishlistModel model : originalList){
                            if (model.getTags().size() == i){
                                filterList.add(model);
                            }
                        }
                    }

                    results.values = filterList;
                    results.count = filterList.size();

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                    if (filterResults.count > 0){
                        setWishlistModelList((List<WishlistModel>) filterResults.values);
                    }

                    notifyDataSetChanged();
                }
            };
        }
    }
}