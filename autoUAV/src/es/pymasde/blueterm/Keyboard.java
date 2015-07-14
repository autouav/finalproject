package es.pymasde.blueterm;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Keyboard {
    LinearLayout ll;
    boolean flag = false;
    SeekBar speedBar;
    TextView num;
    public float progressChanged[];

    public Keyboard(LinearLayout ll, SeekBar speedBar, final TextView num, float speed[]) {
        this.ll = ll;
        this.speedBar = speedBar;
        ll.setVisibility(View.GONE);
        this.progressChanged = speed;
        this.num = num;

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged[0] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                num.setText(progressChanged[0] + "");
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
