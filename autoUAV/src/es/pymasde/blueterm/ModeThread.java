package es.pymasde.blueterm;
import com.codeminders.ardrone.ARDrone;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;

public class ModeThread extends Thread {

    private static Function.droneMode prevMode;

    ARDrone drone;
    float move[];
    String bluetooth[];
    float speed[];
    Function.droneMode droneMode[];
    String whatThreadDo[];
    float sensorArr[];
    GpsPointContainer gpc;
    getNavData getND;

    public GoogleMap map;
    public MarkerOptions myPosition;


    public ModeThread(ARDrone drone, float move[], String bluetooth[], float speed[], Function.droneMode droneMode[], String whatThreadDo[], GpsPointContainer gpc, getNavData getND, GoogleMap map, MarkerOptions myPosition) {
        this.drone = drone;
        this.move = move;
        this.bluetooth = bluetooth;
        this.speed = speed;
        this.droneMode = droneMode;
        prevMode = droneMode[0];
        this.whatThreadDo = whatThreadDo;
        this.getND = getND;
        this.gpc = gpc;
        this.map = map;
        this.myPosition = myPosition;
    }

    public void run() {
        while (true) {
            // 0->left, 1->front, 2->right, 3->MaxSensor, 4,5->GPS_Lon_Lat
            sensorArr = Function.CutBlueString(bluetooth[0]);

            //System.out.println("arr = " + Arrays.toString(sensorArr));
            if (droneMode[0] == Function.droneMode.Stay_And_Warn_Dynamic) {
                if (Function.isAllZero(move, 4) && Function.isAllLowerNum(sensorArr, 3, speed[3])) {
                    whatThreadDo[0] = "-> HOVER <-" + "  Stay_And_Warn_Dynamic";
                }
                else if (Function.isAllZero(move, 4) == false && Function.isAllLowerNum(sensorArr, 3, speed[3])) {
                    prevMode = droneMode[0];
                    droneMode[0] = Function.droneMode.Manual_Flight;

                }
                else if (Function.isAllLowerNum(sensorArr, 3, speed[3]) == false) {
                    prevMode = droneMode[0];
                    droneMode[0] = Function.droneMode.Immediate_Danger;
                }
            }

            if (droneMode[0] == Function.droneMode.Find_Azimuth) {
                whatThreadDo[0] = "-> YAW ->" + "  Find_Azimuth";
                if (gpc.isEmpty()) droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;
                else {
                    GpsPoint temp = gpc.getFirst();
                    double azi = Cords.azmDist(gpc.getLocation(), temp)[0];
                    double aziFix = getND.Yaw - azi;
                    if (aziFix < 0) aziFix += 360;

                    if (aziFix < 350 && aziFix > 10) {
                        if (aziFix>180 && aziFix<360) Function.fillMoveArray(move, 0,0,0,speed[0]);
                        else Function.fillMoveArray(move, 0, 0, 0, -speed[0]);
                    }
                    else {
                        Function.fillMoveArray(move, 0, 0, 0, 0);
                        prevMode = droneMode[0];
                        droneMode[0] = Function.droneMode.Fly_Straight_And_Beware;
                    }
                }
            }

            if (droneMode[0] == Function.droneMode.Immediate_Danger) {
                // 0 ---  <= speed[3]  1 --- > speed[3]
                if (sensorArr[0] <= speed[3] && sensorArr[1] > speed[3] && sensorArr[2] <= speed[3]) { // Front Obstacle -> Go Back
                    Function.fillMoveArray(move,0,speed[0],0,0);
                    whatThreadDo[0] = "-> GO BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] > speed[3] && sensorArr[1] <= speed[3] && sensorArr[2] <= speed[3]) { // Left Obstacle -> Go Right
                    Function.fillMoveArray(move,speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO RIGHT ->" + "  Immediate_Danger";
                } else if (sensorArr[0] <= speed[3] && sensorArr[1] <= speed[3] && sensorArr[2] > speed[3]) { // Right Obstacle -> Go Left
                    Function.fillMoveArray(move,-speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO LEFT ->" + "  Immediate_Danger";
                } else if (sensorArr[0] > speed[3] && sensorArr[1] > speed[3] && sensorArr[2] <= speed[3]) { // Left-Front Obstacle -> Go Right-Back
                    Function.fillMoveArray(move,speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO RIGHT-BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] <= speed[3] && sensorArr[1] > speed[3] && sensorArr[2] > speed[3]) { // Right-Front Obstacle -> Go Left-Back
                    Function.fillMoveArray(move,-speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO LEFT-BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] > speed[3] && sensorArr[1] <= speed[3] && sensorArr[2] > speed[3]) { // Right-Left Obstacle -> Go Straight
                    Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                    whatThreadDo[0] = "-> GO STRAIGHT ->" + "  Immediate_Danger";
                }
                else if (sensorArr[0] <= speed[3] && sensorArr[1] <= speed[3] && sensorArr[2] <= speed[3]) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    droneMode[0] = prevMode; // -------------------------------------------------
                }
            }

            if (droneMode[0] == Function.droneMode.Manual_Flight) {
                whatThreadDo[0] = "Manual_Flight";
                if (Function.isAllZero(move, 4)) {
                    prevMode = droneMode[0];
                    droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;
                }
            }

            if (droneMode[0] == Function.droneMode.Fly_Straight_And_Beware) {
                whatThreadDo[0] = "Fly_Straight_And_Beware";
                if (Function.isAllLowerNum(sensorArr, 3, speed[3]) == false) {
                    prevMode = droneMode[0];
                    droneMode[0] = Function.droneMode.Immediate_Danger;
                }
                else if (sensorArr[3] <= speed[2]) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic; // -------------------------------------------------
                    try {
                        drone.playLED(1,2,10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                }
            }

            if (sensorArr[5] > 20 && sensorArr[5] < 50 && sensorArr[4] > 20 && sensorArr[4] < 50) {
                LatLng point = new LatLng((double) sensorArr[4], (double) sensorArr[5]);
                gpc.setDroneLocation(point);
                myPosition.position(point);
                if (gpc.isEmpty() == false) {
                    double tempData[] = Cords.azmDist(gpc.getLocation(), gpc.getFirst());
                    String distance = String.format("%.2f",tempData[1]);
                    gpc.setDroneGpsDataString("---> GPS distance data <---\nthe Distance to the next point is: " + distance +" Meters");
                }
                else {
                    gpc.resetDroneGpsDataString();
                }
            }
        }
    }
}

