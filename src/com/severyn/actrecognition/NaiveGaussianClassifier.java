package com.severyn.actrecognition;

import java.util.ArrayList;

import android.util.Pair;

public class NaiveGaussianClassifier {
	ArrayList<ArrayList<Double>> entropyMean;
	ArrayList<ArrayList<Double>> entropyVar;

	public NaiveGaussianClassifier(ArrayList<ArrayList<Double>> entropyMean,
			ArrayList<ArrayList<Double>> entropyVar) {
		this.entropyMean = entropyMean;
		this.entropyVar = entropyVar;

	}

	public Pair<ArrayList<Double>, String> classify(AccFeat q) {
		String txt = "";
		txt += ("\n- Starting NBC Classification - may25");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<Double>();

		for (int j = 0; j < 73; j++) {
			qf.add(q.getFeature(j));
		}

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) { // debug
			// result = 1;
				result = 0;
				for (int j = 0; j < entropyMean.get(i).size(); j++) {
					// result = result * p(qf.get(j),
					// entropyMean.get(i).get(j),entropyVar.get(i).get(j));
					// result = result - Math.log(p(qf.get(j),
					// entropyMean.get(i).get(j),entropyVar.get(i).get(j)));
					result += Math.log(p(qf.get(j), entropyMean.get(i).get(j),
							entropyVar.get(i).get(j)));

				}
				results[i] = result;
			}
		}

		int maxindex = 0;
		double maxvalue = results[0];

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6
					&& !Double.isNaN(results[i])) {
				maxvalue = results[i];
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < 9; i++) {
			if (!Double.isNaN(results[i])) {

				if (results[i] != 0.0) {
					txt += ("\n[" + i + "] " + String.format("%.5f", Math.exp(results[i]))+ " log:" + String.format("%.5f", results[i]));
				}
				if (results[i] > results[maxindex] && i != 1 && i != 4
						&& i != 5 && i != 6) {
					maxvalue = results[i];
					maxindex = i;
				}
			}

		}

		for (int i = 0; i < results.length; i++) {
			if (i != maxindex && i != 1 && i != 4 && i != 5 && i != 6) {
				txt += ("\n   Type #" + i + " : "
						+ String.format("%.2f", results[i] / results[maxindex]) + " times less likely.");
			}
		}
		txt += ("\n");

		txt += ("\n- This is an activity of type #" + maxindex);

		ArrayList<Double> resultsArrayList = new ArrayList<Double>();
		for (Double d : results) {
			resultsArrayList.add(d);
		}

		Pair<ArrayList<Double>, String> pair = new Pair<ArrayList<Double>, String>(
				resultsArrayList, txt);
		return pair;

	}

	private double p(double v, double m, double var) {
		if (var == 0)
			return 1;
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

		return p;
	}

	public ArrayList<ArrayList<Double>> getEntropyMean() {
		return entropyMean;
	}

	public ArrayList<ArrayList<Double>> getEntropyVar() {
		return entropyVar;
	}
}