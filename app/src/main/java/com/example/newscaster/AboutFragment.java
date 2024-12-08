package com.example.newscaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView descriptionTextView;
    private TextView contactEmailTextView;
    private TextView contactPhoneTextView;
    private ImageButton facebookButton;
    private ImageButton twitterButton;
    private ImageButton linkedinButton;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Initialize views
        logoImageView = view.findViewById(R.id.logoImageView);
        appNameTextView = view.findViewById(R.id.appNameTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        contactEmailTextView = view.findViewById(R.id.contactEmailTextView);
        contactPhoneTextView = view.findViewById(R.id.contactPhoneTextView);
        facebookButton = view.findViewById(R.id.facebookButton);
        twitterButton = view.findViewById(R.id.twitterButton);
        linkedinButton = view.findViewById(R.id.linkedinButton);

        // Set up click listeners
        facebookButton.setOnClickListener(v -> openUrl("https://www.facebook.com/yourapp"));
        twitterButton.setOnClickListener(v -> openUrl("https://twitter.com/yourapp"));
        linkedinButton.setOnClickListener(v -> openUrl("https://www.linkedin.com/company/yourapp"));

        contactEmailTextView.setOnClickListener(v -> sendEmail(getString(R.string.contact_email)));
        contactPhoneTextView.setOnClickListener(v -> makePhoneCall(getString(R.string.contact_phone)));

        return view;
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(callIntent);
    }
}
