package es.pymasde.blueterm;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.codeminders.ardrone.ARDrone;

import java.io.IOException;
import java.util.Arrays;

public class Keyboard {
    ARDrone drone;          // get the singleton object ARDrone
    LinearLayout ll;        // get the LinearLayout of Keyboard
    boolean flag = false;   // set the flag to false - the ll not seem

    SeekBar speedBar;
    SeekBar tiltBar;
    SeekBar MaxSenBar;
    SeekBar ImmSenBar;

    TextView numSpeed;
    TextView numTilt;
    TextView MaxSenNum;
    TextView ImmSenNum;

    /*
        progressChanged[]
        [0] -> speed
        [1] -> tilt
        [2] -> Sonar Sensor
        [3] -> IR sensor
     */
    public float progressChanged[];

    /**
     * Constructor
     */
    public Keyboard(ARDrone Drone, LinearLayout ll, SeekBar[] seekBars, final TextView[] num, float indicatArray[]) {
        this.drone = Drone;
        this.ll = ll;
        this.speedBar = seekBars[0];
        this.tiltBar = seekBars[1];
        this.MaxSenBar = seekBars[2];
        this.ImmSenBar = seekBars[3];

        ll.setVisibility(View.GONE);
        this.progressChanged = indicatArray;
        this.numSpeed = num[0];
        this.numTilt = num[1];
        this.MaxSenNum = num[2];
        this.ImmSenNum = num[3];

        // ChangeListener of speed (in the move function)
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged[0] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                numSpeed.setText(progressChanged[0] + "");
            }
        });

        // ChangeListener of tilt max
        tiltBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged[1] = i;
                try {
                    drone.setConfigOption("control:control_iphone_tilt", Math.toRadians(i) + "");
                    drone.playLED(2, 10, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                numTilt.setText(progressChanged[1] + "");
            }
        });

        // ChangeListener of the Sonar (0 - Not at all sensitive, 400 - Very sensitive)
        MaxSenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged[2] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MaxSenNum.setText(progressChanged[2] + "");
            }
        });

        // ChangeListener of the IR sensors (800 - Not at all sensitive, 0 - Very sensitive)
        ImmSenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged[3] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ImmSenNum.setText(progressChanged[3] + "");
            }
        });
    }

    /**
     * function to switch the visibility of the keyboard
     */
    public void setVis() {
        if (flag == false) {
            ll.setVisibility(View.VISIBLE);
            flag = true;
        }
        else {
            ll.setVisibility(View.GONE);
            flag = false;
        }
    }
}
