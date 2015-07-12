package es.pymasde.blueterm;

import java.util.StringTokenizer;

public class Function {

    public static final int numOfSensor = 4;

    public static enum droneMode {
        Stay_And_Warn_Dynamic(1),
        Fly_Straight_And_Beware(2),
        Find_Azimuth(3);

        private int code;

        private droneMode(int c) {
            code = c;
        }

        public int getVal() {
            return code;
        }
    }

    public static void fillMoveArray(int arr[], int a0, int a1, int a2, int a3) {
        arr[0] = a0;
        arr[1] = a1;
        arr[2] = a2;
        arr[3] = a3;
    }

    public static boolean isAllZero(int arr[], int size) {
        for (int i = 0; i<size; i++) {
            if (arr[i]!=0) return false;
        }
        return true;
    }

    public static int[] CutBlueString(String bluetooth) {
        int arr[] = new int[numOfSensor];
        int index = 0;
        StringTokenizer st = new StringTokenizer(bluetooth, ", ");
        while (st.hasMoreTokens() && index < numOfSensor) {
            String temp = st.nextToken();
            try {
                arr[index] = (int)Double.parseDouble(temp);
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
