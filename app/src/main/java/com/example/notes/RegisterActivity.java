package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    EditText username, userEmail, userPassword, userPasswordConform;
    Button createAccount;
    TextView loginAccount;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    String usernameString;
    String userEmailString;
    String userPasswordString;
    String userPasswordConformString;

    private static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register Account");
        init();
        onClickLoginAccount();
        onClickCreateAccount();
    }

    private void onClickCreateAccount() {
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameString=username.getText().toString();
                userEmailString=userEmail.getText().toString();
                userPasswordString=userPassword.getText().toString();
                userPasswordConformString=userPasswordConform.getText().toString();
                CheckInput();
                RegisterUser();
            }
        });

    }

    private void CheckInput() {
        if(userEmailString.isEmpty() || usernameString.isEmpty()|| userPasswordConformString.isEmpty()|| userPasswordString.isEmpty()){
            Log.e(TAG, "onClick: enter all the fields");
            Toast.makeText(RegisterActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!userPasswordConformString.equals(userPasswordString)){
            userPasswordConform.setError("password don't match");
        }
    }

    private void RegisterUser() {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential authCredential= EmailAuthProvider.getCredential(userEmailString,userPasswordString);
        firebaseAuth.getCurrentUser().linkWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e(TAG, "onSuccess: note are sync");
                FirebaseUser user=firebaseAuth.getCurrentUser();
                UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(userEmailString).build();
                user.updateProfile(request);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: note are not sync"+ e.getMessage());
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void onClickLoginAccount() {
        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void init() {
        username=findViewById(R.id.userName);
        userEmail=findViewById(R.id.userEmail);
        userPassword=findViewById(R.id.password);
        userPasswordConform=findViewById(R.id.passwordConfirm);
        createAccount=findViewById(R.id.createAccount);
        loginAccount=findViewById(R.id.login);
        progressBar=findViewById(R.id.progressBarRegister);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

}