package Game;

import javax.swing.*;
import java.awt.*;

public class Barline implements FieldObject {

    Image BPMLine;
    private int approachSpeed;
    private int x_loc, y_loc;
    private long perfectHitTime;
    private long coolDownTime; //Time before a new position update may happen (is used for Approach Rate)
    private double scrollManipulator; //A number (> 1 when it's going faster, < 1 when it's going slower) that says how fast a note is moving
    private long barRenderTimeCheck = 0L;

    private static final int FINAL_SPEED = 1;
    private static final long FIXED_NOTE_COOLDOWN_TIME = 2900000; //3ms
    private static int barCount = 0;


    {

        barCount++;

    }

    public Barline(long time){

        this.BPMLine = new ImageIcon("src/Files/bpmline.png").getImage();
        this.x_loc = 556;
        this.y_loc = 8;
        this.approachSpeed = FINAL_SPEED;
        this.perfectHitTime = time;

    }


    public Image getBarLineImage() {

        return this.BPMLine;

    }

    public int getX_loc(){

        return this.x_loc;

    }

    public int getY_loc(){

        return this.y_loc;

    }

    public void updateBarLineLocation(){

        if(System.nanoTime()-this.barRenderTimeCheck >= FIXED_NOTE_COOLDOWN_TIME) {
            this.x_loc -= approachSpeed;
            this.barRenderTimeCheck = System.nanoTime();
        }

    }

    public void setBarRenderTimeCheck(long t){

        this.barRenderTimeCheck = t;

    }

    @Override
    public long getTiming() {
        return this.perfectHitTime;
    }

}
