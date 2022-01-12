package de.codeyourapp.tagmyplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// This class is used as an adapter between the scanArrayList and the listView element and to render the listView element
public class ScanAdapter extends ArrayAdapter<Scan> {
    private Context context;
    private int resource;

    // implementation of the constructor for the class
    public ScanAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Scan> objects) {
        // call the super class constructor
        super(context, resource, objects);
        // set the context and the given resource additionally
        this.context = context;
        this.resource = resource;
    }

    // override the getView function of the super class
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Create a new LayoutInflater for rendering the content of the listView element later!
        // A LayoutInflater converts an XML layout file into corresponding ViewGroups and Widgets.
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Here the list_view_item XML file will be passed later so that the LayoutInflater can
        // inflate it into the listView element.
        convertView = layoutInflater.inflate(resource, parent, false);

        // get the textView element txtDate from the list_view_item XML-file / the inflater
        TextView txtDate = convertView.findViewById(R.id.txtDate);

        // get the textView element txtScanResult from the list_view_item XML-file / the inflater
        TextView txtScanResult = convertView.findViewById(R.id.txtScanResult);

        // feed the text out of the scanArrayList objects into the textView element txtDate
        txtDate.setText(getItem(position).getDate());

        // feed the text out of the scanArrayList objects into the textView element txtScanResult
        txtScanResult.setText(getItem(position).getScanResult());

        return convertView;
    }
}
