package es.pymasde.blueterm;
import com.codeminders.ardrone.ARDrone;

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
                if (Function.isAllZero(move, Function.numOfSensor) && Function.isAllZero(sensorArr, 3)) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);
                    whatThreadDo[0] = "-> HOVER <-";
                }
                else if (Function.isAllZero(move, Function.numOfSensor) == false && Function.isAllZero(sensorArr, 3)) {
                    droneMode[0] = Function.droneMode.Find_Azimuth;
                }
                else if (Function.isAllZero(move, Function.numOfSensor) == false && Function.isAllZero(sensorArr, 3)== false) {
                    droneMode[0] = Function.droneMode.Immediate_Danger;
                }
            }

            if (droneMode[0] == Function.droneMode.Find_Azimuth) {

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
                        droneMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;
                    }
                }
            }


                /*
                 if (Function.isAllZero(sensorArr, 3) && sensorArr[3] >= valMAX) {
                    drone.move(move[0],move[1],move[2],move[3]);
                }

                else if (sensorArr[3] < valMAX) {
                    Function.fillMoveArray(move, 0, 0, 0, 0);

                }

                else {
                    if (sensorArr[0] == 0 && sensorArr[1] != 0 && sensorArr[0] == 0) { // Front Obstacle -> Go Back
                        drone.move(0,speed[0],0,0);
                        whatThreadDo[0] = "-> GO BACK ->";
                    } else if (sensorArr[0] != 0 && sensorArr[1] == 0 && sensorArr[2] == 0) { // Left Obstacle -> Go Right
                        drone.move(speed[0],0,0,0);
                        whatThreadDo[0] = "-> GO RIGHT ->";
                    } else if (sensorArr[0] == 0 && sensorArr[1] == 0 && sensorArr[2] != 0) { // Right Obstacle -> Go Left
                        drone.move(-speed[0],0,0,0);
                        whatThreadDo[0] = "-> GO LEFT ->";
                    } else if (sensorArr[0] != 0 && sensorArr[1] != 0 && sensorArr[2] == 0) { // Left-Front Obstacle -> Go Right-Back
                        drone.move(speed[0],speed[0],0,0);
                        whatThreadDo[0] = "-> GO RIGHT-BACK ->";
                    } else if (sensorArr[0] == 0 && sensorArr[1] != 0 && sensorArr[2] != 0) { // Right-Front Obstacle -> Go Left-Back
                        drone.move(-speed[0],speed[0],0,0);
                        whatThreadDo[0] = "-> GO LEFT-BACK ->";
                    } else if (sensorArr[0] != 0 && sensorArr[1] == 0 && sensorArr[2] != 0) { // Right-Left Obstacle -> Go Straight
                        drone.move(-speed[0],speed[0],0,0);
                        whatThreadDo[0] = "-> GO STRAIGHT ->";
                    }
                }
                */
        }

    }
}

