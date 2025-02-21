package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Equipment;
import com.example.fithit.R;

import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private List<Equipment> equipmentList;
    private OnEquipmentClickListener listener;

    public interface OnEquipmentClickListener {
        void onEquipmentClick(Equipment equipment);
    }

    public EquipmentAdapter(List<Equipment> equipmentList, OnEquipmentClickListener listener) {
        this.equipmentList = equipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipment, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);
        holder.bind(equipment, listener);
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    public void updateEquipment(List<Equipment> newEquipment) {
        this.equipmentList = newEquipment;
        notifyDataSetChanged();
    }

    static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEquipmentName;
        private final TextView tvImage;
        private final ImageButton btnRemove;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipmentName = itemView.findViewById(R.id.tv_equipment_name);
            tvImage = itemView.findViewById(R.id.tv_equipment_image);
            btnRemove = itemView.findViewById(R.id.btn_remove_equipment);
        }

        public void bind(Equipment equipment, OnEquipmentClickListener listener) {
            tvEquipmentName.setText(equipment.getDisplayName());
            tvImage.setText(equipment.getImageResource());

            itemView.setOnClickListener(v -> listener.onEquipmentClick(equipment));
            btnRemove.setOnClickListener(v -> {
                // Handle remove equipment
            });
        }
    }
}