package com.example.mbassjsp.task4;

// Created by A Leeming
// Modified JSP
// Date 17-1-2018
// See https://developer.android.com ,for android classes, methods, etc

// Import classes
import android.util.Log;
import java.io.PrintWriter;



public class Transmitter extends Thread{
    //Declare class variables
    private static final String LOGTAG = "Transmitter"; // Identify logcat messages
    private PrintWriter printerRef = null;   // Transmitter output
    private String transmitterString = null; // Transmitter input

    //Class constructor
    public Transmitter( PrintWriter printerRef, String transmitterString)
    {
        this.printerRef=printerRef; // Link to network connection output
        this.transmitterString=transmitterString;     // Pass text to be transmitted
    }
    // Transmit text to output stream in new short lived thread
    public void run() {
        Log.i(LOGTAG, "Running in new thread"); // Show in Info Logcat window

        try {
            Log.i(LOGTAG,"Sending command: "+transmitterString); // Show in Info Logcat window
            printerRef.println(transmitterString); // Transmit text

        } catch (Exception e) {
            Log.e(LOGTAG, "Failed to transmit\n" + e.getMessage());// Show in Error Logcat window
            e.printStackTrace();
        }
        // Let thread close
        Log.i(LOGTAG, "Thread now closing"); // Show in Info Logcat window
    }

}
