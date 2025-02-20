package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Model.ProfileButtonModel;
import com.example.aptofam.R;

import java.util.List;

public class ProfileAccUsfAdapter extends RecyclerView.Adapter<ProfileAccUsfAdapter.ViewHolder>{
    private final List<ProfileButtonModel> buttonList;
    private final Context context;
    public ProfileAccUsfAdapter(List<ProfileButtonModel> buttonList, Context context) {
        this.buttonList = buttonList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileAccUsfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_profile_account, parent, false);
        return new ProfileAccUsfAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAccUsfAdapter.ViewHolder holder, int position) {
        ProfileButtonModel button = buttonList.get(position);

        holder.buttonTextAcc.setText(button.getText());
        holder.buttonIconAcc.setImageResource(button.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (button.getTargetActivity() != null) {
                Intent intent = new Intent(context, button.getTargetActivity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buttonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView buttonTextAcc;
        ImageView buttonIconAcc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonTextAcc = itemView.findViewById(R.id.titleProfileAccUsf);
            buttonIconAcc = itemView.findViewById(R.id.iconProfileAccUsf);
        }
      }
    }
