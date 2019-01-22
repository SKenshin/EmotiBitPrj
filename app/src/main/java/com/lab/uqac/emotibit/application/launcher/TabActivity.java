package com.lab.uqac.emotibit.application.launcher;

import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TabActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Intent intent = getIntent();

        int tabNumber = intent.getIntExtra("tabNumber", 1);
        int[] selected = intent.getIntArrayExtra("selected");

        tabLayout = findViewById(R.id.tab);

        for(int i = 0; i < 100; i++)
        {
            if(selected[i] != 100)
                tabLayout.addTab(tabLayout.newTab().setText("Emotibit " + selected[i]));
        }

    }
}
