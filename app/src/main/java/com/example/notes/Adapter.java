package com.example.notes;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private static final String TAG = "Adapter";
    List<String> title;
    List<String> content;
    List<Integer> difColor = new ArrayList<>();

    Adapter(List<String> title, List<String> content) {
        this.title = title;
        this.content  = content;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.noteTitle.setText(title.get(position));
        holder.noteCaption.setText(content.get(position));
        int tempColor=getDifferentColor();
        holder.cardView.setCardBackgroundColor(holder.view.getResources().getColor(tempColor,null));

        holder.view.setOnClickListener(v -> {

            Intent intent =new Intent(v.getContext(), NoteContent.class);
            intent.putExtra("title", title.get(position));
            intent.putExtra("content", content.get(position));
            intent.putExtra("color", tempColor);
            intent.putExtra("id", position);
            v.getContext().startActivity(intent);
        });
    }

    private int getDifferentColor() {
        Random rand = new Random();
        int index = rand.nextInt(difColor.size());
        Log.e(TAG, "getDifferentColor: " + index+ " " + difColor.size());
        return difColor.get(index);
    }

    @Override
    public int getItemCount() {
        return content.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteCaption;
        View view;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteCaption = itemView.findViewById(R.id.content);
            noteTitle = itemView.findViewById(R.id.titles);
            cardView = itemView.findViewById(R.id.noteCard);
            view = itemView;

        }
    }

    private void init(){
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
