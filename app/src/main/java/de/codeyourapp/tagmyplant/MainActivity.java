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



public class MainActivity extends AppCompatActivity {
    /* code scanner variables */
    private CodeScanner mCodeScanner;
    private int CAMERA_REQUEST_CODE = 1;

    /* dynamic listView variables */
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    /* button and intent variables */
    private Button showScanResultsButton;

    /* variables for data saving */
    private static final String mainArrayList = "mainArrayList";

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


    private void codeScanner() {
        // create a net object for the text view element
        //TextView tv_textView = findViewById(R.id.tv_textView);

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
                        // show the last decoded text in the TextView element below the CodeScanner
                        //tv_textView.setText("Last Scan: "+result.getText());

                        // add result to the dynamic listView below the scanner
                        stringArrayList.add("Scan: "+result.getText());

                        // update the dynamic listView over its adapter
                        stringArrayAdapter.notifyDataSetChanged();
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
                mCodeScanner.startPreview();    // the CodeScanner will start scanning if its ScanMode isn't set as "CONTINUOUS"
            }
        });
    }

    // specify the actions when the user comes back in the app
    @Override
    protected void onResume() {
        super.onResume();               // do what is usually done
        mCodeScanner.startPreview();    // start scanning for the case that the CodeScanner isn't scanning already
    }

    // specify the actions when the user leaves the app
    @Override
    protected void onPause() {
        super.onPause();                    // do what is usually done
        mCodeScanner.releaseResources();    // avoid memory leaks and release Resources for better performance
        saveData();                         // save the data of the listView element in the storage
    }

    private void setupPermissions(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE){
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
        // get intent from second activity
       /* Intent resultIntent = getIntent();*/

        // get the listView element from the XML-file
        listView = (ListView) findViewById(R.id.listView);

        // load the scan data from storage in form of a ArrayList
        loadData();

        // constructor call for the string array adapter and set context and layout
        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArrayList);

        // set the adapter of the listView element
        listView.setAdapter(stringArrayAdapter);

        // initialize deletion when an item in the list view is pressed long
        initDeletion();
    }

    private void showScanResults(){
        showScanResultsButton = (Button) findViewById(R.id.resultButton);
        showScanResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentResults = new Intent(MainActivity.this, SwipeUpActivity.class);
                startActivity(intentResults);
            }
        });
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList);
        editor.putString(mainArrayList, json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(mainArrayList, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        stringArrayList = gson.fromJson(json, type);

        if (stringArrayList == null){
            stringArrayList = new ArrayList<>();
        }
    }

    private void initDeletion(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int item = i;

                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                stringArrayList.remove(item);
                                stringArrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }
}



