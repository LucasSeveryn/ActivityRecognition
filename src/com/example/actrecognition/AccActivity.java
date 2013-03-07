package com.example.actrecognition;

import java.util.ArrayList;

public class AccActivity {
	ArrayList<Float> xData = new ArrayList<Float>();
	ArrayList<Float> yData = new ArrayList<Float>();
	ArrayList<Float> zData = new ArrayList<Float>();
	ArrayList<Float> xfData = new ArrayList<Float>();
	ArrayList<Float> yfData = new ArrayList<Float>();
	ArrayList<Float> zfData = new ArrayList<Float>();
	float xError;
	float yError;
	float zError;
	int xCrossings;
	int zCrossings;
	int yCrossings;
	double xSD;
	double ySD;
	double zSD;
	String type;
	private float[] minMax;
	
	public ArrayList<Float> getxData() {
		return xData;
	}
	
	public int[] getCrossings(){
		int[] crossings = {xCrossings,yCrossings,zCrossings};
		return crossings;
	}
	public void setxData(ArrayList<Float> xData) {
		this.xData = xData;
	}
	
	public void setfData(ArrayList<Float> xfData,ArrayList<Float> zfData,ArrayList<Float> yfData){
		this.xfData=xfData;
		this.zfData=zfData;
		this.yfData=yfData;
	}
	public ArrayList<Float> getyData() {
		return yData;
	}
	public void setyData(ArrayList<Float> yData) {
		this.yData = yData;
	}
	public ArrayList<Float> getzData() {
		return zData;
	}
	public void setzData(ArrayList<Float> zData) {
		this.zData = zData;
	}
	public float getxError() {
		return xError;
	}
	public void setxError(float xError) {
		this.xError = xError;
	}
	public float getyError() {
		return yError;
	}
	public void setyError(float yError) {
		this.yError = yError;
	}
	public float getzError() {
		return zError;
	}
	public void setzError(float zError) {
		this.zError = zError;
	}
	public int getxCrossings() {
		return xCrossings;
	}
	public void setxCrossings(int xCrossings) {
		this.xCrossings = xCrossings;
	}
	public int getzCrossings() {
		return zCrossings;
	}
	public void setzCrossings(int zCrossings) {
		this.zCrossings = zCrossings;
	}
	public int getyCrossings() {
		return yCrossings;
	}
	public void setyCrossings(int yCrossings) {
		this.yCrossings = yCrossings;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public void setMinMax(float[] maximumDisplacements) {
		this.minMax= maximumDisplacements;
	}
	
	public float[] getMinMax(){
		return minMax;
	}

	public ArrayList<Float> getXfData() {
		return xfData;
	}

	public ArrayList<Float> getYfData() {
		return yfData;
	}

	public ArrayList<Float> getZfData() {
		return zfData;
	}

	public double getxSD() {
		return xSD;
	}

	public void setxSD(double xSD) {
		this.xSD = xSD;
	}

	public double getySD() {
		return ySD;
	}

	public void setySD(double ySD) {
		this.ySD = ySD;
	}

	public double getzSD() {
		return zSD;
	}

	public void setzSD(double zSD) {
		this.zSD = zSD;
	}
	
}
