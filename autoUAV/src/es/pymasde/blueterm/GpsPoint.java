package es.pymasde.blueterm;

import com.google.android.gms.maps.model.LatLng;

public class GpsPoint {

    private double Alt;
    private LatLng point;

    public GpsPoint(double lat, double lon) {
        point = new LatLng(lat,lon);
        Alt = 0;
    }

    public GpsPoint(double lat, double lon, double alt) {
        point = new LatLng(lat,lon);
        Alt = alt;
    }

    public GpsPoint(LatLng location) {
        this(location.latitude, location.longitude);
    }

    public double getLat() {
        return point.latitude;
    }

    public double getLon() {
        return point.longitude;
    }

    public double getAlt() {
        return Alt;
    }

    public LatLng getLatLng() {
        return point;
    }

    public String toString() {
        return point.toString();
    }

}
