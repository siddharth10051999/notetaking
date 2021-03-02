package com.example.notes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "mainActivity";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    View headerView;
    RecyclerView noteList;
    NoteAdapter noteAdapter;
    FloatingActionButton floatingActionButton;
    FirebaseFirestore FStore;
    FirebaseUser user;
    FirebaseAuth fAuth;
    TextView userName,emailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        settingNavBar();
        setAdapter();
        setListener();

        if(user.isAnonymous())
        {
            userName.setText("Temp");
            emailId.setText("Temporary User");
        }
        else{
            userName.setText(user.getDisplayName());
            emailId.setText(user.getEmail());
        }
    }


    public void setListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreate(v);
            }
        });
    }

    public void openCreate(View view) {
        Intent myIntent = new Intent(view.getContext(), CreateNote.class);
        startActivity(myIntent);
    }

    public void setAdapter() {
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);
    }

    private void settingNavBar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void init() {
        FStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        Query query = FStore.collection("Notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        noteAdapter = new NoteAdapter(allNotes);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer);
        noteList = findViewById(R.id.notelist);
        floatingActionButton = findViewById(R.id.floatingActionButtonMain);
        headerView=navigationView.getHeaderView(0);
        userName=headerView.findViewById(R.id.UserNameText);
        emailId=headerView.findViewById(R.id.emailIdText);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.addNotes:
                Intent myIntent = new Intent(this, CreateNote.class);
                startActivity(myIntent);
                break;
            case R.id.sync:
                if(user.isAnonymous()){
                    finish();
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                }
                else
                {
                    Log.e(TAG, "onNavigationItemSelected: user is in account" );
                }
                break;
            case R.id.loginMenu:
                if(user.isAnonymous()){
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                else {
                    Log.e(TAG, "onNavigationItemSelected: user is in account" );
                }
                break;
            case R.id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            Intent data = new Intent(this, SplashActivity.class);
            startActivity(data);
        }
    }


    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("You are logged in with Temporary Account. Logging out will Delete All the notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCurrent();
                    }
                });
        warning.show();
    }


    public void deleteCurrent(){
        FirebaseUser user = fAuth.getCurrentUser();

        FStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "All Temp Notes are Deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "All Temp Notes are not Deleted: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // delete Temp user

        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Temp user Deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Temp user are Deleted: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

}