package com.example.elevent;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scanTest = findViewById(R.id.scan_test);
        scanTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
                startActivity(intent);
            }
        });

        // these buttons are just for testing if the scanner and generator work
        Button genTest = findViewById(R.id.gen_test);
        genTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GenerateQRCodeActivity.class);
                startActivity(intent);
            }
        });
    }
}