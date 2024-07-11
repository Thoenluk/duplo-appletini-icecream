package main.ch.thoenluk;

enum ScalingType {
    NO_SCALING("Don't scale"),
    SQUARE("Square"),
    RECTANGLE("Grid-aligned rectangle"),
    LTR_SLOPE("Sloped (Top left to bottom right)"),
    RTL_SLOPE("Sloped (Bottom left to top right)");

    private final String description;

    ScalingType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
