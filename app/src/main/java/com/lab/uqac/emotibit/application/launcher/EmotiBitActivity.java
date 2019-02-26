package com.lab.uqac.emotibit.application.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.lab.uqac.emotibit.application.launcher.Datas.ExtractedDatas;
import com.lab.uqac.emotibit.application.launcher.Datas.ExtractionDatas;
import com.lab.uqac.emotibit.application.launcher.Datas.MessageGenerator;
import com.lab.uqac.emotibit.application.launcher.Datas.TypesDatas;
import com.lab.uqac.emotibit.application.launcher.Drawing.PlotDatas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import listener.OnSwipeTouchListener;

public class EmotiBitActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, View.OnTouchListener {

    private Context mContext;

    private TextView mTextViewPercentageBat;
    private TextView mTextViewIP;
    private TextView mTextViewGPS;
    private TextView mTextViewHib;
    private ProgressBar mProgressBarBat;
    private ImageView mImViewRecStatus;
    private ImageView mImViewLocation;
    private ImageView mImViewLogo;
    private Button mButtonRecord;
    private Button mButtonGPS;
    private Button mButtonHibernate;
    private Button mButtonSendNote;
    private boolean mIsRecord = false;
    private boolean mIsHibernate = false;
    private boolean mIsGPS = false;
    private GoogleMap mMap;
    private EditText mEditText;
    private Spinner mSpinner;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private int mSelectedGraphPosition = 0;
    private ScrollView mScrollView;
    private int mColor;
    private AsyncTask<Void, String, Void> mAsynck;
    private ExtractionDatas mExtraction;
    private PlotDatas mPlotDatas;
    private HashMap<String, TypesDatas> mMapGraphSelector;
    private DatagramSocket mSocket;
    private int mPort;
    private DatagramPacket mDatagramPacketReceive;
    private GraphView mGraphView;
    private InetAddress mInetAdress;
    private AsyncTask<Void, String, Void> mSendActionTask;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String LOG_TAG = EmotiBitActivity.class.getSimpleName();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotibit);
        Intent intent = getIntent();
        String title = intent.getStringExtra("selected");

        mPort = Integer.valueOf(getString(R.string.port_number));
        mInetAdress = (InetAddress) intent.getSerializableExtra("address");
       // Log.d("CHOUF", "time = " + mInetAdress.getHostName());
        getSupportActionBar().setTitle(title);
        mContext = getApplicationContext();
        mTextViewGPS = (TextView) findViewById(R.id.text_gps);
        mTextViewIP = (TextView) findViewById(R.id.text_ip);
        mTextViewHib = (TextView) findViewById(R.id.text_hibernate);

        mTextViewHib.setText("Hibernate: "+ "OFF");
        mTextViewGPS.setText("GPS: " + "OFF");



        mTextViewPercentageBat = (TextView) findViewById(R.id.percentage_bat);
        mProgressBarBat = (ProgressBar) findViewById(R.id.progressbar_bat);
        mImViewRecStatus = (ImageView) findViewById(R.id.imview_rec);
        mImViewLocation = (ImageView) findViewById(R.id.imview_loc);
        mImViewLogo = (ImageView) findViewById(R.id.imview_log);
        mButtonRecord = (Button) findViewById(R.id.button_record);
        ColorDrawable viewColor = (ColorDrawable) mButtonRecord.getBackground();
        mColor = viewColor.getColor();

        mButtonGPS = (Button) findViewById(R.id.button_gps);
        mButtonHibernate = (Button) findViewById(R.id.button_hibernate);
        mButtonSendNote = (Button) findViewById(R.id.button_send);
        mButtonSendNote.setOnClickListener(this);
        mButtonRecord.setOnClickListener(this);
        mButtonGPS.setOnClickListener(this);
        mButtonHibernate.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.editText_note);
        mEditText.setOnTouchListener(this);
        mEditText.setFocusable(false);
        mSpinner = findViewById(R.id.spinner);
        mGraphView = (GraphView) findViewById(R.id.graph);
        mScrollView = findViewById(R.id.scroll_v);
        mScrollView.setOnTouchListener(this);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_array, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mSelectedGraphPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*Signals*/
        mSeries1 = new LineGraphSeries<>(new DataPoint[] {});

        mSeries2 = new LineGraphSeries<>(new DataPoint[] {});

        mSeries3 = new LineGraphSeries<>(new DataPoint[] {});


        mGraphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        mGraphView.setOnTouchListener(new OnSwipeTouchListener(this){

            @Override
            public void onSwipeLeft() {
                if(mSelectedGraphPosition > 0)
                    mSelectedGraphPosition--;

                mSpinner.setSelection(mSelectedGraphPosition);
            }

            @Override
            public void onSwipeRight() {
                if(mSelectedGraphPosition < adapter.getCount() - 1)
                    mSelectedGraphPosition++;
                mSpinner.setSelection(mSelectedGraphPosition);
            }


        });

        //MAP
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int index, KeyEvent keyEvent) {

                if (index == EditorInfo.IME_ACTION_DONE) {
                    //  editText.clearFocus();
                    //editText.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        mEditText.setTextColor(Color.GRAY);
        mEditText.setBackgroundColor(Color.YELLOW);

        mGraphView.addSeries(mSeries1);
        mGraphView.addSeries(mSeries2);
        mGraphView.addSeries(mSeries3);

        mSeries1.setColor(Color.RED);
        mSeries2.setColor(Color.BLUE);
        mSeries3.setColor(Color.BLACK);

        mPlotDatas = new PlotDatas(mGraphView, mSeries1, mSeries2, mSeries3);

        mMapGraphSelector = TypesDatas.mapString();

        mSpinner.setSelection(TypesDatas.PPGGRN.getmSelectedGraph());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAsynck.cancel(true);
        if(mSocket != null)
            mSocket.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiveAndPlot();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAsynck.cancel(true);
        if(mSocket != null)
            mSocket.close();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAsynck.cancel(true);
        if(mSocket != null)
            mSocket.close();
    }

    private void setBatteryLevel(int value){

        mTextViewPercentageBat.setText("" + value + "%");
        mProgressBarBat.setProgress(value);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if(view == mEditText ){
            mEditText.setFocusableInTouchMode(true);
            mEditText.setFocusable(true);
            mEditText.setBackgroundColor(Color.TRANSPARENT);
            mEditText.setText("");
            mEditText.setTextColor(Color.BLACK);
            return false;
        }else if(view == mScrollView && (event.getX()< mEditText.getX() || event.getX()> mEditText.getMeasuredWidth())
                && (event.getY()< mEditText.getY()  || event.getY()> mEditText.getMeasuredHeight())){

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            mEditText.setFocusableInTouchMode(false);
            mEditText.setFocusable(false);
            if(mEditText.getText().toString().trim().compareTo("") == 0)
            {
                mEditText.setBackgroundColor(Color.YELLOW);
                mEditText.setText("Add a note");
                mEditText.setTextColor(Color.GRAY);
            }
            return false;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

        if( view == mButtonRecord)
        {
            if(!mIsRecord)
            {
                sendAction(TypesDatas.RB, "");
                mImViewRecStatus.setImageResource(R.drawable.rec_64);
                mImViewRecStatus.setVisibility(View.VISIBLE);
                mButtonRecord.setText("Stop Recording");
                mButtonRecord.setBackgroundColor(Color.RED);
                mIsRecord = true;

            }else{
                sendAction(TypesDatas.RE, "");
                mImViewRecStatus.setImageResource(R.drawable.rec_64);
                mImViewRecStatus.setVisibility(View.INVISIBLE);
                mButtonRecord.setText("Record Datas");
                mButtonRecord.setBackgroundColor(mColor);
                mIsRecord = false;
            }
        }
        else if ( view == mButtonHibernate)
        {
            showConfirmationMessage();
        }
        else if( view == mButtonGPS)
        {
            if(!mIsGPS)
            {
                mButtonGPS.setText("Stop GPS");
                mImViewLocation.setVisibility(View.VISIBLE);
                mIsGPS = true;
                statusGPSCheck();
            }else{
                mButtonGPS.setText("Start GPS");
                mImViewLocation.setVisibility(View.INVISIBLE);
                mIsGPS = false;
            }
        }
        else if( view == mButtonSendNote)
        {
            String note = mEditText.getText().toString();
            if(!note.isEmpty())
                sendAction(TypesDatas.UN, note);
            else
                Toast.makeText(this, "You need to insert a note before sending !", Toast.LENGTH_LONG);

        }
    }

    private void showConfirmationMessage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm action");
        builder.setMessage("Do you really want to send this action ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(!mIsHibernate)
                {
                    sendAction(TypesDatas.MH, "");
                    mButtonHibernate.setText("Wake");
                    mImViewLogo.setImageResource(R.drawable.logo_hib_72);
                    mIsHibernate = true;
                    mTextViewHib.setText("Hibernate: " + "ON");
                }else{
                    mButtonHibernate.setText("Hibernate");
                    mImViewLogo.setImageResource(R.drawable.logo_wake_72);
                    mIsHibernate = false;
                    mTextViewHib.setText("Hibernate: " + "OFF");
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);

        askForCurrentLocation();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);
    }

    private void askForCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            statusGPSCheck();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    askForCurrentLocation();

                } else {

                    showDefaultLocation();
                }
                return;
            }

        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng coord = new LatLng(48.351633, -71.138242);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
    }



    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };


    public void statusGPSCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        mTextViewGPS.setText("GPS: " + "ON");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        mTextViewGPS.setText("GPS: " + "OFF");
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void receiveAndPlot()
    {
        mAsynck = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    mSocket = new DatagramSocket(mPort);
                    mSocket.connect(mInetAdress, mPort);

                    mTextViewIP.setText("IP: " + mInetAdress.getHostName());

                    while (mSocket.isConnected() && !mIsHibernate) {

                        byte[] buf = new byte[10000];
                        mDatagramPacketReceive = new DatagramPacket(buf, buf.length);
                        mSocket.receive(mDatagramPacketReceive);

                        String datas = new String(buf, 0, mDatagramPacketReceive.getLength());

                        publishProgress(datas);
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... datas) {
                super.onProgressUpdate(datas[0]);

                mExtraction = new ExtractionDatas(datas[0]);

                ArrayList<ExtractedDatas> extractedDatas = mExtraction.extractDatas();

                for (ExtractedDatas extrDatas : extractedDatas) {

                    String dataType = extrDatas.getmDataType();

                    TypesDatas typesDatas = mMapGraphSelector.get(dataType);

                    Object[] values = extrDatas.getmValues();

                    int selected = typesDatas.getmSelectedGraph();

                    if(selected == mSelectedGraphPosition)
                        mPlotDatas.plot(typesDatas, values);

                    if(dataType.equals("B%")){
                        setBatteryLevel(Integer.valueOf((String)values[0]));
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mSocket.close();
            }
        };

        if (Build.VERSION.SDK_INT >= 11) mAsynck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else mAsynck.execute();
    }

    private void sendAction(final TypesDatas typesDatas, final String datas){

        mSendActionTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    String action = MessageGenerator.generateMessageWithLocalTime(typesDatas,
                            1, 1, 100, datas);

                    byte[] buffer = action.getBytes();

                    DatagramPacket datagramPacketSend = new DatagramPacket(buffer, buffer.length,
                            mInetAdress, mPort);

                    mSocket.send(datagramPacketSend);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        mSendActionTask.execute();
    }
}
