package com.example.goout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText input = findViewById(R.id.input);
        TextView text = findViewById(R.id.text);
        Button button  = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        button.setOnClickListener(v ->{ ;
        text.setText(input.getText());


        });
        button2.setOnClickListener(view -> {
            Intent intent  = new Intent( MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

    }

}