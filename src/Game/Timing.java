package Game;


public class Timing {

    private double BPM;
    private double beatInterval;
    private double fullBeatInterval;
    private long timing;

    /**
     * Measurement: x/y
     * The y stands for how many places there are in one interval (every possible place a note can be placed in).
     * The x stands for how many times one interval has to be repeated to complete one full beat.
     *
     * E.g.: Measurement 4/4 means there are 16 possible places a note can be placed in a full beat.
     */
    private int measurement[];


    public Timing(double BPM, long timing){


        // Measurement is 4/4 by default.
        this.measurement = new int[]{4,4};
        this.BPM = BPM;
        this.timing = timing;

        this.calcFullBeatInterval();
        this.calcBeatInterval();

    }

    public Timing(double BPM, int measurement[], long timing){

        this.measurement = measurement;
        this.BPM = BPM;
        this.timing = timing;

        this.calcFullBeatInterval();
        this.calcBeatInterval();

    }


    /**
     * Returns the time between every position a note can appear
     */
    private void calcBeatInterval(){

        this.beatInterval = ((60/this.BPM)/this.measurement[1])*1000000000;

    }

    /**
     * Returns the time of a full beat interval. (interval*16)
     */
    private void calcFullBeatInterval(){

        this.fullBeatInterval = (((60/this.BPM))*1000000000)*(this.measurement[0] * this.measurement[1]);

    }

    public double getFullBeatInterval() {

        return (Math.floor(this.fullBeatInterval));

    }

    public double getSingleInterval(){

        return Math.floor(this.beatInterval);

    }

    public void setMeasurement(int[] measurement) {
        this.measurement = measurement;

        // Recalculate with updated measurement.
        this.calcFullBeatInterval();
        this.calcBeatInterval();
    }

    public void setBPM(double bpm) {
        this.BPM = bpm;

        // Recalculate with updated BPM.
        this.calcFullBeatInterval();
        this.calcBeatInterval();
    }

    public double getBPM() {
        return this.BPM;
    }

    public int[] getMeasurement(){
        return this.measurement;
    }

    public int getTotalOfIntervalsInFullBeat() {
        return this.measurement[0] * this.measurement[1];
    }

    public long getTiming() {
        return this.timing;
    }

}
