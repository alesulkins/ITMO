package shorties;

public enum Emotions {
    HAPPY("счастливый", "получил деньги"),
    HURT("раненый", "получил ранение"),
    NEUTRAL("нейтральный", "");
    private final String state;
    private String reason;

    Emotions(String state, String reason) {
        this.state = state;
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return state + ", потому что " + reason;
    }
}
