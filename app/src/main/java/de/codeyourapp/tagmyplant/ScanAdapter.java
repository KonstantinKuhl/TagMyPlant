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

public class ScanAdapter extends ArrayAdapter<Scan> {
    private Context context;
    private int resource;

    public ScanAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Scan> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(resource, parent, false);

        TextView txtDate = convertView.findViewById(R.id.txtDate);

        TextView txtScanResult = convertView.findViewById(R.id.txtScanResult);

        txtDate.setText(getItem(position).getDate());

        txtScanResult.setText(getItem(position).getScanResult());

        return convertView;
    }
}
