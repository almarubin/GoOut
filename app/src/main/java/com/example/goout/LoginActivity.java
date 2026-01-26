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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // ודאי שזה השם של ה-XML שלך
        // אתחול Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // קישור רכיבים מה-XML
        EditText email = findViewById(R.id.login_email);
        EditText input = findViewById(R.id.login_password);
        Button button = findViewById(R.id.login_button);

        // תיקון: מקשרים את ה-ID הקיים ב-XML (status_text) למשתנה שנקרא לו 'text'
        TextView text = findViewById(R.id.status_text);

        // תיקון: מקשרים את ה-ID הקיים ב-XML (go_to_register) למשתנה 'button2'
        TextView button2 = findViewById(R.id.go_to_register);

        View glowView = findViewById(R.id.glow_view);
        ImageView logo = findViewById(R.id.login_logo);



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
                            text.setTextColor(0xFF39FF14); // ירוק ניאון
                        } else {
                            text.setText("Error: " + task.getException().getMessage());
                            text.setTextColor(android.graphics.Color.RED);
                        }
                    });
        });

        // מעבר לעמוד הרשמה
        button2.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

}