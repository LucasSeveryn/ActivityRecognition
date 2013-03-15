package com.example.actrecognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.androidplot.xy.SimpleXYSeries;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, CompoundButton.OnCheckedChangeListener,
		TextWatcher, OnItemSelectedListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

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

	float[] averageNoise = { 0, 0, 0 };

	boolean showfft;
	
	int purgeCounter = 0;

	int displayType = 0;

	private AccActivity tempActivity;

	private int index = 0;

	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");

	AccData recordedData;
	AccData monitorPlotData = new AccData();

	static ArrayList<AccActivity> activityLibrary;

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
					if (recordedData.getxData().size() <= 511) {
						recordedData.addX(x);
						recordedData.addY(y);
						recordedData.addZ(z);
					} else {
						recordedData.setNoise(averageNoise);
						finishRecording();
					}
				}

				monitorTab.updatePlot(monitorPlotData.getxData(), xPlotSeries,
						monitorTab.xPlot, x);
				monitorTab.updatePlot(monitorPlotData.getyData(), yPlotSeries,
						monitorTab.yPlot, y);
				monitorTab.updatePlot(monitorPlotData.getzData(), zPlotSeries,
						monitorTab.zPlot, z);

				if (monitorPlotData.getxData().size() == 119) {
					float[] newAverageNoise = {
							FeatureExtractors.average(monitorPlotData
									.getxData()),
							FeatureExtractors.average(monitorPlotData
									.getyData()),
							FeatureExtractors.average(monitorPlotData
									.getzData()) };
					averageNoise = newAverageNoise;
				}

				if (counter % 25 == 0) {
					((TextView) findViewById(R.id.xAccPlotLabel))
							.setText("x-plane acc. Error: " + averageNoise[0]
									+ " Current value: " + x);
					((TextView) findViewById(R.id.yAccPlotLabel))
							.setText("y-plane acc. Error: " + averageNoise[1]
									+ " Current value: " + y);
					((TextView) findViewById(R.id.zAccPlotLabel))
							.setText("z-plane acc. Error: " + averageNoise[2]
									+ " Current value: " + z);
					counter = 1;
				}

				counter++;
			}
		}

	};
	private boolean spinnerFirstInvoke = true;

	public void finishRecording() {
		tempActivity = new AccActivity(recordedData);
		recordingTab.updateActivityDetailText(tempActivity);
		drawRecordingGraph();
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
			pause(500);
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

		activityLibrary = new ArrayList<AccActivity>();

		String ser = SerializeObject.ReadSettings(this, "activityLibrary.dat");
		if (ser != null && !ser.equalsIgnoreCase("")) {
			Object obj = SerializeObject.stringToObject(ser);
			if (obj instanceof ArrayList) {
				activityLibrary = (ArrayList<AccActivity>) obj;
				Toast.makeText(this, "Size: " + activityLibrary.size(),
						Toast.LENGTH_SHORT).show();
				tempActivity = activityLibrary.get(activityLibrary.size()-1);
				index=activityLibrary.size()-1;
			}
		}

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
			monitorTab = (AccMonitorFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			break;
		case 2:
			recordingTab = (ActRecordingFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
			break;
		case 3:
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
		recordedData = new AccData();
		Toast.makeText(this, "Recording will start in 5 sec",
				Toast.LENGTH_SHORT).show();
		Runnable r = new dataRecording();
		new Thread(r).start();
	}

	public void recalculateError(View view) {
		recalculateError();
	}

	public void purge(View view) {
		purgeCounter++;
		if(purgeCounter>3){
			activityLibrary.clear();
		}
	}
	
	void recalculateError() {
		monitorPlotData.clear();
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
			tempActivity = activityLibrary.get(index + 1);
			recordingTab.updateActivityDetailText(tempActivity);
			drawRecordingGraph();
			index++;
			Toast.makeText(this, "Activity #" + index + " selected",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void drawRecognitionGraph() {
		switch (displayType) {
		case 0:
			recordingTab.drawData(tempActivity.getData(), -15, 15, 512);
			break;
		case 1:
			recordingTab.drawData(tempActivity.getlpfData(), -15, 15, 512);
			break;
		case 2:
			recordingTab.drawData(tempActivity.gethpfData(), -15, 15, 512);
			break;
		case 3:
			recordingTab.drawData(tempActivity.getbpfData(), -15, 15, 512);
			break;
		case 4:
			recordingTab.drawData(tempActivity.getfData(), -1, 100, 512);
			break;
		default:
			break;
		}
	}

	public void drawRecordingGraph() {
		switch (displayType) {
		case 0:
			recordingTab.drawData(tempActivity.getData(), -15, 15, 512);
			break;
		case 1:
			recordingTab.drawData(tempActivity.getlpfData(), -15, 15, 512);
			break;
		case 2:
			recordingTab.drawData(tempActivity.gethpfData(), -15, 15, 512);
			break;
		case 3:
			recordingTab.drawData(tempActivity.getbpfData(), -15, 15, 512);
			break;
		case 4:
			recordingTab.drawData(tempActivity.getfData(), -1, 100, 512);
			break;
		default:
			break;
		}
	}

	public void previousAccActivity(View view) {
		if (index - 1 >= 0) {
			tempActivity = activityLibrary.get(index - 1);
			recordingTab.updateActivityDetailText(tempActivity);
			drawRecordingGraph();
			index--;
			Toast.makeText(this, "Activity #" + index + " selected",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void remove(View view){
		activityLibrary.remove(index);
		index=index-1;
		tempActivity = activityLibrary.get(index);
		recordingTab.updateActivityDetailText(tempActivity);
		drawRecordingGraph();
		String ser = SerializeObject.objectToString(activityLibrary);
		if (ser != null && !ser.equalsIgnoreCase("")) {
			SerializeObject.WriteSettings(this, ser, "activityLibrary.dat");
		} else {
			SerializeObject.WriteSettings(this, "", "activityLibrary.dat");
		}
	}
	
	public void identify(View view) {
		AccActivity result = IdentificationEngine.findClosestMatch(
				tempActivity, activityLibrary);
		
		Toast.makeText(this, "Activity Type: " + result.getType(),
				Toast.LENGTH_LONG).show();
	}

	public void saveActivity(View view) {
		index = activityLibrary.size();
		if (!activityLibrary.contains(tempActivity)) {
			tempActivity.setType(recordingTab.getTypeSpinnerValue());
			activityLibrary.add(tempActivity);
			Toast.makeText(this,
					"Activity saved. Library size:" + activityLibrary.size(),
					Toast.LENGTH_SHORT).show();
			String ser = SerializeObject.objectToString(activityLibrary);
			if (ser != null && !ser.equalsIgnoreCase("")) {
				SerializeObject.WriteSettings(this, ser, "activityLibrary.dat");
			} else {
				SerializeObject.WriteSettings(this, "", "activityLibrary.dat");
			}

		} else {
			Toast.makeText(this, "Activity already in the library.",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			showfft = true;
		} else {
			showfft = false;
		}
		drawRecordingGraph();
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
		if (arg0.length() > 0) {
			tempActivity.setRate(Integer.parseInt(arg0.toString()));
			tempActivity.recalculate();
			recordingTab.updateActivityDetailText(tempActivity);
			Toast.makeText(this, "Rate changed to:" + tempActivity.getRate(),
					Toast.LENGTH_SHORT).show();
			if (activityLibrary.size() != 0
					&& tempActivity == activityLibrary.get(index)) {
				activityLibrary.set(index, tempActivity);
			}
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (spinnerFirstInvoke == true) {
			spinnerFirstInvoke = false;
		} else {
			if (tempActivity != null) {
				tempActivity.setType(recordingTab.getTypeSpinnerValue());

				int prevDisplayType = displayType;
				displayType = recordingTab.getdisplaySpinnerValue();
				if (displayType != prevDisplayType)
					drawRecordingGraph();
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
