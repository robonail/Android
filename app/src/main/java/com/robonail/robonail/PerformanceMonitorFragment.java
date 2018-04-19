package com.robonail.robonail;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;




public class PerformanceMonitorFragment extends Fragment {

    View myView;
    public static final String Tag = "PerformanceMonitorFragment";

    private Handler handler = new Handler();
    ProgressBar progressBarMobile, progressBarNav;
    TextView txtStatsMobile, txtStatsNav;
    public static CheckBox checkBoxSaveData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.performance_monitor,container,false);
        super.onCreate(savedInstanceState);

        txtStatsMobile=(TextView) myView.findViewById(R.id.txtStatsMobile);
        txtStatsNav=(TextView) myView.findViewById(R.id.txtStatsNav);
        progressBarMobile=(ProgressBar) myView.findViewById(R.id.progressBarMobile);
        progressBarNav=(ProgressBar) myView.findViewById(R.id.progressBarNav);
        checkBoxSaveData=(CheckBox)  myView.findViewById(R.id.checkBoxSaveData);
        updateUIValues();

        handler.postDelayed(runnable, 30);

        // keep the fragment and all its data across screen rotation
        setRetainInstance(true);
        return myView;
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            updateUIValues();
            handler.postDelayed(this, 30);
        }
    };
    public void updateUIValues(){
        DecimalFormat df = new DecimalFormat("###.##");
        String progress = df.format(((double) RobonailApplication.httpMessagesSuccessfulCount / (double) RobonailApplication.httpMessagesTotalCount)*100.0d);
        txtStatsMobile.setText(progress+"%\n"+RobonailApplication.httpMessagesSuccessfulCount+"/"+RobonailApplication.httpMessagesTotalCount);
    }
}

