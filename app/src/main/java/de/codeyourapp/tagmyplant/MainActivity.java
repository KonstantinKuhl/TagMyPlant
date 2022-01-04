package de.codeyourapp.tagmyplant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    /* code scanner variables */
    private CodeScanner mCodeScanner;
    private int CAMERA_REQUEST_CODE = 1;

    /* dynamic listView variables */
    private ListView listView;
    private ArrayList<Scan> scanArrayList = new ArrayList<>();
    private ScanAdapter scanAdapter;

    /* to get the current date and time*/
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /* button and intent variables */
    private Button showScanResultsButton;

    /* variables for data saving */
    private static final String mainArrayList = "mainArrayList";

    // Function that ist called as soon as the MainActivity-Class is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ask the user for the needed permissions to run the app
        setupPermissions();

        // call of the function for the scanner below
        codeScanner();

        // initialize the dynamic listView element below the scanner
        initListView();

        // change activitiy when button ist pressed: resultActivity
        showScanResults();

    }

    // function to initialize the Code-Scanner, set its parameters and the Decode-Callback-Function
    private void codeScanner() {
        // get the scanner_view object that was created in the activity_main.xml layout file
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        // create a new code scanner object and give it the context (superior class) and the scanner_View
        mCodeScanner = new CodeScanner(this, scannerView);

        // major adjustments at the Code_Scanner object:
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);    // which camera is used: back camera
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);   // what formats can be scanned: all formats
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);  // auto focus operates in fixed intervals
        mCodeScanner.setScanMode(ScanMode.SINGLE);          // scanner stops scanning after every triggered result
        mCodeScanner.setAutoFocusEnabled(true);             // autofocus is enabled by default
        mCodeScanner.setFlashEnabled(false);                // flash lite is disabled by default

        // specify actions when the scanner decodes something:
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override   // override the function "onDecoded" of the supertype mCodeScanner
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {       // define a new Runnable that is given to the UI-Thread
                    @Override
                    public void run() {  // override the run() function of the Runnable interface

                        // add date, time and result to the scanArrayList
                        scanArrayList.add(new Scan("date and time: " + sdf.format(new Date()), "scan result: " + result.getText()));

                        // update the dynamic listView via its adapter
                        scanAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // specify actions if there is an error within the CodeScanner
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {     // create a Logcat error-entry with the error message from the CodeScanner
                        Log.e("Main", "Camera initialization error:"+error.getMessage());
                    }
                });
            }
        });

        // specify actions if the scannerView is clicked
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the CodeScanner will start scanning if its ScanMode isn't set as "CONTINUOUS"
                mCodeScanner.startPreview();
            }
        });
    }

    // specify the actions when the user comes back in the app
    @Override
    protected void onResume() {
        // do what is usually done
        super.onResume();
        // start scanning for the case that the CodeScanner isn't scanning already
        mCodeScanner.startPreview();
    }

    // specify the actions when the user leaves the app
    @Override
    protected void onPause() {
        // do what is usually done
        super.onPause();
        // avoid memory leaks and release Resources for better performance
        mCodeScanner.releaseResources();
        // save the data of the listView element in the storage
        saveData();
    }

    // Function to set up the needed permissions for the App
    private void setupPermissions(){
        // set the needed permissions to the variable "permission"
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        // if not all of the needed permissions are granted in the PackageManager, make a request!
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
    }

    // Function to make a request for the needed permissions, if they are not granted yet
    private void makeRequest() {
        // set the request for the user when the camera permission is not granted yet
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    // This function is called as the result after the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // for the camera permission: check whether the user did accept it or not
        if (requestCode == CAMERA_REQUEST_CODE){
            // if the user did deny the request, tell him, that the camera is needed for this app
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You need the camera permission to be able to use this app!", Toast.LENGTH_SHORT).show();
            } else {
                // successful request -> nothing to do
            }
        }else{
            // do nothing
        }
    }

    // initializes the dynamic listView element
    private void initListView() {

        // get the listView element from the XML-file activity_main.xml
        listView = (ListView) findViewById(R.id.listView);

        // load the scan data from storage in form of an ArrayList
        loadData();

        // array adapter for complex listView items
        scanAdapter = new ScanAdapter(this, R.layout.list_view_item, scanArrayList);

        // set the adapter of the listView element
        listView.setAdapter(scanAdapter);

        // initialize deletion when an item in the listView is pressed for a long time
        initDeletion();
    }

    // Function to change the screen for the other/second activity: SwipeUpActivity
    private void showScanResults(){
        // get the button from the XML-file activity_main.xml
        showScanResultsButton = (Button) findViewById(R.id.resultButton);
        // set a "OnClickListener" for this button
        showScanResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when the button is pressed generate an intent and call it to change the activity to the "SwipeUpActivity"
                Intent intentResults = new Intent(MainActivity.this, SwipeUpActivity.class);
                startActivity(intentResults);
            }
        });
    }

    // Function to store the scan-result-data persistent in the memory
    private void saveData(){
        // SharedPreferences is a key, value pair system for storing data of an application
        // create an instance
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // create an instance of an editor for the shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // create a Gson instance to fill the sharedPreferences with Strings
        Gson gson = new Gson();
        // use the Gson instance to create a json String
        String json = gson.toJson(scanArrayList);
        // load the json String in the sharedPreferences via the editor
        editor.putString(mainArrayList, json);
        editor.apply();
    }

    // Function to load the persistent scan-result-data from the memory
    private void loadData(){
        // create a SharedPreferences instance to read the data from the memory via a key word
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // create a Gson instance to read the data from the sharedPreferences
        Gson gson = new Gson();
        // read the data into a json String via the key word "mainArrayList"
        String json = sharedPreferences.getString(mainArrayList, null);
        // define the type of the data
        Type type = new TypeToken<ArrayList<Scan>>() {}.getType();
        // load the data into its variable
        scanArrayList = gson.fromJson(json, type);

        // if the app didn't generate data yet, create a new variable to avoid errors
        if (scanArrayList == null){
            scanArrayList = new ArrayList<>();
        }
    }

    // Function to initiate the option to delete items from the listView element
    private void initDeletion(){
        // create and set a itemLongClickListener for the listView element
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // save the item number of the clicked item
                final int item = i;
                // create an AlertDialog after the item is clicked
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setIcon(android.R.drawable.ic_delete)              // set an icon for the AlertDialog
                        .setTitle("Are you sure?")                          // set a title
                        .setMessage("Do you want to delete this item?")     // set the message

                        // create a PositiveButton, if it is clicked the item with the number "item" will be deleted
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                scanArrayList.remove(item);
                                scanAdapter.notifyDataSetChanged();
                            }
                        })
                        //create a NegativeButton, if it is clicked, nothing is done
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }
}



