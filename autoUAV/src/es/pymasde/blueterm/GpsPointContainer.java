package es.pymasde.blueterm;

/**
 * Created by zohar on 12/07/2015.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class GpsPointContainer {

    private GpsPoint droneLocation;
    private String droneGpsDataString;
    private LinkedList<GpsPoint> listPoint;
    private LinkedList<LatLng> latLngList;

    public GpsPointContainer(GpsPoint droneLocation) {
        this.droneLocation = droneLocation;
        droneGpsDataString = "---> None GPS distance data <---\n";
        listPoint = new LinkedList<GpsPoint>();
        latLngList = new LinkedList<LatLng>();
        /*
         ------------------------------------------just for test
          */
        //GpsPoint gp = new GpsPoint(32.167524, 35.086128);//(32.1675153, 35.0852831);
        //listPoint.add(gp);
    }

    public void add(GpsPoint gp) {
        listPoint.add(gp);
        latLngList.add(gp.getLatLng());
    }

    public boolean isEmpty() {return listPoint.isEmpty();}

    public void addFootBallList() {
        GpsPoint gp1 = new GpsPoint(32.105368, 35.208137);
        GpsPoint gp2 = new GpsPoint(32.105648, 35.208568);
        listPoint.clear();
        listPoint.add(gp1);
        listPoint.add(gp2);
    }

    public GpsPoint removeFirst() {
        GpsPoint gp = listPoint.removeFirst();
        latLngList.removeFirst();
        return gp;
    }

    public GpsPoint getFirst() {
        GpsPoint gp = listPoint.getFirst();
        return gp;
    }

    public void remove(LatLng location) {
        for (int i = 0; i < getListPointSize(); i++) {
            if (listPoint.get(i).getLat() == location.latitude && listPoint.get(i).getLon() == location.longitude) {
                listPoint.remove(i);
                latLngList.remove(i);
                break;
            }
        }
    }

    public GpsPoint getLocation() {
        return droneLocation;
    }

    public String toString() {
        String str = "";
        if (listPoint.size()==0) return "the ListPoint is empty";
        for (int i=0; i<listPoint.size(); i++) {
            GpsPoint gp = listPoint.get(i);
            if (i+1 == listPoint.size()) str = str + gp;
            else str = str + gp +"\n";
        }
        return str;
    }

    public int getListPointSize() { return listPoint.size(); }

    public void emptyList() {
        listPoint.clear();
        latLngList.clear();
    }

    public LinkedList<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setDroneLocation(LatLng point) {
        droneLocation = new GpsPoint(point.latitude,point.longitude);
    }

    public GpsPoint getDroneLocation() {
        return droneLocation;
    }

    public void setDroneGpsDataString(String data) {
        droneGpsDataString = data;
    }

    public String getDroneGpsDataString() {
        return droneGpsDataString;
    }

    public void resetDroneGpsDataString() {
        droneGpsDataString = "---> None GPS distance data <---\n";
    }
}