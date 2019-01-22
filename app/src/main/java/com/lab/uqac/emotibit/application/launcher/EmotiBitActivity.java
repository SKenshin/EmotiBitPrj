package com.lab.uqac.emotibit.application.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class EmotiBitActivity extends AppCompatActivity {

    private Context _context;

    private TextView _textViewPercentageBat;
    private ProgressBar _progressBarBat;
    private int _progressStatus = 0;
    private Switch _switchRecord;
    private Switch _switchGPS;
    private ImageView _imViewRecStatus;
    private Button _buttonSignals;
    private Button _buttonGPS;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            float percentage = level/ (float) scale;
            _progressStatus = (int)((percentage)*100);
            _textViewPercentageBat.setText("" + _progressStatus + "%");
            _progressBarBat.setProgress(_progressStatus);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotibit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();

        String title = intent.getStringExtra("selected");
        getSupportActionBar().setTitle(title);

        _context = getApplicationContext();
        switchController();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        _context.registerReceiver(mBroadcastReceiver,iFilter);
        _textViewPercentageBat = (TextView) findViewById(R.id.percentage_bat);
        _progressBarBat = (ProgressBar) findViewById(R.id.progressbar_bat);
        _switchRecord   = (Switch)  findViewById(R.id.switch_record);
        _imViewRecStatus = (ImageView) findViewById(R.id.imview_rec);
        _buttonSignals = (Button) findViewById(R.id.button_signals);
        _buttonGPS = (Button) findViewById(R.id.button_gps);
    }


    private <T> void switchController(){

        _switchRecord = (Switch) findViewById(R.id.switch_record);
        _switchGPS = (Switch) findViewById(R.id.switch_gps);

        _switchRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                    _imViewRecStatus.setImageResource(R.drawable.record_on);
                else
                    _imViewRecStatus.setImageResource(R.drawable.record_off);
            }
        });

        _switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _buttonGPS.setEnabled(_switchGPS.isChecked());
            }
        });
    }

    public void displaySignals(View view){

        Intent intent = new Intent(this, SignalsActivity.class);

        startActivity(intent);
    }

    public void displayMap(View view){

        Intent intent = new Intent(this, MapsActivity.class);
        Log.d(LOG_TAG, "displayMap ----------------------- ");
        startActivity(intent);
    }
}
