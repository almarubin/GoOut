package com.example.goout;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        // קישור רכיבים מה-XML
        EditText emailInput = findViewById(R.id.signup_email);
        EditText passwordInput = findViewById(R.id.signup_password);
        EditText confirmPasswordInput = findViewById(R.id.signup_confirm_password);
        EditText addressInput = findViewById(R.id.signup_address);
        EditText ageInput = findViewById(R.id.signup_age);
        Button signUpButton = findViewById(R.id.signup_button);
        TextView statusText = findViewById(R.id.signup_status_text);
        TextView goToLogin = findViewById(R.id.go_to_login);
        View glowView = findViewById(R.id.signup_glow_view);

        // אנימציית הילה
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

        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPass = confirmPasswordInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String age = ageInput.getText().toString().trim();

            // בדיקת שדות ריקים
            if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || address.isEmpty() || age.isEmpty()) {
                statusText.setText("Please fill all fields");
                statusText.setTextColor(android.graphics.Color.RED);
                return;
            }

            // בדיקת התאמת סיסמאות
            if (!password.equals(confirmPass)) {
                statusText.setText("Passwords do not match");
                statusText.setTextColor(android.graphics.Color.RED);
                return;
            }

            // הרשמה ל-Firebase
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            statusText.setText("Registration Successful!");
                            statusText.setTextColor(0xFF39FF14);
                            // כאן בהמשך אפשר לשמור את הכתובת והגיל ב-Firestore
                        } else {
                            statusText.setText("Error: " + task.getException().getMessage());
                            statusText.setTextColor(android.graphics.Color.RED);
                        }
                    });
        });

        goToLogin.setOnClickListener(v -> finish());
    }
}