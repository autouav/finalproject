package es.pymasde.blueterm;

/**
 * Created by zohar on 12/07/2015.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class GpsPointContainer {

    private GpsPoint droneLocation;
    private LinkedList<GpsPoint> listPoint;
    private LinkedList<LatLng> latLngList;

    public GpsPointContainer(GpsPoint droneLocation) {
        this.droneLocation = droneLocation;
        listPoint = new LinkedList<GpsPoint>();
        latLngList = new LinkedList<LatLng>();
        /*
         ------------------------------------------just for test
          */
        //GpsPoint gp = new GpsPoint(35.086128,32.167524);//(35.0852831, 32.1675153);
        //listPoint.add(gp);
    }

    public void add(GpsPoint gp) {
        listPoint.add(gp);
        latLngList.add(gp.getLatLng());
    }

    public boolean isEmpty() {return listPoint.isEmpty();}

    public void addFootBallList() {
        GpsPoint gp1 = new GpsPoint(35.208137,32.105368);
        GpsPoint gp2 = new GpsPoint(35.208568,32.105648);
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
}