package com.example.newscaster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment implements SearchableFragment {

    private RecyclerView recyclerView;
    private List<News> newsList;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        newsList = new ArrayList<>();
        adapter = new NewsAdapter(getContext(), newsList);
        recyclerView.setAdapter(adapter);

        fetchNewsFromFirebase();

        return view;
    }

    private void fetchNewsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        newsList.clear();
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            for (DataSnapshot newsSnapshot : categorySnapshot.getChildren()) {
                                News news = newsSnapshot.getValue(News.class);
                                if (news != null) {
                                    newsList.add(news);
                                }
                            }
                        }

                        // Sort the news list by timestamp in descending order
                        Collections.sort(newsList, new Comparator<News>() {
                            @Override
                            public int compare(News n1, News n2) {
                                return Long.compare(n2.getTimestamp(), n1.getTimestamp());
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void filterResults(String query) {
        List<News> filteredList = new ArrayList<>();
        for (News news : newsList) {
            if (news.getTitle() != null && news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(news);
            }
        }
        adapter.updateList(filteredList);
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
    }
}
