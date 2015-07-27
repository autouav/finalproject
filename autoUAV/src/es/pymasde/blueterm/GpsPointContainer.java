package es.pymasde.blueterm;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

public class GpsPointContainer {

    private LatLng droneLocation;
    private String droneGpsDataString;
    private LinkedList<LatLng> listPoint;

    public GpsPointContainer(LatLng droneLocation) {
        this.droneLocation = droneLocation;
        droneGpsDataString = "---> None GPS distance data <---\n";
        listPoint = new LinkedList<LatLng>();
    }

    public void add(LatLng ll) {
        listPoint.add(ll);
    }

    public boolean isEmpty() {return listPoint.isEmpty();}

    public void addFootBallList() {
        LatLng gp1 = new LatLng(32.105368, 35.208137);
        LatLng gp2 = new LatLng(32.105648, 35.208568);
        listPoint.clear();
        listPoint.add(gp1);
        listPoint.add(gp2);
    }

    public LatLng removeFirst() {
        LatLng ll = listPoint.removeFirst();
        return ll;
    }

    public LatLng getFirst() {
        LatLng ll = listPoint.getFirst();
        return ll;
    }

    public void remove(LatLng location) {
        for (int i = 0; i < getListPointSize(); i++) {
            if (listPoint.get(i).latitude == location.latitude && listPoint.get(i).longitude == location.longitude) {
                listPoint.remove(i);
                break;
            }
        }
    }

    public LatLng getLocation() {
        return droneLocation;
    }

    public String toString() {
        String str = "";
        if (listPoint.size()==0) return "the ListPoint is empty";
        for (int i=0; i<listPoint.size(); i++) {
            LatLng gp = listPoint.get(i);
            if (listPoint.size() == i+1) str = str + gp;
            else str = str + gp +"\n";
        }
        return str;
    }

    public int getListPointSize() { return listPoint.size(); }

    public void emptyList() {
        listPoint.clear();
    }

    public LinkedList<LatLng> getLatLngList() {
        return listPoint;
    }

    public void setDroneLocation(LatLng point) {
        droneLocation = new LatLng(point.latitude,point.longitude);
    }

    public LatLng getDroneLocation() {
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