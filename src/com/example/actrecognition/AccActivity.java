package com.example.actrecognition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

public class AccActivity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5739053446639539286L;
	AccData Data;
	AccData fData;
	AccData lpfData;
	
	int[] crossings;
	float[] sd;
	int type = -1;
	private float[] minMax;
	float spread = 0.30f;
	int rate = 8;
	float alpha = 0.30f;
	float avResAcc;
	float[] peakDistances;
	int[] peakNumber;
	ArrayList<Integer> peakIndicesX;
	ArrayList<Integer> peakIndicesY;
	ArrayList<Integer> peakIndicesZ;

	
	public ArrayList<Integer> getPeakIndicesX(){
		return peakIndicesX;
	}
	
	public ArrayList<Integer> getPeakIndicesY(){
		return peakIndicesY;
	}
	
	public ArrayList<Integer> getPeakIndicesZ(){
		return peakIndicesZ;
	}
	
	private void calculatePeakIndices(){
		peakIndicesX = FeatureExtractors.peakIndices(lpfData.getxData());
		peakIndicesY = FeatureExtractors.peakIndices(lpfData.getyData());
		peakIndicesZ = FeatureExtractors.peakIndices(lpfData.getzData());
	}

	
	
	public float getAvResAcceleration() {
		return avResAcc;
	}
	
	private void calculateAvPeakDistances() {
		float[] newPeakDistances = {
				FeatureExtractors.averageDifference(peakIndicesX),
				FeatureExtractors.averageDifference(peakIndicesY),
				FeatureExtractors.averageDifference(peakIndicesZ) };
		peakDistances=newPeakDistances;
	}


	private void calculateAvResAcceleration() {
		avResAcc = FeatureExtractors.averageResultantAcceleration(
				Data.getxData(), Data.getyData(), Data.getzData());
	}


	public AccActivity(AccData recordedData) {
		Data = recordedData;
		fData = new AccData(FeatureExtractors.iterativeFFT(
				Data.getDenoisedxData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedyData(), 1), FeatureExtractors.iterativeFFT(
				Data.getDenoisedzData(), 1));
		lpfData = new AccData(FeatureExtractors.lowPassFilter(Data.getxData(), alpha),
				FeatureExtractors.lowPassFilter(Data.getyData(), alpha),
				FeatureExtractors.lowPassFilter(Data.getzData(), alpha));
		calculateMaximumDisplacements();
		calculateZeroCrossingCounts();
		calculateStandardDeviation();
		calculateAvResAcceleration();
		calculatePeakIndices();
		calculateAvPeakDistances();
	}

	public float[] getSD() {
		return sd;
	}

	private void calculateStandardDeviation() {
		float[] sd = { FeatureExtractors.standardDeviation(Data.getxData()),
				FeatureExtractors.standardDeviation(Data.getyData()),
				FeatureExtractors.standardDeviation(Data.getzData()) };
		this.sd = sd;
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
				FeatureExtractors.zeroCrossingCount(lpfData.getxData(),
						Data.getXMiddleValue(), spread, rate),
				FeatureExtractors.zeroCrossingCount(lpfData.getyData(),
						Data.getYMiddleValue(), spread, rate),
				FeatureExtractors.zeroCrossingCount(lpfData.getzData(),
						Data.getZMiddleValue(), spread, rate) };
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

	
//	  <string-array name="actTypeArray">
//      <item>Walking (0)</item>
//      <item>Running (1)</item>
//      <item>Walking up the stairs (2)</item>
//      <item>Walking down the stairs (3)</item>
//      <item>Sitting (4)</item>
//      <item>Standing up (5)</item>
//      <item>Jumping (6)</item>
//      <item>Test: Wave Sideways (7)</item>
//      <item>Test: Wave Forward (8)</item>
	public String getType() {
		switch(type){
		case 0:
			return "Walking (0)";
		case 1:
			return "Running (1)";
		case 2:
			return "Walking up the stairs (2)";
		case 3:
			return "Walking down the stairs (3)";
		case 4:
			return "Sitting (4)";
		case 5:
			return "Standing up (5)";
		case 6:
			return "Jumping (6)";
		case 7:
			return "Test: Wave Sideways (7)";
		case 8:
			return "Test: Wave Forward (8)";
		default:
			return "Unspecified";
		
		}
		
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

	public float[] getAvPeakDistance() {
		return peakDistances;
	}

	public int[] getPeakNumber() {
		return peakNumber;
	}

	public AccData getlpfData() {
		return lpfData;
	}

}
