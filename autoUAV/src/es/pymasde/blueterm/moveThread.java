package es.pymasde.blueterm;
import com.codeminders.ardrone.ARDrone;

import java.io.IOException;

public class moveThread extends Thread {

    ARDrone drone;
    int move[];
    String bluetooth[];
    float speed[];
    Function.droneMode droneMode[];
    String whatThreadDo[];
    int sensorArr[];

    public moveThread(ARDrone drone, int move[], String bluetooth[], float speed[], Function.droneMode droneMode[], String whatThreadDo[], GpsPoint gpsPoint) {
        this.drone = drone;
        this.move = move;
        this.bluetooth = bluetooth;
        this.speed = speed;
        this.droneMode = droneMode;
        this.whatThreadDo = whatThreadDo;
    }

    public void run() {
        while (true) {
            // 0->left, 1->front, 2->right
            sensorArr = Function.CutBlueString(bluetooth[0]);
            try {
                if (Function.isAllZero(move, Function.numOfSensor)==true && Function.isAllZero(sensorArr, 3)==truez) {
                    drone.hover();
                    whatThreadDo[0] = "-> HOVER <-";
                }
                else if (Function.isAllZero(sensorArr, 3)==true) {
                    drone.move(move[0],move[1],move[2],move[3]);
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

            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("moveThread -> problem");
            }
        }
    }

}
