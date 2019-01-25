package com.lab.uqac.emotibit.application.launcher;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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

public class EmotiBitActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private Context _context;

    private TextView _textViewPercentageBat;
    private ProgressBar _progressBarBat;
    private int _progressStatus = 0;
    private ImageView _imViewRecStatus;
    private ImageView _imViewLocation;
    private Button _buttonRecord;
    private Button _buttonGPS;
    private Button _buttonHibernate;
    private boolean _isRecord = false;
    private boolean _isHibernate = false;
    private boolean _isGPS = false;
    private GoogleMap _map;
    private EditText _editText;
    private boolean _isEditing = false;
    private Spinner _spinner;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        _context.registerReceiver(mBroadcastReceiver,iFilter);
        _textViewPercentageBat = (TextView) findViewById(R.id.percentage_bat);
        _progressBarBat = (ProgressBar) findViewById(R.id.progressbar_bat);
        _imViewRecStatus = (ImageView) findViewById(R.id.imview_rec);
        _imViewLocation = (ImageView) findViewById(R.id.imview_loc);
        _buttonRecord = (Button) findViewById(R.id.button_record);
        _buttonGPS = (Button) findViewById(R.id.button_gps);
        _buttonHibernate = (Button) findViewById(R.id.button_hibernate);
        _buttonRecord.setOnClickListener(this);
        _buttonGPS.setOnClickListener(this);
        _buttonHibernate.setOnClickListener(this);
        _editText = (EditText) findViewById(R.id.editText_note);
        _editText.setFocusable(false);

        _editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if(!_isEditing) {
                    _editText.setFocusableInTouchMode(true);
                    _isEditing = true;
                }
                    else {
                    _editText.setFocusableInTouchMode(true);
                    _editText.setCursorVisible(false);
                    _isEditing = false;
                }

                return false;
            }
        });

        _spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graph_array, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _spinner.setAdapter(adapter);

        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(adapterView.getContext(), "Item is " +
                        adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*Signals*/
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series1);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 30),
                new DataPoint(1, 30),
                new DataPoint(2, 60),
                new DataPoint(3, 20),
                new DataPoint(4, 50)
        });

        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 100),
                new DataPoint(1, 20),
                new DataPoint(2, 15),
                new DataPoint(3, 30),
                new DataPoint(4, 70)
        });

// set second scale
        graph.getSecondScale().addSeries(series2);
// the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(100);
        series2.setColor(Color.RED);

        graph.getSecondScale().addSeries(series3);
// the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(100);
        series3.setColor(Color.BLACK);

        series1.setTitle("ACC_X");
        series2.setTitle("ACC_Y");
        series3.setTitle("ACC_Z");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        graph.setOnTouchListener(new OnSwipeTouchListener(this){

            @Override
            public void onSwipeLeft() {


            }

            @Override
            public void onSwipeRight() {


            }

        });

        //MAP
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void displaySignals(View view){

        Intent intent = new Intent(this, SignalsActivity.class);

        startActivity(intent);
    }

    public void startRecord(View view){


    }


    @Override
    public void onClick(View view) {


        if( view == _buttonRecord)
        {
            int color = 0;
            if(!_isRecord)
            {
                color = _buttonRecord.getHighlightColor();
                _imViewRecStatus.setImageResource(R.drawable.record_on);
                _buttonRecord.setText("Stop Recording");
                _buttonRecord.setBackgroundColor(Color.RED);
                _isRecord = true;

            }else{
                _imViewRecStatus.setImageResource(R.drawable.record_off);
                _buttonRecord.setText("Record Datas");
                _buttonRecord.setBackgroundColor(color);
                _isRecord = false;
            }
        }
        else if ( view == _buttonHibernate)
        {
            if(!_isHibernate)
            {
                _buttonHibernate.setText("Wake");
                _imViewRecStatus.setImageResource(R.drawable.logo_emotioff);
                _isHibernate = true;
            }else{
                _buttonHibernate.setText("Hibernate");
                _imViewRecStatus.setImageResource(R.drawable.record_off);
                _isHibernate = false;
            }
        }
        else if( view == _buttonGPS)
        {
            if(!_isGPS)
            {
                _buttonGPS.setText("Stop GPS");
                _imViewLocation.setVisibility(View.VISIBLE);
                _isGPS = true;
            }else{
                _buttonGPS.setText("Start GPS");
                _imViewLocation.setVisibility(View.INVISIBLE);
                _isGPS = false;
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;

        _map.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        _map.setOnMyLocationClickListener(onMyLocationClickListener);

        askForCurrentLocation();

        _map.getUiSettings().setZoomControlsEnabled(true);
        _map.setMinZoomPreference(11);
    }

    private void askForCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else if (_map != null) {
            _map.setMyLocationEnabled(true);
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
        _map.moveCamera(CameraUpdateFactory.newLatLng(coord));
    }



    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    _map.setMinZoomPreference(15);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    _map.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    _map.addCircle(circleOptions);
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
