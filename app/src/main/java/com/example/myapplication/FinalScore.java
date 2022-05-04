package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinalScore extends AppCompatActivity {

    private Button back;
    private TextView pnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_score);

        back = findViewById(R.id.back);
        pnt = findViewById(R.id.points);
        Intent intent = getIntent();
        int val =intent.getIntExtra("Quizz",0);
        pnt.setText(""+val+" Points");


        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(FinalScore.this,MainActivity.class));
            }
        }.start();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalScore.this,MainActivity.class));
            }
        });
    }
}