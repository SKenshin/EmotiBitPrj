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
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import listener.OnSwipeTouchListener;

public class EmotiBitActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, View.OnTouchListener {

    private Context mContext;

    private TextView mTextViewPercentageBat;
    private ProgressBar mProgressBarBat;
    private int mProgressStatus = 0;
    private ImageView mImViewRecStatus;
    private ImageView mImViewLocation;
    private ImageView mImViewLogo;
    private Button mButtonRecord;
    private Button mButtonGPS;
    private Button mButtonHibernate;
    private boolean mIsRecord = false;
    private boolean mIsHibernate = false;
    private boolean mIsGPS = false;
    private GoogleMap mMap;
    private EditText mEditText;
    private boolean mIsEditing = false;
    private Spinner mSpinner;
    private final Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private int mSelectedGraphPosition = 0;
    private Runnable mTimer;
    private ScrollView mScrollView;
    private int mColor;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String LOG_TAG = EmotiBitActivity.class.getSimpleName();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            float percentage = level/ (float) scale;
            mProgressStatus = (int)((percentage)*100);
            mTextViewPercentageBat.setText("" + mProgressStatus + "%");
            mProgressBarBat.setProgress(mProgressStatus);
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotibit);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();

        String title = intent.getStringExtra("selected");
        getSupportActionBar().setTitle(title);

        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);
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
        mButtonRecord.setOnClickListener(this);
        mButtonGPS.setOnClickListener(this);
        mButtonHibernate.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.editText_note);
        mEditText.setOnTouchListener(this);
        mEditText.setFocusable(false);
        mSpinner = findViewById(R.id.spinner);
        final GraphView graph = (GraphView) findViewById(R.id.graph);
        mScrollView = findViewById(R.id.scroll_v);
        mScrollView.setOnTouchListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_array, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mHandler.removeCallbacks(mTimer);
                mSelectedGraphPosition = position;
                graph.removeAllSeries();
                mHandler.postDelayed(mTimer, 100);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*Signals*/
        mSeries1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });


        mSeries2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 30),
                new DataPoint(1, 30),
                new DataPoint(2, 60),
                new DataPoint(3, 20),
                new DataPoint(4, 50)
        });

        mSeries3 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 100),
                new DataPoint(1, 20),
                new DataPoint(2, 15),
                new DataPoint(3, 30),
                new DataPoint(4, 70)
        });

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.setOnTouchListener(new OnSwipeTouchListener(this){

            @Override
            public void onSwipeLeft() {
                mSelectedGraphPosition--;
                mSpinner.setSelection(mSelectedGraphPosition);
            }

            @Override
            public void onSwipeRight() {
                mSelectedGraphPosition++;
                mSpinner.setSelection(mSelectedGraphPosition);
            }


        });

        //MAP
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mTimer = new Runnable() {
            @Override
            public void run() {

                if(mSelectedGraphPosition == 1){
                    graph.removeAllSeries();
                    graph.addSeries(mSeries1);
                    mSeries1.setTitle("ACC_X");
                }
                if(mSelectedGraphPosition == 2){
                    graph.removeAllSeries();
                    // set second scale
                    graph.addSeries(mSeries2);
                    // the y bounds are always manual for second scale
                    mSeries2.setColor(Color.RED);
                    mSeries2.setTitle("ACC_Y");
                }
                if(mSelectedGraphPosition == 3){
                    graph.removeAllSeries();
                    graph.addSeries(mSeries3);
                    // the y bounds are always manual for second scale
                    mSeries3.setColor(Color.BLACK);
                    mSeries3.setTitle("ACC_Z");
                }
                mHandler.postDelayed(mTimer, 300);
            }
        };


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
        mHandler.postDelayed(mTimer, 100);
    }


    public void displaySignals(View view){

        Intent intent = new Intent(this, SignalsActivity.class);

        startActivity(intent);
    }

    public void startRecord(View view){


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
                mImViewRecStatus.setImageResource(R.drawable.rec_64);
                mImViewRecStatus.setVisibility(View.VISIBLE);
                mButtonRecord.setText("Stop Recording");
                mButtonRecord.setBackgroundColor(Color.RED);
                mIsRecord = true;

            }else{
                mImViewRecStatus.setImageResource(R.drawable.rec_64);
                mImViewRecStatus.setVisibility(View.INVISIBLE);
                mButtonRecord.setText("Record Datas");
                mButtonRecord.setBackgroundColor(mColor);
                mIsRecord = false;
            }
        }
        else if ( view == mButtonHibernate)
        {
            if(!mIsHibernate)
            {
                mButtonHibernate.setText("Wake");
                mImViewLogo.setImageResource(R.drawable.logo_hib_72);
                mIsHibernate = true;
            }else{
                mButtonHibernate.setText("Hibernate");
                mImViewLogo.setImageResource(R.drawable.logo_wake_72);
                mIsHibernate = false;
            }
        }
        else if( view == mButtonGPS)
        {
            if(!mIsGPS)
            {
                mButtonGPS.setText("Stop GPS");
                mImViewLocation.setVisibility(View.VISIBLE);
                mIsGPS = true;
            }else{
                mButtonGPS.setText("Start GPS");
                mImViewLocation.setVisibility(View.INVISIBLE);
                mIsGPS = false;
            }
        }
    }

    public void sendNote(View view) {

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
            statusCheck();

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


    public void statusCheck() {
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
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
