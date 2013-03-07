package com.example.actrecognition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.androidplot.xy.SimpleXYSeries;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;


public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, CompoundButton.OnCheckedChangeListener, TextWatcher {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	String tagFragment1;
	String tagFragment2;
	String tagFragment3;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private boolean recordingEnabled = false;
	private boolean activityRecordingEnabled = false;

	float yError;
	float zError;
	float xError;

	int xCrossings;
	int yCrossings;
	int zCrossings;
	
	double xSD;
	double ySD;
	double zSD;
	
	boolean showfft;
	
	private float[] minMax;
	
	private AccActivity tempActivity;

	private int index = 0;

	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");

	ArrayList<Float> xMonitorPlotData = new ArrayList<Float>();
	ArrayList<Float> yMonitorPlotData = new ArrayList<Float>();
	ArrayList<Float> zMonitorPlotData = new ArrayList<Float>();

	static ArrayList<Float> xRecordingData = new ArrayList<Float>();
	static ArrayList<Float> yRecordingData = new ArrayList<Float>();
	static ArrayList<Float> zRecordingData = new ArrayList<Float>();

	static ArrayList<Float> xfRecordingData = new ArrayList<Float>();
	static ArrayList<Float> yfRecordingData = new ArrayList<Float>();
	static ArrayList<Float> zfRecordingData = new ArrayList<Float>();

	static ArrayList<AccActivity> activityLibrary = new ArrayList<AccActivity>();

	AccMonitorFragment monitorTab;
	ActRecognitionFragment recognitionTab;
	ActRecordingFragment recordingTab;

	private final SensorEventListener mSensorListener = new SensorEventListener() {
		private int counter = 0;

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (monitorTab != null) {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];

				if (recordingEnabled) {
					if (xRecordingData.size() <= 511) {
						xRecordingData.add(x);
						yRecordingData.add(y);
						zRecordingData.add(z);
					} else {
						recordingFinished();
					}
				}

				if (activityRecordingEnabled) {
					ActRecognitionFragment actRecognitionFragment = (ActRecognitionFragment) getSupportFragmentManager()
							.findFragmentByTag(tagFragment3);
					actRecognitionFragment.passValues(x, y, z);
				}

				monitorTab.updatePlot(xMonitorPlotData, xPlotSeries,
						monitorTab.xPlot, x);
				monitorTab.updatePlot(yMonitorPlotData, yPlotSeries,
						monitorTab.yPlot, y);
				monitorTab.updatePlot(zMonitorPlotData, zPlotSeries,
						monitorTab.zPlot, z);

				if (xMonitorPlotData.size() == 119) {
					Float sumx = 0f;
					Float sumy = 0f;
					Float sumz = 0f;

					for (Float number : xMonitorPlotData) {
						sumx += number;
					}
					for (Float number : yMonitorPlotData) {
						sumy += number;
					}
					for (Float number : zMonitorPlotData) {
						sumz += number;
					}

					xError = sumx / xMonitorPlotData.size();
					yError = sumy / yMonitorPlotData.size();
					zError = sumz / zMonitorPlotData.size();
				}

				if (counter % 25 == 0) {
					((TextView) findViewById(R.id.xAccPlotLabel))
							.setText("x-plane acc. Error: " + xError
									+ " Current value: " + x);
					((TextView) findViewById(R.id.yAccPlotLabel))
							.setText("y-plane acc. Error: " + yError
									+ " Current value: " + y);
					((TextView) findViewById(R.id.zAccPlotLabel))
							.setText("z-plane acc. Error: " + zError
									+ " Current value: " + z);
				}

				counter++;
			}
		}


	};


	public void recordingFinished() {
		Toast.makeText(this, "Recording finished.", Toast.LENGTH_SHORT)
		.show();
		calculateFourier();
		calculateMaximumDisplacements();
		calculateZeroCrossingCounts();
		calculateStandardDeviation();
		AccActivity newActivity = new AccActivity();
		newActivity.setType(recordingTab.getEditTextText());
		newActivity.setxCrossings(xCrossings);
		newActivity.setyCrossings(yCrossings);
		newActivity.setzCrossings(zCrossings);
		newActivity.setxData(xRecordingData);
		newActivity.setyData(yRecordingData);
		newActivity.setzData(zRecordingData);
		newActivity.setMinMax(minMax);
		newActivity.setxSD(xSD);
		newActivity.setySD(ySD);
		newActivity.setzSD(zSD);

		recordingTab.updateActivityDetailText(newActivity);
		tempActivity=newActivity;
		drawWaveGraph();
		
	}

	private void calculateStandardDeviation() {
		FeatureExtractors f = FeatureExtractors.INSTANCE;
		xSD = f.getStandardDeviation(xRecordingData);
		ySD = f.getStandardDeviation(yRecordingData);
		zSD = f.getStandardDeviation(zRecordingData);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				AccMonitorFragment accMonitorFragment = new AccMonitorFragment();
				monitorTab = accMonitorFragment;
				return accMonitorFragment;
			case 1:
				ActRecordingFragment actRecordingFragment = new ActRecordingFragment();
				recordingTab = actRecordingFragment;
				return actRecordingFragment;
			case 2:
				ActRecognitionFragment actRecognitionFragment = new ActRecognitionFragment();
				recognitionTab = actRecognitionFragment;
				return actRecognitionFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	// Data Recording - used in recording tab
	public class dataRecording implements Runnable {
		Vibrator v;

		public dataRecording() {
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}

		public void pause(long n) {
			long t = System.currentTimeMillis();
			long end = t + n;
			while (System.currentTimeMillis() < end) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void run() {
			long t = System.currentTimeMillis();
			pause(5000);
			v.vibrate(100);
			pause(200);
			recalculateError();
			pause(3500);
			v.vibrate(100);
			pause(200);
			recordingEnabled = true;
			t = System.currentTimeMillis();
			pause(12000);
			recordingEnabled = false;
			v.vibrate(100);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mSensorListener, mAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setTabFragment(int index, String tag) {
		switch (index) {
		case 1:
			tagFragment1 = tag;
			monitorTab = (AccMonitorFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			break;
		case 2:
			tagFragment2 = tag;
			recordingTab = (ActRecordingFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			break;
		case 3:
			tagFragment3 = tag;
			recognitionTab = (ActRecognitionFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);

			break;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void startRecording(View view) {
		startRecording();
		Toast.makeText(this, "Recording will start in 5 sec", Toast.LENGTH_LONG)
				.show();
	}

	public void startActivityRecording(View view) {
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);
		// monitorTab.enableActivityRecording();

		Toast.makeText(this, "Recording Activity will start in 5 sec",
				Toast.LENGTH_LONG).show();
	}

	public void recalculateError(View view) {
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);
		recalculateError();
	}

	public void calculateZeroCrossingCounts() {
		FeatureExtractors f = FeatureExtractors.INSTANCE;
		int xZeroCrossingCount = f.getZeroCrossingCount(xRecordingData, xError,
				0.30f);
		int yZeroCrossingCount = f.getZeroCrossingCount(yRecordingData, yError,
				0.30f);
		int zZeroCrossingCount = f.getZeroCrossingCount(zRecordingData, zError,
				0.30f);

		int[] counts = { xZeroCrossingCount, yZeroCrossingCount,
				zZeroCrossingCount };
		xCrossings = counts[0];
		yCrossings = counts[1];
		zCrossings = counts[2];
	}
	
	public void recalculateZeroCrossingCounts(AccActivity temp) {
		FeatureExtractors f = FeatureExtractors.INSTANCE;
		int xZeroCrossingCount = f.getZeroCrossingCount(temp.xData, temp.xError,
				0.30f);
		int yZeroCrossingCount = f.getZeroCrossingCount(temp.yData, temp.yError,
				0.30f);
		int zZeroCrossingCount = f.getZeroCrossingCount(temp.zData, temp.zError,
				0.30f);

		int[] counts = { xZeroCrossingCount, yZeroCrossingCount,
				zZeroCrossingCount };
		temp.setxCrossings(counts[0]);
		temp.setyCrossings(counts[1]);
		temp.setzCrossings(counts[2]);
	}

	


	private void calculateMaximumDisplacements() {
		float[] result = { Collections.max(xRecordingData),
				Collections.min(xRecordingData),
				Collections.max(yRecordingData),
				Collections.min(yRecordingData),
				Collections.max(zRecordingData),
				Collections.min(zRecordingData), xError, yError, zError };
		minMax = result;
	}

	public ArrayList<Float> substractError(ArrayList<Float> data, Float error) {
		for (int i = 0; i < data.size(); i++) {
			data.set(i, data.get(i) - error);
		}
		return data;
	}

	public void calculateFourier() {
		xfRecordingData = substractError(
				(ArrayList<Float>) xRecordingData.clone(), xError);
		yfRecordingData = substractError(
				(ArrayList<Float>) yRecordingData.clone(), yError);
		zfRecordingData = substractError(
				(ArrayList<Float>) zRecordingData.clone(), zError);

		FeatureExtractors f = FeatureExtractors.INSTANCE;
		xfRecordingData = f.iterativeFFT(xfRecordingData, 1);
		yfRecordingData = f.iterativeFFT(yfRecordingData, 1);
		zfRecordingData = f.iterativeFFT(zfRecordingData, 1);

	}
	
	void drawFourierGraph(){
		recordingTab.drawData(xfRecordingData, yfRecordingData,
				zfRecordingData, -1, 100, 512);
	}
	
	void drawWaveGraph(){
		recordingTab.drawData(xRecordingData, yRecordingData,
				zRecordingData, -15, 15, 512);
	}

	void recalculateError() {
		xMonitorPlotData.clear();
		yMonitorPlotData.clear();
		zMonitorPlotData.clear();
	}

	void startRecording() {
		clearDataRecording();
		Runnable r = new dataRecording();
		new Thread(r).start();
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

	public void nextAccActivity(View view) {
		if (index + 1 < activityLibrary.size()) {
			AccActivity activity = activityLibrary.get(index + 1);
			recordingTab.updateActivityDetailText(activity);
			drawGraph(activity);
			tempActivity=activity;
			index++;
			Toast.makeText(this, "Activity #" + index + " selected", Toast.LENGTH_SHORT)
			.show();
		}

	}
	
	public void drawGraph(AccActivity activity){
		if(showfft){
			recordingTab.drawData(activity.getXfData(), activity.getYfData(),
					activity.getZfData(), -1, 100, 512);
		}else{
			recordingTab.drawData(activity.getxData(), activity.getyData(),
					activity.getzData(), -15, 15, 512);
		}
	}

	public void previousAccActivity(View view) {
		if (index - 1 >= 0) {
			AccActivity activity = activityLibrary.get(index - 1);
			recordingTab.updateActivityDetailText(activity);
			drawGraph(activity);
			tempActivity=activity;

			index--;
			Toast.makeText(this, "Activity #" + index + " selected", Toast.LENGTH_SHORT)
			.show();
		}

	}

	public void clearDataRecording() {
		xRecordingData.clear();
		yRecordingData.clear();
		zRecordingData.clear();
	}

	public void saveActivity(View view) {
		index = activityLibrary.size();
		AccActivity newActivity = new AccActivity();
		newActivity.setType(recordingTab.getEditTextText());
		newActivity.setxCrossings(xCrossings);
		newActivity.setyCrossings(yCrossings);
		newActivity.setzCrossings(zCrossings);
		newActivity.setMinMax(minMax);
		newActivity.setxError(xError);
		newActivity.setzError(zError);
		newActivity.setyError(yError);
		newActivity.setxData((ArrayList<Float>) xRecordingData.clone());
		newActivity.setzData((ArrayList<Float>) yRecordingData.clone());
		newActivity.setyData((ArrayList<Float>) zRecordingData.clone());
		newActivity.setfData((ArrayList<Float>) xfRecordingData.clone(),
				(ArrayList<Float>) yfRecordingData.clone(),
				(ArrayList<Float>) zfRecordingData.clone());
		newActivity.setxSD(xSD);
		newActivity.setySD(ySD);
		newActivity.setzSD(zSD);
		activityLibrary.add(newActivity);
		Toast.makeText(this, "Activity saved. Library size:" + activityLibrary.size(), Toast.LENGTH_SHORT)
		.show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 if(isChecked){
			 showfft=true;
			 drawFourierGraph();
		 }else{
			 showfft=false;
			 drawWaveGraph();
		 }
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		if (arg0.length()>0){
			FeatureExtractors f = FeatureExtractors.INSTANCE;
			f.setRate(Integer.parseInt(arg0.toString()));
			recalculateZeroCrossingCounts(tempActivity);
			recordingTab.updateActivityDetailText(tempActivity);
			Toast.makeText(this, "Rate changed to:" + f.getRate(), Toast.LENGTH_SHORT).show();
			if(activityLibrary.size()!=0&&tempActivity==activityLibrary.get(index)){
				activityLibrary.set(index, tempActivity);
			}
		}
		
	}
}
