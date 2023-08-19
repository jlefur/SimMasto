package simmasto0.util;

import java.util.Random;

import data.constants.I_ConstantNumeric;

/** An extension of java.util.Random, which adds methods to generate random numbers in normal distribution given a specific mean
 * or both a mean and a standard deviation.
 * @author kyle wagner, elyk@acm.org Version 1.0, Sat Feb 3 18:38:23 2001 */

public class C_RandomInNormalDistrib extends Random implements I_ConstantNumeric {
	private static final long serialVersionUID = 5152073868989964053L;

	public C_RandomInNormalDistrib() {
		super(GAUSSIAN_RANDOM_SEED);
	}

	public C_RandomInNormalDistrib(long seed) {
		super(seed);
	}

	/** Simply relies on Random's nextGaussian(), but adds mean to it. Standard deviation of the result is 1.0.
	 * @param mean the mean of the normal distribution
	 * @return any legal double, but values will be generated under a normal (gaussian) distribution. */
	public double nextGaussian(double mean) {
		return mean + nextGaussian();
	}

	/** Simply relies on Random's nextGaussian(), but multiplies by standDev and adds mean to it. Standard deviation of the result
	 * is given by standDev.
	 * @param mean the mean of the normal distribution
	 * @param standDev the standard deviation of the normal distribution
	 * @return any legal double, but values will be generated under a normal (gaussian) distribution. */
	public double nextGaussian(double mean, double standDev) {
		return mean + (standDev * nextGaussian());
	}

	//////////////////////////////////////////////////////////////////////////
	// PROTECTED/PRIVATE METHODS
	//////////////////////////////////////////////////////////////////////////

	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing MyRandom");

		long GAUSSIAN_RANDOM_SEED = 321657567;
		C_RandomInNormalDistrib myRandom = new C_RandomInNormalDistrib(GAUSSIAN_RANDOM_SEED);
		double mean = 10;
		double standDev = 2;

		if (args.length > 1) {
			mean = Double.parseDouble(args[0]);
			standDev = Double.parseDouble(args[1]);
		}

		System.out.println("rand #s, mean = " + mean);
		double accum = 0;
		double r = 0;
		final int numSamples = 21;
		for (int i = 1; i <= numSamples; i++) {
			r = myRandom.nextGaussian(mean);
			accum += r;
			System.out.print(r);
			System.out.print("\t");
			if (i % 3 == 0) System.out.println();
		}
		System.out.println("Actual mean: " + (accum / numSamples));

		System.out.println("\nrand #s, mean = " + mean + " stand dev=" + standDev);
		accum = 0;
		for (int i = 1; i <= numSamples; i++) {
			r = myRandom.nextGaussian(mean, standDev);
			accum += r;
			System.out.print(r);
			System.out.print("\t");
			if (i % 3 == 0) System.out.println();
		}
		System.out.println("Actual mean: " + (accum / numSamples));
	}
}
