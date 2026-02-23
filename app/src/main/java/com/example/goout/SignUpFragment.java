package com.example.goout;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * פרגמנט הרשמה המופעל מתוך LoginActivity.
 * מחליף את ה-Activity הישן כדי לאפשר חוויית משתמש חלקה יותר.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // שימוש ב-XML הקיים של ההרשמה
        View view = inflater.inflate(R.layout.activity_signup, container, false);

        auth = FirebaseAuth.getInstance();

        // קישור רכיבים מהממשק
        EditText emailInput = view.findViewById(R.id.signup_email);
        EditText passwordInput = view.findViewById(R.id.signup_password);
        EditText confirmPasswordInput = view.findViewById(R.id.signup_confirm_password);
        EditText addressInput = view.findViewById(R.id.signup_address);
        EditText ageInput = view.findViewById(R.id.signup_age);
        Button signUpButton = view.findViewById(R.id.signup_button);
        TextView statusText = view.findViewById(R.id.signup_status_text);
        TextView goToLogin = view.findViewById(R.id.go_to_login);
        View glowView = view.findViewById(R.id.signup_glow_view);

        // אנימציית הילה ללוגו (כפי שהייתה ב-Activity)
        if (glowView != null) {
            ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(
                    glowView,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.3f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.3f),
                    PropertyValuesHolder.ofFloat("alpha", 0.2f, 0.05f)
            );
            pulse.setDuration(1500);
            pulse.setRepeatCount(ValueAnimator.INFINITE);
            pulse.setRepeatMode(ValueAnimator.REVERSE);
            pulse.start();
        }

        // לוגיקת כפתור הרשמה
        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String age = ageInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || address.isEmpty() || age.isEmpty()) {
                statusText.setText("Please fill all fields");
                statusText.setTextColor(android.graphics.Color.RED);
                return;
            }

            if (!password.equals(confirmPass)) {
                statusText.setText("Passwords do not match");
                statusText.setTextColor(android.graphics.Color.RED);
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            statusText.setText("Registration Successful!");
                            statusText.setTextColor(0xFF39FF14);

                            // מעבר לעמוד הבית וסגירת ה-Activity המארח
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                            if (getActivity() != null) getActivity().finish();
                        } else {
                            statusText.setText("Error: " + task.getException().getMessage());
                            statusText.setTextColor(android.graphics.Color.RED);
                        }
                    });
        });

        // לחיצה על "כבר יש לי חשבון" מפעילה את ה-Back של ה-Activity
        goToLogin.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}