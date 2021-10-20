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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class SwipeUpActivity extends AppCompatActivity {

    /* dynamic listView variables*/
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    private ConstraintLayout constraintLayout;
    float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_up);

        // initialize the dynamic listView element below the scanner
        initListView();

        swipeDetection();
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

    @SuppressLint("ClickableViewAccessibility")
    private void swipeDetection() {
        /*constraintLayout = findViewById(R.id.constraintLayout);*/
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        y2 = motionEvent.getY();
                        if ((x1 > x2) && (x1 - x2 > 100)) {
                            Toast.makeText(getApplicationContext(), "You swiped right!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SwipeUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
/*                        else if ((x1 < x2) && (x2 - x1 > 100)) {
                            Toast.makeText(getApplicationContext(), "You swiped left!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SwipeUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }*/
                }
            return false;
            }
        });
    }

}