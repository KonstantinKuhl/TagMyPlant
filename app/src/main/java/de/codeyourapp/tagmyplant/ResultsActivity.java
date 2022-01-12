package de.codeyourapp.tagmyplant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    /* dynamic listView variables*/
    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> stringArrayAdapter;

    /* new ArrayList for more complicate items*/
    private ArrayList<Scan> scanArrayList = new ArrayList<>();
    private ScanAdapter scanAdapter;

    /* button and intent variables*/
    private Button showScannerButton;

    /* variables for data saving */
    private static final String mainArrayList = "mainArrayList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

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
        // stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);

        // new array adapter for more complex listView
        scanAdapter = new ScanAdapter(this, R.layout.list_view_item, scanArrayList);

        // set the adapter of the listView element
        listView.setAdapter(scanAdapter);

        // initialize deletion when an item in the list view is pressed long
        initDeletion();
    }

    // sets up the button to return to the scanner activity
    private void showScanner(){
        // find the button in the XML-file
        showScannerButton = (Button) findViewById(R.id.scannerButton);
        // set an on click listener
        showScannerButton.setOnClickListener(new View.OnClickListener() {
            // create a new on click listener
            @Override
            public void onClick(View view) {
                // when the button is clicked change the current activity with an intent
                Intent intentScanner = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(intentScanner);
            }
        });
    }

    // Function to store the scan-result-data persistent in the memory of the phone
    private void saveData(){
        // SharedPreferences is a key, value pair system for storing data of an application
        // create an instance
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // create an instance of an editor for the shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // create a Gson instance to fill the sharedPreferences with Strings
        // Gson is a simple library to serialize Java objects to JSON
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
                new AlertDialog.Builder(ResultsActivity.this, R.style.AlertDialogCustom)
                        .setIcon(android.R.drawable.ic_delete)              // set an icon for the AlertDialog
                        .setTitle("Are you sure?")                          // set a title
                        .setMessage("Do you want to delete this item?")     // set the message

                        // create and set a PositiveButton, if it is clicked the item with the number "item" will be deleted
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                scanArrayList.remove(item);
                                scanAdapter.notifyDataSetChanged();
                            }
                        })
                        //create and set a NegativeButton, if it is clicked, nothing is done
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }
}