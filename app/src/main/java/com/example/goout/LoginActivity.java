package com.example.goout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.initializeApp(this);
        EditText input = findViewById(R.id.input);
        TextView text = findViewById(R.id.text);
        Button button  = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        EditText email = findViewById(R.id.email);

        button.setOnClickListener(v ->{ ;
        text.setText(input.getText());




        });
        button2.setOnClickListener(view -> {
            Intent intent  = new Intent( LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }

}