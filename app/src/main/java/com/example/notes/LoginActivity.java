package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    EditText email, password;
    TextView createAccount;
    Button login;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    ProgressBar progressBar;
    String passwordString, emailString;
    private int flag=0;
    private static final String TAG = "LoginActivity";
    Map<String,Note> map= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login to Notes");
        init();
        showWarning();
        onClickcreateAccount();
        onClickLogin();
    }

    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("You are logged in with Temporary Account. Logging in will Delete All the Notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
                        data();
                        flag=0;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).setNegativeButton("Don't Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag=1;
                    }
                });
        warning.show();
    }


    private void onClickLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkingUserInput();
                progressBar.setVisibility(View.VISIBLE);

                if (flag==1)
                {
                    deleteCurrentUser();
                    user =FirebaseAuth.getInstance().getCurrentUser();
                    signInWithEmail();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else
                {
                    signInWithEmail();
                    user=FirebaseAuth.getInstance().getCurrentUser();

                }

            }

        });
    }

    private void createNode(String title_create, String content_create){
        Log.e(TAG, "onClick: before create");
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user.isAnonymous())
        {
            Log.e(TAG, "createNode: this is anonymous but it should be emailVerified");
        }
        DocumentReference documentReference = firebaseFirestore.collection("Notes").document(user.getUid()).collection("myNotes").document();
        Map<String, Object> m = new HashMap<>();
        m.put("title", title_create);
        m.put("content", content_create);
        Log.e(TAG, "onClick: after create "+ user.getEmail());
        documentReference.set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "createNode"+title_create + " "+content_create);
            }
        });
    }

    private void data(){
        firebaseFirestore.collection("Notes").document(user.getUid()).collection("myNotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.e(TAG, document.getId() +document.getData());
                            String s= document.getData().get("title").toString();
                            Note temp=new Note(document.getData().get("title").toString() , document.getData().get("content").toString());
                            map.put(document.getId(),temp) ;
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    private void signInWithEmail()
    {
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if(flag==0) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        for (Map.Entry<String, Note> entry : map.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            createNode(entry.getValue().getTitle(), entry.getValue().getContent());
                        }
                        Log.e(TAG, "onComplete: login" + user.getEmail());
                    }
                    else
                    {
                        deleteCurrentUser();
                    }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e(TAG, "onSuccess: login" );
            }
        });
    }

    private void deleteCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        deleteUserData(user);
        deleteUser(user);
    }

    private void deleteUser(FirebaseUser user) {
        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginActivity.this, "Temp user Deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Temp user are Deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserData(FirebaseUser user) {
        firebaseFirestore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginActivity.this, "All Temp Notes are Deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "All Temp Notes are not Deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkingUserInput() {
        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Field Are Required", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private void onClickcreateAccount() {
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.lPassword);
        login = findViewById(R.id.loginBtn);
        createAccount = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBarLogin);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

}