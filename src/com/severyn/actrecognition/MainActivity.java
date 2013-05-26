package com.severyn.actrecognition;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;
import com.example.actrecognition.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, CompoundButton.OnCheckedChangeListener,
		TextWatcher, OnItemSelectedListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGyro;
	private NaiveGaussianClassifier ng;
	private String apiKey = "Ix7evhXTw3uwk1gDHCvzz-uMNEhOy8ZN";

	
	private double samplingRate = 40; // Hz
	int sensorDelayMicroseconds = (int) (Math
			.round(((1 / this.samplingRate) * 1000000.0)));
	
	
	private double samplingRateG = 40; // Hz
	int sensorDelayMicrosecondsG = (int) (Math
			.round(((1 / this.samplingRateG) * 1000000.0)));
	

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
	float[] averageGNoise = { 0, 0, 0 };
	
	boolean showfft;

	int purgeCounter = 0;

	int displayType = 0;

	private AccActivity tempActivity;
	private AccFeat tempFeat;

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
					recordedData.addX(x);
					recordedData.addY(y);
					recordedData.addZ(z);
				}
				if (recordedData!=null&&recordedData.size()>511){
					recordingEnabled = false;
					finishRecording();
				}
//				if (recordingEnabled) {
//					if (recordedData.getxData().size() <= 511) {
//						recordedData.addX(x);
//						recordedData.addY(y);
//						recordedData.addZ(z);
//					} else {
//						Log.d("****Accelero****", "Accelerometer finished recording");
//						recordedData.setNoise(averageNoise);
//						finishRecording();
//					}
//				}

				 monitorTab.updatePlot(monitorPlotData.getxData(),
				 xPlotSeries,
				 monitorTab.xPlot, x);
				 monitorTab.updatePlot(monitorPlotData.getyData(),
				 yPlotSeries,
				 monitorTab.yPlot, y);
				 monitorTab.updatePlot(monitorPlotData.getzData(),
				 zPlotSeries,
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
	private final SensorEventListener mGSensorListener = new SensorEventListener() {
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

				
				if (recordingEnabled&&recordedGData!=null&&recordedGData.size()<512) {
					recordedGData.addX(x);
					recordedGData.addY(y);
					recordedGData.addZ(z);
				}

				
//				if (recordingEnabled) {
//					if (recordedGData.getxData().size() <= 511) {
//						recordedGData.addX(x);
//						recordedGData.addY(y);
//						recordedGData.addZ(z);
//					} else {
//						Log.d("****Gyro****", "Gyro finished recording");
//						recordedGData.setNoise(averageGNoise);
//						finishRecording();
//					}
//				}

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
					averageGNoise = newAverageNoise;
				}

				if (counter % 25 == 0) {
					((TextView) findViewById(R.id.xAccPlotLabel))
							.setText("x-plane acc. Error: " + averageGNoise[0]
									+ " Current value: " + x);
					((TextView) findViewById(R.id.yAccPlotLabel))
							.setText("y-plane acc. Error: " + averageGNoise[1]
									+ " Current value: " + y);
					((TextView) findViewById(R.id.zAccPlotLabel))
							.setText("z-plane acc. Error: " + averageGNoise[2]
									+ " Current value: " + z);
					counter = 1;
				}

				counter++;
			}
		}

	};

	private boolean spinnerFirstInvoke = true;
	private AccData recordedGData;

	public void finishRecording() {
		recordingEnabled = false;
//		recordedData.setNoise(averageNoise);
//		recordedGData.setNoise(averageGNoise);
		tempActivity = new AccActivity(recordedData, recordedGData);
		tempFeat = FeatureExtractors2.calculateFeatures(tempActivity.getData());
		recordedData = new AccData();
		recordedGData = new AccData();
		recordingTab.updateActivityDetailText(tempActivity,tempFeat);
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
			pause(13000);
			recordingEnabled = false;
			v.vibrate(100);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
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
				tempActivity = activityLibrary.get(activityLibrary.size() - 1);
				tempFeat = FeatureExtractors2.calculateFeatures(tempActivity.getData());
				index = activityLibrary.size() - 1;
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
				sensorDelayMicroseconds);
//		mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//		mSensorManager.registerListener(mGSensorListener, mGyro,
//				sensorDelayMicrosecondsG);
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
		recordedGData = new AccData();
		Toast.makeText(this, "Recording will start in 5 sec",
				Toast.LENGTH_SHORT).show();
		Runnable r = new dataRecording();
		new Thread(r).start();
	}
	
	public void toast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	public void recalculateError(View view) {
		recalculateError();
	}

	public void purge(View view) {
		purgeCounter++;
		if (purgeCounter > 3) {
			activityLibrary.clear();
		}
	}

	public void send(View view) {
		String apiURI = "https://api.mongolab.com/api/1/databases/activity_recognition/collections/accelerometer_data?apiKey=Ix7evhXTw3uwk1gDHCvzz-uMNEhOy8ZN";
		try {

			// make web service connection
			final HttpPost request = new HttpPost(apiURI);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");

			// Build JSON string
			// JSONStringer TestApp = new
			// JSONStringer().object().key("id").value("1").endObject();
			// StringEntity entity = new StringEntity(TestApp.toString());
			Gson gson = new Gson();
			Gson gson2 = new Gson();
			JsonElement gyroReadings = gson.toJsonTree(tempActivity.getGyroData());
			JsonElement jsonElement = gson.toJsonTree(tempActivity.getData());
			jsonElement.getAsJsonObject().add("gyro", gyroReadings);
			jsonElement.getAsJsonObject()
					.addProperty("type", tempActivity.type);
			

			String json = gson.toJson(jsonElement);
			StringEntity entity = new StringEntity(json);

			Log.d("****Parameter Input****", "Testing:" + json);
			request.setEntity(entity);
			// Send request to WCF service
			final DefaultHttpClient httpClient = new DefaultHttpClient();

			new AsyncTask<Void, Void, Void>() {
				@Override
				public Void doInBackground(Void... arg) {
					try {
						HttpResponse response = httpClient.execute(request);
						Log.d("WebInvoke", "Saving: "
								+ response.getStatusLine().toString());
						// Get the status of web service
						BufferedReader rd = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						// print status in log
						String line = "";
						while ((line = rd.readLine()) != null) {
							Log.d("****Status Line***", "Webservice: " + line);

						}
					} catch (Exception e) {
						Log.e("SendMail", e.getMessage(), e);
					}
					return null;
				}
			}.execute();

		} catch (Exception e) {
			e.printStackTrace();
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
			tempFeat = FeatureExtractors2.calculateFeatures(tempActivity.getData());
			recordingTab.updateActivityDetailText(tempActivity,tempFeat);
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
			tempFeat = FeatureExtractors2.calculateFeatures(tempActivity.getData());
			recordingTab.updateActivityDetailText(tempActivity,tempFeat);
			drawRecordingGraph();
			index--;
			Toast.makeText(this, "Activity #" + index + " selected",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void remove(View view) {
		activityLibrary.remove(index);
		index = index - 1;
		tempActivity = activityLibrary.get(index);
		recordingTab.updateActivityDetailText(tempActivity,tempFeat);
		drawRecordingGraph();
		String ser = SerializeObject.objectToString(activityLibrary);
		if (ser != null && !ser.equalsIgnoreCase("")) {
			SerializeObject.WriteSettings(this, ser, "activityLibrary.dat");
		} else {
			SerializeObject.WriteSettings(this, "", "activityLibrary.dat");
		}
	}

	public void identify(View view) {
//		finishRecording();	
		Pair <ArrayList<Double>,String> classification = ng.classify(FeatureExtractors2.calculateFeatures(tempActivity.getData()));
		recognitionTab.updateStatusText(classification.second,false);
		ArrayList<Double> results = classification.first;
		
		int maxindex = 0;
		double maxvalue = results.get(0);

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6
					&& !Double.isNaN(results.get(i))) {
				maxvalue = results.get(i);
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < 9; i++) {
			if (!Double.isNaN(results.get(i))) {
				if (results.get(i) > results.get(i) && i != 1 && i != 4
						&& i != 5 && i != 6) {
					maxvalue = results.get(i);
					maxindex = i;
				}
			}

		}
	
		recognitionTab.updateStatusText2(FeatureExtractors2.getType(maxindex), true);
		recognitionTab.drawData(classification.first);
//		AccActivity result = IdentificationEngine.findClosestMatch(
//				tempActivity, activityLibrary);
//
//		Toast.makeText(this, "Activity Type: " + result.getType(),
//				Toast.LENGTH_LONG).show();
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
			recordingTab.updateActivityDetailText(tempActivity,tempFeat);
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
	
	
	public void loadEntropyFromCloud() {
		String apiURI = null;
		// try {
		apiURI = "https://api.mongolab.com/api/1/databases/activity_recognition/collections/entropy_data"
				// + "?f="
				// + URLEncoder.encode("{\"" + arrayName + "\": 1}", "UTF-8")
				// + "&l=1"
				+ "?apiKey=" + apiKey;
		// } catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		Log.d("****Status Line***", "" + apiURI);

		try {

			// make web service connection
			final StringBuilder builder = new StringBuilder();

			final HttpGet request = new HttpGet(apiURI);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			final DefaultHttpClient httpClient = new DefaultHttpClient();

			new AsyncTask<Void, Void, String>() {

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					writeEntropyData(result);
				}

				@Override
				public String doInBackground(Void... arg) {
					try {
						HttpResponse response = httpClient.execute(request);
						StatusLine statusLine = response.getStatusLine();
						int statusCode = statusLine.getStatusCode();
						if (statusCode == 200) {

							HttpEntity entity = response.getEntity();
							InputStream content = entity.getContent();

							BufferedReader reader = new BufferedReader(
									new InputStreamReader(content));
							String line;
							while ((line = reader.readLine()) != null) {
								builder.append(line);
							}
							Log.d("****Status Line***", "Success");

							return builder.toString();

						} else {
							Log.d("****Status Line***",
									"Failed to download file");
						}

					} catch (Exception e) {
						Log.e("SendMail", e.getMessage(), e);
					}
					return null;
				}
			}.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void writeEntropyData(String result) {
		recognitionTab.updateStatusText("Entropy data loaded succefully.\nFirst 128 characters:" + result.substring(0, Math.min(result.length(), 128)) + "...", false);
		toast("Entropy data loaded succesfully. First 128 characters:" + result.substring(0, Math.min(result.length(), 128)) + "...");
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			
		   
			
			ArrayList<ArrayList<Double>> entropyMean = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>> entropyVar = new ArrayList<ArrayList<Double>>();
			ArrayList<Double> filler = new ArrayList<Double>();
			
			 for (int i = 0; i < 9; i++) {
			        entropyMean.add(filler);
			        entropyVar.add(filler);
			 }
			
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = (JSONObject) jsonArray.get(i);
				Iterator<?> keys = obj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if (key.startsWith("mean")) {
						ArrayList<Double> tempArray = new ArrayList<Double>();
						JSONArray dataArray = obj.getJSONArray(key);
						for (int j = 0; j < dataArray.length(); j++) {
							JSONObject arrayEntry = (JSONObject) dataArray
									.get(j);
							tempArray.add(arrayEntry.getDouble(Integer
													.toString(j)));
						}
						entropyMean
						.set((Integer.valueOf(key.substring(key
								.length() - 1))),tempArray);
					} else if (key.startsWith("var")) {
						ArrayList<Double> tempArray = new ArrayList<Double>();
	
						JSONArray dataArray = obj.getJSONArray(key);
						for (int j = 0; j < dataArray.length(); j++) {
							JSONObject arrayEntry = (JSONObject) dataArray
									.get(j);
							tempArray.add(arrayEntry.getDouble(Integer
													.toString(j)));
						}
						entropyVar
						.set((Integer.valueOf(key.substring(key
								.length() - 1))),tempArray);
					}
	
				}
	
			}
			ng = new NaiveGaussianClassifier(entropyMean, entropyVar);
	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void getEntropyData(View view) {
		loadEntropyFromCloud();
	}
}
