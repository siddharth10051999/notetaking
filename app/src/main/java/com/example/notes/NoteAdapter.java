package com.example.notes;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewholder> {
    private static final String TAG = "Adapter";

    List<Integer> difColor = new ArrayList<>();
    FirebaseFirestore FStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
        init();
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewholder noteViewholder, int i, @NonNull Note note) {
        noteViewholder.noteTitle.setText(note.getTitle());
        noteViewholder.noteContent.setText(note.getContent());
        final int tempColor = getDifferentColor();
        final String Id = getSnapshots().getSnapshot(i).getId();
        noteViewholder.cardView.setCardBackgroundColor(noteViewholder.view.getResources().getColor(tempColor, null));
        noteViewholder.view.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NoteContent.class);
            intent.putExtra("title", note.getTitle());
            intent.putExtra("content", note.getContent());
            intent.putExtra("color", tempColor);
            intent.putExtra("Id", Id);
            v.getContext().startActivity(intent);
        });
        noteViewholder.imageView.setOnClickListener(v -> {
            popupMenu(v, note ,Id);
        });
    }

    protected void popupMenu(View v, Note note ,String Id){
        Log.e(TAG, "onBindViewHolder:  imageView");
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(item -> {
            Log.e(TAG, "onBindViewHolder:  imageView Edit");
            Intent Data = new Intent(v.getContext(), EditNote.class);
            Data.putExtra("title", note.getTitle());
            Data.putExtra("content", note.getContent());
            Data.putExtra("Id", Id);
            v.getContext().startActivity(Data);
            return false;
        });

        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(item -> {

            deleteNode(Id);
            return false;
        });
        popupMenu.setGravity(Gravity.END);
        popupMenu.show();
    }


    protected void deleteNode(String Id)
    {
        Log.e(TAG, "onBindViewHolder:  imageView Delete");
        DocumentReference documentReference = FStore.collection("Notes").document(user.getUid()).collection("myNotes").document(Id);
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }


    private int getDifferentColor() {
        Random rand = new Random();
        int index = rand.nextInt(difColor.size());
        Log.e(TAG, "getDifferentColor: " + index + " " + difColor.size());
        return difColor.get(index);
    }


    @NonNull
    @Override
    public NoteViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
        return new NoteAdapter.NoteViewholder(view);
    }

    public class NoteViewholder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteContent;
        View view;
        CardView cardView;
        ImageView imageView;
        String id;
        public NoteViewholder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.content);
            noteTitle = itemView.findViewById(R.id.titles);
            cardView = itemView.findViewById(R.id.noteCard);
            imageView = itemView.findViewById(R.id.menuIcon);
            FStore = FirebaseFirestore.getInstance();
            firebaseAuth=FirebaseAuth.getInstance();
            user= firebaseAuth.getCurrentUser();
            view = itemView;
        }
    }


    private void init() {
        difColor.add(R.color.blue);
        difColor.add(R.color.skyBlue);
        difColor.add(R.color.yellow);
        difColor.add(R.color.pink);
        difColor.add(R.color.lightPurple);
        difColor.add(R.color.gray);
        difColor.add(R.color.red);
        difColor.add(R.color.greenLight);
        difColor.add(R.color.lightGreen);
        difColor.add(R.color.notGreen);
    }

}
