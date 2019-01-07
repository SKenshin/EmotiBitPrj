package emo.lab.uqac.emotibit.application.launcher;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import emo.lab.uqac.emotibit.R;

public class EmotiBitActivity extends AppCompatActivity {

    private Context mContext;

    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            float percentage = level/ (float) scale;
            mProgressStatus = (int)((percentage)*100);
            mTextViewPercentage.setText("" + mProgressStatus + "%");
            mProgressBar.setProgress(mProgressStatus);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);
        mTextViewPercentage = (TextView) findViewById(R.id.tv_percentage);
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
    }
}
