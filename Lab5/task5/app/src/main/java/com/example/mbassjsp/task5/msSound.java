package com.example.mbassjsp.task5;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class msSound {
//    private int Fs = 22050; // Samping rate (Hz)
    private int Fs = 8000;
    private int length = Fs*10; // length of array for 10 seconds
    private short[] data = new short[length]; // Array of 16-bit samples
    private int i;
    private AudioTrack track;
    private String LOGTAG = "msSound";

    // Method for filling data array with random noise:
//    void fillRandom() {
//        Random rand = new Random();
//        for ( i = 0 ; i < length ; i++ ) { data[i] = (short) rand.nextInt();} //Fill data array
//    } // end of method fillRandom

    // read a file into java array
    public void msSpeech(Context context, int rawResource) {
        // Method for reading 16-bit samples from a wav file into array data
        int i;
        int b1;
        int b2;
        try
        { // Make speechis an input stream object for accessing a wav file placed in R.raw
            InputStream speechis = context.getResources().openRawResource(rawResource);
            // Read & discard the 44 byte header of the wav file:
            for ( i = 0 ; i < 44 ; i++ ) { b1 = speechis.read(); }
            // Read rest of 16-bit samples from wav file byte-by-byte:
            for ( i = 0 ; i < length ; i++ ) {
                b1 = speechis.read(); // Get first byte of 16-bit sample in least sig 8 bits of b1
                if (b1 == -1) {b1 = 0;} // b1 becomes -1 if we try to read past End of File
                b2 = speechis.read(); // Get second byte of sample value in b2
                if (b2 == -1) {b2 = 0;} // trying to read past EOF
                b2 = b2<<8 ; // shift b2 left by 8 binary places
                data[i] = (short) (b1 | b2); //Concat 2 bytes to make 16-bit sample value
            } // end of for loop
            speechis.close();
        } catch (FileNotFoundException e) {
            Log.e("msSound", "wav file not found", e);
        } catch (IOException e) {
            Log.e("msSound", "Failed to close input stream", e);
        }
    } // end of msSpeech method

    // Method for dealing with "spikes"
    public void smoothAudio() {
        int threshold = 12000;
        int hasError = 1;
        for(i = 200; i < length; i+=200) {
            for(int j = 0; j < 200; j++) {
                if(data[i+j] != 0) {
                    hasError = 0;
                    break;
                }
            }
            if(hasError==1) {
                for(int j = 0; j < 200; j++) {
                    data[i+j] = data[i+j-200];
                }
            }
            hasError = 1;
        }
        for(i = 1; i < length-1; i++) {
            short temp = (short) ((data[i-1] + data[i]) / 2);
            if(Math.abs(data[i] - temp) > threshold) {
                data[i] = temp;
            }
        }
    }

    // Method for playing sound from an array:
    public void arrayplay(Context context, int rawResource) {
        int CONF = AudioFormat.CHANNEL_OUT_MONO;
        int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        int MDE = AudioTrack.MODE_STATIC; //Need static mode.
        int STRMTYP = AudioManager.STREAM_ALARM;
        track = new AudioTrack(STRMTYP, Fs, CONF, FORMAT, length*2, MDE);
        msSpeech(context, rawResource);
        smoothAudio();
        track.write(data, 0, length);
        track.play();
        Log.i(LOGTAG, "array version start to play");
        while(track.getPlaybackHeadPosition() != length) {}; //Wait before playing more
        track.stop();
        Log.i(LOGTAG, "array version has stopped playing");
        track.setPlaybackHeadPosition(0);
        while(track.getPlaybackHeadPosition() != 0) {}; // wait for head position
    } // end of arrayplay method

    // Method for transform short array into 01string
    public static String shortToString(short[] data) {
        String str;
        String result = "";
        Log.i("short to str", Integer.toString(data.length));
        for(int i = 0; i < data.length; i++) {
            str = Integer.toBinaryString(0xFFFF & data[i]);
            while(str.length() < 16) {
                str = "0" + str;
            }
            Log.i("short to string", Integer.toString(i));
            result = result + str.substring(str.length()-16);
        }
        Log.i("short to string", "convert to 01 string completed");
        return result;
    }
    // Method for transform 01string into short array
    public static void stringToshort(String data, short[] result) {

        Log.i("str to short", Integer.toString(data.length()));
        //If the message does not have a length that is a multiple of 8, we can't decrypt it
        if(data.length() % 8 != 0) {
            Log.e("str to short", "msg's length is not a multiple of 8");
            return;
        }

        //Splits the string into 16 bit segments with spaces in between
        String characters = "";
        for(int i = 0; i < data.length() - 15; i += 16) {
            characters += data.substring(i, i + 16) + " ";
        }

        //Creates a string array with bytes that represent each of the characters in the message
        String[] bytes = characters.split(" ");
        int j = 0;
        for(int i = 0; i < bytes.length; i++) {
            result[j] = (short) Integer.parseInt(bytes[i], 2);
//            Log.i("test transfer", Short.toString(result[j]));
            j++;
        }
        Log.i("str to short", "convert to short array completed");
    }
} //end of msSound class
