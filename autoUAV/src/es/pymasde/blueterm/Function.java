package es.pymasde.blueterm;

import java.util.StringTokenizer;

public class Function {

    public static final int numOfSensor = 4;

    public static enum droneMode {
        Stay_And_Warn_Dynamic(1),
        Find_Azimuth(2),
        Fly_Straight_And_Beware(3),
        Immediate_Danger(4);

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

    public static float[] CutBlueString(String bluetooth) {
        float arr[] = new float[numOfSensor];
        int index = 0;
        StringTokenizer st = new StringTokenizer(bluetooth, ", ");
        while (st.hasMoreTokens() && index < numOfSensor) {
            String temp = st.nextToken();
            try {
                arr[index] = (float) Double.parseDouble(temp);
            } catch (Exception e) {
                arr[index] = -1;
                System.out.println("Function.CutBlueString -> problem");
                e.printStackTrace();
            }
            index++;
        }
        return arr;
    }

}
