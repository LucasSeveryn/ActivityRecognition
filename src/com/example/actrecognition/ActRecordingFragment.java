package com.example.actrecognition;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.Contacts.Intents;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActRecordingFragment extends Fragment{
	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");
	XYPlot xyzPlot;

	@SuppressWarnings("deprecation")
	public void drawData(ArrayList<Float> xDataRecording, ArrayList<Float> yDataRecording, ArrayList<Float> zDataRecording) {
		xPlotSeries.setModel(xDataRecording,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		yPlotSeries.setModel(yDataRecording,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		zPlotSeries.setModel(zDataRecording,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

		LineAndPointFormatter lineAndPointFormatter = new LineAndPointFormatter(
	            Color.RED, null, null);
	    Paint paint = lineAndPointFormatter.getLinePaint();
	    paint.setStrokeWidth(1);
	    lineAndPointFormatter.setLinePaint(paint);
	    
		xyzPlot.addSeries(xPlotSeries, lineAndPointFormatter);
		
		lineAndPointFormatter = new LineAndPointFormatter(
	            Color.BLUE, null, null);
	    paint = lineAndPointFormatter.getLinePaint();
	    paint.setStrokeWidth(1);
	    lineAndPointFormatter.setLinePaint(paint);
	    
	    
		xyzPlot.addSeries(yPlotSeries, lineAndPointFormatter);
		
		lineAndPointFormatter = new LineAndPointFormatter(
	            Color.GREEN, null, null);
	    paint = lineAndPointFormatter.getLinePaint();
	    paint.setStrokeWidth(1);
	    lineAndPointFormatter.setLinePaint(paint);
		
		xyzPlot.addSeries(zPlotSeries, lineAndPointFormatter);

		xyzPlot.redraw();

	}

	public SimpleXYSeries getPlotSeries(int axis) {
		switch (axis) {
		case 0:
			return xPlotSeries;
		case 1:
			return yPlotSeries;
		case 2:
			return zPlotSeries;
		}
		return null;

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.act_recording, container,
				false);

		xyzPlot = (XYPlot) rootView.findViewById(R.id.xyzPlot);
		((MainActivity) getActivity()).setTabFragment(2, getTag());

		xyzPlot.setRangeBoundaries(-15, 15, BoundaryMode.FIXED);
		xyzPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		xyzPlot.setDomainBoundaries(0, 480, BoundaryMode.FIXED);
		xyzPlot.setTicksPerRangeLabel(1);
		xyzPlot.setDomainLabel("Time");
		xyzPlot.getDomainLabelWidget().pack();
		xyzPlot.setRangeLabel("Acceleration");
		xyzPlot.getRangeLabelWidget().pack();
		xyzPlot.setTitle("Recording");

		xyzPlot.redraw();
		return rootView;
	}

	public void updateZeroCrossingRateText(int[] zeroCrossingCountsRecording) {
		TextView zeroCrossingRateText = (TextView) this.getView().findViewById(
				R.id.zeroCrossingRateText);
		zeroCrossingRateText.setText("Zero crossing rate. X: "
				+ zeroCrossingCountsRecording[0] + " Y: "
				+ zeroCrossingCountsRecording[1] + " Z: "
				+ zeroCrossingCountsRecording[2]);
	}

	public void updateMaximumDisplacementText(float[] maximumDisplacements) {
		TextView maximumDisplacementText = (TextView) this.getView()
				.findViewById(R.id.maximumDisplacementText);
		maximumDisplacementText.setText("Max/Min displacement \nX: "
				+ maximumDisplacements[0] + "/" + maximumDisplacements[1] + " zero: " + maximumDisplacements[6]
				+ "\nY: " + maximumDisplacements[2] + "/"
				+ maximumDisplacements[3] + " zero: " + maximumDisplacements[7] +  "\nZ: " + maximumDisplacements[4]
				+ "/" + maximumDisplacements[5]  + " zero: " + maximumDisplacements[8]);

	}


}
