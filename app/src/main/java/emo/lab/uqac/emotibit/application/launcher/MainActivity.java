package emo.lab.uqac.emotibit.application.launcher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import emo.lab.uqac.emotibit.R;



public class MainActivity extends AppCompatActivity {

    private Button _buttonEmot1;
    private Button _buttonEmot2;
    private Switch _switch1;
    private Switch _switch2;
    private Button _buttonExit;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _buttonExit = (Button) findViewById(R.id.button_exit);

        _buttonEmot1 = (Button) findViewById(R.id.button_emot1);
        _buttonEmot2 = (Button) findViewById(R.id.button_emot2);

        _switch1 = (Switch) findViewById(R.id.switch1);
        _switch2 = (Switch) findViewById(R.id.switch2);

        _buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });

        activateEmotiBit();
    }

    private <T> void activateEmotiBit(){

            _switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    _buttonEmot1.setEnabled(_switch1.isChecked());
                }
            });

            _switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    _buttonEmot2.setEnabled(_switch2.isChecked());
                }
            });
    }

    public void launchEmotiBit(View view) {

        Intent intent = new Intent(this, EmotiBitActivity.class);

        startActivity(intent);
    }
}

