package com.emre.footballersbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emre.footballersbook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class FootballAdapter extends RecyclerView.Adapter<FootballAdapter.FootballHolder> {

    ArrayList<Football> footballarrayList;

    public FootballAdapter(ArrayList<Football> footballarrayList) {
        this.footballarrayList = footballarrayList;

    }

    @NonNull
    @Override
    public FootballHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false);

        return new FootballHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FootballHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind.recyclerViewTextView.setText(footballarrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), FootballersDetail.class);
                intent.putExtra("info", "old");
                intent.putExtra("footballersid", footballarrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return footballarrayList.size();
    }

    public class FootballHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding bind;


        public FootballHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.bind = binding;
        }
    }


}
