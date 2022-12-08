package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapter.ItemAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.model.ItemModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // volley
    private static final String URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
    private RequestQueue requestQueue;

    private Gson gson;

    // todo: documentation
    // https://developer.android.com/topic/libraries/view-binding#java

    private RecyclerView itemsRecyclerView;
    private ItemAdapter itemAdapter;

    private List<ItemModel> itemList;

    // set up view binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        itemList = new ArrayList<>();

        // fetch items data from json
        requestQueue = Volley.newRequestQueue(this);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        fetchItems();

        // set up view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(this);
        itemsRecyclerView.setAdapter(itemAdapter);
        itemAdapter.setItems(itemList);
    }

    private void fetchItems() {
        StringRequest request = new StringRequest(Request.Method.GET, URL, onItemsLoaded, onItemsError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onItemsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<ItemModel> items = Arrays.asList(gson.fromJson(response, ItemModel[].class));
            Comparator<ItemModel> orderByListId = (o1, o2) -> o1.listId - o2.listId;

            items.sort(orderByListId);
            // set with fetched objects
            itemAdapter.setItems(items);
        }
    };

    private final Response.ErrorListener onItemsError = error -> Log.e("Item", error.toString());
}
