package com.example.goout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * מסך הבית המנהל את טופס החיפוש ואת הצגת התוצאות באמצעות פרגמנט.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "GeminiAPI";
    private final String apiKey = "AIzaSyBuZeRUSnSqpcMMeyIiyfYEUkWqCSmSVU0";
    private ProgressBar progressBar;
    private View formContainer;
    private FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // קישור רכיבי הממשק מה-XML החדש
        formContainer = findViewById(R.id.form_container);
        fragmentContainer = findViewById(R.id.fragment_container);

        Spinner activitySpinner = findViewById(R.id.spinner_activity_type);
        EditText distanceInput = findViewById(R.id.input_distance);
        EditText priceInput = findViewById(R.id.input_price);
        EditText locationInput = findViewById(R.id.input_location);
        Button btnFind = findViewById(R.id.btn_find_activity);
        progressBar = findViewById(R.id.home_progress_bar);

        // הגדרת רשימת הפעילויות ב-Spinner
        String[] activities = {"Food", "Art", "Attraction"};
        activitySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities));

        btnFind.setOnClickListener(v -> {
            String type = activitySpinner.getSelectedItem().toString();
            String distance = distanceInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (distance.isEmpty() || price.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "אנא מלאי את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            // בניית הפרומפט עבור ה-AI לקבלת נתוני JSON
            String prompt = "I am in " + location + ". Suggest 10 " + type + " places. " +
                    "Max distance: " + distance + "km, Max budget: " + price + " NIS. " +
                    "Return ONLY a JSON array of objects. Each object must have: " +
                    "'name' (Hebrew), 'distance' (string with km), 'rating' (number 1-5), 'avg_price' (number in NIS), " +
                    "'description' (A detailed and inviting description in Hebrew, 2-3 sentences). " +
                    "Do not include markdown code blocks, just the raw JSON array.";

            getAIRecommendations(prompt);
        });
    }

    private void getAIRecommendations(String prompt) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String aiResponse = callGeminiAPI(prompt);
            runOnUiThread(() -> {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (aiResponse != null) {
                    showResults(aiResponse);
                } else {
                    Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * פונקציה המסתירה את הטופס ומציגה את הפרגמנט עם התוצאות
     */
    private void showResults(String jsonResults) {
        if (formContainer != null && fragmentContainer != null) {
            formContainer.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            // טעינת ResultsFragment לתוך המיכל
            ResultsFragment fragment = ResultsFragment.newInstance(jsonResults);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // מאפשר חזרה לטופס בלחיצת Back
                    .commit();
        }
    }

    /**
     * ניהול לחיצה על כפתור חזור - אם התוצאות מוצגות, נחזור לטופס
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
            if (formContainer != null) formContainer.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private String callGeminiAPI(String userQuery) {
        try {
            // שימוש במודל 1.5 פלאש העדכני
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", userQuery);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            jsonBody.put("contents", contents);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonBody.toString());
            writer.flush();

            if (conn.getResponseCode() == 200) {
                Scanner sc = new Scanner(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) sb.append(sc.nextLine());
                JSONObject responseJson = new JSONObject(sb.toString());
                String rawText = responseJson.getJSONArray("candidates").getJSONObject(0)
                        .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                // ניקוי תגיות markdown במידה וה-AI החזיר אותן
                return rawText.replace("```json", "").replace("```", "").trim();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return null;
    }
}