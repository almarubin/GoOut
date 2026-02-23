package com.example.goout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // קישור ה-RecyclerView - ודאי שקיים ב-activity_results.xml
        RecyclerView recyclerView = findViewById(R.id.results_recycler_view);
        Button backBtn = findViewById(R.id.btn_back_home);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            String jsonRaw = getIntent().getStringExtra("JSON_RESULTS");
            List<Place> placeList = new ArrayList<>();

            try {
                if (jsonRaw != null) {
                    JSONArray array = new JSONArray(jsonRaw);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        // שליפת הנתונים לפי המבנה החדש שביקשת מה-AI
                        placeList.add(new Place(
                                obj.optString("name", "Unknown"),
                                obj.optString("distance", "N/A"),
                                obj.optDouble("rating", 0.0),
                                obj.optInt("avg_price", 0)
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            recyclerView.setAdapter(new PlaceAdapter(placeList));
        }

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }

    // אובייקט נתונים מעודכן עם התכונות שביקשת
    static class Place {
        String name, distance;
        double rating;
        int price;

        Place(String name, String distance, double rating, int price) {
            this.name = name;
            this.distance = distance;
            this.rating = rating;
            this.price = price;
        }
    }

    // אדפטר לניהול התצוגה ברשימה
    static class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
        List<Place> places;

        PlaceAdapter(List<Place> places) { this.places = places; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // ניפוח ה-XML של החלונית הבודדת
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Place p = places.get(position);
            holder.name.setText(p.name);
            // הצגת כל הנתונים בחלונית
            holder.distanceText.setText("מרחק: " + p.distance);
            holder.ratingText.setText("דירוג: ⭐ " + p.rating);
            holder.priceText.setText("הוצאה ממוצעת: ₪" + p.price);
        }

        @Override
        public int getItemCount() { return places.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, distanceText, ratingText, priceText;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.item_place_name);
                distanceText = v.findViewById(R.id.item_place_distance);
                ratingText = v.findViewById(R.id.item_place_rating);
                priceText = v.findViewById(R.id.item_place_price);
            }
        }
    }
}