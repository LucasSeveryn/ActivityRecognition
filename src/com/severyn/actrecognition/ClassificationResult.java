package com.severyn.actrecognition;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassificationResult {
	List<Double> p;
	Date date;
	Integer result;
	int[] types = {0,1,2,3,4,5,7};
	
	
	public ClassificationResult() {
		p = new ArrayList<Double>();
	}
	
	public ClassificationResult(List<Double> v, Date d){
		this.p=v;
		this.date=d;
		this.result=types[GetMaxIndex()];
	}
	
	public ClassificationResult(ClassificationResult c){
		this.p=c.getVforJSON();
		this.date=c.getDate();
		this.result=types[c.GetMaxIndex()];
	}
	
	public int getResultType(){
		return types[GetMaxIndex()];
	}
	
	public int GetMaxIndex(){
		int maxindex = 0;
		double maxvalue = p.get(0);

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(p.get(i))) {
				maxvalue = p.get(i);
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(p.get(i))) {
				if (p.get(i) > p.get(maxindex)) {
					maxvalue = p.get(i);
					maxindex = i;
				}
			}

		}
		return maxindex;
	}

	public int GetSecondMaxIndex(){
		int maxindex = 0;
		double maxvalue = p.get(0);

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(p.get(i))) {
				maxvalue = p.get(i);
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(p.get(i))) {
				if (p.get(i) > p.get(maxindex)) {
					maxvalue = p.get(i);
					maxindex = i;
				}
			}

		}
		ArrayList<Double> v = new ArrayList<Double>(p);
		v.set(maxindex, Double.NaN);
		maxindex = 0;
		maxvalue = v.get(0);

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(v.get(i))) {
				maxvalue = v.get(i);
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < p.size(); i++) {
			if (!Double.isNaN(v.get(i))) {
				if (v.get(i) > v.get(maxindex)) {
					maxvalue = v.get(i);
					maxindex = i;
				}
			}

		}
		
		return maxindex;
	}
	
	public double getMaxProbabilityValue(){
		double r =  p.get(this.GetMaxIndex());
		return r;
	}
	
	public double getSecondMaxProbabilityValue(){
		double r=p.get(this.GetSecondMaxIndex());
		return r;
	}
	
	public double getDifferenceBetweenTopAndRunupP(){
		double max = getMaxProbabilityValue();
		double snd = getSecondMaxProbabilityValue();
		if(Double.isInfinite(snd)||Double.isInfinite(max)) return Double.POSITIVE_INFINITY;
		else return snd/max;
//		if (snd)
//		return Math.abs()-);
	}
	
	public List<Double> getV() {
		return p;
	}

	public List<Double> getVforJSON() {
		List<Double> returnList = new ArrayList<Double>();
		for(double d: p){
			if(Double.isInfinite(d)) returnList.add(0.0);
			else returnList.add(d);
		}
		return returnList;
	}
	
	public void setV(List<Double> v) {
		this.p = v;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getResult() {
		return result;
	}


}

