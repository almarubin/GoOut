package com.example.goout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment המציג את תוצאות החיפוש.
 * בלחיצה על פריט, נפתח דיאלוג המציג פרטים מורחבים כולל תיאור.
 */
public class ResultsFragment extends Fragment {

    private static final String ARG_JSON = "JSON_RESULTS";

    // יצירת מופע חדש של הפרגמנט עם נתוני ה-JSON מה-AI
    public static ResultsFragment newInstance(String jsonResults) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JSON, jsonResults);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ניפוח הממשק של מסך התוצאות
        View view = inflater.inflate(R.layout.activity_results, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.results_recycler_view);
        Button backBtn = view.findViewById(R.id.btn_back_home);

        if (getArguments() != null && recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            String jsonRaw = getArguments().getString(ARG_JSON);
            List<Place> placeList = new ArrayList<>();

            try {
                if (jsonRaw != null) {
                    JSONArray array = new JSONArray(jsonRaw);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        // פירוק הנתונים לאובייקט Place הכולל את התיאור המפורט
                        placeList.add(new Place(
                                obj.optString("name", "Unknown"),
                                obj.optString("distance", "N/A"),
                                obj.optDouble("rating", 0.0),
                                obj.optInt("avg_price", 0),
                                obj.optString("description", "אין תיאור זמין עבור מקום זה.")
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // הגדרת האדפטר והעברת הנתונים לדיאלוג (שם ותיאור) בעת לחיצה
            recyclerView.setAdapter(new PlaceAdapter(placeList, place -> {
                // קריאה לדיאלוג הפרטים המעודכן
                PlaceDetailDialogFragment dialog = PlaceDetailDialogFragment.newInstance(place.name, place.description);
                dialog.show(getChildFragmentManager(), "place_detail_dialog");
            }));
        }

        // הגדרת כפתור חזרה למסך הבית
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        return view;
    }

    /**
     * מחלקת נתונים המייצגת מקום בודד, כולל שדה תיאור
     */
    static class Place {
        String name, distance, description;
        double rating;
        int price;

        Place(String name, String distance, double rating, int price, String description) {
            this.name = name;
            this.distance = distance;
            this.rating = rating;
            this.price = price;
            this.description = description;
        }
    }

    /**
     * ממשק לטיפול בלחיצות על פריטים ברשימה
     */
    interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    /**
     * אדפטר לניהול ה-RecyclerView ותצוגת הכרטיסיות
     */
    static class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
        private final List<Place> places;
        private final OnPlaceClickListener listener;

        PlaceAdapter(List<Place> places, OnPlaceClickListener listener) {
            this.places = places;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Place p = places.get(position);
            holder.name.setText(p.name);
            holder.distanceText.setText("מרחק: " + p.distance);
            holder.ratingText.setText("דירוג: ⭐ " + p.rating);
            holder.priceText.setText("מחיר ממוצע: ₪" + p.price);

            // הפעלת ה-Listener כשלוחצים על כל הכרטיסייה
            holder.itemView.setOnClickListener(v -> listener.onPlaceClick(p));
        }

        @Override
        public int getItemCount() {
            return places.size();
        }

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