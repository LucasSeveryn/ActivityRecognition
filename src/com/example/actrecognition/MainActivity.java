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
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
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
	
	ArrayList<Float> xDataHistory = new ArrayList<Float>();
	ArrayList<Float> yDataHistory = new ArrayList<Float>();
	ArrayList<Float> zDataHistory = new ArrayList<Float>();
	
	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");
	
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
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];



			if (recordingEnabled) {
				ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager().findFragmentByTag(tagFragment2);
				actRecordingFragment.passValues(x, y, z);
			}

			if (activityRecordingEnabled) {
				ActRecognitionFragment actRecognitionFragment = (ActRecognitionFragment) getSupportFragmentManager().findFragmentByTag(tagFragment3);
				actRecognitionFragment.passValues(x, y, z);
			}

			updatePlot(xDataHistory, xPlotSeries, xPlot, x);
			updatePlot(yDataHistory, yPlotSeries, yPlot, y);
			updatePlot(zDataHistory, zPlotSeries, zPlot, z);

			if (xDataHistory.size() == 119) {
				Float sumx = 0f;
				Float sumy = 0f;
				Float sumz = 0f;

				for (Float number : xDataHistory) {
					sumx += number;
				}
				for (Float number : yDataHistory) {
					sumy += number;
				}
				for (Float number : zDataHistory) {
					sumz += number;
				}

				xError = sumx / xDataHistory.size();
				yError = sumy / yDataHistory.size();
				zError = sumz / zDataHistory.size();
			}

			if (counter % 50 == 0) {
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

	};

	public void setTabFragment(int index, String tag) {
		switch (index) {
		case 1:
			tagFragment1 = tag;
			break;
		case 2:
			tagFragment2 = tag;
			break;
		case 3:
			tagFragment3 = tag;
			break;
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

		mInitialised = false;
		mSensorManager = (SensorManager) getSystemService(
				Context.SENSOR_SERVICE);
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
				return accMonitorFragment;
			case 1:
				ActRecordingFragment actRecordingFragment = new ActRecordingFragment();
				return actRecordingFragment;
			case 2:
				ActRecognitionFragment actRecognitionFragment = new ActRecognitionFragment();
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

	public void startRecording(View view) {
		ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment2);
		actRecordingFragment.clearDataRecording();
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);
		accMonitorFragment.enableRecording();

		Toast.makeText(this, "Recording will start in 5 sec", Toast.LENGTH_LONG)
				.show();
	}

	public void startActivityRecording(View view) {
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);
		accMonitorFragment.enableActivityRecording();

		Toast.makeText(this, "Recording Activity will start in 5 sec",
				Toast.LENGTH_LONG).show();
	}

	public void recalculateError(View view) {
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);
		accMonitorFragment.recalculateError();
	}

	public int[] getZeroCrossingCountsRecording(View view) {
		ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment2);
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);

		int xZeroCrossingCount = getZeroCrossingCount(xActRecording,
				xError, 0.30f, 2);
		int yZeroCrossingCount = getZeroCrossingCount(yActRecording,
				yError, 0.30f, 2);
		int zZeroCrossingCount = getZeroCrossingCount(zActRecording,
				zError, 0.30f, 2);

		int[] counts = { xZeroCrossingCount, yZeroCrossingCount,
				zZeroCrossingCount };

		return counts;
	}

	public int getZeroCrossingCountsRecording(ArrayList<Float> data, float zero,
			float spread, int rate) {

		int count = 0;

		for (int j = 0; j < data.size(); j++) {
			float n = data.get(j);
			if (n < zero + spread && n > zero - spread) {
				data.set(j, zero);
			}
		}

		float x;
		float previous = data.get(0);
		for (int i = rate; i + rate <= data.size(); i = i + rate) {
			x = data.get(i);
			if (Math.signum(previous - zero) != Math.signum(x - zero)) {

				count++;
			}
			previous = x;
		}
		return count;

	}

	public void featureExtraction(View view) {
		ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment2);
		actRecordingFragment
				.updateZeroCrossingRateText(getZeroCrossingCountsRecording(view));
		actRecordingFragment
				.updateMaximumDisplacementText(getMaximumDisplacements(view));

	}

	private float[] getMaximumDisplacements(View view) {
		ActRecordingFragment actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment2);
		AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager()
				.findFragmentByTag(tagFragment1);

		ArrayList<Float> xActRecording = actRecordingFragment.xDataRecording;
		ArrayList<Float> yActRecording = actRecordingFragment.yDataRecording;
		ArrayList<Float> zActRecording = actRecordingFragment.zDataRecording;

		float[] result = { Collections.max(xActRecording),
				Collections.min(xActRecording), Collections.max(yActRecording),
				Collections.min(yActRecording), Collections.max(zActRecording),
				Collections.min(zActRecording), xError,
				yError, zError };

		return result;
	}
	
	void recalculateError() {
		xDataHistory.clear();
		yDataHistory.clear();
		zDataHistory.clear();
	}
	
	
	//Data Recording - used in recording tab
	public class dataRecording implements Runnable {
		Vibrator v;
		public dataRecording() {
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}

		public void run() {
			long t = System.currentTimeMillis();
			long end = t + 5000;
			while (System.currentTimeMillis() < end) {
				//wait for 5 sec to allow putting phone into pocket
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			recalculateError();
			t = System.currentTimeMillis();
			end = t + 3500;
			while (System.currentTimeMillis() < end) {
				//wait for 5 sec to allow putting phone into pocket
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			v.vibrate(100);	
			t = System.currentTimeMillis();
			end = t + 1000;
			while (System.currentTimeMillis() < end) {
				//wait for 5 sec to allow putting phone into pocket
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			recordingEnabled = true;
			t = System.currentTimeMillis();
			end = t + 10000;
			while (System.currentTimeMillis() < end) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			recordingEnabled = false;
			v.vibrate(100);	
		}
	}

	void enableRecording() {
		Runnable r = new dataRecording();
		new Thread(r).start();
	}

	public SimpleXYSeries getPlotSeries(int axis) {
		switch(axis){
		case 0:
			return xPlotSeries;
		case 1:
			return yPlotSeries;
		case 2:
			return zPlotSeries;
		}
		return null;
		
	}
	
}
