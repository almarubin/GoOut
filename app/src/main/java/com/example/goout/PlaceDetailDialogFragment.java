package com.example.goout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * דיאלוג המציג תיאור מפורט של המקום ואפשרות ניווט.
 */
public class PlaceDetailDialogFragment extends DialogFragment {

    private static final String ARG_NAME = "place_name";
    private static final String ARG_DESC = "place_desc";

    // מעדכנים את ה-newInstance שיקבל גם תיאור מהפרגמנט
    public static PlaceDetailDialogFragment newInstance(String name, String description) {
        PlaceDetailDialogFragment fragment = new PlaceDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESC, description);
        fragment.setArguments(args);
        return fragment;
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
        // שימוש ב-Layout הקיים של הכרטיסייה
        View v = inflater.inflate(R.layout.item_place_card, container, false);

        TextView nameTv = v.findViewById(R.id.item_place_name);
        TextView descTv = v.findViewById(R.id.item_place_distance); // שימוש זמני בשדה המרחק להצגת התיאור

        // הסתרת שדות שאינם נחוצים בחלונית הפרטים
        v.findViewById(R.id.item_place_rating).setVisibility(View.GONE);
        v.findViewById(R.id.item_place_price).setVisibility(View.GONE);

        String placeName = getArguments() != null ? getArguments().getString(ARG_NAME) : "Unknown";
        String placeDesc = getArguments() != null ? getArguments().getString(ARG_DESC) : "אין תיאור זמין.";

        nameTv.setText(placeName);
        descTv.setText(placeDesc);
        descTv.setTextColor(0xFFCCCCCC); // צבע אפור בהיר לתיאור

        // הוספת כפתור ניווט
        LinearLayout layout = (LinearLayout) nameTv.getParent();
        Button navBtn = new Button(getContext());
        navBtn.setText("Navigate Now");
        navBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF39FF14));
        navBtn.setTextColor(0xFF0A1A0A);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 30, 0, 0);
        navBtn.setLayoutParams(params);
        layout.addView(navBtn);

        navBtn.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(placeName));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(placeName);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
            dismiss();
        });

        return v;
    }
}