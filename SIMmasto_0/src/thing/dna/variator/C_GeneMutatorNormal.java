package thing.dna.variator;

import simmasto0.C_ContextCreator;

/**
 * Mutate double-valued genes (alleles) under a normal distribution.
 * 
 * 
 * @author kyle wagner, elyk@acm.org
 * Version 1.0, Wed Dec 13 20:33:52 2000
 */

public class C_GeneMutatorNormal implements I_GeneMutator {

	//
	// CONSTANTS
	//
	/*
	 * public static final double DEFAULT_MUT_MIN = -0.1; public static final
	 * double DEFAULT_MUT_MAX = 0.1;
	 */

	public static final double DEFAULT_GENE_MUTATOR_MEAN = 0;
	public static final double DEFAULT_GENE_MUTATOR_STAND_DEV = 1;

	//
	// FIELDS
	//

	private double mean, standDev;

	// private double mutRange;

	//
	// METHODS
	//

	/**
	 * @param mean
	 *            the mean of generated allele values
	 * @param standDev
	 *            the standard deviation of allele values (should be >0)
	 */
	public C_GeneMutatorNormal(double mean, double standDev) {
		/*
		 * if (mutMin >= mutMax) throw new
		 * RuntimeException("Mutation min/max values overlap!");
		 */

		this.mean = mean;
		this.standDev = standDev;
		/*
		 * this.mutMin = mutMin; this.mutMax = mutMax; this.mutRange = mutMax -
		 * mutMin;
		 */
	}

	/**
	 * Creates a new random object to use for mutation.
	 */
	public C_GeneMutatorNormal() {
		this(DEFAULT_GENE_MUTATOR_MEAN, DEFAULT_GENE_MUTATOR_STAND_DEV);
	}

	/**
	 * Determine a new value for the Double, value, and return it. A number
	 * between mutMin and MutMax is generated, added to value, and that is
	 * returned. Does no checking on info's or value's type, for efficiency's
	 * sake.
	 * 
	 * @param info
	 *            the mutation rate, expressed as a Double in interval [0,1]
	 * @param value
	 *            the value that is being mutated. A Double.
	 */
	public Object mutate(Object info, Object value) {
		Double mutRate = (Double) info;
		// Double d = (Double)value;
		Number d = (Number) value;

		// If randomly chosen number is below mutation rate, mutate the gene,
		// otherwise leave it alone (return its original value).
		// double r = C_ContextCreator.randomGeneratorForDNA.nextDouble();
		// System.out.println("r=" + r + "    d=" + d + "    info=" + info);
		if (C_ContextCreator.randomGeneratorForDNA.nextDouble() < mutRate.doubleValue()) {
			// Mutate the gene by adding a value to it that accords with mean
			// and
			// standDev.
			double mutAmount = C_ContextCreator.randomGaussianGenerator.nextGaussian(mean, standDev);
			return new Double(mutAmount + d.doubleValue());
		} else {
			return value;
		}
	}

	public String toString() {
		/*
		 * return "<mutator range:" + Double.toString(mutRange) + " min:" +
		 * Double.toString(mutMin) + " max:" + Double.toString(mutMax) + ">";
		 */
		return "<mutator mean=" + Double.toString(mean) + " sd="
				+ Double.toString(standDev) + ">";
	}

	// ////////////////////////////////////////////////////////////////////////
	// PROTECTED/PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////

	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing NormalGeneMutator");

		C_GeneMutatorNormal dblMut = new C_GeneMutatorNormal(0, 2.0);
		Double val = new Double(1.5);
		Double mutRate = new Double(0.5);

		System.out.println("val = " + val);
		System.out.println("Mutating at this rate: " + mutRate);
		for (int i = 1; i <= 10; i++) {
			System.out.println("mutated val = " + dblMut.mutate(mutRate, val));
		}

	}
}
