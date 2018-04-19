package com.robonail.robonail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StartTaskFragment extends Fragment {

    View myView;
    public static final String Tag = "PerformanceMonitorFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.start_task,container,false);
        super.onCreate(savedInstanceState);
        return myView;
    }
}