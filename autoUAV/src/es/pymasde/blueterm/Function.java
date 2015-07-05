package es.pymasde.blueterm;

import java.util.StringTokenizer;

public class Function {

    public static final int numOfSensor = 3;

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

    public static boolean isAllZero(int arr[]) {
        for (int i = 0; i<arr.length; i++) {
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
                arr[index] = Integer.parseInt(temp);
            } catch (Exception e) {
                System.out.println("Function.CutBlueString -> problem");
            }
            index++;
        }
        return arr;
    }

}
