package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    EditText title;
    EditText content;
    ActionBar actionBar;
    FirebaseFirestore FStore;
    ProgressBar progressBar;
    FirebaseUser user;
    private static final String TAG = "CreateNote";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        init();
        onClickFloatingButton();
    }


    private void onClickFloatingButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String title_create = title.getText().toString();
                String content_create = content.getText().toString();
                if (title_create.isEmpty()) {
                    title_create = "untitled";
                }
                if (content_create.isEmpty()) {
                    content_create = " ";
                }
                createNode(title_create, content_create);
            }
        });
    }


    private void createNode(String title_create, String content_create){
        Log.e(TAG, "onClick: before create");
        DocumentReference documentReference = FStore.collection("Notes").document(user.getUid()).collection("myNotes").document();
        Map<String, Object> map = new HashMap<>();
        map.put("title", title_create);
        map.put("content", content_create);

        Log.e(TAG, "onClick: after create");
        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: ");
                Toast.makeText(CreateNote.this, "Note Added", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
                Toast.makeText(CreateNote.this, "Try Again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        getSupportActionBar().setTitle("create Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        floatingActionButton = findViewById(R.id.floatingActionButtonCreate);
        title = findViewById(R.id.title_create);
        content = findViewById(R.id.text_create);
        FStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}