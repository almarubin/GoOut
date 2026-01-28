package com.example.goout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    // ==========================================
    // חלק 1: הגדרות משתנים עבור ה-AI
    // ==========================================
    private final String apiKey = "AIzaSyBMBqGfChbGGHwwHLyv49JHxA0KtxEq5dk"; // המפתח יוזרק אוטומטית בזמן הריצה
    private ProgressBar progressBar;
    private TextView resultsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Spinner activitySpinner = findViewById(R.id.spinner_activity_type);
        EditText distanceInput = findViewById(R.id.input_distance);
        EditText priceInput = findViewById(R.id.input_price);
        Button btnFind = findViewById(R.id.btn_find_activity);

        progressBar = new ProgressBar(this);
        resultsDisplay = new TextView(this);

        String[] activities = {"Food", "Art", "Attraction"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, activities);
        activitySpinner.setAdapter(adapter);

        // ==========================================
        // חלק 2: לחיצה על הכפתור ושליחת הבקשה ל-AI
        // ==========================================
        btnFind.setOnClickListener(v -> {
            String type = activitySpinner.getSelectedItem().toString();
            String distance = distanceInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();

            if (distance.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "אנא מלאי את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            // יצירת הפרומפט (ההוראה) עבור ה-AI
            String prompt = "Give me a list of 5 " + type + " places to go to. " +
                    "The maximum distance is " + distance + "km and the budget is " + price + " NIS. " +
                    "Format the output as a clear list with names and short descriptions in Hebrew.";

            getAIRecommendations(prompt);
        });
    }

    // ==========================================
    // חלק 3: פונקציה שמפעילה את ה-AI ברקע
    // ==========================================
    private void getAIRecommendations(String prompt) {
        Toast.makeText(this, "מחפש מקומות עבורך...", Toast.LENGTH_SHORT).show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String aiResponse = callGeminiAPI(prompt, 0);

            runOnUiThread(() -> {
                if (aiResponse != null) {
                    // כאן התוצאה חוזרת מה-AI
                    Toast.makeText(HomeActivity.this, "נמצאו תוצאות!", Toast.LENGTH_SHORT).show();
                    // תוכלי להדפיס את aiResponse ללוג או להציג בתיבת טקסט
                } else {
                    Toast.makeText(HomeActivity.this, "שגיאה בקבלת המלצות, נסי שוב", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // ==========================================
    // חלק 4: פונקציית התקשורת מול שרתי Google Gemini
    // ==========================================
    private String callGeminiAPI(String userQuery, int retryCount) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", userQuery);
            contentsArray.put(new JSONObject().put("parts", new JSONArray().put(partsObject)));
            jsonBody.put("contents", contentsArray);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonBody.toString());
            writer.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                JSONObject resultJson = new JSONObject(response.toString());
                return resultJson.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            } else if (retryCount < 5) {
                Thread.sleep((long) Math.pow(2, retryCount) * 1000);
                return callGeminiAPI(userQuery, retryCount + 1);
            }
        } catch (Exception e) {
            if (retryCount < 5) {
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 1000);
                } catch (InterruptedException ignored) {}
                return callGeminiAPI(userQuery, retryCount + 1);
            }
        }
        return null;
    }
}