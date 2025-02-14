package com.example.fithit.Enums;

public enum EquipmentType {
    Dumbbell("Dumbbell", "dumbbells_image.webp"),
    Barbell("Barbell", "barbell_set_image.jpg"),
    Bench("Bench", "bench_image.jpg"),
    PullUpBar("Pull Up Bar", "pullup_bar_image.jpg"),
    Kettlebell("Kettlebell", "kettlebells_image.webp"),
    ResistanceBand("Resistance Band", "resistance_bands_image.webp"),
    BosuBall("Bosu Ball", "bosu_ball_image.png"),
    StabilityBall("Stability Ball", "stability_ball_image.jpg"),
    BattleRopes("Battle Ropes", "battle_ropes_image.jpg"),
    PlyoBox("Plyo Box", "plyo_box_image.jpg"),
    JumpRope("Jump Rope", "jump_rope_image.jpg"),
    YogaMat("Yoga Mat", "yoga_mat_image.jpg");

    private final String displayName;
    private final String imageFileName;

    EquipmentType(String displayName, String imageFileName) {
        this.displayName = displayName;
        this.imageFileName = imageFileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
