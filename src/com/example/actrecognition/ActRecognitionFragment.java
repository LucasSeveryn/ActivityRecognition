package com.example.actrecognition;

import com.androidplot.xy.XYPlot;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActRecognitionFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.act_recognition,
				container, false);

		return rootView;
        }
	
}
