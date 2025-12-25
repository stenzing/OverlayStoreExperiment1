package sg.overlay;

public record Metadata(boolean isDeleted, String schema) {
    public static final Metadata DEFAULT = new Metadata(false, ".*");
    public static final Metadata DELETED = new Metadata(true, "^$");
}
