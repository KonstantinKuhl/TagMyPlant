package de.codeyourapp.tagmyplant;

// This class is represents a made Scan from the Scanner Framework.
public class Scan {
    // It needs to have 2 attributes:
    String date;            // a timestamp when the scan happened
    String scanResult;      // the decoded scan data

    // tiny constructor for the class:
    public Scan(String date, String scanResult) {
        this.date = date;
        this.scanResult = scanResult;
    }

    // getter function for the timestamp
    public String getDate() {
        return date;
    }

    // getter function for the scan data
    public String getScanResult() {
        return scanResult;
    }
}
