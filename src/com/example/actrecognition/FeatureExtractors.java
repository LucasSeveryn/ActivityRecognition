package com.example.actrecognition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.*;

public final class FeatureExtractors {
	public final static FeatureExtractors INSTANCE = new FeatureExtractors();

	private FeatureExtractors() {
		// Exists only to defeat instantiation.
	}

	private static ArrayList<Integer> removeSimilar(ArrayList<Integer> v, int k) {
		Collections.sort(v);
		int previous = v.get(0);
		int current;
		int i = 1;

		while (i < v.size()) {
			current = v.get(i);
			if ((current - previous) < k) {
				v.remove(i);
			} else {
				i++;
			}
			previous = current;
		}
		return v;
	}

	public static ArrayList<Integer> peakIndices(ArrayList<Float> v) {
		ArrayList<Integer> peakIndices = new ArrayList<Integer>();
		float max = Collections.max(v);
		float min = Collections.min(v);
		if (max - min < 1.0f) {
			peakIndices.add(Integer.valueOf(0));
			return peakIndices;
		}
		
		float cutoff = max * 0.9f;
		int iterations = 0;

		while (peakIndices.size() < 3 && iterations < 5) {
			iterations++;
			for (int i = 0; i < v.size(); i++) {
				if (v.get(i) > cutoff) {
					peakIndices.add(Integer.valueOf(i));
				}
			}

			if (peakIndices.size() != 0) {
				peakIndices = removeSimilar(peakIndices, 16);
			}

			cutoff -= 0.05f;
		}

		if (peakIndices.size() != 0) {
			peakIndices = removeSimilar(peakIndices, 16);
		} else {
			peakIndices.add(Integer.valueOf(0));
		}

		return peakIndices;

	}

	public static float averageDifference(ArrayList<Integer> v) {
		int previous = v.get(0);
		int current;
		int difference = 0;
		for (int i = 1; i < v.size(); i++) {
			current = v.get(i);
			difference = difference + (current - previous);
			previous = current;
		}
		return (difference / v.size());
	}

	public static float averageResultantAcceleration(ArrayList<Float> xv,
			ArrayList<Float> yv, ArrayList<Float> zv) {
		float result = 0;
		for (int i = 0; i < xv.size(); i++) {
			double atomicResult = 0;
			float x = xv.get(i);
			float y = yv.get(i);
			float z = zv.get(i);

			x = x * x;
			y = y * y;
			z = z * z;

			atomicResult = x + y + z;
			atomicResult = Math.sqrt(atomicResult);
			result += atomicResult;
		}

		return result / xv.size();
	}

	// /FFT

	public static ArrayList<Float> iterativeFFT(ArrayList<Float> v, int dir) {
		int n = v.size();
		// ArrayList<Complex> bitRevCopy = new ArrayList<Complex>();
		ArrayList<Complex> bitRevCopy = bitReverseCopy(v, n);

		double upperLoopBound = Math.log(n) / Math.log(2);

		for (int s = 1; s <= upperLoopBound; s++) {
			int m = (int) Math.pow(2, s);
			Complex Wm = (new Complex(0, 2 * Math.PI / m)).exp();
			Complex dirMod = new Complex(1, 0);
			if (dir == -1) {
				Wm = new Complex(1, 0).divides(Wm);
				dirMod = new Complex(2, 0);
			}
			for (int k = 0; k < n; k += m) {
				Complex w = new Complex(1, 0);
				for (int i = 0; i < m / 2; i++) {
					Complex t = w.times(bitRevCopy.get(k + i + m / 2));
					Complex u = bitRevCopy.get(k + i);
					bitRevCopy.set(k + 1, (u.plus(t)).divides(dirMod));
					bitRevCopy.set(k + i + m / 2, (u.minus(t)).divides(dirMod));
					w = w.times(Wm);
				}
			}
		}

		return toReal(bitRevCopy);
	}

	public static ArrayList<Float> toReal(ArrayList<Complex> input) {
		ArrayList<Float> output = new ArrayList<Float>();
		for (Complex n : input) {
			output.add(Float.valueOf((float) n.abs()));
		}
		return output;
	}

	public static ArrayList<Complex> bitReverseCopy(ArrayList<Float> v, int n) {
		ArrayList<Complex> bitRevCopy = new ArrayList<Complex>();
		Complex zero = new Complex(0, 0);
		while (bitRevCopy.size() < 512)
			bitRevCopy.add(zero);

		double maxbits = Math.log(n) / Math.log(2);

		if (((int) maxbits) - maxbits != 0) {
			return null;
		}

		for (int i = 0; i < n; i++) {
			bitRevCopy.set(revBits(i, (int) maxbits), new Complex(v.get(i), 0));
		}

		return bitRevCopy;
	}

	public static int revBits(int n, int size) {
		int result = 0;

		for (int i = size; i != 0; --i) {
			result = result << 1;
			if ((n & 1) != 0) {
				result = result | 1;
			}
			n = n >>> 1;
		}
		return result;
	}

	// END OF FFT

	public static int zeroCrossingCount(ArrayList<Float> data2, float zero,
			float spread, int rate) {
		int count = 0;

		ArrayList<Float> data = (ArrayList<Float>) data2.clone();

		// for (int j = 0; j < data.size(); j++) {
		// float n = data.get(j);
		// if (n < zero + spread && n > zero - spread) {
		// data.set(j, zero);
		// }
		// }

		float x;
		float previous = data.get(0);
		for (int i = rate; i + rate <= data.size(); i = i + rate) {
			x = data.get(i);
			if (previous < zero && x > zero || previous > zero && x < zero) {
				count++;
			}
			previous = x;

		}

		return count;
	}

	public static float standardDeviation(ArrayList<Float> v) {
		DescriptiveStatistics stats = new DescriptiveStatistics();

		// Add the data from the array
		for (int i = 0; i < v.size(); i++) {
			stats.addValue(v.get(i));
		}
		return (float) stats.getStandardDeviation();

	}

	public static float average(ArrayList<Float> v) {
		Float sum = 0f;
		for (Float number : v) {
			sum += number;
		}
		return sum / v.size();
	}

	public static ArrayList<Float> lowPassFilter(ArrayList<Float> v, float alpha) {
		ArrayList<Float> output = new ArrayList<Float>();

		output.add(v.get(0));
		for (int i = 1; i < v.size(); i++) {
			output.add(alpha * v.get(i) + (1 - alpha) * output.get(i - 1));
		}

		return output;
	}

}