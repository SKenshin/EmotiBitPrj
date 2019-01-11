package com.lab.uqac.emotibit.application.launcher;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class SignalsActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private LineGraphSeries<DataPoint> series;
    private static double currentX;
    private ThreadPoolExecutor liveChartExecutor;
    private LinkedBlockingQueue<Double> accelerationQueue = new LinkedBlockingQueue<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signals);

        GraphView graph = (GraphView) findViewById(R.id.graph_acc);
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


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        GraphView graph_gyro = (GraphView) findViewById(R.id.graph_gyro);

        series = new LineGraphSeries<>();
        series.setColor(Color.GREEN);
        graph_gyro.addSeries(series);

        // activate horizontal zooming and scrolling
        graph_gyro.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph_gyro.getViewport().setScrollable(true);

        // activate horizontal and vertical zooming and scrolling
        graph_gyro.getViewport().setScalableY(true);

        // activate vertical scrolling
        graph_gyro.getViewport().setScrollableY(true);
        // To set a fixed manual viewport use this:
        // set manual X bounds
        graph_gyro.getViewport().setXAxisBoundsManual(true);
        graph_gyro.getViewport().setMinX(0.5);
        graph_gyro.getViewport().setMaxX(6.5);

        // set manual Y bounds
        graph_gyro.getViewport().setYAxisBoundsManual(true);
        graph_gyro.getViewport().setMinY(0);
        graph_gyro.getViewport().setMaxY(10);

        series.setTitle("Gyro ");

        graph_gyro.getLegendRenderer().setVisible(true);
        graph_gyro.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        currentX = 0;

        // Start chart thread
        liveChartExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        if (liveChartExecutor != null)
            liveChartExecutor.execute(new AccelerationChart(new AccelerationChartHandler()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        long timestamp = System.currentTimeMillis();



        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long timestamp1 = System.currentTimeMillis();
            long deltaTime = timestamp - timestamp1;
            getAccelerometer(sensorEvent);
            // mSeries1.appendData(new DataPoint(deltaTime, event.values[0]), true, 10);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        double x = values[0];
        double y = values[1];
        double z = values[2];

        double accelerationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        double acceleration = Math.sqrt(accelerationSquareRoot);

        accelerationQueue.offer(acceleration);
    }

    private class AccelerationChartHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Double accelerationY = 0.0D;
            if (!msg.getData().getString("ACCELERATION_VALUE").equals(null) && !msg.getData().getString("ACCELERATION_VALUE").equals("null")) {
                accelerationY = (Double.parseDouble(msg.getData().getString("ACCELERATION_VALUE")));
            }

            series.appendData(new DataPoint(currentX, accelerationY), true, 10);
            currentX = currentX + 1;
        }
    }

    private class AccelerationChart implements Runnable {
        private boolean drawChart = true;
        private Handler handler;

        public AccelerationChart(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (drawChart) {
                Double accelerationY;
                try {
                    Thread.sleep(300); // Speed up the X axis
                    accelerationY = accelerationQueue.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                if (accelerationY == null)
                    continue;

                // currentX value will be excced the limit of double type range
                // To overcome this problem comment of this line
                // currentX = (System.currentTimeMillis() / 1000) * 8 + 0.6;

                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("ACCELERATION_VALUE", String.valueOf(accelerationY));
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }
        }
    }
}
