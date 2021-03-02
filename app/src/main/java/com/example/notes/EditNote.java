package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class EditNote extends AppCompatActivity {

    Intent intent;
    EditText title;
    EditText content;
    FirebaseFirestore FStore;
    ProgressBar progressBar;
    FirebaseUser user;
    private static final String TAG = "EditNote";
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        init();
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content"));
        onClickFloatingButton();
    }

    private void onClickFloatingButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String titleText=title.getText().toString();
                String contentText=content.getText().toString();
                if(titleText.isEmpty())
                {
                    titleText="untitled";
                }
                if(contentText.isEmpty())
                {
                    contentText=" ";
                }

                editNode(titleText,contentText);
            }
        });
    }

    protected void editNode(String titleText, String contentText){
        Log.e(TAG, "onClick: before create" );
        DocumentReference documentReference=FStore.collection("Notes").document(user.getUid()).collection("myNotes").document(intent.getStringExtra("Id"));
        Map<String , Object> map=new HashMap<>();
        map.put("title", titleText);
        map.put("content", contentText);

        Log.e(TAG, "onClick: after create" );
        documentReference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: " );
                Toast.makeText(EditNote.this, "Note Added", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class ));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ",e );
                Toast.makeText(EditNote.this, "Try Again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void init(){
        intent=getIntent();
        title=findViewById(R.id.editTitle);
        content=findViewById(R.id.editContent);
        floatingActionButton=findViewById(R.id.floatingActionButtonEdit);
        FStore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBarEdit);
        user= FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Note");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

}