package com.example.newscaster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LatestFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<News> latestNewsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        latestNewsList = new ArrayList<>();

        // Initialize the adapter with the context and the news list
        adapter = new NewsAdapter(getContext(), latestNewsList);
        recyclerView.setAdapter(adapter);

        // Load the latest news into latestNewsList and update the adapter
        loadLatestNews();

        return view;
    }

    private void loadLatestNews() {
        // Use the full constructor with all parameters
        latestNewsList.add(new News("newsId1", "Title 1", "This is the first article.", "Category 1", "Location 1", "2024-08-09", "10:00 AM", "https://example.com/image1.jpg", "status1", System.currentTimeMillis()));
        latestNewsList.add(new News("newsId2", "Title 2", "This is the second article.", "Category 2", "Location 2", "2024-08-09", "11:00 AM", "https://example.com/image2.jpg", "status2", System.currentTimeMillis()));

        // Notify the adapter about data changes
        adapter.updateList(latestNewsList);
    }
}
