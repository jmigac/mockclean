package hr.from.juricamigac.mockclean.core;

public final class JsonCleanResult {

    private final String json;
    private final int removedCount;

    JsonCleanResult(final String json, final int removedCount) {
        this.json = json;
        this.removedCount = removedCount;
    }

    public String getJson() {
        return this.json;
    }

    public int getRemovedCount() {
        return this.removedCount;
    }

}
