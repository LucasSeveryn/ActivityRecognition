package com.severyn.actrecognition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;
import com.example.actrecognition.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, CompoundButton.OnCheckedChangeListener,
		TextWatcher, OnItemSelectedListener, OnInitListener, LocationListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private GaussianNaiveBayesClassifier ng;
	private String apiKey = "Ix7evhXTw3uwk1gDHCvzz-uMNEhOy8ZN";
	private boolean entropyDataLoaded = false;
	private TextToSpeech tts;
	private double samplingRate = 40; // Hz
	int sensorDelayMicroseconds = (int) (Math
			.round(((1 / this.samplingRate) * 1000000.0)));

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
	private boolean monitorEnabled = false;

	double[] averageNoise = { 0, 0, 0 };
	// float[] averageGNoise = { 0, 0, 0 };

	boolean showfft;

	int purgeCounter = 0;

	int displayType = 0;

	private AccData tempData;
	private AccFeat tempFeat;

	private int index = 0;

	private SimpleXYSeries xPlotSeries = new SimpleXYSeries("x acceleration");
	private SimpleXYSeries yPlotSeries = new SimpleXYSeries("y acceleration");
	private SimpleXYSeries zPlotSeries = new SimpleXYSeries("z acceleration");

	AccData recordedData;
	AccData monitorPlotData = new AccData();

	static ArrayList<AccData> accDataLibrary;
	static ArrayList<ClassificationResult> gnbcLibrary;

	public String f(Double d) {
		return String.format("%.6f", d);
	}

	AccMonitorFragment monitorTab;
	ActRecognitionFragment recognitionTab;
	ActRecordingFragment recordingTab;
	private boolean constantRecordingEnabled = false;
	private boolean constantSavingEnabled = false;
	private boolean constantIdentifying = false;
	private int cc = 1;
	private int limit = 511;
	private int burstCounter = 0;
	private boolean halfSizeMode = true;
	LocationManager lm;

	private final SensorEventListener mSensorListener = new SensorEventListener() {
		private int counter = 0;

		AccData tempBurstActivity;

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (monitorTab != null) {
				double x = event.values[0];
				double y = event.values[1];
				double z = event.values[2];
				if (recordingEnabled || constantRecordingEnabled) {
					recordedData.addX(x);
					recordedData.addY(y);
					recordedData.addZ(z);
					recordingTab.updateProgressBar(recordedData.size());
				}

				if (halfSizeMode)
					limit = 255;
				else
					limit = 511;
				if (recordedData != null && recordedData.size() > limit) {

					if (recordingEnabled) {
						recordingEnabled = false;
						finishRecording();
					} else if (constantRecordingEnabled) {
						if (constantIdentifying) {
							classify(recordedData);
							toast("classification number: " + cc);
							cc++;
						}

						if (constantSavingEnabled) {
							burstCounter++;
							if (burstCounter % 2 == 0 & burstCounter < 100) {
								AccData tempBurstActivityForAdd = new AccData(
										new ArrayList<Double>(
												recordedData.getxData()),
										new ArrayList<Double>(recordedData
												.getyData()),
										new ArrayList<Double>(recordedData
												.getzData()));

								addBurstActivity(tempBurstActivityForAdd);
								tempBurstActivity = new AccData();
							}

						}
						if (halfSizeMode)
							recordedData = recordedData
									.removeQuarterOfElements();
						else
							recordedData = recordedData.removeHalfOfElements();
					}
				}

				if (monitorEnabled) {
					monitorTab.updatePlot(monitorPlotData.getxData(),
							xPlotSeries, monitorTab.xPlot, x);
					monitorTab.updatePlot(monitorPlotData.getyData(),
							yPlotSeries, monitorTab.yPlot, y);
					monitorTab.updatePlot(monitorPlotData.getzData(),
							zPlotSeries, monitorTab.zPlot, z);

					if (monitorPlotData.getxData().size() == 119) {
						double[] newAverageNoise = {
								FeatureExtractors
										.calculateMean((monitorPlotData
												.getxData())),
								FeatureExtractors.calculateMean(monitorPlotData
										.getyData()),
								FeatureExtractors.calculateMean(monitorPlotData
										.getzData()) };
						averageNoise = newAverageNoise;
					}
				}
				if (counter % 25 == 0) {
					((TextView) findViewById(R.id.xAccPlotLabel))
							.setText("x-plane acc. Error: "
									+ f(averageNoise[0]) + " Current value: "
									+ f(x));
					((TextView) findViewById(R.id.yAccPlotLabel))
							.setText("y-plane acc. Error: "
									+ f(averageNoise[1]) + " Current value: "
									+ f(y));
					((TextView) findViewById(R.id.zAccPlotLabel))
							.setText("z-plane acc. Error: "
									+ f(averageNoise[2]) + " Current value: "
									+ f(z));
					counter = 1;
				}

				counter++;
			}
		}

	};

	private boolean spinnerFirstInvoke = true;
	private AccData recordedGData;
	private ClassificationResult tempGNBC;
	private int gnbcIndex;
	private int cpurgeCounter = 0;
	private int userid = 1337;

	public void finishRecording() {
		recordingEnabled = false;
		// recordedData.setNoise(averageNoise);
		// recordedGData.setNoise(averageGNoise);
		tempData = recordedData;
		tempFeat = FeatureExtractors.buildFeatureObject(recordedData);
		tempFeat.setType(9);
		recordedData = new AccData();
		recordedGData = new AccData();
		recordingTab.updateActivityDetailText(recordedData, tempFeat);
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
			if (!halfSizeMode)
				pause(13000);
			else
				pause(6500);
			recordingEnabled = false;
			v.vibrate(100);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		//
		// StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		accDataLibrary = new ArrayList<AccData>();
		gnbcLibrary = new ArrayList<ClassificationResult>();

		/*
		 * LOADING SERIALIZED ACTIVITY LIBRARY v.1
		 */

		String ser = SerializeObject.ReadSettings(this, "activityLibrary.dat");
		if (ser != null && !ser.equalsIgnoreCase("")) {
			Object obj = SerializeObject.stringToObject(ser);
			if (obj instanceof ArrayList) {
				accDataLibrary = (ArrayList<AccData>) obj;
				Toast.makeText(this, "Size: " + accDataLibrary.size(),
						Toast.LENGTH_SHORT).show();
				tempData = accDataLibrary.get(accDataLibrary.size() - 1);
				// recordingTab.setTypeCombobox(tempData.type);
				tempFeat = FeatureExtractors.buildFeatureObject(tempData);
				index = accDataLibrary.size() - 1;
			}
		}

		/*
		 * LOADING SERIALIZED ACTIVITY LIBRARY v.2
		 */

		// String line;
		// ArrayList<AccData> list = new ArrayList<AccData>();
		// StringBuilder fullText = new StringBuilder();
		//
		// try {
		// FileInputStream stream = this.openFileInput("activityLibrary.dat");
		// InputStreamReader inputStreamReader = new InputStreamReader(stream);
		// BufferedReader bufferedReader = new
		// BufferedReader(inputStreamReader);
		// while ((line = bufferedReader.readLine()) != null)
		// {
		// fullText.append(line);
		// }
		// if (fullText.length() > 0)
		// {
		// String[] myActivities = fullText.toString().split(",");
		// for (String activityString : myActivities)
		// {
		// list.add((AccData) activityString));
		// }
		// }
		// } catch (Exception e) {}
		// return list;

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
		// mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		// mSensorManager.registerListener(mGSensorListener, mGyro,
		// sensorDelayMicrosecondsG);

		tts = new TextToSpeech(this, this);

		// lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//
		// Criteria crit = new Criteria();
		// crit.setAccuracy(Criteria.ACCURACY_FINE);
		// String best = lm.getBestProvider(crit, false);
		// lm.requestLocationUpdates(best, 0, 1, this);
		// lm.requestLocationUpdates(best, 10000, 1, this);

	}

	public void onInit(int status) {
		// TODO Auto-generated method stub
		// TTS is successfully initialized
		if (status == TextToSpeech.SUCCESS) {
			// Setting speech language
			int result = tts.setLanguage(Locale.US);
			// If your device doesn't support language you set above
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Cook simple toast message with message
				Toast.makeText(this, "Language not supported",
						Toast.LENGTH_LONG).show();
				Log.e("TTS", "Language is not supported");
			}
			// Enable the button - It was disabled in main.xml (Go back and
			// Check it)
			else {

			}
			// TTS is not initialized properly
		} else {
			Toast.makeText(this, "TTS Initilization Failed", Toast.LENGTH_LONG)
					.show();
			Log.e("TTS", "Initilization Failed");
		}
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
		if (!recordingEnabled) {
			recordedData = new AccData();
			recordedGData = new AccData();
			cc = 1;
			burstCounter = 0;
			Toast.makeText(this, "Recording will start in 5 sec",
					Toast.LENGTH_SHORT).show();
			Runnable r = new dataRecording();
			new Thread(r).start();
		} else {
			toast("Recording in progress!");
		}
	}

	public void toggleMonitor(View view) {
		boolean toggleOn = monitorTab.getMonitorToggleStatus();
		if (!toggleOn) {
			monitorEnabled = false;
		} else {
			monitorEnabled = true;

		}
	}

	public void startConstantRecording(View view) {
		boolean toggleOn = recordingTab.getRecordingToggleStatus();
		if (!toggleOn) {
			toast("Stopping constant recording");
			constantRecordingEnabled = false;
		} else {
			cc = 1;
			burstCounter = 0;
			recordedData = new AccData();
			recordedGData = new AccData();
			constantRecordingEnabled = true;
			constantSavingEnabled = recordingTab
					.getConstantSavingCheckboxValue();
			constantIdentifying = recordingTab
					.getConstantIdentificationCheckboxValue();
			halfSizeMode = recordingTab.getHalfSizeCheckboxValue();
			toast("Starting constant recording.\nSaving: "
					+ constantSavingEnabled + ". Identifying: "
					+ constantIdentifying + ". Tagging: "
					+ recordingTab.getAutoTagCheckboxValue() + ". Half size: "
					+ recordingTab.getHalfSizeCheckboxValue() + ".");
		}
		recordingTab.toggleCheckboxes();

		//
		// if (!recordingEnabled) {
		// recordedData = new AccData();
		// recordedGData = new AccData();
		// Toast.makeText(this, "Recording will start in 5 sec",
		// Toast.LENGTH_SHORT).show();
		// Runnable r = new dataRecording();
		// new Thread(r).start();
		// } else {
		// toast("Recording in progress!");
		// }
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
			accDataLibrary.clear();
			toast("Local libary purged. activityLibrary size:"
					+ accDataLibrary.size());
			purgeCounter = 0;
			index = 0;

		} else {
			toast("Press " + (4 - purgeCounter)
					+ " more times to purge the library");
		}
	}

	public void clearClassificationResults(View view) {
		cpurgeCounter++;
		if (cpurgeCounter > 3) {
			gnbcLibrary.clear();
			toast("Classifcation results cleared. gnbcLibrary size:"
					+ gnbcLibrary.size());
			recognitionTab.updateStatusText("", false);
			recognitionTab.updateStatusText2("", false);
			cpurgeCounter = 0;
			gnbcIndex = 0;
		} else {
			toast("Press " + (4 - cpurgeCounter)
					+ " more times to clear results");
		}
	}

	public void send(View view) {
		// String apiURI =
		// "https://api.mongolab.com/api/1/databases/activity_recognition/collections/accelerometer_data?apiKey=Ix7evhXTw3uwk1gDHCvzz-uMNEhOy8ZN";
		String apiURI = "https://api.mongolab.com/api/1/databases/activity_recognition/collections/accelerometer_data_new?apiKey="
				+ apiKey;
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
			// JsonElement gyroReadings = gson.toJsonTree(tempData
			// .getGyroData());
			JsonElement jsonElement = gson.toJsonTree(tempData);
			// jsonElement.getAsJsonObject().add("gyro", gyroReadings);
			jsonElement.getAsJsonObject().addProperty("type",
					tempData.getType());

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

	public void sendClassificationResult(ClassificationResult result) {
		// String apiURI =
		// "https://api.mongolab.com/api/1/databases/activity_recognition/collections/accelerometer_data?apiKey=Ix7evhXTw3uwk1gDHCvzz-uMNEhOy8ZN";
		String apiURI = "https://api.mongolab.com/api/1/databases/activity_recognition/collections/classification_results?apiKey="
				+ apiKey;
		try {

			// make web service connection
			final HttpPost request = new HttpPost(apiURI);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");

			Gson gson = new Gson();
			Gson gson2 = new Gson();
			
			SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
			String justDate = dateformat.format(result.getDate());

			JsonElement jsonElement = gson.toJsonTree(new ClassificationResult(
					result));

			// jsonElement.getAsJsonObject().add("gyro", gyroReadings);
			jsonElement.getAsJsonObject().addProperty("userid", userid);
			jsonElement.getAsJsonObject().addProperty("day", justDate);

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
		if (index + 1 < accDataLibrary.size()) {
			tempData = accDataLibrary.get(index + 1);
			tempFeat = FeatureExtractors.buildFeatureObject(tempData);
			tempFeat.setType(tempData.getType());
			recordingTab.setTypeCombobox(tempData.getType());
			index++;
			recordingTab.updateActivityDetailText(tempData, tempFeat);
			recordingTab.setIndexTextView(index + 1, accDataLibrary.size());
			drawRecordingGraph();

			// Toast.makeText(this, "Activity #" + index + " selected",
			// Toast.LENGTH_SHORT).show();
		}

	}

	public void nextGNBC(View view) {
		if (gnbcIndex + 1 < gnbcLibrary.size()) {
			tempGNBC = gnbcLibrary.get(gnbcIndex + 1);
			recognitionTab.drawData(tempGNBC.getV());
			gnbcIndex++;
			toast("GNBC result #" + gnbcIndex + " selected");
			recognitionTab.updateStatusText("Selected GNBC result: "
					+ gnbcIndex + "\n" + resultArrayToString(tempGNBC.getV()),
					false);
		}

	}

	public List<Double> fromDoubleArrayToArrayList(double[] array) {
		List<Double> result = new ArrayList<Double>();
		for (double d : array) {
			result.add(d);
		}
		return result;
	}

	public void drawRecordingGraph() {
		switch (displayType) {
		case 0:
			recordingTab.drawData(tempData, -12, 15, 512);
			break;
		case 1:
			AccData lpfData = new AccData(
					FeatureExtractors.lowPassFilter(tempData.getxData()),
					FeatureExtractors.lowPassFilter(tempData.getyData()),
					FeatureExtractors.lowPassFilter(tempData.getzData()));
			recordingTab.drawData(lpfData, -12, 15, 512);
			break;
		case 2:
			AccData hpfData = new AccData(
					FeatureExtractors.highPassFilter(tempData.getxData()),
					FeatureExtractors.highPassFilter(tempData.getyData()),
					FeatureExtractors.highPassFilter(tempData.getzData()));
			recordingTab.drawData(hpfData, -12, 15, 512);
			break;
		case 3:
			AccData lpfData1 = new AccData(
					FeatureExtractors.lowPassFilter(tempData.getxData()),
					FeatureExtractors.lowPassFilter(tempData.getyData()),
					FeatureExtractors.lowPassFilter(tempData.getzData()));
			AccData bpfData = new AccData(
					FeatureExtractors.highPassFilter(lpfData1.getxData()),
					FeatureExtractors.highPassFilter(lpfData1.getyData()),
					FeatureExtractors.highPassFilter(lpfData1.getzData()));
			recordingTab.drawData(bpfData, -12, 15, 512);
			break;
		case 4:
			AccData fData = new AccData(
					fromDoubleArrayToArrayList(FeatureExtractors.fftest(tempData
							.getxData())),
					fromDoubleArrayToArrayList(FeatureExtractors
							.fftest(tempData.getyData())),
					fromDoubleArrayToArrayList(FeatureExtractors
							.fftest(tempData.getzData())));
			recordingTab.drawData(fData, -1, 100, 512);
			break;
		default:
			break;
		}
	}

	public void previousAccActivity(View view) {
		if (index - 1 >= 0) {
			tempData = accDataLibrary.get(index - 1);
			tempFeat = FeatureExtractors.buildFeatureObject(tempData);
			tempFeat.setType(tempData.getType());
			index--;
			recordingTab.setIndexTextView(index + 1, accDataLibrary.size());
			recordingTab.updateActivityDetailText(tempData, tempFeat);
			recordingTab.setTypeCombobox(tempData.getType());
			drawRecordingGraph();
			// Toast.makeText(this, "Activity #" + index + " selected",
			// Toast.LENGTH_SHORT).show();

		}

	}

	public void previousGNBC(View view) {
		if (gnbcIndex - 1 >= 0 && gnbcLibrary.size() > 0) {
			tempGNBC = gnbcLibrary.get(gnbcIndex - 1);
			recognitionTab.drawData(tempGNBC.getV());
			gnbcIndex--;
			toast("GNBC result #" + gnbcIndex + " selected");
			recognitionTab.updateStatusText("Selected GNBC result: "
					+ gnbcIndex + "\n" + resultArrayToString(tempGNBC.getV()),
					false);
		}

	}

	public void remove(View view) {
		accDataLibrary.remove(index);
		index = index - 1;
		tempData = accDataLibrary.get(index);
		recordingTab.updateActivityDetailText(tempData, tempFeat);
		drawRecordingGraph();
		String ser = SerializeObject.objectToString(accDataLibrary);
		if (ser != null && !ser.equalsIgnoreCase("")) {
			SerializeObject.WriteSettings(this, ser, "activityLibrary.dat");
		} else {
			SerializeObject.WriteSettings(this, "", "activityLibrary.dat");
		}
	}

	public String resultArrayToString(List<Double> array) {
		String txt = "";
		for (int i = 0; i < array.size(); i++) {
			double d = array.get(i);
			if (!Double.isNaN(d)) {
				DecimalFormat df = new DecimalFormat("000E00");

				if (d != 0.0) {
					txt += ("\n[" + i + "] "
							+ df.format(Math.exp(array.get(i))) + " log:" + String
							.format("%.5f", array.get(i)));

				}
			}

		}
		return txt;
	}

	public void idButtonClick(View view) {
		if (entropyDataLoaded) {
			classify(tempData);
		} else {
			toast("entropy data not loaded - loading data");
			loadEntropyFromCloud();
		}

	}

	ArrayList<ClassificationResult> classificationBin = new ArrayList<ClassificationResult>();
	private boolean heuristicsOn=true;
	private ClassificationResult previousGNBC;

	private void classify(AccData activity) {
		// finishRecording();
		if (entropyDataLoaded) {
			// toast("identifying...");
			Pair<ArrayList<Double>, String> classification = ng
					.classify(FeatureExtractors.buildFeatureObject(activity));
			recognitionTab.updateStatusText(classification.second, false);
			ArrayList<Double> results = classification.first;
			Date date = new Date();
			tempGNBC = new ClassificationResult(results, date);
			double pValue =tempGNBC.getMaxProbabilityValue();
			SimpleDateFormat sdf = new SimpleDateFormat();

			if (!heuristicsOn || pValue> (-220) || (tempGNBC.getResult()!=3&&tempGNBC.getResult()!=2&&pValue>(-270))) { //HEURISTIC TIME!

				
				gnbcIndex = gnbcLibrary.size();
				gnbcLibrary.add(tempGNBC);
				// toast("GNBC result saved. Library size:" +
				// gnbcLibrary.size());
				gnbcIndex = gnbcLibrary.size() - 1;

				
				String currentDateandTime = sdf.format(new Date());
				recognitionTab.updateStatusText2(
						currentDateandTime
								+ " ["
								+ gnbcIndex
								+ "] "
								+ FeatureExtractors.getType(tempGNBC
										.GetMaxIndex()), true);
				recognitionTab.drawData(classification.first);
				previousGNBC=tempGNBC;
			}else{
				if(previousGNBC!=null) tempGNBC=previousGNBC;
			}
			SimpleDateFormat sdfms = new SimpleDateFormat("dd-MM hh:mm:ss.SSS");

			if (recognitionTab.getSendToServerCheckboxValue()) {
				String currentDateandTimeMs = sdfms.format(new Date());
				sendClassificationResult(tempGNBC);
				recognitionTab.updateStatusText2(
						currentDateandTimeMs
								+ " [!] "
								+ FeatureExtractors
										.getTypeNoNumber(tempGNBC.getResult()), true);
			}

			tts.speak(FeatureExtractors.getTypeNoNumber(tempGNBC
					.GetMaxIndex()), TextToSpeech.QUEUE_FLUSH, null);

		}

	}

	private void classify2(AccData activity) {
		// finishRecording();
		if (entropyDataLoaded) {
			// toast("identifying...");
			Pair<ArrayList<Double>, String> classification = ng
					.classify(FeatureExtractors.buildFeatureObject(activity));
			recognitionTab.updateStatusText(classification.second, false);
			ArrayList<Double> results = classification.first;
			Date date = new Date();
			tempGNBC = new ClassificationResult(results, date);

			gnbcIndex = gnbcLibrary.size();
			gnbcLibrary.add(tempGNBC);
			// toast("GNBC result saved. Library size:" + gnbcLibrary.size());
			gnbcIndex = gnbcLibrary.size() - 1;

			// SimpleDateFormat sdf = new SimpleDateFormat();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM hh:mm:ss");
			SimpleDateFormat sdfms = new SimpleDateFormat("dd-MM hh:mm:ss.SSS");

			String currentDateandTime = sdf.format(new Date());
			recognitionTab
					.updateStatusText2(
							currentDateandTime
									+ " ["
									+ gnbcIndex
									+ "] "
									+ FeatureExtractors.getType(tempGNBC
											.GetMaxIndex()), true);
			recognitionTab.drawData(classification.first);

			if (classificationBin.size() < 3) {
				classificationBin.add(tempGNBC);
			}

			if (classificationBin.size() == 3) {
				// need to decide which one is best
				double one = classificationBin.get(0)
						.getDifferenceBetweenTopAndRunupP();
				double two = classificationBin.get(1)
						.getDifferenceBetweenTopAndRunupP();
				double three = classificationBin.get(2)
						.getDifferenceBetweenTopAndRunupP();
				int selected = 99;

				if (one >= two && one >= three) {
					selected = 0;
				} else if (two >= one && two >= three) {
					selected = 1;
				} else if (three >= one & three >= two) {
					selected = 2;
				} else if (one == two && two == three) {
					selected = 1;
				}

				if (recognitionTab.getSendToServerCheckboxValue()) {
					sendClassificationResult(classificationBin.get(selected));
				}
				String currentDateandTimeMs = sdfms.format(new Date());
				recognitionTab.updateStatusText2(
						currentDateandTimeMs
								+ " [!] "
								+ FeatureExtractors
										.getTypeNoNumber(classificationBin
												.get(selected).result), true);

				tts.speak(FeatureExtractors.getTypeNoNumber(classificationBin
						.get(selected).GetMaxIndex()),
						TextToSpeech.QUEUE_FLUSH, null);

				classificationBin.remove(0);
				classificationBin.remove(0);
			}

		}

	}

	public void addBurstActivity(AccData tempBurstActivity) {
		if (!accDataLibrary.contains(tempBurstActivity)) {

			if (recordingTab.getAutoTagCheckboxValue()) {
				tempBurstActivity.setType(recordingTab.getTypeSpinnerValue());
			} else {
				tempBurstActivity.setType(9);
			}

			accDataLibrary.add(tempBurstActivity);
			Toast.makeText(this,
					"Activity saved. Library size:" + accDataLibrary.size(),
					Toast.LENGTH_SHORT).show();
			// index = accDataLibrary.size()-1;
			recordingTab.setIndexTextView(index + 1, accDataLibrary.size());

		} else {
			Toast.makeText(this, "Activity already in the library.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void saveLibrary(View view) {
		new AsyncTask<Context, Void, Void>() {

			@Override
			protected Void doInBackground(Context... params) {
				String ser = SerializeObject.objectToString(accDataLibrary);
				if (ser != null && !ser.equalsIgnoreCase("")) {
					SerializeObject.WriteSettings(params[0], ser,
							"activityLibrary.dat");
				} else {
					SerializeObject.WriteSettings(params[0], "",
							"activityLibrary.dat");
				}
				return null;

				//
				// FileOutputStream fos;
				// try
				// {
				// fos = params[0].openFileOutput("activityLibrary.dat",
				// Context.MODE_PRIVATE);
				// for (AccData a : activityLibrary)
				// {
				// fos.write((a.toString() + ",").getBytes());
				// }
				// fos.close();
				// } catch (FileNotFoundException e){}
				// catch (IOException e){}
				//
				// return null;
			}
		}.execute(getApplicationContext());

	}

	public void addActivity(View view) {
		if (!accDataLibrary.contains(tempData)) {
			tempData.setType(recordingTab.getTypeSpinnerValue());
			accDataLibrary.add(tempData);
			Toast.makeText(this,
					"Activity saved. Library size:" + accDataLibrary.size(),
					Toast.LENGTH_SHORT).show();
			index = accDataLibrary.size() - 1;

		} else {
			Toast.makeText(this, "Activity already in the library.",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void addGNBC(View view) {
		if (!gnbcLibrary.contains(tempGNBC)) {
			gnbcLibrary.add(tempGNBC);
			toast("GNBC result saved. Library size:" + gnbcLibrary.size());
			gnbcIndex = gnbcLibrary.size() - 1;
			// String ser = SerializeObject.objectToString(activityLibrary);
			// if (ser != null && !ser.equalsIgnoreCase("")) {
			// SerializeObject.WriteSettings(this, ser, "activityLibrary.dat");
			// } else {
			// SerializeObject.WriteSettings(this, "", "activityLibrary.dat");
			// }
			gnbcIndex = gnbcLibrary.size() - 1;
		} else {
			toast("GNBC result already in the library.");
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// toast("checkedchanged" + isChecked);
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
		 userid=(Integer.parseInt(arg0.toString()));
		 toast("Userid changed to:" + userid);
		 }

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (spinnerFirstInvoke == true) {
			spinnerFirstInvoke = false;
		} else {
			if (tempData != null) {
				tempData.setType(recordingTab.getTypeSpinnerValue());
				tempFeat.setType(recordingTab.getTypeSpinnerValue());
				recordingTab.updateActivityDetailText(tempData, tempFeat);
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
						entropyMean.set((Integer.valueOf(key.substring(key
								.length() - 1))), tempArray);
					} else if (key.startsWith("var")) {
						ArrayList<Double> tempArray = new ArrayList<Double>();

						JSONArray dataArray = obj.getJSONArray(key);
						for (int j = 0; j < dataArray.length(); j++) {
							JSONObject arrayEntry = (JSONObject) dataArray
									.get(j);
							tempArray.add(arrayEntry.getDouble(Integer
									.toString(j)));
						}
						entropyVar.set((Integer.valueOf(key.substring(key
								.length() - 1))), tempArray);
					}

				}

			}
			ng = new GaussianNaiveBayesClassifier(entropyMean, entropyVar);
			recognitionTab.updateStatusText(
					"Entropy data loaded succefully.\nFirst 128 characters:"
							+ result.substring(0,
									Math.min(result.length(), 128)) + "...",
					false);
			toast("Entropy data loaded succesfully.");
			entropyDataLoaded = true;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getEntropyData(View view) {
		loadEntropyFromCloud();
	}

	public AccData getTempData() {
		return tempData;
	}

	public AccFeat getTempFeat() {
		return tempFeat;
	}

	@Override
	public void onLocationChanged(Location location) {
		SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss] ");
		String currentDateandTime = sdf.format(new Date());
		recognitionTab.updateStatusText2(
				currentDateandTime + location.getAltitude(), true);

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
