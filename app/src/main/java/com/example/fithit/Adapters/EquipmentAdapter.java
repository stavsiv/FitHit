package com.example.fithit.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Models.Equipment;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private List<Equipment> equipmentList;
    private OnEquipmentSelectionChangedListener listener;

    public interface OnEquipmentSelectionChangedListener {
        void onEquipmentSelectionChanged(Equipment equipment, boolean isSelected);
    }

    // Constructor to create a list of all equipment types
    public EquipmentAdapter(OnEquipmentSelectionChangedListener listener) {
        this.equipmentList = new ArrayList<>();
        for (EquipmentType type : EquipmentType.values()) {
            this.equipmentList.add(new Equipment(type));
        }
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

    // Updated method to handle List<Equipment>
   /* public void updateEquipmentList(List<Equipment> newList) {
        this.equipmentList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }*/

    // Method to update equipment based on selected status
    public void updateEquipmentSelection(List<String> selectedEquipmentNames) {
        for (Equipment equipment : equipmentList) {
            equipment.setSelected(selectedEquipmentNames.contains(equipment.getDisplayName()));
        }
        notifyDataSetChanged();
    }
    private static void loadImageEfficiently(ImageView imageView, int resourceId) {
        try {
            // Create options to decode the bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();

            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(imageView.getResources(), resourceId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 200, 200); // Adjust size as needed

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Uses less memory
            Bitmap bitmap = BitmapFactory.decodeResource(imageView.getResources(), resourceId, options);

            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("EquipmentAdapter", "Error loading image: " + e.getMessage());
            // Fallback to a small default image
            try {
                imageView.setImageResource(R.drawable.default_image); // Replace with an actual small icon
            } catch (Exception ex) {
                // Last resort, don't set any image
                Log.e("EquipmentAdapter", "Could not set fallback image: " + ex.getMessage());
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
            tvEquipmentName.setText(equipment.getDisplayName());

            loadImageEfficiently(ivEquipmentImage, equipment.getImageResourceId());

            cbEquipmentSelected.setChecked(equipment.isSelected());
            cbEquipmentSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                equipment.setSelected(isChecked);
                if (listener != null) {
                    listener.onEquipmentSelectionChanged(equipment, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                cbEquipmentSelected.setChecked(!cbEquipmentSelected.isChecked());
            });
        }
    }

    public List<Equipment> getSelectedEquipment() {
        List<Equipment> selectedEquipment = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            if (equipment.isSelected()) {
                selectedEquipment.add(equipment);
            }
        }
        return selectedEquipment;
    }
}