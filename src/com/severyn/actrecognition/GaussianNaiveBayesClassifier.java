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
			21,
			 7,
			16,
			12,
			51,
			41,
			50,
			53,
			39,
			17,
			13,
			46,
			66,
			44,
			47,
			76,
			49,
			37,
			22,
			 5,
			 4,
			56,
			19,
			43,
			68,
			15,
			54,
			45,
			14,
			52,
			25,
			69,
			67,
			 3,
			38,
			78,
			48,
			58,
			24,
			63,
			77,
			75,
			79,
			62,
			55,
			85,
			82,
			84,
			81,
			57,
			83,
			11,
			59,
			90,
			36,
			74,
			94,
			70,
			42,
			31,
			64,
			61,
			60,
			73,
			65,
			80,
			32,
			72,
			30
	};
	double[] weights = {
			 2.00348,
			 1.95511,
			 1.95511,
			 1.87402,
			 1.86996,
			 1.83862,
			 1.8181,
			 1.81198,
			 1.76002,
			 1.68145,
			 1.63202,
			 1.62493,
			 1.60145,
			 1.59761,
			 1.568,  
			 1.5631, 
			 1.56102,
			 1.52887,
			 1.5253, 
			 1.52467,
			 1.52263,
			 1.52116,
			 1.51781,
			 1.51497,
			 1.50329,
			 1.50318,
			 1.50162,
			 1.4822 ,
			 1.48164,
			 1.47304,
			 1.4708 ,
			 1.43623,
			 1.42213,
			 1.40987,
			 1.4051 ,
			 1.34989,
			 1.3476 ,
			 1.34702,
			 1.33112,
			 1.33041,
			 1.30703,
			 1.29392,
			 1.28322,
			 1.27532,
			 1.27209,
			 1.26361,
			 1.25536,
			 1.2515 ,
			 1.24914,
			 1.24594,
			 1.24491,
			 1.24148,
			 1.23432,
			 1.21793,
			 1.21774,
			 1.21271,
			 1.20254,
			 1.18993,
			 1.18865,
			 1.18744,
			 1.18312,
			 1.17821,
			 1.17119,
			 1.16637,
			 1.15158,
			 1.15127,
			 1.14181,
			 1.13615,
			 1.13572,
			 1.12988,
			 1.10058,
			 1.07186,
			 1.01885
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
