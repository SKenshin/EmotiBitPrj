package com.lab.uqac.emotibit.application.launcher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {

    private Button _buttonEmot1;
    private Button _buttonEmot2;
    private Button _buttonExit;
    private ImageView _imViewEmoti1;
    private ImageView _imViewEmoti2;
    private Handler _handler = new Handler();
    private int _index = 1;
    private LinearLayout _verticalLayout;
    private LinearLayout.LayoutParams _params;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String _texteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _buttonExit = (Button) findViewById(R.id.button_exit);
        _verticalLayout = (LinearLayout) findViewById(R.id.row_main);
        _params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        _buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });

        try {
            _handler.removeCallbacks(addButtonTask);
            _handler.postDelayed(addButtonTask, 1000); // delay 1 second
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void launchEmotiBit(View view) {

        Intent intent = new Intent(this, EmotiBitActivity.class);

        intent.putExtra("selected", _texteButton );

        startActivity(intent);
    }


    private Runnable addButtonTask = new Runnable() {
        public void run() {

            Button button = new Button(MainActivity.this);
            button.setText("EmotiBit " + _index);
            button.setOnClickListener(buttonListener);

            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.logo_wake_72, 0);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10,10);

            // Set the height of this ImageButton
            layoutParams.height = 50;

            // Set the width of that ImageButton
            layoutParams.width = 50;

            // Apply the updated layout parameters to last ImageButton
            button.setLayoutParams(layoutParams);

            _verticalLayout.addView(button, _params);

            _params.setMargins(0,50,0, 0);


            _index++;


            try {
                _handler.removeCallbacks(addButtonTask);
                _handler.postDelayed(addButtonTask, 5000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            _texteButton = ((Button)v).getText().toString();

            _handler.removeCallbacks(addButtonTask);

            launchEmotiBit(v);
        }
    };


}

