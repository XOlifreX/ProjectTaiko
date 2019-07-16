package Game;

public class MapEvent {

    private GogoStatus kiai;
    private long timing;
    private double scrollModifier;
    private MapStatus mapStatus;


    public MapEvent(long timing) {
        this.timing = timing;
        this.kiai = GogoStatus.IDLE;
        this.scrollModifier = 1;
        this.mapStatus = MapStatus.IDLE;
    }

    public MapEvent(boolean mapStatus, long timing) {
        this.timing = timing;
        this.kiai = GogoStatus.IDLE;
        this.scrollModifier = 1;
        this.setMapStatus(mapStatus);
    }

    public MapEvent(long timing, boolean kiai) {
        this.timing = timing;
        this.setKiai(kiai);
        this.scrollModifier = 1;
        this.mapStatus = MapStatus.IDLE;
    }

    public MapEvent(long timing, double scrollModifier) {
        this.timing = timing;
        this.kiai = GogoStatus.IDLE;
        this.scrollModifier = scrollModifier;
        this.mapStatus = MapStatus.IDLE;
    }

    public MapEvent(long timing, boolean kiai, double scrollModifier) {
        this.timing = timing;
        this.setKiai(kiai);
        this.scrollModifier = scrollModifier;
        this.mapStatus = MapStatus.IDLE;
    }


    private void setKiai(boolean kiai) {
        if (kiai)
            this.kiai = GogoStatus.STARTING;
        else
            this.kiai = GogoStatus.ENDING;
    }

    private void setMapStatus(boolean ms) {
        if (ms)
            this.mapStatus = MapStatus.STARTING;
        else
            this.mapStatus = MapStatus.ENDING;
    }

    public GogoStatus getKiaiEvent() {
        return this.kiai;
    }

    public double getScrollModifier() {
        return scrollModifier;
    }

    public void setScrollModifier(double scrollModifier) {
        this.scrollModifier = scrollModifier;
    }

    public long getTiming() {
        return this.timing;
    }

}
