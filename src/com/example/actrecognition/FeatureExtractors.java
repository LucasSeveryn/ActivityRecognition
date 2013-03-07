package com.example.actrecognition;

import java.util.ArrayList;

public class FeatureExtractors {

	public ArrayList<Float> iterativeFFT(ArrayList<Float> v, int dir) {
		int n = v.size();
		//ArrayList<Complex> bitRevCopy = new ArrayList<Complex>();
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
	
	public ArrayList<Float> toReal(ArrayList<Complex> input){
		ArrayList<Float> output = new ArrayList<Float>();
		for(Complex n : input){
			output.add(Float.valueOf((float)n.abs()));
		}
		return output;
	}

	public ArrayList<Complex> bitReverseCopy(ArrayList<Float> v, int n) {
		ArrayList<Complex> bitRevCopy = new ArrayList<Complex>();
		Complex zero = new Complex(0,0);
		while(bitRevCopy.size() < 512) bitRevCopy.add(zero);
		
		double maxbits = Math.log(n) / Math.log(2);
		
		if (((int) maxbits) - maxbits != 0) {
			return null;
		}

		for (int i = 0; i < n; i++) {
			bitRevCopy.set(revBits(i, (int) maxbits), new Complex(v.get(i), 0));
		}

		return bitRevCopy;
	}

	public int revBits(int n, int size) {
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
}