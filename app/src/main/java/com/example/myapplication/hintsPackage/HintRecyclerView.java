package com.example.myapplication.hintsPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import com.example.myapplication.RecyclerViewClick;


public class HintRecyclerView extends RecyclerView.Adapter<HintRecyclerView.MyViewHlder>{
    private final RecyclerViewClick recyclerViewClick;
    Context context;
    String[] hints;

    public HintRecyclerView(Context context ,String[] hints ,RecyclerViewClick recyclerViewClick) {
        this.context = context;
        this.hints=hints;
        this.recyclerViewClick = recyclerViewClick;
    }


    @NonNull
    @Override
    public MyViewHlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.hints,parent,false);
        return new HintRecyclerView.MyViewHlder(view,recyclerViewClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHlder holder, int position) {
        holder.Question.setText(hints[position]);
    }

    @Override
    public int getItemCount() {
        return hints.length;
    }

    public class MyViewHlder extends RecyclerView.ViewHolder {
        ImageView ok,no;
        TextView Question;
        public MyViewHlder(@NonNull View itemView,RecyclerViewClick recyclerViewClick) {
            super(itemView);
            Question = itemView.findViewById(R.id.texthint);
            ok = itemView.findViewById(R.id.imageok);
            no = itemView.findViewById(R.id.imageno);
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
