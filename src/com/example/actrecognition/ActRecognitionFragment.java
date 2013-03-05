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
	static ArrayList<Float> xActRecording = new ArrayList<Float>();
	static ArrayList<Float> yActRecording = new ArrayList<Float>();
	static ArrayList<Float> zActRecording = new ArrayList<Float>();
	private SimpleXYSeries xActPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yActPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zActPlotSeries = new SimpleXYSeries("z acceleration");
	XYPlot xyzActivityPlot;
	XYPlot xyzRecordingPlot;
	boolean doneRecording=false;


	public void passValues(float x,float y, float z){
		if(doneRecording==true){
			xActRecording.clear();
			yActRecording.clear();
			zActRecording.clear();		
			doneRecording=false;
		}
		if(xActRecording.size()==480){
			Toast.makeText(getActivity(),
					"480 records done",
					Toast.LENGTH_LONG).show();			
			drawData();
			pullData();
			doneRecording=true;
		}else{
			xActRecording.add(x);
			yActRecording.add(y);
			zActRecording.add(z);
		}
	}
	
	private void drawData() {
		xyzActivityPlot.setBackgroundColor(Color.GREEN);
		xActPlotSeries.setModel(xActRecording, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		yActPlotSeries.setModel(yActRecording, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		zActPlotSeries.setModel(zActRecording, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

		xyzActivityPlot.addSeries(xActPlotSeries, new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT));
		xyzActivityPlot.addSeries(yActPlotSeries, new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT));
		xyzActivityPlot.addSeries(zActPlotSeries, new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT));

		xyzActivityPlot.redraw();
	}
	
	private void pullData(){
		String tag2 = ((MainActivity)getActivity()).tagFragment2;
		 ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getActivity().getSupportFragmentManager().findFragmentByTag(tag2);
		 
		
		SimpleXYSeries xPlotSeries = actRecordingFragment.getPlotSeries(0);
		SimpleXYSeries yPlotSeries = actRecordingFragment.getPlotSeries(1);
		SimpleXYSeries zPlotSeries = actRecordingFragment.getPlotSeries(2);
								
		
		xyzRecordingPlot.addSeries(xPlotSeries, new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT));
		xyzRecordingPlot.addSeries(yPlotSeries, new LineAndPointFormatter(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT));
		xyzRecordingPlot.addSeries(zPlotSeries, new LineAndPointFormatter(Color.BLUE, Color.TRANSPARENT, Color.TRANSPARENT));

		xyzRecordingPlot.redraw();
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.act_recognition,
				container, false);

		((MainActivity)getActivity()).setTabFragment(3, getTag());
		
		xyzActivityPlot = (XYPlot) rootView.findViewById(R.id.xyzActivityPlot);
		xyzActivityPlot.setRangeBoundaries(-40, 40, BoundaryMode.FIXED);
		xyzActivityPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		xyzActivityPlot.setDomainBoundaries(0, 480, BoundaryMode.FIXED);
		xyzActivityPlot.setTicksPerRangeLabel(1);
		xyzActivityPlot.setDomainLabel("Time");
		xyzActivityPlot.getDomainLabelWidget().pack();
		xyzActivityPlot.setRangeLabel("Acceleration");
		xyzActivityPlot.getRangeLabelWidget().pack();
		xyzActivityPlot.setTitle("Activity Plot");
		xyzActivityPlot.redraw();
		
		xyzRecordingPlot = (XYPlot) rootView.findViewById(R.id.xyzSavedPlot);
		xyzRecordingPlot.setRangeBoundaries(-40, 40, BoundaryMode.FIXED);
		xyzRecordingPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		xyzRecordingPlot.setDomainBoundaries(0, 480, BoundaryMode.FIXED);
		xyzRecordingPlot.setTicksPerRangeLabel(1);
		xyzRecordingPlot.setDomainLabel("Time");
		xyzRecordingPlot.getDomainLabelWidget().pack();
		xyzRecordingPlot.setRangeLabel("Acceleration");
		xyzRecordingPlot.getRangeLabelWidget().pack();
		xyzRecordingPlot.setTitle("Saved Data Plot");
		xyzRecordingPlot.redraw();
		
		return rootView;
        }
	
}
