package com.example.aybuceng.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aybuceng.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodListFragment extends Fragment {

    private List<String> foodList;
    private ListView listView;
    private ArrayAdapter<String> adapter;


    private TextView date;

    public FoodListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        foodList = new ArrayList<>();

        listView = view.findViewById(R.id.food_list_view);
        date = view.findViewById(R.id.txt_date);

        getFoodList();

        return view;
    }

    private void getFoodList() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://aybu.edu.tr/sks/").get();
                    Elements elements = document.select("table tr");
                    for (Element element : elements) {
                        if(element.text() != null && !element.text().isEmpty())
                            foodList.add(element.text());
                    }
                    foodList.remove(0); // remove full menu list
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // set date to textview and remove from food list
                            date.setText(foodList.get(0));
                            foodList.remove(0);

                            adapter = new ArrayAdapter<>(
                                    getActivity(),
                                    R.layout.food_list_layout,
                                    R.id.content,
                                    foodList
                            );

                            listView.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
