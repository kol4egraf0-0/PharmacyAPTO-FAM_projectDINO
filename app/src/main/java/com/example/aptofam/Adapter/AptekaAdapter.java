package com.example.aptofam.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Model.ApteksModel;
import com.example.aptofam.R;

import java.util.List;

public class AptekaAdapter extends RecyclerView.Adapter<AptekaAdapter.ViewHolder> {

    private List<ApteksModel> aptekaList;

    public AptekaAdapter(List<ApteksModel> aptekaList) {
        this.aptekaList = aptekaList;
    }


    @NonNull
    @Override
    public AptekaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_item_apteka, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AptekaAdapter.ViewHolder holder, int position) {
        ApteksModel apteka = aptekaList.get(position);

        holder.aptekaName.setText(apteka.getAptekaName());
    }

    @Override
    public int getItemCount() {
        return aptekaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView aptekaName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aptekaName = itemView.findViewById(R.id.aptekaName);
        }
    }
}
