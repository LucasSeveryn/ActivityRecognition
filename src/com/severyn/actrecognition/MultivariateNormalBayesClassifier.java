package com.severyn.actrecognition;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.util.Pair;


import Jama.Matrix;

public class MultivariateNormalBayesClassifier {
	List<AccFeat> lib;
	List<ArrayList<Double>> meanVectors = new ArrayList<ArrayList<Double>>();
	List<ArrayList<Double>> varianceVectors = new ArrayList<ArrayList<Double>>();
	List<CMatrix> cMatrices = new ArrayList<CMatrix>();
	int[] attr = { 
			2,3,4,5,6,7};
	int[] types = {0,1,2,3,4,5,6,7};


	
	double getCovariance(int featureX, int featureY, int type) {
		double meanX = meanVectors.get(type).get(featureX);
		double meanY = varianceVectors.get(type).get(featureY);

		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += (a.getFeature(featureX) - meanX)
						* (a.getFeature(featureY) - meanY);
				count++;
			}
		}
		double result = sum / (count - 1);
		return result;

	}

	class CMatrix {
		double[][] mat;
		int d;

		CMatrix(int type) {
			this.d = attr.length;
			mat = new double[d][d];
			for (int i = 0; i < attr.length; i++) {
				for (int j = 0; j < attr.length; j++) {
//					System.out.println("#" + type + " attr[i]: " + WekaFileGenerator.intToAttributeName(attr[i]) + " attr[j]: " + WekaFileGenerator.intToAttributeName(attr[j]));
//					double covariance = getCovariance(attr[i], attr[j], type);
					double covariance = getCovariance(i, j, type);
					this.set(i, j, covariance);
				}
			}
			
		}
		
		
		CMatrix(double[][] mat){
			this.mat=mat;
			this.d=mat[0].length;
		}

		double get(int i, int j) {
			return mat[i][j];
		}

		int getD() {
			return d;
		}

		void set(int i, int j, double x) {
			mat[i][j] = x;
		}

		double[][] getMatrix() {
			return mat;
		}

	}

	public MultivariateNormalBayesClassifier(List<ArrayList<Double>> m, List<ArrayList<Double>> v, List<double[][]> covM){
		for (int j = 0; j < attr.length; j++) {
			attr[j] = attr[j] - 2;
		}
		this.meanVectors=m;
		this.varianceVectors=v;
		for(int i=0;i<covM.size();i++){
			cMatrices.add(new CMatrix(covM.get(i)));
		}

	}
	
	
	public MultivariateNormalBayesClassifier(List<AccFeat> lib) {
		this.lib = lib;
		for (int j = 0; j < attr.length; j++) {
			attr[j] = attr[j] - 2;
		}
//		for (Integer i : attr) {
//			System.out.println(WekaFileGenerator.intToAttributeName(i));
//		}
		ArrayList<Double> m0 = new ArrayList<Double>();
		ArrayList<Double> m1 = new ArrayList<Double>();
		ArrayList<Double> m2 = new ArrayList<Double>();
		ArrayList<Double> m3 = new ArrayList<Double>();
		ArrayList<Double> m4 = new ArrayList<Double>();
		ArrayList<Double> m5 = new ArrayList<Double>();
//		ArrayList<Double> m6 = null;
		ArrayList<Double> m6 = new ArrayList<Double>();
		 ArrayList<Double> m7 = new ArrayList<Double>();
		// ArrayList<Double> m8 = new ArrayList<Double>();

		meanVectors.add(0, m0);
		meanVectors.add(1, m1);
		meanVectors.add(2, m2);
		meanVectors.add(3, m3);
		meanVectors.add(4, m4);
		meanVectors.add(5, m5);
		meanVectors.add(6, m6);
		 meanVectors.add(7, m7);
		// entropyMean.add(8, m8);

		ArrayList<Double> v0 = new ArrayList<Double>();
		ArrayList<Double> v1 = new ArrayList<Double>();
		ArrayList<Double> v2 = new ArrayList<Double>();
		ArrayList<Double> v3 = new ArrayList<Double>();
		ArrayList<Double> v4 = new ArrayList<Double>();
		ArrayList<Double> v5 = new ArrayList<Double>();
//		ArrayList<Double> v6 = null;
		ArrayList<Double> v6 = new ArrayList<Double>();
		 ArrayList<Double> v7 = new ArrayList<Double>();
		// ArrayList<Double> v8 = new ArrayList<Double>();


		 
		 
		varianceVectors.add(0, v0);
		varianceVectors.add(1, v1);
		varianceVectors.add(2, v2);
		varianceVectors.add(3, v3);
		varianceVectors.add(4, v4);
		varianceVectors.add(5, v5);
		varianceVectors.add(6, v6);
		 varianceVectors.add(7, v7);
		// entropyVar.add(8, v8);

		
		 
		entropy();

		CMatrix t0 = new CMatrix(0);
		CMatrix t1 = new CMatrix(1);
		CMatrix t2 = new CMatrix(2);
		CMatrix t3 = new CMatrix(3);
		CMatrix t4 = new CMatrix(4);
		CMatrix t5 = new CMatrix(5);
//		CMatrix t6 = null;
		CMatrix t6 = new CMatrix(6);
		CMatrix t7 = new CMatrix(7);
//		CMatrix t8 = new CMatrix(8);
		cMatrices.add(t0);
		cMatrices.add(t1);
		cMatrices.add(t2);
		cMatrices.add(t3);
		cMatrices.add(t4);
		cMatrices.add(t5);
		cMatrices.add(t6);
		cMatrices.add(t7);
//		cMatrices.add(t8);

	}

	public void entropy() {
		for (int i = 0; i < types.length; i++) {
			for (int k = 0; k < attr.length; k++) {
				double mean = getSampleMean(attr[k], types[i]);
				meanVectors.get(types[i]).add(mean);
				double var = getSampleVariance(attr[k], types[i], mean);
				varianceVectors.get(types[i]).add(var);
			}

		}

	}
	

	private double p(List<Double> qf, int type) {
		Matrix coeff = new Matrix(cMatrices.get(type).getMatrix());
//		coeff.print(10, 4);
		Matrix coeff_inv = coeff.inverse();

		double result = Math.pow((2 * Math.PI), -coeff.getRowDimension() / 2);
		double det = coeff.det();
		double det_pow = Math.pow(det, -0.5);
		
		result *= Math.pow(coeff.det(), -0.5);

		double[][] xminusmt = new double[1][attr.length];
		double[][] xminusm = new double[attr.length][1];
		double difference;
		for (int i = 0; i < (attr.length); i++) {
			difference = qf.get(i) - meanVectors.get(type).get(i);
			xminusmt[0][i] = difference;
			xminusm[i][0] = difference;
		}
		Matrix x_minus_m = new Matrix(xminusm);
		Matrix x_minus_m_T = new Matrix(xminusmt);

		Matrix half_times_x_minus_m_T = x_minus_m_T.times(-0.5);

		Matrix x_minus_m_T_times_C_inv = half_times_x_minus_m_T
				.times(coeff_inv);
		double norml1 = x_minus_m_T_times_C_inv.times(x_minus_m).norm1();
		double n = Math.exp(norml1);
		result *= n;

		return result;
	}

	public Pair<ArrayList<Double>, String> classify(AccFeat q) {
		String txt = "";
		txt += ("Gaussian Multivariate Bayes Classification");
		txt+="\n";
		double[] results = new double[types.length];
		ArrayList<Double> qf = new ArrayList<Double>();

		for (int j = 0; j < attr.length; j++) {
			qf.add(q.getFeature(attr[j]));
		}

		int maxindex=0;
		double maxvalue=0;
		
		for (int i = 0; i < types.length; i++) {
			results[i] = p(qf, types[i]);
			if (!Double.isNaN(results[i])) {
				txt += ("\n[" + types[i] + "] -" 
				+ String.format("%.5f", Math.log(results[i])));
				 }

			
			if(!Double.isInfinite(results[i])){
				 maxindex=i;
				 maxvalue = results[i];
			}
		}
		
		for (int i = 0; i < types.length; i++) {
			if (results[i] < results[maxindex] && !Double.isInfinite(results[i])) {
				maxvalue = results[i];
				maxindex = i;
			}
		}
		txt+=("\n MVC: Classified as type #" + types[maxindex]);

		txt+="\n";
		
		ArrayList<Double> resultsArrayList = new ArrayList<Double>();
		for (Double d : results) {
			resultsArrayList.add(-Math.log(d));
		}
		
		Pair<ArrayList<Double>, String> pair = 
				new Pair<ArrayList<Double>, String>(resultsArrayList, txt);
		return pair;

	}

	double getSampleMean(int feature, int type) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getFeature(feature);
				count++;
			}
		}
		return sum / count;
	}

	double getSampleVariance(int feature, int type, double mean) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getFeature(feature) - mean), 2);
				count++;
			}
		}
		return sum / count;
	}


	public List<ArrayList<Double>> getEntropyMean() {
		return meanVectors;
	}

	public List<ArrayList<Double>> getEntropyVar() {
		return varianceVectors;
	}

	public List<CMatrix> getcMatrices() {
		return cMatrices;
	}
}
