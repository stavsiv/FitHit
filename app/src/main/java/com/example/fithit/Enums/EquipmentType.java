package com.example.fithit.Enums;

import com.example.fithit.R;

public enum EquipmentType {
    Dumbbell("Dumbbell", "dumbbells_image.webp", R.drawable.dumbbells_image),
    Barbell("Barbell", "barbell_set_image.jpg", R.drawable.barbell_set_image),
    Bench("Bench", "bench_image.jpeg", R.drawable.bench_image),
    PullUpBar("Pull Up Bar", "pullup_bar_image.jpg", R.drawable.pullup_bar_image),
    Kettlebell("Kettlebell", "kettlebells_image.webp",  R.drawable.kettlebells_image),
    ResistanceBand("Resistance Band", "resistance_bands_image.webp", R.drawable.resistance_bands_image),
    BosuBall("Bosu Ball", "bosu_ball_image.png", R.drawable.bosu_ball_image),
    StabilityBall("Stability Ball", "stability_ball_image.jpg", R.drawable.stability_ball_image),
    BattleRopes("Battle Ropes", "battle_ropes_image.jpg", R.drawable.battle_ropes_image),
    PlyoBox("Plyo Box", "plyo_box_image.jpg", R.drawable.plyo_box_image),
    JumpRope("Jump Rope", "jump_rope_image.jpg", R.drawable.jump_rope_image),
    YogaMat("Yoga Mat", "yoga_mat_image.jpeg", R.drawable.yoga_mat_image);

    private final String displayName;
    private final String imageFileName;
    private final int resourceId;

    EquipmentType(String displayName, String imageFileName, int resourceId) {
        this.displayName = displayName;
        this.imageFileName = imageFileName;
        this.resourceId = resourceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public int getResourceId() {
        return resourceId;
    }
}
