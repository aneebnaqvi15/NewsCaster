package com.example.newscaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "settings_prefs";
    private static final String DARK_MODE_KEY = "dark_mode";
    private static final String LOGGED_IN_KEY = "logged_in";
    private static final String USER_TYPE_KEY = "user_type"; // Added to differentiate between user and admin

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        SwitchCompat switchNotification = view.findViewById(R.id.switch_notification);
        SwitchCompat switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        // Load saved preferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        switchDarkMode.setChecked(isDarkModeEnabled);

        // Setting switch listeners using lambdas
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(getContext(), "Notification " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show()
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DARK_MODE_KEY, isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(getContext(), "Dark mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        // Setting click listeners for feedback, share, and logout buttons
        view.findViewById(R.id.feedback_button).setOnClickListener(this::sendFeedback);
        view.findViewById(R.id.share_button).setOnClickListener(this::shareApp);
        view.findViewById(R.id.logout_button).setOnClickListener(this::logout);

        return view;
    }

    public void sendFeedback(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "syedaneebajk00786@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void shareApp(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: [App Link]");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Check if the user is logged in
        boolean isLoggedIn = sharedPreferences.getBoolean(LOGGED_IN_KEY, false);
        String userType = sharedPreferences.getString(USER_TYPE_KEY, "guest");

        if (isLoggedIn) {
            // Clear user session
            editor.putBoolean(LOGGED_IN_KEY, false);
            editor.putString(USER_TYPE_KEY, "guest");
            editor.apply();

            // Redirect to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // Redirect guest to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
