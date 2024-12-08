package com.example.newscaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShareFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Button to share the app on all available platforms
        view.findViewById(R.id.share_button).setOnClickListener(this::shareApp);

        // Button to share the app on WhatsApp
        view.findViewById(R.id.share_whatsapp_button).setOnClickListener(v -> shareOnSpecificApp("com.whatsapp", "Discover what's happening in our city with CityNewsCaster! Get the app now: [App Link]"));

        // Button to share the app on Facebook
        view.findViewById(R.id.share_facebook_button).setOnClickListener(v -> shareOnSpecificApp("com.facebook.katana", "CityNewsCaster keeps you informed about our city's latest news! Download now: [App Link]"));

        return view;
    }

    // General share logic for all platforms
    public void shareApp(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String message = "Join me on CityNewsCaster! Stay updated with the latest city news tailored to your interests. Check out this amazing app: [App Link]";
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Specific sharing logic for a particular app (e.g., WhatsApp, Facebook)
    private void shareOnSpecificApp(String packageName, String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.setPackage(packageName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (android.content.ActivityNotFoundException ex) {
            // Handle case where the app is not installed
        }
    }
}
