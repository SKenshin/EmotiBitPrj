package com.lab.uqac.emotibit.application.launcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lab.uqac.emotibit.application.launcher.Network.Connection;
import com.lab.uqac.emotibit.application.launcher.Network.NetworkUtils;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import GUI.EmotiBitButton;


public class MainActivity extends AppCompatActivity {

    private Button mButtonExit;


    private LinearLayout mVerticalLayout;
    private LinearLayout.LayoutParams mParams;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mTextButton;
    private Connection mConnection;
    private HashMap<View, InetAddress> mMapButton;
    private EmotiBitButton mEmotiBitButton;
    private Button mButtonHotspot;
    NetworkUtils mNetworkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonExit = (Button) findViewById(R.id.button_exit);
        mVerticalLayout = (LinearLayout) findViewById(R.id.row_main);

        mButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });

        mButtonHotspot = findViewById(R.id.button_connect);

        mMapButton = new HashMap<View, InetAddress>();

        mEmotiBitButton = new EmotiBitButton(this, mMapButton);

        int maxDevice = Integer.valueOf(getString(R.string.max_devices));

        mNetworkUtils = new NetworkUtils(this);

        int port = Integer.valueOf(getString(R.string.port_number));

        try {
            InetAddress broadcastAddress = mNetworkUtils.extractBroadcastAddress();

            if(broadcastAddress != null)
                mConnection = new Connection(port, broadcastAddress, maxDevice, mEmotiBitButton);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void enableHotspot(View view){

     //   mNetworkUtils.enableHotspot();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnection.end();
        mMapButton.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnection.start();
    }





}

