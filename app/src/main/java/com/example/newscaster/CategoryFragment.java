package com.example.newscaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CategoryFragment extends Fragment {

    private Button localNewsButton, politicsButton, sportsButton, eventsButton, crimeButton, weatherButton, businessButton, educationButton, otherButton;
    private DatabaseReference categoriesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize buttons
        localNewsButton = view.findViewById(R.id.local_news_button);
        politicsButton = view.findViewById(R.id.politics_button);
        sportsButton = view.findViewById(R.id.sports_button);
        eventsButton = view.findViewById(R.id.events_button);
        crimeButton = view.findViewById(R.id.crime_button);
        weatherButton = view.findViewById(R.id.weather_button);
        businessButton = view.findViewById(R.id.business_button);
        educationButton = view.findViewById(R.id.education_button);
        otherButton = view.findViewById(R.id.other_button);

        // Set click listeners for buttons
        setButtonListeners();

        // Initialize Firebase reference
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        // Load categories from Firebase
        loadCategories();

        return view;
    }

    private void setButtonListeners() {
        localNewsButton.setOnClickListener(v -> navigateToCategory(LocalNewsActivity.class, "Local News"));
        politicsButton.setOnClickListener(v -> navigateToCategory(PoliticsActivity.class, "Politics"));
        sportsButton.setOnClickListener(v -> navigateToCategory(SportsActivity.class, "Sports"));
        eventsButton.setOnClickListener(v -> navigateToCategory(EventsActivity.class, "Events"));
        crimeButton.setOnClickListener(v -> navigateToCategory(CrimeActivity.class, "Crime"));
        weatherButton.setOnClickListener(v -> navigateToCategory(WeatherActivity.class, "Weather"));
        businessButton.setOnClickListener(v -> navigateToCategory(BusinessActivity.class, "Business"));
        educationButton.setOnClickListener(v -> navigateToCategory(EducationActivity.class, "Education"));
        otherButton.setOnClickListener(v -> navigateToCategory(OtherActivity.class, "Other"));
    }

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryName = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    Log.d("CategoryFragment", "Category: " + categoryName + ", Title: " + title + ", Image URL: " + imageUrl);

                    // Verify that news items are present in the category
                    for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                        String newsId = newsSnapshot.getKey();
                        String newsTitle = newsSnapshot.child("title").getValue(String.class);
                        Log.d("CategoryFragment", "News ID: " + newsId + ", Title: " + newsTitle);
                    }

                    // Handle category-specific UI updates as needed
                    if (categoryName != null) {
                        switch (categoryName) {
                            case "Local News":
                                localNewsButton.setText(title != null ? title : categoryName);
                                break;
                            case "Politics":
                                politicsButton.setText(title != null ? title : categoryName);
                                break;
                            case "Sports":
                                sportsButton.setText(title != null ? title : categoryName);
                                break;
                            case "Events":
                                eventsButton.setText(title != null ? title : categoryName);
                                break;
                            case "Crime":
                                crimeButton.setText(title != null ? title : categoryName);
                                break;
                            case "Weather":
                                weatherButton.setText(title != null ? title : categoryName);
                                break;
                            case "Business":
                                businessButton.setText(title != null ? title : categoryName);
                                break;
                            case "Education":
                                educationButton.setText(title != null ? title : categoryName);
                                break;
                            case "Other":
                                otherButton.setText(title != null ? title : categoryName);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToCategory(Class<?> categoryClass, String category) {
        Intent intent = new Intent(getActivity(), categoryClass);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
