package com.example.actrecognition;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

	public void setTabFragment(int index, String tag){
		switch(index){
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
		
//	    AccMonitorFragment accMonitorFragment;
//	    if (savedInstanceState != null) {
//	    	accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager().findFragmentByTag("accMonitorFragment");
//	    } else {
//	    	accMonitorFragment = new AccMonitorFragment();
//	        getSupportFragmentManager().beginTransaction().add(R.id.pager, accMonitorFragment, "accMonitorFragment").commit(); 
//	    }
//	    
//	    ActRecognitionFragment actRecognitionFragment;
//	    if (savedInstanceState != null) {
//	    	actRecognitionFragment = (ActRecognitionFragment) getSupportFragmentManager().findFragmentByTag("actRecognitionFragment");
//	    } else {
//	    	actRecognitionFragment = new ActRecognitionFragment();
//	        getSupportFragmentManager().beginTransaction().add(R.id.pager, actRecognitionFragment, "actRecognitionFragment").commit(); 
//	    }
//	    
//	    ActRecordingFragment actRecordingFragment;
//	    if (savedInstanceState != null) {
//	    	actRecordingFragment = (ActRecordingFragment) getSupportFragmentManager().findFragmentByTag("actRecordingFragment");
//	    } else {
//	    	actRecordingFragment = new ActRecordingFragment();
//	        getSupportFragmentManager().beginTransaction().add(R.id.pager, actRecordingFragment, "actRecordingFragment").commit(); 
//	    }
	    
	    
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
//			Bundle args = new Bundle();
//			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
//			fragment.setArguments(args);
//			return fragment;
			
	
			switch(position){
			case 0:
				AccMonitorFragment accMonitorFragment = new AccMonitorFragment();
//			    getSupportFragmentManager().beginTransaction().add(R.id.pager, accMonitorFragment, "accMonitorFragment").commit(); 				
				return accMonitorFragment;
			case 1:
				ActRecordingFragment actRecordingFragment = new ActRecordingFragment();
//			    getSupportFragmentManager().beginTransaction().add(R.id.pager, actRecordingFragment, "actRecordingFragment").commit(); 				
				return actRecordingFragment;
			case 2:
				ActRecognitionFragment actRecognitionFragment= new ActRecognitionFragment();
//			    getSupportFragmentManager().beginTransaction().add(R.id.pager, actRecognitionFragment, "actRecognitionFragment").commit(); 				
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
			AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager().findFragmentByTag(tagFragment1);
			accMonitorFragment.enableRecording();

			Toast.makeText(this,
					"Recording will start in 5 sec",
					Toast.LENGTH_LONG).show();
		 }

	 public void startActivityRecording(View view) {
			AccMonitorFragment accMonitorFragment = (AccMonitorFragment) getSupportFragmentManager().findFragmentByTag(tagFragment1);
			accMonitorFragment.enableActivityRecording();

			Toast.makeText(this,
					"Recording Activity will start in 5 sec",
					Toast.LENGTH_LONG).show();
		 }
}
