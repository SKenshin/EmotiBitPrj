package GUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lab.uqac.emotibit.application.launcher.EmotiBitActivity;
import com.lab.uqac.emotibit.application.launcher.MainActivity;
import com.lab.uqac.emotibit.application.launcher.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmotiBitButton {

    private int mIndex = 0;
    private HashMap<View, InetAddress> mMapButton;
    private Activity mActivity;
    private LinearLayout mVerticalLayout;
    private LinearLayout.LayoutParams mParams;
    private String mTextButton;

    public EmotiBitButton(Activity activity, HashMap<View, InetAddress> map){
        mMapButton = map;
        mActivity = activity;
        mVerticalLayout = mActivity.findViewById(R.id.row_main);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void add(InetAddress inetAddress) {

        if(!mMapButton.containsValue(inetAddress)) {

            Button button = new Button(mActivity);
            button.setText("EmotiBit " + (mIndex + 1));
            button.setOnClickListener(buttonListener);

            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.logo_wake_72, 0);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);

            layoutParams.height = 50;

            layoutParams.width = 50;

            button.setLayoutParams(layoutParams);

            mVerticalLayout.addView(button, mParams);

            mParams.setMargins(0, 50, 0, 0);

            mMapButton.put(button, inetAddress);

            mIndex++;
        }
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View view) {
            mTextButton = ((Button)view).getText().toString();

            launchEmotiBit(view);
        }
    };

    public void launchEmotiBit(View view) {

        Intent intent = new Intent(mActivity, EmotiBitActivity.class);

        intent.putExtra("selected", mTextButton );

        intent.putExtra("address", mMapButton.get(view));

        mActivity.startActivity(intent);
    }

    public HashMap<View, InetAddress> getmMapButton() {
        return mMapButton;
    }

}
