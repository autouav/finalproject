package es.pymasde.blueterm;

/**
 * Created by zohar on 12/07/2015.
 */

import java.util.LinkedList;

public class GpsPointContainer {

    private GpsPoint droneLocation;
    private LinkedList<GpsPoint> listPoint;

    public GpsPointContainer(GpsPoint droneLocation) {
        this.droneLocation = droneLocation;
        listPoint = new LinkedList<GpsPoint>();
        /*
         ------------------------------------------just for test
          */
        GpsPoint gp = new GpsPoint(35.209840, 32.102688);
        listPoint.add(gp);
    }

    public void add(GpsPoint gp) {
        listPoint.add(gp);
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
        return gp;
    }

    public GpsPoint getFirst() {
        GpsPoint gp = listPoint.getFirst();
        return gp;
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




}