package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.classes.Question;
import com.example.myapplication.hintsPackage.HintRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayTheQuizz extends AppCompatActivity implements RecyclerViewClick {
    private DatabaseReference mDatabase;
    private Toolbar appbar;
    private TextView start,total,question;
    private  AlertDialog.Builder builder;
    private static String Subject;
    private  int nbr =0;
    private static ArrayList<Question> myQuestions;
    private RecyclerView myHints;
    private Button next;
    private List<String> CheckResponse;
    private static String CorrectResponse="";
    private boolean answer =false;
    private static int points =0,newpoints =0,wrong=0,Solved=0;
    LottieAnimationView oups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_the_quizz);

        start = findViewById(R.id.start);
        total = findViewById(R.id.total);
        builder= new AlertDialog.Builder(this);
        appbar = findViewById(R.id.toolbar);
        Subject =getIntent().getStringExtra("Value");
        appbar.setTitle(""+Subject);
        setSupportActionBar(appbar);
        myQuestions = new ArrayList<>();
        myHints = findViewById(R.id.recyclehints);
        CheckResponse = new ArrayList<>();
        oups = findViewById(R.id.oups);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        question = (TextView) findViewById(R.id.thequestion);

        SetupQuestionAndAnswers(Subject);
        updateScore();
        next  = findViewById(R.id.nextQuestion);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // gonnext();

            }
        });


    }
    private void updateScore() {
        mDatabase.child("Points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points = Integer.parseInt(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("Number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             Solved = Integer.parseInt(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonnext() {
        if(nbr <= 8 && answer){
            myQuestions.clear();
            nbr+=1;
            answer =false;
            SetupQuestionAndAnswers(Subject);

        }else {
            Toast.makeText(PlayTheQuizz.this, "Choose An Answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        myHints.setLayoutManager(new LinearLayoutManager(this));
        String[] incorrecttosend;

        incorrecttosend = myQuestions.get(nbr).getIncorrect().toArray(new String[0]);
        List<String> list = new ArrayList<>(Arrays.asList(incorrecttosend));

        list.add(myQuestions.get(nbr).getResponse());
        Collections.shuffle(list);
        CorrectResponse =myQuestions.get(nbr).getResponse();
        String[] send = list.toArray(new String[0]);
        CheckResponse= Arrays.asList(send);
        HintRecyclerView MyAdapter = new HintRecyclerView(this,send,this);
        myHints.setAdapter(MyAdapter);
        question.setText(""+myQuestions.get(nbr).getQuestion().toString());

    }

    private void SetupQuestionAndAnswers(String subject) {
        String val="";
     if(subject.equals("Animals")){
         val="Anim";
     }else if(subject.equals("Music")){
         val="Music";
     }else if(subject.equals("Sport")){
         val = "Sport";
        }else if(subject.equals("Video Games")){
         val="VG";
     }
        mDatabase.child(""+val).child("results").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  for (DataSnapshot snapsho : snapshot.getChildren()) {

                      String val=snapsho.child("correct").getValue().toString();
                      String val1= snapsho.child("question").getValue().toString();
                      ArrayList<String> strings = (ArrayList<String>) snapsho.child("incorrect").getValue();
                      myQuestions.add(new Question(val,strings,val1));

                  }
                  setupRecyclerView();
                  UpdateUi();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateUi() {
        start.setText(""+(nbr+1));
        total.setText(""+myQuestions.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.cancel){
          showAlertDialog();
        }
        return true;
    }

    private void showAlertDialog(){
        builder.setMessage("Are you Sure !") .setTitle("Quiett");

        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onItemClick(int position) {
        Log.v("TAG","Clicked !"+nbr);
                
       if(CheckResponse.get(position).equals(CorrectResponse) && nbr<=8){
         answer =true;
           wrong=0;
         gonnext();
         updatePoints();
        }else {
           if(nbr==9){

               int newscore = points+newpoints;
               mDatabase.child("Points").setValue(newscore);
               Solved+=1;
               mDatabase.child("Number").setValue(Solved);
               Intent i = new Intent(PlayTheQuizz.this,FinalScore.class);
               i.putExtra("Quizz",newscore);
               startActivity(i);
               //Toast.makeText(PlayTheQuizz.this, "No More Questions", Toast.LENGTH_SHORT).show();
           }else{
               wrong+=1;
               if(wrong==3){
                   myHints.setEnabled(false);
                   Toast.makeText(this, "Wrong Answer Take The Quizz Again", Toast.LENGTH_SHORT).show();
                   oups.setVisibility(View.VISIBLE);
                   new CountDownTimer(3000, 1000) {
                       @Override
                       public void onTick(long l) {

                       }

                       @Override
                       public void onFinish() {
                           wrong=0;
                           finish();
                       }
                   }.start();

               }else{
                   Toast.makeText(this, "Wrong Answer Still "+(3-wrong)+" Chances", Toast.LENGTH_SHORT).show();
               }

           }


        }
    }

    private void updatePoints() {
        newpoints+=25;
        next.setText(""+newpoints);
    }
}