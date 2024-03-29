package com.severyn.actrecognition;

import java.util.List;

import android.content.Context;
import android.graphics.Color;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.example.actrecognition.R;

public class AccMonitorFragment extends Fragment {
	Context context;
	XYPlot xPlot;
	XYPlot yPlot;
	XYPlot zPlot;

	public void updatePlot(List<Double> dataHistory, SimpleXYSeries series,
			XYPlot plot, Double value) {
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
		View rootView = inflater
				.inflate(R.layout.acc_monitor, container, false);

		((MainActivity) getActivity()).setTabFragment(1, getTag());

		xPlot = (XYPlot) rootView.findViewById(R.id.xAccPlot);
		yPlot = (XYPlot) rootView.findViewById(R.id.yAccPlot);
		zPlot = (XYPlot) rootView.findViewById(R.id.zAccPlot);

		initialisePlot(xPlot, ((MainActivity) getActivity()).getPlotSeries(0),Color.RED);
		initialisePlot(yPlot, ((MainActivity) getActivity()).getPlotSeries(1),Color.BLUE);
		initialisePlot(zPlot, ((MainActivity) getActivity()).getPlotSeries(2),Color.GREEN);

		return rootView;
	}

	void initialisePlot(XYPlot plot, SimpleXYSeries series, int colour) {
		plot.setRangeBoundaries(-16, 16, BoundaryMode.FIXED);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		plot.setDomainBoundaries(0, 120, BoundaryMode.FIXED);
		plot.addSeries(series, new LineAndPointFormatter(colour,
				Color.TRANSPARENT, Color.TRANSPARENT));
		plot.setTicksPerRangeLabel(1);
		plot.setDomainLabel("Time");
		plot.getDomainLabelWidget().pack();
		plot.setRangeLabel("Acceleration");
		plot.getRangeLabelWidget().pack();
		plot.setTitle(series.getTitle());
	}

	public boolean getMonitorToggleStatus() {
		ToggleButton monitorToggle = (ToggleButton) this.getView()
				.findViewById(R.id.monitorToggle);
		return monitorToggle.isChecked();
	}

}
