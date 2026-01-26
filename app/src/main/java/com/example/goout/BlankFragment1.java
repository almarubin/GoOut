package com.example.goout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class BlankFragment1 extends Fragment {

    // 1. הצהרה על FirebaseAuth
    private com.google.firebase.auth.FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. הגדרת המשתנים וקישורם ל-ID מה-XML
        EditText emailField = view.findViewById(R.id.email);
        EditText passwordField = view.findViewById(R.id.password);
        Button button = view.findViewById(R.id.next);

        // 2. הגדרת פעולת הלחיצה
        button.setOnClickListener(v -> {
            // שימי לב: אנחנו משתמשים ב-emailField כפי שהגדרנו למעלה
            String userEmail = emailField.getText().toString().trim();
            String userPassword = passwordField.getText().toString().trim();

            if (!userEmail.isEmpty() && userPassword.length() >= 6) {
                com.google.firebase.auth.FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                android.widget.Toast.makeText(getContext(), "נרשמת בהצלחה!", android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                android.widget.Toast.makeText(getContext(), "שגיאה: " + task.getException().getMessage(), android.widget.Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                // הצגת שגיאה למשתמש במידה והנתונים לא תקינים
                if (userEmail.isEmpty()) emailField.setError("נא להזין אימייל");
                if (userPassword.length() < 6) passwordField.setError("סיסמה חייבת להיות לפחות 6 תווים");
            }
        });
    }
}
