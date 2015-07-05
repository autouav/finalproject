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
    public int progressChanged;

    public Keyboard(LinearLayout ll, SeekBar speedBar, final TextView num) {
        this.ll = ll;
        this.speedBar = speedBar;
        ll.setVisibility(View.GONE);
        progressChanged = 10;
        this.num = num;

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                num.setText(progressChanged + "");
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
