package es.pymasde.blueterm;
import com.codeminders.ardrone.ARDrone;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ModeThread extends Thread {

    private static Function.droneMode prevMode;

    ARDrone drone;
    float move[];
    String bluetooth[];
    float speed[];
    Function.droneMode droneMode[];
    String whatThreadDo[];
    static float bluetoothData[];
    GpsPointContainer gpc;
    getNavData getND;

    public GoogleMap map;
    public MarkerOptions myPosition;

    double azimutDistance[];

    private static long start = System.currentTimeMillis();
    private static long end = start;

    private static double GpsRadius = 10.0;
    private static int rangeToFindAziAgain[] = {300,60};

    // Log variables
    final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Long currentTime = System.currentTimeMillis();
    Long prevTime = currentTime;
    Date resultdate = new Date(currentTime);
    String logName = resultdate.toString();

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

    public void setMode(Function.droneMode newMode) {
        prevMode = droneMode[0];
        droneMode[0] = newMode;
    }

    public void run() {
        while (true) {

            // 0->left, 1->front, 2->right, 3->MaxSensor, 4,5->GPS_Lon_Lat, 6->Yaw from compass
            bluetoothData = Function.CutBlueString(bluetooth[0]);

            if (currentTime - prevTime > 500) {
                Function.appendLog(dateFormat.format(resultdate)
                        + ";\tDrone mode: " + whatThreadDo[0]
                        + ";\tPrev mode: " + prevMode.name()
                        //+ ";\tGPS Data: " + gpc.getLocation()
                        + ";\tSensor Arr: " + Arrays.toString(bluetoothData), logName);

                prevTime = currentTime;
            }

            currentTime = System.currentTimeMillis();
            resultdate.setTime(currentTime);

            azimutDistance = new double[]{0,0,0};
            if (bluetoothData[5] > 20 && bluetoothData[5] < 50 && bluetoothData[4] > 20 && bluetoothData[4] < 50) {
                LatLng point = new LatLng((double) bluetoothData[4], (double) bluetoothData[5]);
                gpc.setDroneLocation(point);
                myPosition.position(point);
                if (gpc.isEmpty() == false) {
                    azimutDistance = Cords.azmDist(gpc.getLocation(), gpc.getFirst());
                    String distance = String.format("%.2f",azimutDistance[1]);
                    gpc.setDroneGpsDataString("---> GPS distance data <---\nthe Distance to the next point is: " + distance +" Meters");
                }
                else gpc.resetDroneGpsDataString();
            }

            if (droneMode[0] == Function.droneMode.Stay_And_Warn_Dynamic) {
                if (Function.isAllZero(move, 4) && Function.isAllLowerNum(bluetoothData, 3, speed[3])) {
                    whatThreadDo[0] = "-> HOVER <-" + "  Stay_And_Warn_Dynamic";
                }
                else if (Function.isAllZero(move, 4) == false && Function.isAllLowerNum(bluetoothData, 3, speed[3])) {
                    setMode(Function.droneMode.Manual_Flight);

                }
                else if (Function.isAllLowerNum(bluetoothData, 3, speed[3]) == false) {
                    setMode(Function.droneMode.Immediate_Danger);
                }
            }

            if (droneMode[0] == Function.droneMode.Find_Azimuth) {
                whatThreadDo[0] = "-> YAW ->" + "  Find_Azimuth";
                if (gpc.isEmpty()) {
                    Function.fillMoveArray(move,0,0,0,0);
                    droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;
                }
                else {
                    LatLng temp = gpc.getFirst();
                    double azi = Cords.azmDist(gpc.getLocation(), temp)[0];
                    double aziFix = bluetoothData[6] - azi;
                    if (aziFix < 0) aziFix += 360;

                    // need to check it..
                    if (Function.isAllLowerNum(bluetoothData, 3, speed[3]) == false) {
                        setMode(Function.droneMode.Immediate_Danger);
                    }

                    if (aziFix < 350 && aziFix > 10) {
                        if (aziFix>180 && aziFix<360) Function.fillMoveArray(move, 0,0,0,speed[0]);
                        else Function.fillMoveArray(move, 0, 0, 0, -speed[0]);
                    }
                    else {
                        Function.fillMoveArray(move, 0, 0, 0, 0);
                        setMode(Function.droneMode.Fly_Straight_And_Beware);
                    }
                }
            }

            if (droneMode[0] == Function.droneMode.Immediate_Danger) {
                // 0 ---  <= speed[3]  1 --- > speed[3]
                if (bluetoothData[0] <= speed[3] && bluetoothData[1] > speed[3] && bluetoothData[2] <= speed[3]) { // Front Obstacle -> Go Back
                    Function.fillMoveArray(move,0,speed[0],0,0);
                    whatThreadDo[0] = "-> GO BACK ->" + "  Immediate_Danger";
                } else if (bluetoothData[0] > speed[3] && bluetoothData[1] <= speed[3] && bluetoothData[2] <= speed[3]) { // Left Obstacle -> Go Right
                    Function.fillMoveArray(move,speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO RIGHT ->" + "  Immediate_Danger";
                } else if (bluetoothData[0] <= speed[3] && bluetoothData[1] <= speed[3] && bluetoothData[2] > speed[3]) { // Right Obstacle -> Go Left
                    Function.fillMoveArray(move,-speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO LEFT ->" + "  Immediate_Danger";
                } else if (bluetoothData[0] > speed[3] && bluetoothData[1] > speed[3] && bluetoothData[2] <= speed[3]) { // Left-Front Obstacle -> Go Right-Back
                    Function.fillMoveArray(move,speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO RIGHT-BACK ->" + "  Immediate_Danger";
                } else if (bluetoothData[0] <= speed[3] && bluetoothData[1] > speed[3] && bluetoothData[2] > speed[3]) { // Right-Front Obstacle -> Go Left-Back
                    Function.fillMoveArray(move,-speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO LEFT-BACK ->" + "  Immediate_Danger";
                } else if (bluetoothData[0] > speed[3] && bluetoothData[1] <= speed[3] && bluetoothData[2] > speed[3]) { // Right-Left Obstacle -> Go Straight
                    Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                    whatThreadDo[0] = "-> GO STRAIGHT ->" + "  Immediate_Danger";
                }
                else if (bluetoothData[0] <= speed[3] && bluetoothData[1] <= speed[3] && bluetoothData[2] <= speed[3]) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    droneMode[0] = prevMode;
                }
            }

            if (droneMode[0] == Function.droneMode.Manual_Flight) {
                whatThreadDo[0] = "Manual_Flight";
                if (Function.isAllZero(move, 4)) {
                    setMode(Function.droneMode.Stay_And_Warn_Dynamic);
                }
            }

            if (droneMode[0] == Function.droneMode.Fly_Straight_And_Beware) {
                whatThreadDo[0] = "Fly_Straight_And_Beware";
                if (Function.isAllLowerNum(bluetoothData, 3, speed[3]) == false) {
                    setMode(Function.droneMode.Immediate_Danger);
                }
                else if (bluetoothData[3] <= speed[2]) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    setMode(Function.droneMode.Skip_Obstacle);
                    try {
                        drone.playLED(1,5,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (Function.isAllZero(azimutDistance,3)==false && azimutDistance[1] < GpsRadius) {
                    gpc.removeFirst();
                    start = System.currentTimeMillis();
                    setMode(Function.droneMode.Wait_5_seconds);
                }
                else if (Function.isAllZero(azimutDistance,3)==false &&
                        (azimutDistance[0]<rangeToFindAziAgain[0] || azimutDistance[0]>rangeToFindAziAgain[1])) {
                    setMode(Function.droneMode.Find_Azimuth);
                }
                else {
                    Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                }
            }

            if (droneMode[0] == Function.droneMode.Wait_5_seconds) {
                whatThreadDo[0] = "-> HOVER <-" + " Wait 5 seconds";
                Function.fillMoveArray(move,0,0,0,0);
                end = System.currentTimeMillis();

                // need to check it...
                if (Function.isAllLowerNum(bluetoothData, 3, speed[3]) == false) {
                    setMode(Function.droneMode.Immediate_Danger);
                }

                if (end - start > 5000) {
                    setMode(Function.droneMode.Find_Azimuth);
                }
            }

            if (droneMode[0] == Function.droneMode.Skip_Obstacle) {
                whatThreadDo[0] = "Skip_Obstacle";
                // need to check it...
                if (Function.isAllLowerNum(bluetoothData, 3, speed[3]) == false) {
                    setMode(Function.droneMode.Immediate_Danger);
                }
                else if (bluetoothData[3] <= speed[2]) {
                    Function.fillMoveArray(move,speed[0],0,0,0);
                }
                else {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    setMode(Function.droneMode.Fly_Straight_And_Beware);
                }
            }
        }
    }
}

