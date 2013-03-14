package com.example.actrecognition;

import java.util.ArrayList;
import java.util.Arrays;

public final class IdentificationEngine {
	public final static IdentificationEngine INSTANCE = new IdentificationEngine();

	private IdentificationEngine() {
		// Exists only to defeat instantiation.
	}

	private static float[] getPeakRatio(AccActivity a) {
		float[] resultRatio = {
				a.getAvPeakDistance()[0] / a.getAvPeakDistance()[1],
				a.getAvPeakDistance()[0] / a.getAvPeakDistance()[2],
				a.getAvPeakDistance()[1] / a.getAvPeakDistance()[2] };
		return resultRatio;
	}

	private static float[] getCrossingRatio(AccActivity a) {
		float[] resultRatio = { a.getxCrossings() / a.getyCrossings(),
				a.getxCrossings() / a.getzCrossings(),
				a.getyCrossings() / a.getzCrossings() };
		return resultRatio;
	}

	private static Float sum(float[] sdDifference) {
		float sum = 0;
		for (Float i : sdDifference)
			sum = sum + i;
		return sum;
	}

	public static AccActivity findClosestMatch(AccActivity query,
			ArrayList<AccActivity> library) {
		AccActivity guess = library.get(0);
		float[] queryPeakRatio = getPeakRatio(query);
		float[] queryCrossingRateRatio = getCrossingRatio(query);

		// compute for first guess
		float[] guessPeakRatio = getPeakRatio(guess);

		float[] peakRatioDifference = {
				Math.abs(guessPeakRatio[0] - queryPeakRatio[0]),
				Math.abs(guessPeakRatio[1] - queryPeakRatio[1]),
				Math.abs(guessPeakRatio[2] - queryPeakRatio[2]) };

		float[] guessCrossingRateRatio = getCrossingRatio(guess);

		float[] crossingRatioDifference = {
				Math.abs(guessCrossingRateRatio[0] - queryCrossingRateRatio[0]),
				Math.abs(guessCrossingRateRatio[1] - queryCrossingRateRatio[1]),
				Math.abs(guessCrossingRateRatio[2] - queryCrossingRateRatio[2]) };

		float[] sdDifference = { Math.abs(guess.getSD()[0] - query.getSD()[0]),
				Math.abs(guess.getSD()[1] - query.getSD()[1]),
				Math.abs(guess.getSD()[2] - query.getSD()[2]) };

		Arrays.sort(peakRatioDifference);
		Arrays.sort(sdDifference);
		Arrays.sort(crossingRatioDifference);
		Arrays.sort(guessPeakRatio);

		peakRatioDifference[2]=0;
		sdDifference[2]=0;
		crossingRatioDifference[2]=0;
		guessPeakRatio[2]=0;
		
		
		float avResAccelerationDifferenceKo = Math.abs(guess
				.getAvResAcceleration() - query.getAvResAcceleration());
		float sdDifferenceKo = sum(sdDifference);
		float crossingRatioDifferenceKo = sum(crossingRatioDifference);
		float peakRatioDifferenceKo = sum(peakRatioDifference);

		// //

		int points = 0;

		for (int i = 1; i < library.size(); i++) {
			AccActivity potentialGuess = library.get(i);
			float[] guessPeakRatio2 = getPeakRatio(potentialGuess);

			float[] peakRatioDifference2 = {
					Math.abs(guessPeakRatio2[0] - queryPeakRatio[0]),
					Math.abs(guessPeakRatio2[1] - queryPeakRatio[1]),
					Math.abs(guessPeakRatio2[2] - queryPeakRatio[2]) };

			float[] guessCrossingRateRatio2 = getCrossingRatio(potentialGuess);

			float[] crossingRatioDifference2 = {
					Math.abs(guessCrossingRateRatio2[0]
							- queryCrossingRateRatio[0]),
					Math.abs(guessCrossingRateRatio2[1]
							- queryCrossingRateRatio[1]),
					Math.abs(guessCrossingRateRatio2[2]
							- queryCrossingRateRatio[2]) };

			float[] sdDifference2 = {
					Math.abs(potentialGuess.getSD()[0] - query.getSD()[0]),
					Math.abs(potentialGuess.getSD()[1] - query.getSD()[1]),
					Math.abs(potentialGuess.getSD()[2] - query.getSD()[2]) };

			
			Arrays.sort(peakRatioDifference2);
			Arrays.sort(sdDifference2);
			Arrays.sort(crossingRatioDifference2);
			Arrays.sort(guessPeakRatio);

			peakRatioDifference2[2]=0;
			sdDifference2[2]=0;
			crossingRatioDifference2[2]=0;
			guessPeakRatio2[2]=0;
			
			
			float avResAccelerationDifferenceK = Math.abs(potentialGuess
					.getAvResAcceleration() - query.getAvResAcceleration());
			float sdDifferenceK = sum(sdDifference2);
			float crossingRatioDifferenceK = sum(crossingRatioDifference2);
			float peakRatioDifferenceK = sum(peakRatioDifference2);

		
			
			
			if (avResAccelerationDifferenceK < avResAccelerationDifferenceKo) {
				points = points + 5;
			}
			if (crossingRatioDifferenceK < crossingRatioDifferenceKo) {
				points = points + 15;
			}
			if (peakRatioDifferenceK < peakRatioDifferenceKo) {
				points = points + 15;
			}
			if (sdDifferenceK < sdDifferenceKo) {
				points = points + 5;
			}

			if (points >= 25) {
				
				guess = potentialGuess;
				peakRatioDifferenceKo = peakRatioDifferenceK;
				sdDifferenceKo = sdDifferenceK;
				crossingRatioDifferenceK = crossingRatioDifferenceKo;
				avResAccelerationDifferenceK = avResAccelerationDifferenceKo;
			}
			points = 0;
		}
		
		return guess;
	}
}
