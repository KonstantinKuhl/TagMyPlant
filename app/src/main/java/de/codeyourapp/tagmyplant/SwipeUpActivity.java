package de.codeyourapp.tagmyplant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SwipeUpActivity extends AppCompatActivity {

    /* dynamic listView variables*/
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    /* button and intent variables*/
    private Button showScannerButton;

    /* variables for data saving */
    private static final String mainArrayList = "mainArrayList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_up);

        // initialize the dynamic listView element below the scanner
        initListView();

        // change activity when button ist pressed: mainActivity
        showScanner();
    }

    // specify the actions when the user leaves the app
    @Override
    protected void onPause() {
        super.onPause();                    // do what is usually done
        saveData();                         // save the data of the listView element in the storage
    }

    // initializes the dynamic listView element
    private void initListView() {

        // get the listView element from the XML-file
        listView = (ListView) findViewById(R.id.listView);

        // load the scanned data from the storage in form of an arrayList
        loadData();

        // constructor call for the string array adapter and set context and layout
        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);

        // set the adapter of the listView element
        listView.setAdapter(stringArrayAdapter);

        // initialize deletion when an item in the list view is pressed long
        initDeletion();
    }

    private void showScanner(){
        showScannerButton = (Button) findViewById(R.id.scannerButton);
        showScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentScanner = new Intent(SwipeUpActivity.this, MainActivity.class);
                startActivity(intentScanner);
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

                new AlertDialog.Builder(SwipeUpActivity.this, R.style.AlertDialogCustom)
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