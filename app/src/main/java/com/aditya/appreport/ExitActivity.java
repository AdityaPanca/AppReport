package com.aditya.appreport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        Button exit =(Button) findViewById(R.id.ExitButton);
        Button reupload  =(Button) findViewById(R.id.ReloadButton);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        reupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reupload = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(reupload);
            }
        });
    }
}