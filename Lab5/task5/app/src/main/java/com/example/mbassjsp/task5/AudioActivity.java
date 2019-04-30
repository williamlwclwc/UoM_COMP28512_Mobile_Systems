package com.example.mbassjsp.task5;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioActivity extends AppCompatActivity {

    private MediaPlayer mySound;
    private int audioToPlay = R.raw.damagedspeech;
    private msSound arraySound;

    // recording
    private static final String LOGTAG = "AudioRecord";
    private String wavFileName = null;
    private int Fs = 8000; // Sampling freq (Hz)
    private int NS = Fs*10; // Number of samples
    private short soundSamples[] = new short [(int) NS];
    private short soundReceived[] = new short [(int) NS];
    private String packetStrSend;
    private String packetStrReceived;
    private short[] packetArrayReceived = new short [200];
    private int recBufferSize = AudioRecord.getMinBufferSize
            (Fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private int recChunks = ((int) (NS/recBufferSize));
    private AudioRecord recorder = new AudioRecord ( MediaRecorder.AudioSource.MIC, (int) Fs,
                AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, recBufferSize*2 );
    private AudioTrack track = new AudioTrack
            ( AudioManager.STREAM_MUSIC, Fs, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, NS*2, AudioTrack.MODE_STATIC );
    private AudioTrack track12 = new AudioTrack
            ( AudioManager.STREAM_MUSIC, 12000, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, NS*2, AudioTrack.MODE_STATIC );
    private int[] wavHeader = {0x46464952, 44+NS*2, 0x45564157, 0x20746D66,16, 0x00010001,
            Fs, Fs*2, 0x00100002, 0x61746164, NS*2};

    private NetworkConnectionAndReceiver networkConnectionAndReceiver = null;
    private String transmitterText;

    private int hasRec = 0;
    private int hasSent = 0;

    // stop playing
    public void stopSound(View v) {
        if(mySound != null) {
            mySound.stop();
            mySound.release();
            mySound = null;
        }
    }

    @Override
    //Extend the onCreate method, called whenever an activity is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Extend the onCreate method
        // Set up the view using xml description res>layout>activity_audio.xml
        setContentView(R.layout.activity_audio);

        this.networkConnectionAndReceiver = activitySwitch.networkConnectionAndReceiver;

        arraySound = new msSound();

        final List<String> fileList = new ArrayList<String>();
        fileList.add("HQ music");
        fileList.add("damaged speech");

        final Spinner audioFile = findViewById(R.id.spinnerAudio);
        final ArrayAdapter<String> audioAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fileList);
        audioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        audioFile.setAdapter(audioAdapter);
        audioFile.setPrompt("Select an audio to play:");

        Button playButton = findViewById(R.id.btnPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    // OnClick actions here
                    if(mySound == null) {
                        if(audioFile.getSelectedItem().toString().equals("HQ music")) {
                            audioToPlay = R.raw.hqmusic;
                        } else if(audioFile.getSelectedItem().toString().equals("damaged speech")) {
                            audioToPlay = R.raw.damagedspeech;
                        }
                        mySound = MediaPlayer.create(getApplicationContext(), audioToPlay);
                    }
                    if(mySound.isPlaying() == false) {
                        mySound.start();
                    }
            }
        });

        final Button arrayPlayButton = findViewById(R.id.btnArrayPlay);
        arrayPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // OnClick actions here
                if(audioFile.getSelectedItem().toString().equals("HQ music")) {
                    audioToPlay = R.raw.hqmusic;
                } else if(audioFile.getSelectedItem().toString().equals("damaged speech")) {
                    audioToPlay = R.raw.damagedspeech;
                }
                arraySound.arrayplay(getApplicationContext(), audioToPlay);
            }
        });

        // recording
        wavFileName = getExternalFilesDir(null) + "/Barry.wav";
        Log.i(LOGTAG, "Wav file is " + wavFileName);
        // Get the following buttons as defined in Res dir xml code
        final Button recButton = findViewById(R.id.btnRec);
        final Button playRecButton = findViewById(R.id.btnPlayRec);
        final Button resetRecButton = findViewById(R.id.btnResetRec);

        resetRecButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recButton.setText("Record");
                hasRec = 0;
            } // end of onClick method
        }); // end of recButton.setOnClickListener

        // Make the buttons receptive to being clicked
        recButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(hasRec == 0) {
                    recButton.setText("Recording");
                    Log.i(LOGTAG, "recButton has set text");
                    recorder.startRecording();
                    Log.i(LOGTAG, "recording has started");
                    int i;
                    for (i = 0; i < recChunks; i++) {
                        recorder.read(soundSamples, i * recBufferSize, (int) recBufferSize);
                    }
                    recorder.read(soundSamples, i * recBufferSize, NS - i * recBufferSize);
                    recorder.stop();
                    Log.i(LOGTAG, "Finished recording");
                    try {
                        File wavFile = new File(wavFileName);
                        FileOutputStream wavOutputStream = new FileOutputStream(wavFile);
                        DataOutputStream wavDataOutputStream = new DataOutputStream(wavOutputStream);
                        for (i = 0; i < wavHeader.length; i++) {
                            wavDataOutputStream.writeInt(Integer.reverseBytes(wavHeader[i]));
                        }
                        for (i = 0; i < soundSamples.length; i++) {
                            wavDataOutputStream.writeShort(Short.reverseBytes(soundSamples[i]));
                        }
                        wavOutputStream.close();
                        Log.i(LOGTAG, "Wav file saved");
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Wavfile write error");
                    }
                    recButton.setText("DONE");
                    hasRec = 1;
                }
            } // end of onClick method
        }); // end of recButton.setOnClickListener
        playRecButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                playRecButton.setText("Playing");
                    track.write(soundSamples, 0, (int) NS);
                    track.play();
                    while(track.getPlaybackHeadPosition() < NS) { }; //Wait before playing more
                    track.stop();
                    track.setPlaybackHeadPosition(0);
                    while(track.getPlaybackHeadPosition() != 0) { }; // wait for head position
                    playRecButton.setText("PLAY"); // for next time
                } //end of onClick method
        } ); //end of playButton.setOnClickListener

        final Spinner chMode = findViewById(R.id.spinnerMode);
        final List<String> modeList = new ArrayList<String>();
        modeList.add(" "); // null as default, like task4
        modeList.add("0"); // 0 as no bit errors
        modeList.add("1"); // 1 evenly spaced low rate errors
        modeList.add("2"); // 2 evenly spaced high rate errors
        modeList.add("3"); // 3 burst errors
        modeList.add("4"); // 4 lose packets low rate
        modeList.add("5"); // 5 lose packets high rate

        final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modeList);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chMode.setAdapter(modeAdapter);
        chMode.setPrompt("Select a channel mode:");

        final int packetSize = 40;
        final Button sendRecButton = findViewById(R.id.btnSendRec);
        sendRecButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                if(hasSent == 0) {
                    for (int i = 0; i < soundSamples.length; i = i + packetSize) {
                        short[] temp = Arrays.copyOfRange(soundSamples, i, i + packetSize);
                        packetStrSend = msSound.shortToString(temp);
                        // send the binary string
                        // Instantiate the transmitter passing the output stream and text to it
                        if (networkConnectionAndReceiver.getStreamOut() != null) {
                            // Check that output stream has be setup
                            if (chMode.getSelectedItem().toString().equals(" ")) {
                                transmitterText = "msg " + "BarryBot" + " " + packetStrSend;
                            } else {
                                transmitterText = chMode.getSelectedItem().toString() + "MSG " + "BarryBot" + " " + packetStrSend;
                            }
                            Log.i(LOGTAG, "msgSend button clicked, prepare to transmit text: " + transmitterText);
                            Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                            transmitter.start();        // Run on its own thread
                        } else {
                            Log.e(LOGTAG, "output stream not set");
                        }
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        packetStrReceived = networkConnectionAndReceiver.getPacketReceived();
                        while(packetStrReceived == null) {
                            packetStrReceived = networkConnectionAndReceiver.getPacketReceived();
                        }
                        msSound.stringToshort(packetStrReceived, packetArrayReceived);
                        for (int j = 0; j < packetSize; j++) {
                            soundReceived[i + j] = packetArrayReceived[j];
                        }
                        networkConnectionAndReceiver.setPacketReceived(null);
                    }
                    sendRecButton.setText("Done");
                    hasSent = 1;
                }
            } //end of onClick method
        } );

        final Button play8kButton = findViewById(R.id.btnPlay8k);
        play8kButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                play8kButton.setText("Playing");
                track.write(soundReceived, 0, (int) NS);
                track.play();
                while(track.getPlaybackHeadPosition() < NS) { }; //Wait before playing more
                track.stop();
                track.setPlaybackHeadPosition(0);
                while(track.getPlaybackHeadPosition() != 0) { }; // wait for head position
                play8kButton.setText("8Hz"); // for next time
            } //end of onClick method
        } );

        final Button play12kButton = findViewById(R.id.btnPlay12k);
        play12kButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                play12kButton.setText("Playing");
                track12.write(soundReceived, 0, (int) NS);
                track12.play();
                while(track12.getPlaybackHeadPosition() < NS) { }; //Wait before playing more
                track12.stop();
                track12.setPlaybackHeadPosition(0);
                while(track12.getPlaybackHeadPosition() != 0) { }; // wait for head position
                play12kButton.setText("12Hz"); // for next time
            } //end of onClick method
        } );

        final Button resetReceivedButton = findViewById(R.id.btnResetReceived);
        resetReceivedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendRecButton.setText("Send");
                hasSent = 0;
            } // end of onClick method
        }); // end of recButton.setOnClickListener
    } // end of overrided onCreate
} // end of AudioActivity
