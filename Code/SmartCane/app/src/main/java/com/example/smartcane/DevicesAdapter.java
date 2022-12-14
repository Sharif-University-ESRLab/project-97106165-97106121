package com.example.smartcane;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {

    private ArrayList<Pair<String, String>> devices;
    private ItemClickListener clickListener;
    public DevicesAdapter(ArrayList<Pair<String, String>> devices){
        this.devices = devices;
    }

    @NonNull
    @Override
    public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.device_view, parent, false);
        DevicesViewHolder devicesViewHolder = new DevicesViewHolder(constraintLayout);
        return devicesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesViewHolder holder, int position) {
        holder.devName.setText(this.devices.get(position).first);
        holder.devAddress.setText(this.devices.get(position).second);
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }

    public class DevicesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView devName;
        TextView devAddress;
        public DevicesViewHolder(@NonNull ConstraintLayout constraintLayout) {
            super(constraintLayout);
            devName = constraintLayout.findViewById(R.id.device_name);
            devAddress = constraintLayout.findViewById(R.id.device_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }
}
