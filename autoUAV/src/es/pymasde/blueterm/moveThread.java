package es.pymasde.blueterm;

import com.codeminders.ardrone.ARDrone;

import java.io.IOException;
import java.util.Arrays;

public class MoveThread extends Thread{

    ARDrone drone;
    float move[];
    float speed[];

    public MoveThread(ARDrone drone, float move[], float speed[]) {
        this.drone = drone;
        this.move = move;
        this.speed = speed;
    }

    public void run() {
        while (true) {
            try {
                if (Function.isAllZero(move, 4)) drone.hover();
                else drone.move(move[0], move[1], move[2], move[3]);
                System.out.println("move = " + Arrays.toString(move));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
