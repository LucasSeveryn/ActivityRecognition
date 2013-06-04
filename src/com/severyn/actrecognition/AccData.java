package com.severyn.actrecognition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4027578149606001414L;
	private List<Double> xData;
	private List<Double> yData;
	private List<Double> zData;
	private int type=9;
//	private float[] averageNoise;

	public AccData() {
		xData = new ArrayList<Double>();
		yData = new ArrayList<Double>();
		zData = new ArrayList<Double>();
	}
	
	public double getXMiddleValue(){
		return (Collections.max(xData) + Collections.min(xData))/2;
	}
	
	public double getYMiddleValue(){
		return (Collections.max(yData) + Collections.min(yData))/2;
	}
	
	public double getZMiddleValue(){
		return (Collections.max(zData) + Collections.min(zData))/2;
	}
	
	public int size(){
		return xData.size();
	}
	
//	public float[] getNoise() {
//	//	return averageNoise;
//
//	}

	public AccData(List<Double> xData, List<Double> yData,
			List<Double> zData) {
		this.xData = xData;
		this.yData = yData;
		this.zData = zData;
	}


	
	public List<Double> getxData() {
		return xData;
	}

	public void setxData(List<Double> xData) {
		this.xData = xData;
	}

	public List<Double> getyData() {
		return yData;
	}

	public void setyData(List<Double> yData) {
		this.yData = yData;
	}

	public List<Double> getzData() {
		return zData;
	}

	public void setzData(List<Double> zData) {
		this.zData = zData;
	}

	public void clear() {
		xData.clear();
		yData.clear();
		zData.clear();
	}

	public void addX(double x) {
		xData.add(x);
	}

	public void addZ(double z) {
		zData.add(z);
	}

	public void addY(double y) {
		yData.add(y);
	}
	
	public AccData removeHalfOfElements(){
		List<Double> xDatar = this.xData.subList(256, xData.size());
		List<Double> yDatar = this.yData.subList(256, xData.size());
		List<Double> zDatar = this.zData.subList(256, xData.size());
		AccData result = new AccData(xDatar, yDatar, zDatar);
		return result;
	}
	
	public AccData removeQuarterOfElements(){
		List<Double> xDatar = this.xData.subList(128, xData.size());
		List<Double> yDatar = this.yData.subList(128, xData.size());
		List<Double> zDatar = this.zData.subList(128, xData.size());
		AccData result = new AccData(xDatar, yDatar, zDatar);
		return result;
	}

	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

//	public void setNoise(float[] averageNoise) {
//		this.averageNoise = averageNoise;
//	}
}
