package es.pymasde.blueterm;

public class GpsPoint {

    double Lon, Lat, Alt;

    public GpsPoint(double lon, double lat) {
        Lon = lon;
        Lat = lat;
        Alt = 0;
    }

    public GpsPoint(double lon, double lat, double alt) {
        Lon = lon;
        Lat = lat;
        Alt = alt;
    }

    public String toString() {
        return Lon+","+Lat;
    }

}
