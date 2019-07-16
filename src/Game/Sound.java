package Game;

/**
 * Created by olivi on 21/06/2019.
 */
import Game.Lib.WaveData;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class Sound {

    private int intBuffer;
    private String soundLoc;
    private int soundBuffer;
    private int soundSource;
    private static long device;
    private static long context;


    public Sound(String loc) {

        this.soundLoc = loc;
        this.initSound();

    }


    private void initSound() {

        try {

            WaveData data = WaveData.create(new BufferedInputStream(new FileInputStream(this.soundLoc)));
            this.soundBuffer = AL10.alGenBuffers();
            this.soundSource = AL10.alGenSources();

            AL10.alBufferData(this.soundBuffer, data.format, data.data, data.samplerate);
            data.dispose();

            AL10.alSourcei(this.soundSource, AL10.AL_BUFFER, this.soundBuffer);

        }
        catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }

    }



    public void play(){

        AL10.alSourcePlay(this.soundSource);

    }

    public void destroy() {

        AL10.alDeleteBuffers(soundBuffer);

    }

    public static void initContextDevice() {

        device = ALC10.alcOpenDevice((ByteBuffer) null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

    }

    public static void destroyContextDevice() {

        ALC10.alcCloseDevice(device);
        ALC10.alcDestroyContext(context);

    }

}
