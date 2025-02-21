package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Equipment;
import com.example.fithit.R;

import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private List<Equipment> equipmentList;
    private OnEquipmentSelectionChangedListener listener;

    public interface OnEquipmentSelectionChangedListener {
        void onEquipmentSelectionChanged(Equipment equipment, boolean isSelected);
    }

    public EquipmentAdapter(List<Equipment> equipmentList,
                            OnEquipmentSelectionChangedListener listener) {
        this.equipmentList = equipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipment, parent, false);
        return new EquipmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        holder.bind(equipmentList.get(position));
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    public void updateEquipmentList(List<Equipment> newList) {
        this.equipmentList = newList;
        notifyDataSetChanged();
    }

    static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivEquipmentImage;
        private TextView tvEquipmentName;
        private CheckBox cbEquipmentSelected;
        private OnEquipmentSelectionChangedListener listener;

        public EquipmentViewHolder(@NonNull View itemView,
                                   OnEquipmentSelectionChangedListener listener) {
            super(itemView);
            this.listener = listener;

            ivEquipmentImage = itemView.findViewById(R.id.iv_equipment_image);
            tvEquipmentName = itemView.findViewById(R.id.tv_equipment_name);
            cbEquipmentSelected = itemView.findViewById(R.id.cb_equipment_selected);
        }

        public void bind(Equipment equipment) {
            // Set equipment name
            tvEquipmentName.setText(equipment.getDisplayName());

            // Set default image (replace with actual logic)
            ivEquipmentImage.setImageResource(equipment.getImageResourceId());

            // Set checkbox state
            cbEquipmentSelected.setChecked(equipment.isSelected());

            // Checkbox change listener
            cbEquipmentSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                equipment.setSelected(isChecked);
                if (listener != null) {
                    listener.onEquipmentSelectionChanged(equipment, isChecked);
                }
            });

            // Item view click listener to toggle checkbox
            itemView.setOnClickListener(v -> {
                cbEquipmentSelected.setChecked(!cbEquipmentSelected.isChecked());
            });
        }
    }
}