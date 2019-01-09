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

    private Button button_emot1;
    private Button button_emot2;
    private Switch switch1;
    private Switch switch2;
    private Button button_exit;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MESSAGE =
            "com.example.android.twoactivities.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_exit = (Button) findViewById(R.id.button_exit);

        button_emot1 = (Button) findViewById(R.id.button_emot1);
        button_emot2 = (Button) findViewById(R.id.button_emot2);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);

        button_exit.setOnClickListener(new View.OnClickListener() {
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

            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    button_emot1.setEnabled(switch1.isChecked());
                }
            });

            switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    button_emot2.setEnabled(switch2.isChecked());
                }
            });
    }

    public void launchEmotiBit(View view) {
        Log.d(LOG_TAG, "Button clicked!");

        Intent intent = new Intent(this, EmotiBitActivity.class);

        startActivity(intent);
    }
}

