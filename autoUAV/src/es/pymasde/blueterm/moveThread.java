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

    public moveThread(ARDrone drone, int move[], String bluetooth[], float speed[], Function.droneMode droneMode[], String whatThreadDo[]) {
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
            int sensorArr[] = Function.CutBlueString(bluetooth[0]);
            try {
                if (Function.isAllZero(move)==true && Function.isAllZero(sensorArr)==true) {
                    drone.hover();
                    whatThreadDo[0] = "-> HOVER <-";
                }
                else if (Function.isAllZero(sensorArr)==true) {
                    drone.move(move[0],move[1],move[2],move[3]);
                }

                else {
                    if (sensorArr[1] != 0 && sensorArr[0] == 0) {
                        drone.move(-speed[0],0,0,0);
                        whatThreadDo[0] = "Bypassing an obstacle";
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
