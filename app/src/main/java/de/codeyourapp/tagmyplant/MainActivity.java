package de.codeyourapp.tagmyplant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.zxing.Result;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    /* code scanner variables */
    private CodeScanner mCodeScanner;
    private int CAMERA_REQUEST_CODE = 1;

    /* dynamic listView variables*/
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    /* variables for swiping between activities*/
    float x1, x2, y1, y2;
    private RelativeLayout relativeLayout;


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

        swipeDetection();
    }


    private void codeScanner() {
        // create a net object for the text view element
        TextView tv_textView = findViewById(R.id.tv_textView);

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
                        tv_textView.setText("Last Scan: "+result.getText());

                        // add result to the dynamic listView below the scanner
                        stringArrayList.add("Scan "+(stringArrayAdapter.getCount()+1)+": "+result.getText());

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
        // get the listView element from the XML-file
        listView = (ListView) findViewById(R.id.listView);

        // constructor call for the string array list variable
        stringArrayList = new ArrayList<String>();

        // constructor call for the string array adapter and set context and layout
        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArrayList);

        // set the adapter of the listView element
        listView.setAdapter(stringArrayAdapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeDetection(){

        /*relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);*/
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        y2 = motionEvent.getY();
                        if( (x1>x2) && (x1-x2 > 100) ){
                            Toast.makeText(getApplicationContext(), "You swiped right!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, SwipeUpActivity.class);
                            intent.putStringArrayListExtra("stringArrayListMain",stringArrayList);
                            startActivity(intent);
                        }
                        /*else if ( (x1<x2) && (x2-x1 > 100) ){
                            Toast.makeText(getApplicationContext(), "You swiped left!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, SwipeUpActivity.class);
                            intent.putStringArrayListExtra("stringArrayListMain",stringArrayList);
                            startActivity(intent);
                        }*/
                }
            return false;
            }
        });
    }

}
