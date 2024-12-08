package com.example.newscaster;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SectionEditorActivity extends AppCompatActivity {

    private TextView newsIdTV, status;
    private EditText title, date, time, location, article;
    private Spinner categorySpinner;
    private ImageView uploadedMedia;
    private Button btnApprove, btnReject, btnSubmit, btnNext, proceedToDashboard;

    private DatabaseReference reporterRef;
    private DatabaseReference sectionEditorRef;

    private String currentNewsId; // To hold the current News ID
    private List<String> newsIdList = new ArrayList<>();
    private int currentIndex = 0;
    private String currentImageUrl; // To hold the current image URL

    private static final String TAG = "SectionEditorActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_editor);

        // Initialize views
        newsIdTV = findViewById(R.id.news_id);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        categorySpinner = findViewById(R.id.category_spinner);
        article = findViewById(R.id.article);
        status = findViewById(R.id.status);
        uploadedMedia = findViewById(R.id.uploaded_media);
        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);
        proceedToDashboard = findViewById(R.id.proceed_to_dashboard);

        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.news_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Initialize Firebase references
        reporterRef = FirebaseDatabase.getInstance().getReference("reporter");
        sectionEditorRef = FirebaseDatabase.getInstance().getReference("sectionEditor");

        // Fetch all news IDs
        fetchAllNewsIds();

        // Date picker dialog
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SectionEditorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Approve button click listener
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("Status: Approved");
                Toast.makeText(SectionEditorActivity.this, "News Approved", Toast.LENGTH_SHORT).show();
            }
        });

        // Reject button click listener
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("Status: Rejected");
                Toast.makeText(SectionEditorActivity.this, "News Rejected", Toast.LENGTH_SHORT).show();
                moveToNextNews();
            }
        });

        // Submit button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentStatus = status.getText().toString();
                if (currentStatus.equals("Status: Approved")) {
                    saveSectionEditorData();
                } else {
                    Toast.makeText(SectionEditorActivity.this, "Please approve the news before submitting", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Next button click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextNews();
            }
        });

        // Proceed to Dashboard button click listener
        proceedToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SectionEditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to fetch all news IDs
    private void fetchAllNewsIds() {
        reporterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String newsId = snapshot.getKey();
                    newsIdList.add(newsId);
                }
                if (!newsIdList.isEmpty()) {
                    currentNewsId = newsIdList.get(currentIndex);
                    fetchReporterData(currentNewsId);
                } else {
                    Log.d(TAG, "No news IDs found in reporter table");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SectionEditorActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to fetch data from reporter table
    private void fetchReporterData(String newsId) {
        reporterRef.child(newsId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String newsIdStr = newsId;
                    String titleStr = dataSnapshot.child("title").getValue(String.class);
                    String dateStr = dataSnapshot.child("date").getValue(String.class);
                    String timeStr = dataSnapshot.child("time").getValue(String.class);
                    String locationStr = dataSnapshot.child("location").getValue(String.class);
                    String categoryStr = dataSnapshot.child("category").getValue(String.class);
                    String articleStr = dataSnapshot.child("article").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    Log.d(TAG, "Fetched newsId: " + newsId); // Log the fetched newsId

                    if (newsIdStr != null) {
                        newsIdTV.setText(newsIdStr);
                    }
                    if (titleStr != null) title.setText(titleStr);
                    if (dateStr != null) date.setText(dateStr);
                    if (timeStr != null) time.setText(timeStr);
                    if (locationStr != null) location.setText(locationStr);
                    if (categoryStr != null) {
                        int spinnerPosition = ((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(categoryStr);
                        categorySpinner.setSelection(spinnerPosition);
                    }
                    if (articleStr != null) article.setText(articleStr);

                    currentImageUrl = imageUrl; // Store the image URL
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(SectionEditorActivity.this).load(imageUrl).into(uploadedMedia);
                    } else {
                        uploadedMedia.setImageResource(R.drawable.placeholder); // Use a placeholder image
                    }
                } else {
                    Toast.makeText(SectionEditorActivity.this, "News data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SectionEditorActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to save data to sectionEditor table
    private void saveSectionEditorData() {
        String newsIdStr = newsIdTV.getText().toString();
        String titleStr = title.getText().toString();
        String dateStr = date.getText().toString();
        String timeStr = time.getText().toString();
        String locationStr = location.getText().toString();
        String categoryStr = categorySpinner.getSelectedItem().toString();
        String articleStr = article.getText().toString();
        String statusStr = status.getText().toString();
        String imageUrl = currentImageUrl; // Use the stored image URL

        SectionEditorData sectionEditorData = new SectionEditorData(newsIdStr, titleStr, dateStr, timeStr, locationStr, categoryStr, articleStr, statusStr, imageUrl);

        sectionEditorRef.child(newsIdStr).setValue(sectionEditorData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SectionEditorActivity.this, "News submitted successfully", Toast.LENGTH_SHORT).show();
                    moveToNextNews();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SectionEditorActivity.this, "Failed to submit news", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to submit news: " + e.getMessage());
                });
    }

    // Method to move to the next news
    private void moveToNextNews() {
        currentIndex++;
        if (currentIndex < newsIdList.size()) {
            currentNewsId = newsIdList.get(currentIndex);
            fetchReporterData(currentNewsId);
        } else {
            Toast.makeText(SectionEditorActivity.this, "No more news to display", Toast.LENGTH_SHORT).show();
        }
    }
}
