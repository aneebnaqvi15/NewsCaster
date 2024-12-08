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

import androidx.annotation.NonNull;
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

public class SubEditorActivity extends AppCompatActivity {

    private TextView newsId, status;
    private EditText title, date, time, location, article;
    private Spinner categorySpinner;
    private ImageView uploadedMedia;
    private Button btnApprove, btnReject, btnSubmit, btnNext, btnProceedToDashboard;

    private DatabaseReference sectionEditorRef;
    private DatabaseReference subEditorRef;
    private DatabaseReference categoryRef;
    private DatabaseReference latestNewsRef; // Reference for the latest news

    private String currentNewsId; // To hold the current News ID
    private List<String> newsIdList = new ArrayList<>();
    private int currentIndex = 0;

    private static final String TAG = "SubEditorActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_editor);

        // Initialize views
        newsId = findViewById(R.id.news_id);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        categorySpinner = findViewById(R.id.category_spinner);
        article = findViewById(R.id.article);
        uploadedMedia = findViewById(R.id.uploaded_media);
        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);
        status = findViewById(R.id.status);  // Initialize status TextView
        btnProceedToDashboard = findViewById(R.id.proceed_to_dashboard); // Initialize proceed to dashboard button

        // Logging to verify initialization
        Log.d(TAG, "Views initialized");

        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.news_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Initialize Firebase references
        sectionEditorRef = FirebaseDatabase.getInstance().getReference("sectionEditor");
        subEditorRef = FirebaseDatabase.getInstance().getReference("subEditor");
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        latestNewsRef = FirebaseDatabase.getInstance().getReference("latestNews"); // Initialize latest news reference

        // Fetch all news IDs
        fetchAllNewsIds();

        // Date picker dialog
        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(SubEditorActivity.this,
                    (view, year1, month1, dayOfMonth) -> date.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        // Approve button click listener
        btnApprove.setOnClickListener(v -> {
            if (status != null) {
                status.setText("Status: Approved");
                Toast.makeText(SubEditorActivity.this, "News Approved", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Status TextView is null");
            }
        });

        // Reject button click listener
        btnReject.setOnClickListener(v -> {
            if (status != null) {
                status.setText("Status: Rejected");
                Toast.makeText(SubEditorActivity.this, "News Rejected", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Status TextView is null");
            }
        });

        // Submit button click listener
        btnSubmit.setOnClickListener(v -> {
            if (status != null) {
                String currentStatus = status.getText().toString();
                if (currentStatus.equals("Status: Approved")) {
                    saveSubEditorData();
                } else {
                    Toast.makeText(SubEditorActivity.this, "Please approve the news before submitting", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Status TextView is null");
            }
        });

        // Next button click listener
        btnNext.setOnClickListener(v -> moveToNextNews());

        // Proceed to Dashboard button click listener
        btnProceedToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(SubEditorActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Method to fetch all news IDs
    private void fetchAllNewsIds() {
        sectionEditorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String newsId = snapshot.getKey();
                    newsIdList.add(newsId);
                }
                if (!newsIdList.isEmpty()) {
                    currentNewsId = newsIdList.get(currentIndex);
                    fetchSectionEditorData(currentNewsId);
                } else {
                    Log.d(TAG, "No news IDs found in sectionEditor table");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubEditorActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to fetch data from sectionEditor table
    private void fetchSectionEditorData(String newsId) {
        sectionEditorRef.child(newsId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String newsIdStr = dataSnapshot.child("newsId").getValue(String.class);
                    String titleStr = dataSnapshot.child("title").getValue(String.class);
                    String dateStr = dataSnapshot.child("date").getValue(String.class);
                    String timeStr = dataSnapshot.child("time").getValue(String.class);
                    String locationStr = dataSnapshot.child("location").getValue(String.class);
                    String categoryStr = dataSnapshot.child("category").getValue(String.class);
                    String articleStr = dataSnapshot.child("article").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    SubEditorActivity.this.newsId.setText(newsIdStr);
                    title.setText(titleStr);
                    date.setText(dateStr);
                    time.setText(timeStr);
                    location.setText(locationStr);
                    int spinnerPosition = ((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(categoryStr);
                    categorySpinner.setSelection(spinnerPosition);
                    article.setText(articleStr);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(SubEditorActivity.this).load(imageUrl).into(uploadedMedia);
                        uploadedMedia.setTag(imageUrl); // Set the tag for saving later
                    } else {
                        uploadedMedia.setImageResource(R.drawable.placeholder); // Use a placeholder image
                    }
                } else {
                    Toast.makeText(SubEditorActivity.this, "News data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubEditorActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to save data to subEditor and update categories table
    private void saveSubEditorData() {
        String newsIdStr = newsId.getText().toString();
        String titleStr = title.getText().toString();
        String dateStr = date.getText().toString();
        String timeStr = time.getText().toString();
        String locationStr = location.getText().toString();
        String categoryStr = categorySpinner.getSelectedItem().toString();
        String articleStr = article.getText().toString();
        String statusStr = status.getText().toString();
        String imageUrl = (String) uploadedMedia.getTag(); // Retrieve the imageUrl from the tag

        SubEditorData subEditorData = new SubEditorData(newsIdStr, titleStr, dateStr, timeStr, locationStr, categoryStr, articleStr, statusStr, imageUrl);

        subEditorRef.child(newsIdStr).setValue(subEditorData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    sectionEditorRef.child(newsIdStr).removeValue(); // Remove from sectionEditor
                    Toast.makeText(SubEditorActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                    if (statusStr.equals("Status: Approved")) {
                        // Add to categories table
                        DatabaseReference categoryNewsRef = categoryRef.child(categoryStr).child(newsIdStr);
                        categoryNewsRef.setValue(subEditorData);

                        // Update latestNews with approved news data
                        latestNewsRef.child(newsIdStr).setValue(subEditorData);

                        // Set a 24-hour expiration for the latest news
                        latestNewsRef.child(newsIdStr).getRef().getParent().getRef()
                                .child(newsIdStr).removeValue(); // Remove news after 24 hours
                    }
                    moveToNextNews(); // Move to the next news after saving
                } else {
                    Log.e(TAG, "Data saving error: " + databaseError.getMessage());
                    Toast.makeText(SubEditorActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to move to the next news item
    private void moveToNextNews() {
        currentIndex++;
        if (currentIndex < newsIdList.size()) {
            currentNewsId = newsIdList.get(currentIndex);
            fetchSectionEditorData(currentNewsId);
        } else {
            Toast.makeText(this, "No more news available", Toast.LENGTH_SHORT).show();
        }
    }
    public class SubEditorData {

        private String newsId;
        private String title;
        private String date;
        private String time;
        private String location;
        private String category;
        private String article;
        private String status;
        private String imageUrl;

        // Default constructor required for calls to DataSnapshot.getValue(SubEditorData.class)
        public SubEditorData() {
        }

        // Parameterized constructor
        public SubEditorData(String newsId, String title, String date, String time, String location, String category, String article, String status, String imageUrl) {
            this.newsId = newsId;
            this.title = title;
            this.date = date;
            this.time = time;
            this.location = location;
            this.category = category;
            this.article = article;
            this.status = status;
            this.imageUrl = imageUrl;
        }

        // Getters and Setters
        public String getNewsId() {
            return newsId;
        }

        public void setNewsId(String newsId) {
            this.newsId = newsId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
