package com.example.mbassjsp.task5;

import android.support.v7.app.AppCompatActivity;
// Default android imports
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
//Added imports for this app
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageActivity extends AppCompatActivity {
    void msImage(String filename, ImageView image)
    { // Reads from an image file into an array for image processing in Java.
        try {
            int res_id = getResources().getIdentifier(filename, "raw", getPackageName() );
            InputStream image_is = getResources().openRawResource(res_id);
            int filesize = image_is.available(); //Get image file size in bytes
            byte[] image_array = new byte[filesize]; //Create array to hold image
            image_is.read(image_array); //Load image into array
            image_is.close(); // Close in-out file stream
            // Add your code here to process image_array & save processed version to a file.
            File newImageFile = new File(getFilesDir(), filename);
            OutputStream image_os = new FileOutputStream(newImageFile);
            image_os.write(image_array, 0, filesize);
            image_os.flush();
            image_os.close();
            //Display the processed image-file
            Bitmap newBitmap = BitmapFactory.decodeFile(newImageFile.getAbsolutePath());
            image.setImageBitmap(newBitmap);
        } // end of try
        catch (FileNotFoundException e) { Log.e("tag", "File not found ", e);}
        catch (IOException e) { Log.e("tag", "Failed for stream", e); }
    } //end of msImage method
    @Override
    protected void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        // Create ImageView object
        ImageView image_peppers = (ImageView) findViewById(R.id.peppers);
        ImageView image_peppersjpg = (ImageView) findViewById(R.id.peppersjpg);
        ImageView damaged_peppers = (ImageView) findViewById(R.id.damagedpeppers);
        ImageView damaged_peppersjpg = (ImageView) findViewById(R.id.damagedpeppersjpg);
        ImageView very_damaged_peppers = (ImageView) findViewById(R.id.verydamagedpeppers);
        msImage("peppers", image_peppers);
        msImage("peppersjpg", image_peppersjpg);
        msImage("damagedpeppers", damaged_peppers);
        msImage("damagedpeppersjpg", damaged_peppersjpg);
        msImage("verydamagedpeppers", very_damaged_peppers);
    } // end of onCreate method
} // end of MainActivity class

