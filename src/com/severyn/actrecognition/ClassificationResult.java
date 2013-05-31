package com.severyn.actrecognition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassificationResult {
	List<Double> p;
	Date date;
	Integer result;
	
	
	public ClassificationResult() {
		p = new ArrayList<Double>();
	}
	
	public ClassificationResult(List<Double> v, Date d){
		this.p=v;
		this.date=d;
		this.result=GetMaxIndex();
	}
	
	public ClassificationResult(ClassificationResult c){
		this.p=c.getVforJSON();
		this.date=c.getDate();
		this.result=c.GetMaxIndex();
	}
	
	
	
	public int GetMaxIndex(){
		int maxindex = 0;
		double maxvalue = p.get(0);

		for (int i = 0; i < 6; i++) {
			if (!Double.isNaN(p.get(i))) {
				maxvalue = p.get(i);
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < 6; i++) {
			if (!Double.isNaN(p.get(i))) {
				if (p.get(i) > p.get(maxindex)) {
					maxvalue = p.get(i);
					maxindex = i;
				}
			}

		}
		return maxindex;
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
}
