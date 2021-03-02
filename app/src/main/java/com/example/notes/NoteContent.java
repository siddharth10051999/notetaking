package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteContent extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ActionBar actionBar;
    TextView title;
    TextView content;
    ConstraintLayout constraintLayout;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        init();
        setMyActionBar();
        settingData();
        editButtonListener();
    }

    private void editButtonListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Data= new Intent(v.getContext(), EditNote.class );
                Data.putExtra("title",intent.getStringExtra("title"));
                Data.putExtra("content", intent.getStringExtra("content"));
                Data.putExtra("Id",intent.getStringExtra("Id"));
                startActivity(Data);
            }
        });
    }

    private void settingData() {
        intent= getIntent();
        content.setText(intent.getStringExtra("content"));
        title.setText(intent.getStringExtra("title"));
        constraintLayout.setBackgroundColor( getResources().getColor( intent.getIntExtra("color" , 0) , null));
    }

    private void setMyActionBar() {
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        actionBar=getSupportActionBar();
        title=findViewById(R.id.title_note_content);
        content=findViewById(R.id.content_note_content);
        constraintLayout=findViewById(R.id.note_content);
        floatingActionButton=findViewById(R.id.floating_button_edit);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}