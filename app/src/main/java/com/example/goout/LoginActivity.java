package com.example.goout;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

/**
 * מסך התחברות המנהל גם את המעבר לפרגמנט ההרשמה.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private View loginFormContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // אתחול Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // קישור רכיבים מה-XML
        loginFormContainer = findViewById(R.id.login_main_layout); // ודאי שהוספת ID זה למיכל הראשי ב-XML
        EditText email = findViewById(R.id.login_email);
        EditText input = findViewById(R.id.login_password);
        Button button = findViewById(R.id.login_button);
        TextView text = findViewById(R.id.status_text);
        TextView btnGoToRegister = findViewById(R.id.go_to_register);

        // לוגיקת כפתור התחברות
        button.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = input.getText().toString().trim();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                text.setText("Please enter email and password");
                text.setTextColor(android.graphics.Color.RED);
                return;
            }

            auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            text.setText("Login successful!");
                            text.setTextColor(0xFF39FF14);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            text.setText("Error: " + task.getException().getMessage());
                            text.setTextColor(android.graphics.Color.RED);
                        }
                    });
        });

        // החלפת תצוגת הלוגין בפרגמנט ההרשמה
        btnGoToRegister.setOnClickListener(view -> {
            if (loginFormContainer != null) {
                // הסתרת הטופס הקיים
                loginFormContainer.setVisibility(View.GONE);

                // טעינת הפרגמנט לתוך המיכל (נשתמש ב-android.R.id.content כברירת מחדל אם אין קונטיינר ספציפי)
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SignUpFragment())
                        .addToBackStack(null) // מאפשר חזרה למסך הלוגין בלחיצת כפתור חזור
                        .commit();
            }
        });
    }

    /**
     * ניהול לחיצה על כפתור חזור - אם אנחנו בפרגמנט הרשמה, נחזור ללוגין
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (loginFormContainer != null) loginFormContainer.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}