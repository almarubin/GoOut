package com.example.goout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PlaceDetailDialogFragment extends DialogFragment {

    private static final String ARG_NAME = "place_name";
    private static final String ARG_DESC = "place_desc";

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public static PlaceDetailDialogFragment newInstance(String name, String description) {
        PlaceDetailDialogFragment fragment = new PlaceDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESC, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_place_card, container, false);

        TextView nameTv = v.findViewById(R.id.item_place_name);
        TextView descTv = v.findViewById(R.id.item_place_distance);

        v.findViewById(R.id.item_place_rating).setVisibility(View.GONE);
        v.findViewById(R.id.item_place_price).setVisibility(View.GONE);

        String placeName = getArguments() != null ? getArguments().getString(ARG_NAME) : "Unknown";
        String placeDesc = getArguments() != null ? getArguments().getString(ARG_DESC) : "No description available.";

        nameTv.setText(placeName);
        descTv.setText(placeDesc);

        LinearLayout layout = (LinearLayout) nameTv.getParent();

        // יצירת כפתור מועדפים (לב)
        ImageButton favBtn = new ImageButton(getContext());
        favBtn.setImageResource(android.R.drawable.btn_star_big_off); // אייקון כוכב/לב מובנה
        favBtn.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        // כפתור ניווט
        Button navBtn = new Button(getContext());
        navBtn.setText("Navigate Now");
        navBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF39FF14));
        navBtn.setTextColor(0xFF0A1A0A);

        layout.addView(favBtn);
        layout.addView(navBtn);

        // לוגיקת שמירה למועדפים ב-Firebase
        favBtn.setOnClickListener(view -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();
            Map<String, Object> favoritePlace = new HashMap<>();
            favoritePlace.put("name", placeName);
            favoritePlace.put("description", placeDesc);
            favoritePlace.put("timestamp", System.currentTimeMillis());

            // שמירה ב-Firestore תחת המשתמש המחובר
            db.collection("users").document(userId).collection("favorites")
                    .document(placeName) // שימוש בשם המקום כמזהה כדי למנוע כפילויות
                    .set(favoritePlace)
                    .addOnSuccessListener(aVoid -> {
                        favBtn.setImageResource(android.R.drawable.btn_star_big_on);
                        Toast.makeText(getContext(), "Added to Favorites!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving", Toast.LENGTH_SHORT).show());
        });

        navBtn.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(placeName));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            dismiss();
        });

        return v;
    }
}