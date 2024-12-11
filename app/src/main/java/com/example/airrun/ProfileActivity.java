package com.example.airrun;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    // UI Components
    private ImageView editProfileIcon;
    private ImageView profileImage;
    private TextView profileNameText;
    private TextView profileLevelText;

    // Navigation Bar Components
    private ImageView navMainIcon;
    private ImageView navAchievementIcon;
    private ImageView navProfileIcon;

    // Stats Components
    private TextView distanceValueText;
    private TextView timeValueText;
    private TextView caloriesValueText;

    // Menu Items
    private View personalParametersItem;
    private View achievementsItem;
    private View settingsItem;
    private View contactItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI Components
        initializeViews();

        // Set up Click Listeners
        setupClickListeners();

        // Load Profile Data
        loadProfileData();
    }

    private void initializeViews() {
        // Header Components
        editProfileIcon = findViewById(R.id.edit);
        profileImage = findViewById(R.id.drawable_image_12);
        profileNameText = findViewById(android.R.id.text1);
        profileLevelText = findViewById(android.R.id.text2);

        // Navigation Bar
        navMainIcon = findViewById(R.id.nav_main);
        navAchievementIcon = findViewById(R.id.nav_achievement);
        navProfileIcon = findViewById(R.id.nav_profile);

        // Stats Components
        distanceValueText = findViewById(R.id.text_distance_value);
        timeValueText = findViewById(R.id.text_time_value);
        caloriesValueText = findViewById(R.id.text_calories_value);

        // Menu Items
//        personalParametersItem = findViewById(R.id.personal_parameters);
//        achievementsItem = findViewById(R.id.achievements);
//        settingsItem = findViewById(R.id.settings);
//        contactItem = findViewById(R.id.contact);
    }

    private void setupClickListeners() {
        // Edit Profile Click Listener
        editProfileIcon.setOnClickListener(v -> openEditProfileDialog());

        // Navigation Bar Click Listeners
        navMainIcon.setOnClickListener(v -> navigateToMainScreen());
        navAchievementIcon.setOnClickListener(v -> navigateToAchievementsScreen());
        navProfileIcon.setOnClickListener(v -> {}); // Current screen, do nothing

        // Menu Item Click Listeners
        personalParametersItem.setOnClickListener(v -> openPersonalParameters());
        achievementsItem.setOnClickListener(v -> openAchievementsScreen());
        settingsItem.setOnClickListener(v -> openSettingsScreen());
        contactItem.setOnClickListener(v -> openContactScreen());
    }

    private void loadProfileData() {
        // TODO: Replace with actual data loading logic
        profileNameText.setText("Andrew");
        profileLevelText.setText("Beginner");

        // Sample stats
        distanceValueText.setText("103.2");
        timeValueText.setText("16.9");
        caloriesValueText.setText("1.5k");
    }

    // Navigation Methods
    private void navigateToMainScreen() {
        // TODO: Implement navigation to main screen
    }

    private void navigateToAchievementsScreen() {
        // TODO: Implement navigation to achievements screen
    }

    // Menu Action Methods
    private void openEditProfileDialog() {
        // TODO: Implement edit profile logic
    }

    private void openPersonalParameters() {
        // TODO: Implement personal parameters screen/dialog
    }

    private void openAchievementsScreen() {
        // TODO: Implement achievements screen
    }

    private void openSettingsScreen() {
        // TODO: Implement settings screen
    }

    private void openContactScreen() {
        // TODO: Implement contact screen
    }
}
