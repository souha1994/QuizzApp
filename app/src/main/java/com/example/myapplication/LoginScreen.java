package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Random;

public class LoginScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private Button login,nrmllogin;
    private GoogleSignInClient mGoogleSignInClient;
    View Decoreview;
    Dialog dialog;
    TextView Signup;
    EditText Email,password;
    SharedPreferences myShared;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        myShared = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.google_button);
        nrmllogin = (Button) findViewById(R.id.login);
        Email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Signup = findViewById(R.id.signup);
        progress = findViewById(R.id.progress);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isconnected()){
                    showDiag();
                }else{
                    Toast.makeText(getApplicationContext(),"There is No Internet",Toast.LENGTH_SHORT).show();
                }

            }
        });

        nrmllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isconnected()){
                    if(Email.getText().toString().equals("") || password.getText().toString().equals("") ){
                        Toast.makeText(getApplicationContext(),"Invalid Data !",Toast.LENGTH_LONG).show();
                    }else{
                        loginwithfirebase(Email.getText().toString(),password.getText().toString());

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"There is No Internet",Toast.LENGTH_SHORT).show();
                }

            }
        });


        dialog = new Dialog(this);


        Createrequest();
        setScreenFull();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isconnected()){
                    signIn();

                }else{
                    Toast.makeText(getApplicationContext(),"There is No Internet",Toast.LENGTH_SHORT).show();
                }

            }


        });
    }

    private void loginwithfirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Random generator = new Random();


                            int myrandomnumber = generator.nextInt()%9999+1;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor editor = myShared.edit();
                            editor.putInt("Normal-Login",100);
                            editor.putString("Player-Name","Player"+myrandomnumber);
                            editor.commit();
                            nrmllogin.setVisibility(View.INVISIBLE);
                            progress.setVisibility(View.VISIBLE);
                            new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    Intent i = new Intent(LoginScreen.this,MainActivity.class);
                                    startActivity(i);
                                }
                            }.start();


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }


    private void showDiag() {
        dialog.setContentView(R.layout.signup);
        Button login = dialog.findViewById(R.id.login);
        TextView signin = dialog.findViewById(R.id.signin);
        EditText username = dialog.findViewById(R.id.username);
        EditText emailo = dialog.findViewById(R.id.email);
        EditText password = dialog.findViewById(R.id.password);
        EditText confirmpassword = dialog.findViewById(R.id.Rpassword);

     login.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            if(TextUtils.isEmpty(username.getText().toString()) ||
                       TextUtils.isEmpty(emailo.getText().toString()) ||
                          TextUtils.isEmpty(password.getText().toString()) ||
                             TextUtils.isEmpty(confirmpassword.getText().toString()) ){
                 Toast.makeText(getApplicationContext(),"Missing information !",Toast.LENGTH_SHORT).show();
             }else{
                if(isValidEmail(emailo.getText().toString())){
                    if(password.getText().toString().equals(confirmpassword.getText().toString())){
                        //Work Goes Here
                      mAuth.createUserWithEmailAndPassword(emailo.getText().toString(),password.getText().toString())
                              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if(task.isSuccessful()){
                                          Toast.makeText(getApplicationContext(),"SignUp Succeed !",Toast.LENGTH_SHORT).show();
                                          dialog.dismiss();
                                      }else{
                                          Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                          Toast.makeText(getApplicationContext(),"SignUp Failed  !",Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              });

                    }else{
                        Toast.makeText(getApplicationContext(),"Password Incorrect !",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Email  !",Toast.LENGTH_SHORT).show();
                }


             }
         }
     });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(LoginScreen.this,MainActivity.class));
            Log.v("TAG","User Already Signed in");
        }
    }



    private void Createrequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginScreen.this,MainActivity.class));

                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }



    private void setScreenFull() {
        Decoreview = getWindow().getDecorView();
        Decoreview.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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