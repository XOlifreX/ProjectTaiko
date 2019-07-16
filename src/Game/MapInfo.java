package Game;

import java.util.ArrayList;

public class MapInfo {

    private String songName;
    private int difficulity;
    private double offset;
    private double demoStart;
    // private long hitPrecision = 150000000;
    private Sound music;
    private ArrayList<Timing> mapFlowChanges;
    private ArrayList<MapEvent> mapEvents;
    private ArrayList<FieldObject> fieldObjects;
    private ArrayList<FieldObject> fieldObjectsRenderOrder;


    public MapInfo() {
        this.mapFlowChanges = new ArrayList<>();
        this.mapEvents = new ArrayList<>();
        this.fieldObjects = new ArrayList<>();
        this.fieldObjectsRenderOrder = new ArrayList<>();
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getDifficulity() {
        return difficulity;
    }

    public void setDifficulity(int difficulity) {
        this.difficulity = difficulity;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getDemoStart() {
        return demoStart;
    }

    public void setDemoStart(double demoStart) {
        this.demoStart = demoStart;
    }

    public Sound getMusic() {
        return music;
    }

    public void setMusic(Sound music) {
        this.music = music;
    }

    public void addTiming(Timing t) {
        this.mapFlowChanges.add(t);
    }

    public Timing getCurrentTiming() {
        return this.getTiming(this.mapFlowChanges.size() - 1);
    }

    public Timing getTiming(int i) {
        if (this.mapFlowChanges != null && this.mapFlowChanges.size() > 0)
            return this.mapFlowChanges.get(i);
        return null;
    }

    public ArrayList<Timing> getMapFlowChanges() {
        return mapFlowChanges;
    }

    public void addMapEvent(MapEvent me) {
        this.mapEvents.add(me);
    }

    public MapEvent getCurrentMapEvent() {
        return this.getMapEvent(this.mapEvents.size() - 1);
    }

    public MapEvent getMapEvent(int i) {
        if (this.mapEvents != null && this.mapEvents.size() > 0)
            return this.mapEvents.get(i);
        return null;
    }

    public ArrayList<MapEvent> getMapEvents() {
        return this.mapEvents;
    }

    public void setFieldObjects(ArrayList<FieldObject> fos) {
        this.fieldObjects = fos;
    }

}
