package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements RecyclerViewClick{

    private TextView out;
    private Toolbar appbar;
    private FirebaseAuth mAuth;
    private ImageView profileImage;
    private TextView username;
    SharedPreferences myshared;
    private DatabaseReference mDatabase;
    private int val;
    private String PlayerName;
    private String[] Subjects ={"Animals","Music","Sport","Video Games"};
    private int[] Images ={R.drawable.animals,R.drawable.music,R.drawable.footbal,R.drawable.vg};
    private RecyclerView recyclerView;
    private TextView ponts,score;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appbar = findViewById(R.id.toolbar);
        appbar.setTitle("Quizz App");
        setSupportActionBar(appbar);
        myshared = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyleView);
        dialog = new Dialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        profileImage= findViewById(R.id.userimage);
        username = findViewById(R.id.username);
        val = myshared.getInt("Normal-Login",0);
        PlayerName =myshared.getString("Player-Name","Player");

        ponts = findViewById(R.id.points);
        score = findViewById(R.id.score);

        if(val==100){
            profileImage.setBackgroundResource(R.drawable.spong);
            username.setText(""+PlayerName);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapter MyAdapter = new RecyclerViewAdapter(this, this, Subjects, Images);
        recyclerView.setAdapter(MyAdapter);
        if(isconnected()){
            updateScore();
        }else{
            dialog.setContentView(R.layout.niinternet);
            dialog.show();
        }


    }

    private void updateScore() {
        mDatabase.child("Points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ponts.setText(""+snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("Number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                score.setText(""+snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null && currentUser.getDisplayName() !=""){
            username.setText(currentUser.getDisplayName());
            Glide.with(this).load(currentUser.getPhotoUrl()).into(profileImage);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbarmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            mDatabase.child("Points").setValue(0);
            mDatabase.child("Number").setValue(0);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginScreen.class));
            SharedPreferences.Editor editor = myshared.edit();
            editor.putInt("Normal-Login",0);
            editor.commit();
            //updateUI(user);
        }else if(id == R.id.About){
            showDialog();
        }else  if(id == R.id.share){
            Toast.makeText(getApplicationContext(),"Share Selected !",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showDialog() {
        dialog.setContentView(R.layout.aboutlayout);
        ImageView out = dialog.findViewById(R.id.out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onItemClick(int position) {
        if(isconnected()){
            Intent i = new Intent(MainActivity.this,PlayTheQuizz.class);
            i.putExtra("Value",""+Subjects[position]);
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(),"There is no Internet",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isconnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfomobile =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (networkInfo != null && networkInfo.isConnected() || networkInfomobile !=null && networkInfomobile.isConnected()){
            return  true;
        }else{
            return false;
        }
    }
}