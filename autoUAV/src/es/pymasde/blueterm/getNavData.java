package es.pymasde.blueterm;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.NavDataListener;

public class getNavData {
    ARDrone drone;
    String str[];
    NavDataListener ndl;
    private float Battery;
    private float Yaw;
    private float Roll;
    private float Pitch;
    private float Altitude;


    public getNavData(ARDrone drone, String str[]) {
        this.drone = drone;
        this.str = str;
        Battery = Yaw = Roll = Pitch = Altitude = -1;
    }

    public void getNavdata() {
        ndl = new NavDataListener() {
            @Override
            public void navDataReceived(NavData nd) {
                String temp = "";
                Battery = nd.getBattery();
                Yaw = nd.getYaw();
                if (Yaw <0 && Yaw!=-1) Yaw += 360;
                Roll = nd.getRoll();
                Pitch = nd.getPitch();
                Altitude = nd.getAltitude();
                temp = temp + "Battery: " + Battery +"\t\t\t"+  "Yaw:         " + Yaw + "\n"+ "Roll:        " + Roll + "\t\t\t" + "Pitch:       " + Pitch + "\n";
                temp = temp + "Altitude:    " + Altitude + "\t\t\t" + "flyingState: " + nd.getFlyingState() + "\n";
                str[0] = temp;
            }
        };
        drone.addNavDataListener(ndl);
    }

    public float getYaw() {
        return Yaw;
    }
}
