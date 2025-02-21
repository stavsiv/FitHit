//package com.example.fithit.Models;
//
//import com.example.fithit.Enums.EquipmentType;
//
//
////public class Equipment {
////    private EquipmentType equipmentType;
////
////    public Equipment(EquipmentType equipmentType) {
////        this.equipmentType = equipmentType;
////    }
////
////    public String getImageResource() {
////        return equipmentType.getImageFileName();
////    }
////
////    public String getDisplayName() {
////        return equipmentType.getDisplayName();
////    }
////
////    public String toString() {
////        return "Equipment{" +
////                "type=" + equipmentType.getDisplayName() +
////                ", image='" + getImageResource() + '\'' +
////                '}';
////    }
////}
//
//
//public class Equipment {
//    private EquipmentType equipmentType;
//    private boolean isSelected;
//
//    public Equipment(EquipmentType equipmentType) {
//        this.equipmentType = equipmentType;
//        this.isSelected = false;
//    }
//
//    public EquipmentType getEquipmentType() {
//        return equipmentType;
//    }
//
//    public String getImageResource() {
//        return equipmentType.getImageFileName();
//    }
//
//    public String getDisplayName() {
//        return equipmentType.getDisplayName();
//    }
//
//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }
//
//    @Override
//    public String toString() {
//        return "Equipment{" +
//                "type=" + equipmentType.getDisplayName() +
//                ", image='" + getImageResource() + '\'' +
//                ", selected=" + isSelected +
//                '}';
//    }
//}

package com.example.fithit.Models;

import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.R;

public class Equipment {
    private EquipmentType equipmentType;
    private boolean isSelected;

    public Equipment(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        this.isSelected = false;
    }

    public String getDisplayName() {
        return equipmentType.getDisplayName();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "type=" + equipmentType.getDisplayName() +
                ", selected=" + isSelected +
                '}';
    }

    public int getImageResourceId() {
        switch (equipmentType) {
            case Dumbbell:
                return R.drawable.dumbbells_image;
            case Barbell:
                return R.drawable.barbell_set_image;
            case Bench:
                return R.drawable.bench_image;
            case PullUpBar:
                return R.drawable.pullup_bar_image;
            case Kettlebell:
                return R.drawable.kettlebells_image;
            case ResistanceBand:
                return R.drawable.resistance_bands_image;
            case BosuBall:
                return R.drawable.bosu_ball_image;
            case StabilityBall:
                return R.drawable.stability_ball_image;
            case BattleRopes:
                return R.drawable.battle_ropes_image;
            case PlyoBox:
                return R.drawable.plyo_box_image;
            case JumpRope:
                return R.drawable.jump_rope_image;
            case YogaMat:
                return R.drawable.yoga_mat_image;
            default:
                return R.drawable.default_image;
        }
    }
}