package com.example.actrecognition;

import java.util.ArrayList;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ActRecognitionFragment extends Fragment {
	XYPlot xyzPlot;


	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.act_recognition,
				container, false);

		((MainActivity)getActivity()).setTabFragment(3, getTag());
		
		xyzPlot = (XYPlot) rootView.findViewById(R.id.xyzPlot);
		xyzPlot.setRangeBoundaries(-15, 15, BoundaryMode.FIXED);
		xyzPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		xyzPlot.setDomainBoundaries(0, 512, BoundaryMode.FIXED);
		xyzPlot.setTicksPerRangeLabel(1);
		xyzPlot.setDomainLabel("Time");
		xyzPlot.getDomainLabelWidget().pack();
		xyzPlot.setRangeLabel("Acceleration");
		xyzPlot.getRangeLabelWidget().pack();
		xyzPlot.setTitle("Recording");

		xyzPlot.redraw();
		
		return rootView;
        }
	
}
