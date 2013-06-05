package com.severyn.actrecognition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;
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
import android.widget.Spinner;
import android.widget.TextView;

public class ActRecognitionFragment extends Fragment {
	XYPlot pPlot = null;
	private SimpleXYSeries pSeries = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.act_recognition, container,
				false);

		((MainActivity) getActivity()).setTabFragment(3, getTag());

		pPlot = (XYPlot) rootView.findViewById(R.id.pPlot);
		
		
		pSeries = new SimpleXYSeries("Probability");
		pSeries.useImplicitXVals();
		pPlot.addSeries(pSeries, new BarFormatter(Color.argb(100, 51, 132, 187), Color.rgb(11, 108, 174)));
		
//		pPlot.setRangeBoundaries(, 15, BoundaryMode.FIXED);
//		xyzPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
		pPlot.setDomainBoundaries(0, 8, BoundaryMode.FIXED);
		pPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
//		xyzPlot.setTicksPerRangeLabel(1);
		pPlot.setDomainLabel("Type");
		pPlot.getDomainLabelWidget().pack();
		pPlot.setRangeLabel("Probability");
		pPlot.getRangeLabelWidget().pack();
		pPlot.setTitle("Classification Results");
		pPlot.getLayoutManager().remove(pPlot.getLegendWidget());
		pPlot.setRangeValueFormat(new DecimalFormat("#"));
		pPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);

		EditText useridEditText = (EditText) rootView
				.findViewById(R.id.useridEditText);
		useridEditText.addTextChangedListener((MainActivity) getActivity());
		
		
		
	     BarRenderer barRenderer = (BarRenderer) pPlot.getRenderer(BarRenderer.class);
	       if(barRenderer != null) {
	           // make our bars a little thicker than the default so they can be seen better:
	           barRenderer.setBarWidth(60);
	       }

		pPlot.redraw();
		
		return rootView;
	}
	
	public boolean getSendToServerCheckboxValue(){
		CheckBox sendToServerCheckbox = (CheckBox) this.getView()
				.findViewById(R.id.sendToServerCheckbox);
		return sendToServerCheckbox.isChecked();
	}

	public void drawData(List<Double> results) {
		ArrayList<Double> probabilityData = new ArrayList<Double>();
		
		for(int i=0;i<results.size();i++){
			if (!Double.isInfinite(results.get(i))  &&  i != 6 && i != 7 && i != 8) { //debug
				probabilityData.add(results.get(i));			
				//probabilityData.add(Math.exp(results.get(i)));
			}
			else probabilityData.add(0.0);
		}
		
		ArrayList<Double> probabilityData2 = new ArrayList<Double>();

		for(int i=0;i<probabilityData.size();i++){
			if (probabilityData.get(i)==0.0){
				probabilityData2.add(Collections.min(probabilityData)*1.5);
			}else{
				probabilityData2.add(probabilityData.get(i));
			}

		}
		
		pPlot.setRangeBoundaries(Collections.max(probabilityData2)*3,0, BoundaryMode.FIXED);
//		pPlot.setRangeValueFormat(new DecimalFormat("0000E0"));
		pSeries.setModel(probabilityData2,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);


		pPlot.redraw();

	}
	
	
	public void updateStatusText(String text, boolean append) {
		TextView statusText = (TextView) this.getView().findViewById(
				R.id.statusText);
		if(append){
			statusText.setText(statusText.getText()+ " " + text);
		}
		else statusText.setText(text);

	}
	
	public void updateStatusText2(String text, boolean append) {
		TextView statusText = (TextView) this.getView().findViewById(
				R.id.statusText2);
		if(append){
//			String existing = 
			statusText.setText(statusText.getText().toString() + text + "\n");
		}else{
			statusText.setText(text);
		}
		

	}

}
