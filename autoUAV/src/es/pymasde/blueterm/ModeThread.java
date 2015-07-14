package es.pymasde.blueterm;
import com.codeminders.ardrone.ARDrone;

import java.io.IOException;

public class ModeThread extends Thread {

    private static final int valMAX = 150;

    ARDrone drone;
    float move[];
    String bluetooth[];
    float speed[];
    Function.droneMode droneMode[];
    String whatThreadDo[];
    float sensorArr[];
    GpsPointContainer gpc;
    getNavData getND;


    public ModeThread(ARDrone drone, float move[], String bluetooth[], float speed[], Function.droneMode droneMode[], String whatThreadDo[], GpsPointContainer gpc, getNavData getND) {
        this.drone = drone;
        this.move = move;
        this.bluetooth = bluetooth;
        this.speed = speed;
        this.droneMode = droneMode;
        this.whatThreadDo = whatThreadDo;
        this.getND = getND;
        this.gpc = gpc;
    }

    public void run() {
        while (true) {
            // 0->left, 1->front, 2->right
            sensorArr = Function.CutBlueString(bluetooth[0]);


            if (droneMode[0] == Function.droneMode.Stay_And_Warn_Dynamic) {
                if (Function.isAllZero(move, 4) && Function.isAllZero(sensorArr, 3)) {
                    whatThreadDo[0] = "-> HOVER <-" + "  Stay_And_Warn_Dynamic";
                }
                else if (Function.isAllZero(move, 4) == false && Function.isAllZero(sensorArr, 3)) {
                    droneMode[0] = Function.droneMode.Manual_Flight;
                }
                else if (Function.isAllZero(sensorArr, 3)== false) {
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
                        if (aziFix>180 && aziFix<360) Function.fillMoveArray(move, 0,0,0,speed[0]); // drone.move(0,0,0,speed[0]);
                        else Function.fillMoveArray(move, 0, 0, 0, -speed[0]); // drone.move(0,0,0,-speed[0]);
                    }
                    else {
                        Function.fillMoveArray(move, 0, 0, 0, 0);
                        droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic; // -------------------------------------------------
                    }
                }
            }

            if (droneMode[0] == Function.droneMode.Immediate_Danger) {
                if (sensorArr[0] == 0 && sensorArr[1] != 0 && sensorArr[0] == 0) { // Front Obstacle -> Go Back
                    Function.fillMoveArray(move,0,speed[0],0,0);
                    whatThreadDo[0] = "-> GO BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] != 0 && sensorArr[1] == 0 && sensorArr[2] == 0) { // Left Obstacle -> Go Right
                    Function.fillMoveArray(move,speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO RIGHT ->" + "  Immediate_Danger";
                } else if (sensorArr[0] == 0 && sensorArr[1] == 0 && sensorArr[2] != 0) { // Right Obstacle -> Go Left
                    Function.fillMoveArray(move,-speed[0],0,0,0);
                    whatThreadDo[0] = "-> GO LEFT ->" + "  Immediate_Danger";
                } else if (sensorArr[0] != 0 && sensorArr[1] != 0 && sensorArr[2] == 0) { // Left-Front Obstacle -> Go Right-Back
                    Function.fillMoveArray(move,speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO RIGHT-BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] == 0 && sensorArr[1] != 0 && sensorArr[2] != 0) { // Right-Front Obstacle -> Go Left-Back
                    Function.fillMoveArray(move,-speed[0],speed[0],0,0);
                    whatThreadDo[0] = "-> GO LEFT-BACK ->" + "  Immediate_Danger";
                } else if (sensorArr[0] != 0 && sensorArr[1] == 0 && sensorArr[2] != 0) { // Right-Left Obstacle -> Go Straight
                    Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                    whatThreadDo[0] = "-> GO STRAIGHT ->" + "  Immediate_Danger";
                }
                else if (sensorArr[0] == 0 && sensorArr[1] == 0 && sensorArr[2] == 0) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic; // -------------------------------------------------
                }
            }

            if (droneMode[0] == Function.droneMode.Manual_Flight) {
                whatThreadDo[0] = "Manual_Flight";
                if (Function.isAllZero(move, 4)) {
                    droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;
                }
            }

            if (droneMode[0] == Function.droneMode.Fly_Straight_And_Beware) {
                whatThreadDo[0] = "Fly_Straight_And_Beware";
                Function.fillMoveArray(move, 0, -speed[0], 0, 0);
                if (Function.isAllZero(sensorArr, 3)== false) {
                    droneMode[0] = Function.droneMode.Immediate_Danger;
                }
                else if (sensorArr[3] <= valMAX) {
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
        }



    }
}
