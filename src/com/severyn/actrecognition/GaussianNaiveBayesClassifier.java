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
			21,
			23,
			 7,
			12,
			51,
			16,
			41,
			50,
			53,
			46,
			39,
			17,
			 4,
			13,
			66,
			19,
			76,
			47,
			44,
			49,
			37,
			56,
			 5,
			22,
			15,
			43,
			54,
			25,
			68,
			14,
			45,
			52,
			 3,
			69,
			78,
			38,
			67,
			48,
			58,
			24,
			75,
			63,
			62,
			85,
			79,
			84,
			82,
			55,
			81,
			77,
			59,
			83,
			36,
			11,
			57,
			31,
			70,
			94,
			42,
			64,
			61,
			74,
			60,
			65,
			73,
			90,
			80,
			72,
			32
	};
	double[] weights = {
			2.00842,
			1.90362,
			1.90362,
			1.85225,
			1.83463,
			1.82784,
			1.76513,
			1.75496,
			1.7364 ,
			1.65055,
			1.62761,
			1.61655,
			1.59692,
			1.58928,
			1.56968,
			1.54069,
			1.5376 ,
			1.53315,
			1.51196,
			1.50264,
			1.49833,
			1.4974 ,
			1.49706,
			1.49527,
			1.48138,
			1.48037,
			1.47657,
			1.46071,
			1.45906,
			1.45426,
			1.44838,
			1.4355 ,
			1.40742,
			1.40619,
			1.4024 ,
			1.39379,
			1.33425,
			1.31976,
			1.31704,
			1.3015 ,
			1.29358,
			1.28328,
			1.26973,
			1.26196,
			1.25153,
			1.24595,
			1.24158,
			1.23882,
			1.23825,
			1.23424,
			1.23406,
			1.23196,
			1.22484,
			1.21866,
			1.21471,
			1.21452,
			1.20573,
			1.20324,
			1.17514,
			1.1733 ,
			1.1729 ,
			1.1667 ,
			1.14785,
			1.14756,
			1.14645,
			1.14544,
			1.135  ,
			1.13376,
			1.12697,
			1.12648,
			1.07333,
			1.05079
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
