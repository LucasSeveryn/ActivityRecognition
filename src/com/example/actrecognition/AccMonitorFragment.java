package com.example.actrecognition;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;


public class AccMonitorFragment  extends Fragment {
	Context context;
	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");
	
	XYPlot xPlot;
	XYPlot yPlot;
	XYPlot zPlot;
	
	ArrayList<Float> xDataHistory = new ArrayList<Float>();
	ArrayList<Float> yDataHistory = new ArrayList<Float>();
	ArrayList<Float> zDataHistory = new ArrayList<Float>();
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean mInitialised;

	private float mAccelLast; // last acceleration including gravity
	private final SensorEventListener mSensorListener = new SensorEventListener() {
		private int counter=0;

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
		      float x = event.values[0];
		      float y = event.values[1];
		      float z = event.values[2];
		      
		      
		      
		      updatePlot(xDataHistory,xPlotSeries,xPlot,x);
		      updatePlot(yDataHistory,yPlotSeries,yPlot,y);
		      updatePlot(zDataHistory,zPlotSeries,zPlot,z);    
		      
		      if(counter % 50 == 0){
			  ((TextView) getActivity().findViewById(R.id.xAccPlotLabel)).setText("x-plane Acceleration plot. Current value: " + x);
			  ((TextView) getActivity().findViewById(R.id.yAccPlotLabel)).setText("y-plane Acceleration plot. Current value: " + y);
			  ((TextView) getActivity().findViewById(R.id.zAccPlotLabel)).setText("z-plane Acceleration plot. Current value: " + z);
		      }
		      
		      counter++;
		}


	  };

	public void updatePlot(ArrayList<Float> dataHistory, SimpleXYSeries series, XYPlot plot, Float value){
		if (dataHistory.size() < 120) {
			dataHistory.add(value);
		} else {
			dataHistory.remove(0);// remove oldest
			dataHistory.add(value);
			series.setModel(dataHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			plot.redraw();	
		}
	}
	  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.acc_monitor,
				container, false);

		
		xPlot = (XYPlot) rootView.findViewById(R.id.xAccPlot);
		yPlot = (XYPlot) rootView.findViewById(R.id.yAccPlot);
		zPlot = (XYPlot) rootView.findViewById(R.id.zAccPlot);

		initialisePlot(xPlot, xPlotSeries);
		initialisePlot(yPlot, yPlotSeries);
		initialisePlot(zPlot, zPlotSeries);

		mInitialised = false;
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mSensorListener, mAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);	    
		return rootView;
        }
    
    
    void initialisePlot(XYPlot plot, SimpleXYSeries series){
		plot.setRangeBoundaries(-16, 16, BoundaryMode.FIXED);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		plot.setDomainBoundaries(0, 120, BoundaryMode.FIXED);
		plot.addSeries(series, new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT));
		plot.setTicksPerRangeLabel(1);
		plot.setDomainLabel("Time");
		plot.getDomainLabelWidget().pack();
		plot.setRangeLabel("Acceleration");
		plot.getRangeLabelWidget().pack();
		plot.setTitle(series.getTitle());
    }
}
