package com.severyn.actrecognition;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.util.Pair;

public class GaussianNaiveBayesClassifier {
	ArrayList<ArrayList<Double>> entropyMean;
	ArrayList<ArrayList<Double>> entropyVar;
	int[] attr = { 3, 4, 6, 12, 14, 20, 21, 22, 23, 24, 25, 32, 40, 46, 50, 51,
	94 };
	
	public GaussianNaiveBayesClassifier(ArrayList<ArrayList<Double>> entropyMean,
			ArrayList<ArrayList<Double>> entropyVar) {
		
		for (int j = 0; j < attr.length; j++) {
			attr[j] = attr[j] - 2;
		}
		
		this.entropyMean = entropyMean;
		this.entropyVar = entropyVar;

	}

	public Pair<ArrayList<Double>, String> classify(AccFeat q) {
		String txt = "";
		txt += ("Gaussian Naive Bayes Classification");
		double[] results = new double[6];
		double result;
		ArrayList<Double> qf = new ArrayList<Double>();

		for (int k = 0; k < attr.length; k++) {
			qf.add(q.getFeature(attr[k]));
		}
		
		
		for (int i = 0; i < 6; i++) {
				result = 0;
				for (int j = 0; j < entropyMean.get(i).size(); j++) {
				
					result += Math.log(p(qf.get(j), entropyMean.get(i).get(j),
							entropyVar.get(i).get(j)));

				}
				results[i] = result;
			
		}

		int maxindex = 0;
		double maxvalue = results[0];

		for (int i = 0; i < 6; i++) {
			if ( !Double.isNaN(results[i])) {
				maxvalue = results[i];
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < 6; i++) {
			if (!Double.isNaN(results[i])) {
	            DecimalFormat df = new DecimalFormat("0.00E000");
//	            DecimalFormat df = new DecimalFormat("#.######");

//				if (results[i] != 0.0) {
//	        	txt += ("\n[" + i + "] " + Math.exp(results[i]) + " log:" + String.format("%.5f", results[i]));
//					txt += ("\n[" + i + "] " + String.format("%.5f", Math.exp(results[i]))+ " log:" + String.format("%.5f", results[i]));
					txt += ("\n[" + i + "] " + df.format(Math.exp(results[i]))+ " log:" + String.format("%.5f", results[i]));
//				}
				if (results[i] > results[maxindex]) {
					maxvalue = results[i];
					maxindex = i;
				}
			}

		}

		for (int i = 0; i < results.length; i++) {
			if (i != maxindex) {
				String value = String.format("%.2f", results[i] / results[maxindex]);
				if(value.equals("Infinity")) value = "inf.";
				txt += ("\n   Type #" + i + " : "
						+ value + " times less likely.");
			}
		}
		txt += ("\n");

		txt += ("\n- This is an activity of type #" + maxindex);

		ArrayList<Double> resultsArrayList = new ArrayList<Double>();
		for (Double d : results) {
			resultsArrayList.add(d);
		}

		Pair<ArrayList<Double>, String> pair = 
				new Pair<ArrayList<Double>, String>(resultsArrayList, txt);
		return pair;

	}

	private double p(double v, double m, double var) {
		if (var == 0)
			return 1;
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

//		if(p==0) return 1;
		return p;
	}

	public ArrayList<ArrayList<Double>> getEntropyMean() {
		return entropyMean;
	}

	public ArrayList<ArrayList<Double>> getEntropyVar() {
		return entropyVar;
	}
}
