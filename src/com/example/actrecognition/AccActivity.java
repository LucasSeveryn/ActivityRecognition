package com.example.actrecognition;

import java.util.Collections;

public class AccActivity {
	AccData Data;
	AccData fData;
	int[] crossings;
	float[] sd;
	int type = 0;
	private float[] minMax;
	float spread = 0.30f;
	int rate = 8;

	public AccActivity(AccData recordedData, int type) {
		Data = recordedData;
		this.type=type;
		fData = new AccData(FeatureExtractors.iterativeFFT(
				Data.getDenoisedxData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedyData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedzData(), 1));
		calculateMaximumDisplacements();
		calculateZeroCrossingCounts();
		calculateStandardDeviation();

	}
	
	public float[] getSD(){
		return sd;
	}

	private void calculateStandardDeviation() {
		float[] sd = {
				FeatureExtractors.getStandardDeviation(Data.getxData()),
				FeatureExtractors
						.getStandardDeviation(Data.getyData()),
								FeatureExtractors.getStandardDeviation(Data
										.getzData())};
		this.sd=sd;
	}

	private void calculateMaximumDisplacements() {
		float[] minMax = { Collections.max(Data.getxData()),
				Collections.min(Data.getxData()),
				Collections.max(Data.getyData()),
				Collections.min(Data.getyData()),
				Collections.max(Data.getzData()),
				Collections.min(Data.getzData()) };
		this.minMax = minMax;
	}

	public void recalculate() {
		fData = new AccData(FeatureExtractors.iterativeFFT(
				Data.getDenoisedxData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedyData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedzData(), 1));
		calculateMaximumDisplacements();
		calculateZeroCrossingCounts();
	}

	private void calculateZeroCrossingCounts() {
		int[] crossings = {
				FeatureExtractors.getZeroCrossingCount(Data.getxData(),
						Data.getNoise()[0], spread, rate),
				FeatureExtractors.getZeroCrossingCount(Data.getzData(),
						Data.getNoise()[1], spread, rate),
				FeatureExtractors.getZeroCrossingCount(Data.getxData(),
						Data.getNoise()[2], spread, rate) };
		this.crossings = crossings;
	}

	public int[] getCrossings() {
		return crossings;
	}

	public int getxCrossings() {
		return crossings[0];
	}

	public void setxCrossings(int xCrossings) {
		crossings[0] = xCrossings;
	}

	public int getzCrossings() {
		return crossings[2];
	}

	public void setzCrossings(int zCrossings) {
		crossings[2] = zCrossings;
	}

	public int getyCrossings() {
		return crossings[1];
	}

	public void setyCrossings(int yCrossings) {
		crossings[1] = yCrossings;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setMinMax(float[] maximumDisplacements) {
		this.minMax = maximumDisplacements;
	}

	public float[] getMinMax() {
		return minMax;
	}

	public AccData getData() {
		return Data;
	}

	public AccData getfData() {
		return fData;
	}

	public float getSpread() {
		return spread;
	}

	public void setSpread(float spread) {
		this.spread = spread;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

}
