package de.codeyourapp.tagmyplant;

public class Scan {
    String date;
    String scanResult;

    public Scan(String date, String scanResult) {
        this.date = date;
        this.scanResult = scanResult;
    }

    public String getDate() {
        return date;
    }

    public String getScanResult() {
        return scanResult;
    }
}
