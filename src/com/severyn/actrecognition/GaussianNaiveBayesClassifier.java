package com.severyn.actrecognition;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.util.Pair;

public class GaussianNaiveBayesClassifier {
	ArrayList<ArrayList<Double>> entropyMean;
	ArrayList<ArrayList<Double>> entropyVar;
	int[] attr = {
			40,
			 6,
			20,
			23,
			16,
			21,
			12,
			 7,
			51,
			50,
			 4,
			17,
			41,
			53,
			39,
			46,
			66,
			 5,
			19,
			49,
			15,
			22,
			25,
			56,
			14,
			37,
			44,
			76,
			43,
			38,
			 3,
			13,
			47,
			68,
			54,
			67,
			52,
			45,
			24,
			48,
			77,
			57,
			78,
			31,
			11,
			58,
			61,
			80,
			81,
			79,
			36,
			60,
			72,
			82,
			42,
			69,
			90,
			73,
			62,
			83,
			71,
			70,
			59,
			94,
			74,
			84
	};
	double[] weights = {
			1.91464,
			1.86173,
			1.85109,
			1.78223,
			1.715  ,
			1.71424,
			1.70875,
			1.69531,
			1.64979,
			1.58758,
			1.54313,
			1.51859,
			1.50909,
			1.5041 ,
			1.47753,
			1.4756 ,
			1.46557,
			1.44619,
			1.44179,
			1.42563,
			1.39821,
			1.39804,
			1.38488,
			1.38036,
			1.36769,
			1.35894,
			1.35703,
			1.35376,
			1.32802,
			1.31612,
			1.31367,
			1.311  ,
			1.29376,
			1.28537,
			1.28117,
			1.27821,
			1.26106,
			1.25526,
			1.24823,
			1.24485,
			1.21422,
			1.17971,
			1.17504,
			1.17398,
			1.17275,
			1.1581 ,
			1.1434 ,
			1.14247,
			1.12734,
			1.11912,
			1.11113,
			1.10561,
			1.10215,
			1.10036,
			1.09901,
			1.08603,
			1.07385,
			1.07358,
			1.06918,
			1.06747,
			1.04589,
			1.03798,
			1.03701,
			1.01956,
			1.01902,
			1.00377
	};

	
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
		
		double weight;
		for (int i = 0; i < 6; i++) {
				result = 0;
				for (int j = 0; j < entropyMean.get(i).size(); j++) {
					if(weights.length>0) weight=weights[j]; else weight=1;
					result += Math.log(weight*p(qf.get(j), entropyMean.get(i).get(j),
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
