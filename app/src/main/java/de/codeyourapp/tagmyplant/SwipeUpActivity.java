package de.codeyourapp.tagmyplant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class SwipeUpActivity extends AppCompatActivity {

    /* dynamic listView variables*/
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    /* button and intent variables*/
    private Button showScannerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_up);

        // initialize the dynamic listView element below the scanner
        initListView();

        // change activity when button ist pressed: mainActivity
        showScanner();
    }

    // initializes the dynamic listView element
    private void initListView() {
        Intent intent = getIntent();

        // get the listView element from the XML-file
        listView = (ListView) findViewById(R.id.listView);

        // constructor call for the string array list variable
        stringArrayList = intent.getStringArrayListExtra("stringArrayListMain");

        // constructor call for the string array adapter and set context and layout
        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);

        // set the adapter of the listView element
        listView.setAdapter(stringArrayAdapter);
    }

    private void showScanner(){
        showScannerButton = (Button) findViewById(R.id.scannerButton);
        showScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentScanner = new Intent(SwipeUpActivity.this, MainActivity.class);
                intentScanner.putStringArrayListExtra("stringArrayListResults",stringArrayList);
                startActivity(intentScanner);
            }
        });
    };
}