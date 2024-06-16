package pjs.golf.app.sheet.dto;

public enum BroadCastType {
    PLAYER_CAST("playCast"), STATUS_CAST("statusCast"), SCORE_CAST("scoreCast");

    private final String value;

    BroadCastType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
