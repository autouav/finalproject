package es.pymasde.blueterm;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.codeminders.ardrone.ARDrone;

import java.io.IOException;

public class Keyboard {
    ARDrone drone;
    LinearLayout ll;
    boolean flag = false;
    SeekBar speedBar;
    SeekBar tiltBar;
    TextView numSpeed;
    TextView numTilt;

    SeekBar MaxSenBar;
    TextView MaxSenNum;
    SeekBar ImmSenBar;
    TextView ImmSenNum;

    public float progressChanged[]; // progressChanged[0] -> speed, progressChanged[1] -> tilt

    public Keyboard(ARDrone d, LinearLayout ll, SeekBar[] seekBars, final TextView[] num, float speed[]) {
        this.drone = d;
        this.ll = ll;
        this.speedBar = seekBars[0];
        this.tiltBar = seekBars[1];
        this.MaxSenBar = seekBars[2];
        this.ImmSenBar = seekBars[3];

        ll.setVisibility(View.GONE);
        this.progressChanged = speed;
        this.numSpeed = num[0];
        this.numTilt = num[1];
        this.MaxSenNum = num[2];
        this.ImmSenNum = num[3];

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
