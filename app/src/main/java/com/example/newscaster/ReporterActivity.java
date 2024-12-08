package com.example.newscaster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ReporterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private TextView welcomeText, newsStatus, selectedImageInfo;
    private EditText newsTitle, newsArticle, newsLocation, newsDate, newsTime;
    private Spinner newsCategory;
    private Button selectImageFile, upload, addNew, save, proceedToDashboard;
    String firebaseImageUrl;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("reporter");

        // Initialize views
        welcomeText = findViewById(R.id.welcome_text);
        newsStatus = findViewById(R.id.news_status);
        selectedImageInfo = findViewById(R.id.selected_image_info);
        newsTitle = findViewById(R.id.news_title);
        newsArticle = findViewById(R.id.news_article);
        newsCategory = findViewById(R.id.news_category);
        newsLocation = findViewById(R.id.news_location);
        newsDate = findViewById(R.id.news_date);
        newsTime = findViewById(R.id.news_time);
        selectImageFile = findViewById(R.id.select_image_file);
        upload = findViewById(R.id.upload);
        addNew = findViewById(R.id.add_new);
        save = findViewById(R.id.save);
        proceedToDashboard = findViewById(R.id.proceed_to_dashboard); // Initialize the new button

        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "User");

        // Set the welcome text with the user's name
        welcomeText.setText("Hello " + userName);

        // Set initial news status
        newsStatus.setText("News Status: Pending");

        // Populate the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.news_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsCategory.setAdapter(adapter);

        // Add logic for the buttons
        selectImageFile.setOnClickListener(v -> checkPermissions());

        upload.setOnClickListener(v -> {
            // Validate input fields
            if (newsTitle.getText().toString().isEmpty() || newsArticle.getText().toString().isEmpty()
                    || newsLocation.getText().toString().isEmpty() || newsDate.getText().toString().isEmpty()
                    || newsTime.getText().toString().isEmpty()) {
                Toast.makeText(ReporterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (newsCategory.getSelectedItem().toString().equals("Please select a category")) {
                Toast.makeText(ReporterActivity.this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            } else {
                // Upload the news report
                uploadNewsReport();
            }
        });

        addNew.setOnClickListener(v -> {
            // Clear all input fields
            newsTitle.setText("");
            newsArticle.setText("");
            newsCategory.setSelection(0);
            newsLocation.setText("");
            newsDate.setText("");
            newsTime.setText("");
            selectedImageInfo.setText("No image selected");
            newsStatus.setText("News Status: Pending");
        });

        save.setOnClickListener(v -> {
            // Save the news report
            if (newsTitle.getText().toString().isEmpty() || newsArticle.getText().toString().isEmpty()
                    || newsLocation.getText().toString().isEmpty() || newsDate.getText().toString().isEmpty()
                    || newsTime.getText().toString().isEmpty()) {
                Toast.makeText(ReporterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (newsCategory.getSelectedItem().toString().equals("Please select a category")) {
                Toast.makeText(ReporterActivity.this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            } else {
                saveNewsReport();
            }
        });

        // Set up date and time pickers
        newsDate.setOnClickListener(v -> showDatePickerDialog());
        newsTime.setOnClickListener(v -> showTimePickerDialog());

        // Set up the click listener for the "Proceed to Dashboard" button
        proceedToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ReporterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0 &&grantResults[0] == PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImageInfo.setText(selectedImageUri.toString());
                // Call the uploadImageToFirebase method
            }
        }
    }


    private void uploadImageToFirebase(Uri imageUri, final ImageUploadCallback callback) {
        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique file name
        String fileName = "images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Return the image URL via callback
                        String imageUrl = uri.toString();
                        callback.onSuccess(imageUrl);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get download URL
                        callback.onFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    callback.onFailure(e);
                });
    }

    // Callback interface for image upload result
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    private void uploadNewsReport() {
        // Ensure the image URI is properly parsed
        String imageUrlText = selectedImageInfo.getText().toString();
        if (imageUrlText.isEmpty()) {
            Log.e("Upload", "No image URL provided");
            return;
        }

        Uri imageUri = Uri.parse(imageUrlText);

        // Upload the image to Firebase Storage
        uploadImageToFirebase(imageUri, new ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                // Update the news report data with the image URL
                firebaseImageUrl = imageUrl;
                saveNewsReportToFirebase();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Upload", "Failed to upload image: " + e.getMessage());
                Toast.makeText(ReporterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewsReportToFirebase() {
        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "User");

        String newsId = UUID.randomUUID().toString();
        String newsTitleText = newsTitle.getText().toString();
        String newsArticleText = newsArticle.getText().toString();
        String newsCategoryText = newsCategory.getSelectedItem().toString();
        String newsLocationText = newsLocation.getText().toString();
        String newsDateText = newsDate.getText().toString();
        String newsTimeText = newsTime.getText().toString();

        Map<String, Object> newsData = new HashMap<>();
        newsData.put("newsId", newsId);
        newsData.put("title", newsTitleText);
        newsData.put("article", newsArticleText);
        newsData.put("category", newsCategoryText);
        newsData.put("location", newsLocationText);
        newsData.put("date", newsDateText);
        newsData.put("time", newsTimeText);
        newsData.put("imageUrl", firebaseImageUrl);
        newsData.put("status", "Pending");


        databaseReference.child(newsId).setValue(newsData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReporterActivity.this, "News report uploaded successfully", Toast.LENGTH_SHORT).show();
                    newsStatus.setText("News Status: Uploaded");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReporterActivity.this, "Failed to upload news report", Toast.LENGTH_SHORT).show();
                    newsStatus.setText("News Status: Failed");
                });
    }

    private void saveNewsReport() {
        String newsId = UUID.randomUUID().toString();
        String newsTitleText = newsTitle.getText().toString();
        String newsArticleText = newsArticle.getText().toString();
        String newsCategoryText = newsCategory.getSelectedItem().toString();
        String newsLocationText = newsLocation.getText().toString();
        String newsDateText = newsDate.getText().toString();
        String newsTimeText = newsTime.getText().toString();

        Map<String, Object> newsData = new HashMap<>();
        newsData.put("newsId", newsId);
        newsData.put("title", newsTitleText);
        newsData.put("article", newsArticleText);
        newsData.put("category", newsCategoryText);
        newsData.put("location", newsLocationText);
        newsData.put("date", newsDateText);
        newsData.put("time", newsTimeText);
        newsData.put("status", "Draft");

        databaseReference.child(newsId).setValue(newsData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReporterActivity.this, "News report saved as draft", Toast.LENGTH_SHORT).show();
                    newsStatus.setText("News Status: Draft");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReporterActivity.this, "Failed to save news report", Toast.LENGTH_SHORT).show();
                    newsStatus.setText("News Status: Failed");
                });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            newsDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = hourOfDay + ":" + minute1;
            newsTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }
}
