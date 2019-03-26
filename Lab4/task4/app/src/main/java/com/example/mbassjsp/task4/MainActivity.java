package com.example.mbassjsp.task4;

// Created by A Leeming
// Modified JSP
// Date 17-1-2018
// See https://developer.android.com ,for android classes, methods, etc


// Import classes
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

// Android apps must have a MainActivity class that extends Activity or AppCompatActivity class
public class MainActivity extends AppCompatActivity {
    //Declare class variables
    private static final String LOGTAG = "Main UI"; //Logcat messages from UI are identified
    private NetworkConnectionAndReceiver networkConnectionAndReceiver = null;
    private String transmitterText; //Transmitter data variable
    private String registerText; // register+username

    // Class methods
    @Override
    //Extend the onCreate method, called whenever an activity is started
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Extend the onCreate method
        // Set up the view using xml description res>layout>activity_main.xml
        setContentView(R.layout.activity_main);

        Log.i(LOGTAG, "Starting task4 app"); // Report to Logcat

        // Instantiate the network connection and receiver object
        networkConnectionAndReceiver = new NetworkConnectionAndReceiver(this);
        networkConnectionAndReceiver.start();    // Start socket-receiver thread


        // Get the receiving text area as defined in the Res dir xml code
        TextView receiverTextArea = findViewById(R.id.txtServerResponse);
        // Make the receiving text area scrollable
        receiverTextArea.setMovementMethod(new ScrollingMovementMethod());

        // Get the kill button as defined in the Res dir xml code
        Button killButton = findViewById(R.id.btnKill);
        // Make the kill button receptive to being clicked
        // Button click handler
        killButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the killButton object
            public void onClick(View v) {
                // OnClick actions here
                // Exit app
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    transmitterText = "disconnect";
                    Log.i(LOGTAG, "kill button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
                Log.i(LOGTAG, "kill this app");
                System.exit(0);
            }
        });

        // register function
        final Spinner targetName = findViewById(R.id.spinnerWho);
        final List<String> list = new ArrayList<String>();

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetName.setAdapter(dataAdapter);
        targetName.setPrompt("Select an online user:");

        final EditText userNameText;
        userNameText = findViewById(R.id.userName);
        Button registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    registerText = "register " + userNameText.getText().toString();
                    userNameText.setCursorVisible(false);
                    userNameText.setFocusable(false);
                    userNameText.setFocusableInTouchMode(false);
                    Log.i(LOGTAG, "register button clicked, prepare to transmit text:"+registerText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), registerText);
                    transmitter.start();        // Run on its own thread

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    transmitterText = "who";
                    Log.i(LOGTAG, "after registered, who is online "+transmitterText);
                    Transmitter transmitter2 = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter2.start();        // Run on its own thread

                    // update dropdown list
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String[] onlineUsers = networkConnectionAndReceiver.getOnlineUsers();
                    if(onlineUsers != null) {
                        list.clear();
                        for (int i = 0; i < onlineUsers.length; i++) {
                            if (!onlineUsers[i].equals(", ")) {
                                list.add(onlineUsers[i]);
                            }
                        }
                        targetName.setAdapter(dataAdapter);
                    }
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

        // set up connections
        Button inviteButton = findViewById(R.id.btnInvite);
        // Button click handler
        inviteButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    if(targetName.getSelectedItem() == null) {
                        Log.e(LOGTAG, "drop down list value is null");
                        transmitterText = "";
                    } else {
                        transmitterText = "invite " + targetName.getSelectedItem().toString();
                    }
                    Log.i(LOGTAG, "invite button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

        // set up connections
        Button endButton = findViewById(R.id.btnEnd);
        // Button click handler
        endButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    if(targetName.getSelectedItem() == null) {
                        Log.e(LOGTAG, "drop down list value is null");
                        transmitterText = "";
                    } else {
                        transmitterText = "end " + targetName.getSelectedItem().toString();
                    }
                    Log.i(LOGTAG, "end button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

        Button acceptButton = findViewById(R.id.btnAccept);
        Button declineButton = findViewById(R.id.btnDecline);
        // Button click handler
        acceptButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    if(targetName.getSelectedItem() == null) {
                        Log.e(LOGTAG, "drop down list value is null");
                        transmitterText = "";
                    } else {
                        transmitterText = "accept " + targetName.getSelectedItem().toString();
                    }
                    Log.i(LOGTAG, "accept button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });
        // Button click handler
        declineButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    if(targetName.getSelectedItem() == null) {
                        Log.e(LOGTAG, "drop down list value is null");
                        transmitterText = "";
                    } else {
                        transmitterText = "decline " + targetName.getSelectedItem().toString();
                    }
                    Log.i(LOGTAG, "decline button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

//        // Get the text area for commands to be transmitted as defined in the Res dir xml code
//        final EditText command;
//        command = findViewById(R.id.command);
//        // Get the send button as defined in the Res dir xml code
//        Button sendButton =  findViewById(R.id.btnSendCmd);
//        // Make the kill button receptive to being clicked
//        // Button click handler
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            // onClick method implementation for the sendButton object
//            public void onClick(View v) {
//                // OnClick actions here
//                // Instantiate the transmitter passing the output stream and text to it
//                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
//                    transmitterText = command.getText().toString();
//                    command.setText("");
//                    Log.i(LOGTAG, "send button clicked, prepare to transmit text:"+transmitterText);
//                    command.clearFocus();
//                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
//                    transmitter.start();        // Run on its own thread
//                } else {
//                    Log.e(LOGTAG, "output stream not set");
//                }
//            }
//        });

        // set disconnect button
        Button disconnectButton = findViewById(R.id.btnDisconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    transmitterText = "disconnect";
                    Log.i(LOGTAG, "disconnect button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

        // set refresh button
        Button refreshButton = findViewById(R.id.btnRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    transmitterText = "who";
                    Log.i(LOGTAG, "refresh button clicked, prepare to transmit text:"+transmitterText);
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread

                    // update dropdown list
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String[] onlineUsers = networkConnectionAndReceiver.getOnlineUsers();
                    if(onlineUsers != null) {
                        list.clear();
                        for (int i = 0; i < onlineUsers.length; i++) {
                            if (!onlineUsers[i].equals(", ")) {
                                list.add(onlineUsers[i]);
                            }
                        }
                        targetName.setAdapter(dataAdapter);
                    }

                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

        // msg button
        final EditText msgContent;
        msgContent = findViewById(R.id.msgContent);
        Button msgButton = findViewById(R.id.btnSendMsg);
        msgButton.setOnClickListener(new View.OnClickListener() {
            // onClick method implementation for the sendButton object
            public void onClick(View v) {
                // OnClick actions here
                // Instantiate the transmitter passing the output stream and text to it
                if(networkConnectionAndReceiver.getStreamOut() != null) { // Check that output stream has be setup
                    if(targetName.getSelectedItem() == null) {
                        Log.e(LOGTAG, "drop down list value is null");
                        transmitterText = "";
                    } else {
                        transmitterText = "msg " + targetName.getSelectedItem().toString() + " " + msgContent.getText().toString();
                    }
                    Log.i(LOGTAG, "msgSend button clicked, prepare to transmit text:"+transmitterText);
                    // msgTarget.setText("");
                    msgContent.setText("");
                    Transmitter transmitter = new Transmitter(networkConnectionAndReceiver.getStreamOut(), transmitterText);
                    transmitter.start();        // Run on its own thread
                } else {
                    Log.e(LOGTAG, "output stream not set");
                }
            }
        });

    } //End of app onCreate method

/*  // Following code used when using basic activity
    @Override
    //Create an options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Uses res>menu>main.xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } //End of app onCreateOptionsMenu

    @Override
    //Called when an item is selected from the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //End of app onOptionsItemSelected method
*/

}//End of app MainActivity class