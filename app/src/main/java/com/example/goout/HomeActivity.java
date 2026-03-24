package com.example.goout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * מחלקת מסך הבית - גרסת הפרגמנטים
 * מנהלת את הטופס ומציגה את ResultsFragment בתוך אותו מסך
 */
public class HomeActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View formContainer;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // קישור רכיבי הממשק
        progressBar = findViewById(R.id.home_progress_bar);
        formContainer = findViewById(R.id.form_container);
        fragmentContainer = findViewById(R.id.fragment_container);

        Spinner activitySpinner = findViewById(R.id.spinner_activity_type);
        EditText distanceInput = findViewById(R.id.input_distance);
        EditText priceInput = findViewById(R.id.input_price);
        EditText locationInput = findViewById(R.id.input_location);
        Button btnFind = findViewById(R.id.btn_find_activity);
        ImageButton btnFavorites = findViewById(R.id.btn_my_favorites);

        // הגדרת רשימת הפעילויות לספינר
        String[] activities = {"אוכל", "אמנות", "אטרקציות", "חיי לילה", "טבע"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        activitySpinner.setAdapter(adapter);

        // לוגיקה למעבר למסך מועדפים - הוסר הדירוס שהיה כאן
        if (btnFavorites != null) {
            btnFavorites.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, FavoritesActivity.class);
                startActivity(intent);
            });
        }

        // לוגיקה ללחיצה על כפתור החיפוש
        btnFind.setOnClickListener(v -> {
            String selectedType = activitySpinner.getSelectedItem().toString();
            String distance = distanceInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (location.isEmpty()) {
                Toast.makeText(this, "אנא הזן מיקום לחיפוש", Toast.LENGTH_SHORT).show();
                return;
            }

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            String prompt = String.format(
                    "מצא לי 10 מקומות של %s באזור %s ברדיוס של %s ק\"מ ובתקציב של עד %s ש\"ח. " +
                            "תחזיר את התוצאות בפורמט JSON בלבד כרשימה של אובייקטים עם השדות הבאים בדיוק: " +
                            "name, distance, rating, avg_price, description.",
                    selectedType, location,
                    distance.isEmpty() ? "10" : distance,
                    price.isEmpty() ? "ללא הגבלה" : price
            );

            displayResults(prompt);
        });
    }

    private void displayResults(String prompt) {
        String mockJson = "[" +
                "{\"name\":\"Mesa Restaurant\",\"distance\":\"0.5km\",\"rating\":4.7,\"avg_price\":250,\"description\":\"מסעדת שף יוקרתית.\"}," +
                "{\"name\":\"Park HaYarkon\",\"distance\":\"2.1km\",\"rating\":4.2,\"avg_price\":0,\"description\":\"פארק רחב ידיים.\"}," +
                "{\"name\":\"Cinema City\",\"distance\":\"3.5km\",\"rating\":4.5,\"avg_price\":50,\"description\":\"מתחם קולנוע ענק.\"}" +
                "]";

        if (progressBar != null) progressBar.setVisibility(View.GONE);

        if (formContainer != null) formContainer.setVisibility(View.GONE);
        if (fragmentContainer != null) fragmentContainer.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ResultsFragment.newInstance(mockJson))
                .addToBackStack("results")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (formContainer != null) formContainer.setVisibility(View.VISIBLE);
            if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}