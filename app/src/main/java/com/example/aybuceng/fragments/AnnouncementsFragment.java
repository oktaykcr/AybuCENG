package com.example.aybuceng.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.aybuceng.R;
import com.example.aybuceng.helpers.ActivityHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnnouncementsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Map<String, String> announcementMap;

    private ListView listView;
    private ArrayAdapter<String> adapter;

    public AnnouncementsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        announcementMap = new LinkedHashMap<>();

        listView = view.findViewById(R.id.announcements_list_view);
        listView.setOnItemClickListener(this);

        getAnnouncementList();

        return view;
    }


    private void getAnnouncementList() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://aybu.edu.tr/muhendislik/bilgisayar/").get();
                    Elements elements = document.select(".contentAnnouncements .caContent .cncItem");
                    for (Element element : elements) {
                        if(element.text() != null && !element.text().isEmpty()) {
                            announcementMap.put(element.text(), element.select("a").attr("href"));
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ArrayAdapter<>(
                                    getActivity(),
                                    R.layout.other_list_layout,
                                    R.id.content,
                                    new ArrayList<>(announcementMap.keySet())
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String href = announcementMap.get(listView.getItemAtPosition(position).toString());
        ActivityHelper.openWebURL(getActivity(), "https://aybu.edu.tr/muhendislik/bilgisayar/" + href);
    }
}
