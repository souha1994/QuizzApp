package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHlder> {

    private final RecyclerViewClick recyclerViewClick;
    Context context;
    String[] names;
    int[] images;
    public RecyclerViewAdapter(RecyclerViewClick recyclerViewClick, Context con, String[] names, int[] images){
        this.recyclerViewClick = recyclerViewClick;
        this.context = con;
        this.names = names;
        this.images = images;
    }
    @NonNull
    @Override
    public MyViewHlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recylelayout,parent,false);
        return new MyViewHlder(view,recyclerViewClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHlder holder, int position) {
      holder.image.setImageResource(images[position]);
      holder.subject.setText(names[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }




    public static class MyViewHlder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView subject;
        public MyViewHlder(@NonNull View itemView,RecyclerViewClick recyclerViewClick) {
            super(itemView);
            image = itemView.findViewById(R.id.imagesubject);
            subject = itemView.findViewById(R.id.subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewClick !=null){

                        int position = getAdapterPosition();
                        if (position !=RecyclerView.NO_POSITION){
                            recyclerViewClick.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
