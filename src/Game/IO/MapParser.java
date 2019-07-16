package Game.IO;

import Game.*;
import Game.IO.Exceptions.TMFParsingException;

import com.sun.istack.internal.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapParser {

    private Game.MapInfo currentSongInfo;


    public MapInfo readFileInfo(String filePath) {

        ArrayList<String> songHeader = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        this.currentSongInfo = new Game.MapInfo();

        try {

            File file = new File(filePath);
            FileReader reader = new FileReader(file);
            BufferedReader lineReader = new BufferedReader(reader);
            String tempLine;

            boolean started = false;
            while((tempLine = lineReader.readLine()) != null){

                if(!tempLine.isEmpty()) {

                    if (started)
                        notes.add(tempLine);
                    else
                        songHeader.add(tempLine);

                    if (tempLine.equals("#START"))
                        started = true;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String tempo : songHeader) {

            if (tempo.contains("TITLE:"))
                currentSongInfo.setSongName(tempo.substring(6));
            else if (tempo.contains("BPM:"))
                currentSongInfo.addTiming(new Timing(Double.parseDouble(tempo.substring(4)), 0));
            else if (tempo.contains("WAVE:"))
                currentSongInfo.setMusic(new Sound(tempo.substring(5)));
            else if (tempo.contains("LEVEL:"))
                currentSongInfo.setDifficulity(Integer.parseInt(tempo.substring(6)));
            else if (tempo.contains("OFFSET:"))
                currentSongInfo.setOffset(Double.parseDouble(tempo.substring(7)) * 100000000);
            else if (tempo.contains("DEMOSTART:"))
                currentSongInfo.setDemoStart(Double.parseDouble(tempo.substring(10)) * 100000000);
        }

        ArrayList<FieldObject> fo = this.generateFieldObjects(notes);
        this.currentSongInfo.setFieldObjects(fo);

        return this.currentSongInfo;

    }

    private ArrayList<FieldObject> generateFieldObjects(@NotNull ArrayList<String> notes) throws TMFParsingException {

        ArrayList<FieldObject> fieldObjects = new ArrayList<>();
        long intervalCounter = 0;
        boolean mapIntervalIsChanging = true;

        boolean hasComma;
        int noCommaLength = 0;

        int lineCount = 0;

        fieldObjects.add(new Barline(intervalCounter));
        for (String noteline : notes) {

            // Pre-processing.
            {
                lineCount++;
                hasComma = false;

                // Does it contain any comments? Remove them...
                if (noteline.contains("//"))
                    noteline = noteline.substring(0, noteline.indexOf("/"));

                if (noteline.length() == 0)
                    continue;
            }

            // Handle inline commands.
            {
                if (noteline.charAt(0) == '#') {

                    mapIntervalIsChanging = this.handleInlineCommands(noteline, mapIntervalIsChanging, intervalCounter);
                    continue;

                }

                if (mapIntervalIsChanging)
                    mapIntervalIsChanging = false;

            }

            // Handle note lines.
            {

                if (noteline.contains(",")) {
                    noteline = noteline.substring(0, noteline.length() - 1);
                    if (noteline.contains(","))
                        throw new TMFParsingException("Error at line: " + lineCount + ": Misplaced \',\' symbol.");

                    hasComma = true;
                    if (noCommaLength > 0) {
                        noCommaLength += noteline.length();

                        if (noCommaLength > this.currentSongInfo.getCurrentTiming().getTotalOfIntervalsInFullBeat())
                            throw new TMFParsingException("Error at line: " + lineCount + ": The cardinality of intervals o all no comma lines are not equal to the total allowed intervals for the current measurement (" + this.currentSongInfo.getCurrentTiming().getMeasurement()[0] + "/" + this.currentSongInfo.getCurrentTiming().getMeasurement()[1] + ")");
                    }
                    noCommaLength = 0;
                }

                if (noteline.length() % this.currentSongInfo.getCurrentTiming().getMeasurement()[1] != 0)
                    throw new TMFParsingException("Error at line: " + lineCount + ": The current measurement (" + this.currentSongInfo.getCurrentTiming().getMeasurement()[0] + "/"+  this.currentSongInfo.getCurrentTiming().getMeasurement()[1] + ") is not a fraction of the cardinality of note intervals of this line.");

                double intervalTime = this.currentSongInfo.getCurrentTiming().getFullBeatInterval();
                if (noteline.length() > 0)
                    intervalTime = this.currentSongInfo.getCurrentTiming().getFullBeatInterval() / noteline.length();

                // Atm with no comma note lines we consider them to be divided in actual single intervals. No skipping...
                if (!hasComma){
                    noCommaLength += noteline.length();

                    if (noCommaLength > this.currentSongInfo.getCurrentTiming().getTotalOfIntervalsInFullBeat())
                        throw new TMFParsingException("Error at line: " + lineCount + ": The cardinality of intervals on all no comma lines are not equal to the total allowed intervals for the current measurement (" + this.currentSongInfo.getCurrentTiming().getMeasurement()[0] + "/"+  this.currentSongInfo.getCurrentTiming().getMeasurement()[1] + ")");

                    intervalTime = this.currentSongInfo.getCurrentTiming().getSingleInterval();

                }

                // If the line was only a single comma. Then treat is a full beat of 0's.
                if (noteline.length() == 0) {
                    intervalCounter += intervalTime;
                    continue;
                }

                for (char note: noteline.toCharArray()) {

                    Note temp = this.getNoteType(note, intervalCounter);

                    if (temp != null)
                        fieldObjects.add(temp);

                    intervalCounter += intervalTime;

                }

                if (hasComma)
                    fieldObjects.add(new Barline(intervalCounter));

            }

        }

        return fieldObjects;

    }

    private boolean handleInlineCommands(String noteline, boolean mapIntervalIsChanging, long intervalCounter) throws TMFParsingException {

        if (noteline.contains("MEASURE")) {

            if (mapIntervalIsChanging) {
                this.currentSongInfo.getCurrentTiming().setMeasurement(this.getMeasurementArrayFromString(noteline));
                return mapIntervalIsChanging;
            }

            // We have a new timing change, so a new Timing object needs to be created.
            this.currentSongInfo.addTiming(
                    new Timing(this.currentSongInfo.getCurrentTiming().getBPM(),
                            this.getMeasurementArrayFromString(noteline),
                            intervalCounter)
            );
            return true;
        }

        if (noteline.contains("BPMCHANGE")) {

            if (mapIntervalIsChanging) {
                this.currentSongInfo.getCurrentTiming().setBPM(this.getBPMFromString(noteline));
                return mapIntervalIsChanging;
            }

            // We have a new timing change, so a new Timing object needs to be created.
            this.currentSongInfo.addTiming(
                    new Timing(this.getBPMFromString(noteline),
                            this.currentSongInfo.getCurrentTiming().getMeasurement(),
                            intervalCounter)
            );
            return true;
        }

        if (noteline.contains("SCROLL"))
            this.currentSongInfo.addMapEvent(new MapEvent(intervalCounter, this.getScrollFromString(noteline)));

        if (noteline.contains("GOGO")) {
            if (noteline.contains("START"))
                this.currentSongInfo.addMapEvent(new MapEvent(intervalCounter, true));
            this.currentSongInfo.addMapEvent(new MapEvent(intervalCounter, false));
        }

        if (noteline.equals("#START"))
            this.currentSongInfo.addMapEvent(new MapEvent(true, intervalCounter));

        if (noteline.equals("#END"))
            this.currentSongInfo.addMapEvent(new MapEvent(false, intervalCounter));

        return false;

    }

    private int[] getMeasurementArrayFromString(String line) throws TMFParsingException {

        int measurements[] = new int[2];

        try {
            measurements[0] = Integer.parseInt(line.substring(9, 10));
            measurements[1] = Integer.parseInt(line.substring(11, 12));

            return measurements;
        } catch (NumberFormatException ex) {
            throw new TMFParsingException("Measurement integers misaligned or misplaced.");
        }

    }
    private double getBPMFromString(String line) throws TMFParsingException {

        try {
            return Double.parseDouble(line.substring(11, 14));
        } catch (NumberFormatException ex) {
            throw new TMFParsingException("BPM integer misaligned or misplaced.");
        }

    }
    private double getScrollFromString(String line) throws TMFParsingException {

        try {
            return Integer.parseInt(line.substring(8, 11));
        } catch (NumberFormatException ex) {
            throw new TMFParsingException("BPM integer misaligned or misplaced.");
        }

    }

    private Note getNoteType(char type, long timing) {

        switch(type) {
            case '1': return new Note(NoteType.DON, timing);
            case '2': return new Note(NoteType.KATSU, timing);
            case '3': return new Note(NoteType.bDON, timing);
            case '4': return new Note(NoteType.bKATSU, timing);
            default: return null;
        }

    }

    private ArrayList<FieldObject> calculateRenderTimings(ArrayList<FieldObject> fieldObjects) throws TMFParsingException {

        ArrayList<MapEvent> mapEvents = this.currentSongInfo.getMapEvents();

        long x_res = GameInfo.getX_res();
        long y_res = GameInfo.getY_res();

        // TODO: calculate this in a different manner. Kinda hardcoded...
        // This will be refactored once the UI has been setup and we know where exactly the hit circle is at.
        double perfectHitX_pos = Math.floor(x_res * 0.23828125);
        double perfectHitY_pos = Math.ceil(y_res * 0.39560439);

        double barTravelLengthPixels = x_res - perfectHitX_pos;

        MapEvent currentMapEvent = null;
        Timing currentTiming = null;

        for (FieldObject fo: fieldObjects) {

            for (MapEvent mv : mapEvents) {
                if (mv.getTiming() == fo.getTiming())
                    currentMapEvent = mv;
            }

            boolean found = false;
            long timediff = 0xFFFFFFFF;
            for (Timing t : this.currentSongInfo.getMapFlowChanges()) {
                if (timediff > t.getTiming() - fo.getTiming()) {
                    timediff = t.getTiming() - fo.getTiming();
                    currentTiming = t;
                    found = true;
                }
            }

            if (!found)
                throw new TMFParsingException("No suitable Timing found at timing: " + fo.getTiming() + ".");

            double fullBeatDuration = currentTiming.getFullBeatInterval();
            double timePerFrame = fullBeatDuration / barTravelLengthPixels;

            // Do some manipulation on timePerFrame for AR.
            double scrollManipulator = 1;
            if (currentMapEvent != null)
                scrollManipulator = currentMapEvent.getScrollModifier();



        }

    }

    private ArrayList<FieldObject> orderByRenderTiming(ArrayList<FieldObject> fieldObjects) {

        return null;

    }

}
