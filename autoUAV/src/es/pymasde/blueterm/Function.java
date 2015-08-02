package es.pymasde.blueterm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Function {

    /*
        0 -> IR left
        1 -> IR front
        2 -> IR right
        3 -> MaxSensor front
        4 -> GPS Lon
        5 -> GPS Lat
     */
    public static final int sizeOfArray = 7;

    public static enum droneMode {
        Stay_And_Warn_Dynamic(1),
        Find_Azimuth(2),
        Fly_Straight_And_Beware(3),
        Immediate_Danger(4),
        Manual_Flight(5),
        Wait_5_seconds(6),
        Skip_Obstacle(7);

        private int code;

        private droneMode(int c) {
            code = c;
        }

        public int getVal() {
            return code;
        }

    }

    public static void fillMoveArray(float arr[], float a0, float a1, float a2, float a3) {
        arr[0] = a0;
        arr[1] = a1;
        arr[2] = a2;
        arr[3] = a3;
    }

    public static boolean isAllZero(float arr[], int size) {
        for (int i = 0; i<size; i++) {
            if (arr[i]!=0) return false;
        }
        return true;
    }

    public static boolean isAllZero(double arr[], int size) {
        for (int i = 0; i<size; i++) {
            if (arr[i]!=0) return false;
        }
        return true;
    }

    public static boolean isAllLowerNum(float arr[], int size, float num) {
        for (int i = 0; i<size; i++) {
            if (arr[i]>=num) return false;
        }
        return true;
    }

    public static float[] CutBlueString(String bluetooth) {
        float arr[] = new float[sizeOfArray];
        int index = 0;
        StringTokenizer st = new StringTokenizer(bluetooth, ", ");
        while (st.hasMoreTokens() && index < sizeOfArray) {
            String temp = st.nextToken();
            try {
                arr[index] = Float.parseFloat(temp);
            } catch (Exception e) {
                arr[index] = -1;
                System.out.println("Function.CutBlueString -> problem");
                e.printStackTrace();
            }
            index++;
        }
        return arr;
    }

    public static void appendLog(String text, String fileName) {
        File logFile = new File("sdcard/Download/log.file." + fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
