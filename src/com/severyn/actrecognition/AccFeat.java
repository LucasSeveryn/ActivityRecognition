package com.severyn.actrecognition;
public class AccFeat {
	int id;

	Double[] mean = new Double[3];
	Double[] sd = new Double[3];
	Double[] avPeakDistance = new Double[3];
	Double[] maxDisplacement = new Double[3];
	int[][] histogram = new int[3][10];
	int[][] fftHistogram = new int[3][10];
	double[][] AR = new double[3][4];
	int[] crossingCount = new int[3];
	double[] energy = new double[3];
	double[] correlation = new double[3]; 
	double resultantAcc;
	double SMA;
	int type;

	public int getId() {
		return id;
	}

	public double getFeature(int i) {
		switch (i) {
		case 0:
			return mean[0];
		case 1:
			return mean[1];
		case 2:
			return mean[2];
		case 3:
			return sd[0];
		case 4:
			return sd[1];
		case 5:
			return sd[2];
		case 6:
			return avPeakDistance[0];
		case 7:
			return avPeakDistance[1];
		case 8:
			return avPeakDistance[2];
		case 9:
			return crossingCount[0];
		case 10:
			return crossingCount[1];
		case 11:
			return crossingCount[2];
		case 12:
			return resultantAcc;
		case 13:
			return maxDisplacement[0];
		case 14:
			return maxDisplacement[1];
		case 15:
			return maxDisplacement[2];
		case 16:
			return 0;
		case 17:
			return energy[0];
		case 18:
			return energy[1];
		case 19:
			return energy[2];
		case 20:
			return correlation[0];
		case 21:
			return correlation[1];
		case 22:
			return correlation[2];
		case 23:
			return SMA;

		}
		if (i > 23 && i < 54) {
			return histogram[(int) Math.floor((i - 24) / 10)][(i - 24) % 10];
		}
		if (i > 53 && i < 84) {
			return fftHistogram[(int) Math.floor((i - 54) / 10)][(i - 54) % 10];
		}
		if (i > 83) {
			int axis=(int) Math.floor((i - 84) / 4);
			double result = AR[axis][(i - 84) % 4];
			return result;
		}
		return 0;

	}

	public void setCrossingCount(int i, int cc) {
		this.crossingCount[i] = cc;
	}

	public Double getAvPeakDistance(int i) {
		return this.avPeakDistance[i];
	}

	public Double getCrossingCount(int i) {
		return (double) this.crossingCount[i];
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getResultantAcc() {
		return this.resultantAcc;
	}

	// public double getgResultantAcc() {
	// return this.gresultantAcc;
	// }

	public Double getMean(int i) {
		return mean[i];
	}

	public void setMean(int i, Double mean) {
		this.mean[i] = mean;
	}

	public Double getSd(int i) {
		return sd[i];
	}

	public void setSd(int i, Double sd) {
		this.sd[i] = sd;
	}

	public void setMaxDisplacementValue(int i, Double val) {
		this.maxDisplacement[i] = val;
	}
	
	public void setEnergy(int i, Double val) {
		this.energy[i] = val;
	}
	
	public void setCorrelation(int i, Double val) {
		this.correlation[i] = val;
	}

	public void setResultantAcc(double resultantAcc) {
		this.resultantAcc = resultantAcc;
	}

	public void setAvPeakDistance(int i, double averageDistanceBetweenPeaks) {
		this.avPeakDistance[i] = averageDistanceBetweenPeaks;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public int getFftHistogram(int axis, int index) {
		return fftHistogram[axis][index];
	}

	public void setFftHistogram(int i, int[] fftHistogram) {
		this.fftHistogram[i] = fftHistogram;
	}
	
	public void setAR(int i, double[] arv) {
		this.AR[i] = arv;
	}


	public int getHistogram(int axis, int index) {
		// System.out.println("axis: " + axis + " index: " + index);
		return fftHistogram[axis][index];
	}

	public int[] getHistogramArray(int axis) {
		// System.out.println("axis: " + axis + " index: " + index);
		return histogram[axis];
	}

	public int[] getFFTHistogramArray(int axis) {
		// System.out.println("axis: " + axis + " index: " + index);
		return fftHistogram[axis];
	}

	public void setHistogram(int i, int[] histogram) {
		this.histogram[i] = histogram;
	}

	public double getSMA() {
		return SMA;
	}

	public void setSMA(double sMA) {
		SMA = sMA;
	}

}
