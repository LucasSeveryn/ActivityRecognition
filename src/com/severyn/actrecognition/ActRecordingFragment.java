package com.severyn.actrecognition;

import java.util.ArrayList;
import java.util.List;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.Plot;
import com.androidplot.xy.XYStepMode;
import com.example.actrecognition.R;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ActRecordingFragment extends Fragment {
	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");
	XYPlot xyzPlot;
	View rootView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.act_recording, container,
				false);
	
		xyzPlot = (XYPlot) rootView.findViewById(R.id.xyzPlot);
		((MainActivity) getActivity()).setTabFragment(2, getTag());
	
		xyzPlot.setRangeBoundaries(-12, 15, BoundaryMode.FIXED);
		xyzPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
		xyzPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 50);
//		xyzPlot.setDomainBoundaries(0, 512, BoundaryMode.FIXED);
		xyzPlot.setTicksPerRangeLabel(1);
		xyzPlot.setDomainLabel("Time");
		xyzPlot.getDomainLabelWidget().pack();
		xyzPlot.setRangeLabel("Acceleration");
		xyzPlot.getRangeLabelWidget().pack();
		xyzPlot.setTitle("Recording");
		xyzPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
	
		xyzPlot.redraw();
	
		Spinner typeSpinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
		typeSpinner.setOnItemSelectedListener((MainActivity) getActivity());
		
		
	
		Spinner displaySpinner = (Spinner) rootView
				.findViewById(R.id.displaySpinner);
		displaySpinner.setOnItemSelectedListener((MainActivity) getActivity());
		
//		ScrollView featureScrollView = (ScrollView) rootView
//				.findViewById(R.id.scrollView1);
//		
//		featureScrollView.setScrollbarFadingEnabled(false);
		
		MainActivity m = (MainActivity) getActivity();
	
		
		int size = m.accDataLibrary.size();
		if (size > 0 && !m.getTempData().equals(null) &&  !m.getTempFeat().equals(null)) {
			this.setIndexTextView(size, size);
			xyzPlot.setDomainBoundaries(0, m.getTempData().size(), BoundaryMode.FIXED);
			typeSpinner.setSelection(m.getTempFeat().getType());
			this.updateActivityDetailText(m.getTempData(), m.getTempFeat());


			m.drawRecordingGraph();
		}
	
		return rootView;
	}

	public boolean getSendAsTestSampleValue() {
		CheckBox sendAsTestSample = (CheckBox) this.getView()
				.findViewById(R.id.sendAsTestSample);
		return sendAsTestSample.isChecked();
	}
	
	public void setIndexTextView(int index,int size){
		TextView indexTextView = (TextView) rootView.findViewById(R.id.indexTextView);
		indexTextView.setText(index +"/"+size);
	}
	
	@SuppressWarnings("deprecation")
	public void drawData(AccData data, int lowerBound, int upperBound,
			int upperXBound) {

		List<Double> xDataRecording = data.getxData();
		List<Double> yDataRecording = data.getyData();
		List<Double> zDataRecording = data.getzData();

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

		lineAndPointFormatter = new LineAndPointFormatter(Color.BLUE, null,
				null);
		paint = lineAndPointFormatter.getLinePaint();
		paint.setStrokeWidth(1);
		lineAndPointFormatter.setLinePaint(paint);

		xyzPlot.addSeries(yPlotSeries, lineAndPointFormatter);

		lineAndPointFormatter = new LineAndPointFormatter(Color.GREEN, null,
				null);
		paint = lineAndPointFormatter.getLinePaint();
		paint.setStrokeWidth(1);
		lineAndPointFormatter.setLinePaint(paint);
		xyzPlot.setRangeBoundaries(lowerBound, upperBound, BoundaryMode.FIXED);
		xyzPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
		xyzPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 50);
		xyzPlot.setDomainBoundaries(0, upperXBound, BoundaryMode.FIXED);
		xyzPlot.addSeries(zPlotSeries, lineAndPointFormatter);

		xyzPlot.redraw();

	}

	public int getTypeSpinnerValue() {
		return ((Spinner) getView().findViewById(R.id.typeSpinner))
				.getSelectedItemPosition();
	}

	public boolean getConstantSavingCheckboxValue() {
		CheckBox constantSavingCheckBox = (CheckBox) this.getView()
				.findViewById(R.id.constantRecordingSaveCheckbox);
		return constantSavingCheckBox.isChecked();
	}
	
	public boolean getTwiceSizeCheckboxValue() {
		CheckBox twiceSizeCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.halfSizeCheckbox);
		return twiceSizeCheckbox.isChecked();
	}

	public boolean getAutoTagCheckboxValue() {
		CheckBox autoTagCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.autoTagCheckbox);
		return autoTagCheckbox.isChecked();
	}
	
	public boolean getConstantIdentificationCheckboxValue() {
		CheckBox constantIdentificationCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.constantIdentyfyingCheckbox);
		return constantIdentificationCheckbox.isChecked();
	}

	public int getdisplaySpinnerValue() {
		return ((Spinner) getView().findViewById(R.id.displaySpinner))
				.getSelectedItemPosition();
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

	public boolean getRecordingToggleStatus() {
		ToggleButton recordingToggle = (ToggleButton) this.getView()
				.findViewById(R.id.recordingToggle);
		return recordingToggle.isChecked();
	}

	public void toggleCheckboxes() {
		CheckBox constantSavingCheckBox = (CheckBox) this.getView()
				.findViewById(R.id.constantRecordingSaveCheckbox);
		CheckBox constantIdentificationCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.constantIdentyfyingCheckbox);
		CheckBox autoTagCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.autoTagCheckbox);
		CheckBox halfSizeCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.halfSizeCheckbox);
		
		autoTagCheckbox.setEnabled(!autoTagCheckbox.isEnabled());
		constantSavingCheckBox.setEnabled(!constantSavingCheckBox.isEnabled());
		constantIdentificationCheckbox
				.setEnabled(!constantIdentificationCheckbox.isEnabled());
		halfSizeCheckbox.setEnabled(!halfSizeCheckbox.isEnabled());
	}

	private String printHistogram(int[] array) {
		String result = "";
		for (int i : array) {
			result += (" | " + i);
		}
		result += "|";
		return result;
	}

	public String f(Double d) {
		return String.format("%.3f", d);
	}

	public void updateProgressBar(int i) {
		ProgressBar progressBar = (ProgressBar) this.getView().findViewById(
				R.id.recordingProgressBar);
		progressBar.setProgress(i);
	}

	public void setTypeCombobox(int i) {
		Spinner typeSpinner = (Spinner) this.getView().findViewById(
				R.id.typeSpinner);
		typeSpinner.setSelection(i);

	}
	
	public void setTwiceSizeProgressBar(boolean twiceSize){
		ProgressBar recordingProgressBar = (ProgressBar) this.getView().findViewById(
				R.id.recordingProgressBar);
		if(twiceSize) recordingProgressBar.setMax(512);else recordingProgressBar.setMax(256);
	}
	
	public void clearActivityDetailText(){
		TextView accActivityDetailText = (TextView) rootView.findViewById(R.id.accActDetailText);
		accActivityDetailText.setText("");
	}

	public void updateActivityDetailText(AccData activity, AccFeat accFeat) {
		TextView accActivityDetailText = (TextView) rootView.findViewById(R.id.accActDetailText);
		accActivityDetailText.setText("Type: "
				+ FeatureExtractors.getType((accFeat.getType()))
				// + "\nAccFeat Type: " +
				// FeatureExtractors2.getType(accFeat.getType())
				+ "\nAcc data points: " + activity.getxData().size()
//				+ " Gyro data points: "
//				+ activity.getGyroData().getxData().size() 
				+ "\nMean: X: "
				+ f(accFeat.getMean(0)) + " Y: " + f(accFeat.getMean(1))
				+ " Z: " + f(accFeat.getMean(2)) + "\nStandard deviation: X: "
				+ f(accFeat.getSd(0)) + " Y: " + f(accFeat.getSd(1)) + " Z: "
				+ f(accFeat.getSd(2)) + "\nAverage peak distance: X: "
				+ f(accFeat.getAvPeakDistance(0)) + " Y: "
				+ f(accFeat.getAvPeakDistance(1)) + " Z: "
				+ f(accFeat.getAvPeakDistance(2))
				+ "\nZero crossing count: X: " + accFeat.getCrossingCount(0)
				+ " Y: " + accFeat.getCrossingCount(1) + " Z: "
				+ accFeat.getCrossingCount(2)
				+ "\nAverage resultant acceleration: "
				+ f(accFeat.getResultantAcc()) + "\nAcceleration histograms:"
				+ "\n    X: " + printHistogram(accFeat.getHistogramArray(0))
				+ "\n    Y: " + printHistogram(accFeat.getHistogramArray(1))
				+ "\n    Z: " + printHistogram(accFeat.getHistogramArray(2))
				+ "\nFFT histograms:" + "\n    X: "
				+ printHistogram(accFeat.getFFTHistogramArray(0)) + "\n    Y: "
				+ printHistogram(accFeat.getFFTHistogramArray(1)) + "\n    Z: "
				+ printHistogram(accFeat.getFFTHistogramArray(2)));

	}
}
