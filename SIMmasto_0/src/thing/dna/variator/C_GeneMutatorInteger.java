package thing.dna.variator;

import simmasto0.C_ContextCreator;

/**
 * 
 * 
 * 
 * @author kyle wagner, elyk@acm.org / modif. J.LeFur 2011 (for JEL microsats)
 * Version 1.0, Wed Dec 13 20:33:52 2000
 */

public class C_GeneMutatorInteger implements I_GeneMutator {

	//
	// CONSTANTS
	//

	public static final int DEFAULT_MUT_MIN = -1;
	public static final int DEFAULT_MUT_MAX = 1;

	//
	// FIELDS
	//

	private int mutMin, mutMax;
	private int mutRange;

	//
	// METHODS
	//

	/**
	 * @param mutMin
	 *            Usually a negative number - how low the mutation can go
	 * @param mutMax
	 *            Usually a positive number - how high the mutation can go
	 */
	public C_GeneMutatorInteger(int mutMin, int mutMax) {
		if (mutMin >= mutMax) throw new RuntimeException("Mutation min/max values overlap!");

		this.mutMin = mutMin;
		this.mutMax = mutMax;
		this.mutRange = mutMax - mutMin;
	}

	/**
	 * Creates a new Random object to use for mutation.
	 */
	public C_GeneMutatorInteger() {
		this(DEFAULT_MUT_MIN, DEFAULT_MUT_MAX);
	}

	/**
	 * Determine a new value for the Integer, value, and return it. A number between mutMin and
	 * MutMax is generated, added to value, and that is returned. Does no checking on info's or
	 * value's type, for efficiency's sake.
	 * 
	 * @param mutationRate
	 *            the mutation rate, expressed as a Double in interval [0,1]
	 * @param alleleValue
	 *            the value that is being mutated. A Double.
	 */
	public Object mutate(Object mutationRate, Object alleleValue) {
		Double mutRate = (Double) mutationRate;
		Number d = (Number) alleleValue;
		double random = C_ContextCreator.randomGeneratorForDNA.nextDouble();
		// If randomly chosen number is below mutation rate, mutate the gene, otherwise leave it
		// alone (return its original value).
		if (random < mutRate.doubleValue()) {
			// Get a number in the mutation range, then bump it up to the minimum amount. So its
			// range is now [mutMin, mutMax].
			int mutAmount = (int) (mutRange * (C_ContextCreator.randomGeneratorForDNA.nextDouble()));
			mutAmount = mutAmount + mutMin;
			return new Integer(mutAmount + d.intValue());
		}
		else {
			return alleleValue;
		}
	}

	public String toString() {
		return "<mutator range:" + Integer.toString(mutRange) + " min:" + Integer.toString(mutMin) + " max:" + Integer.toString(mutMax) + ">";
	}

	// ////////////////////////////////////////////////////////////////////////
	// PROTECTED/PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////

	//
	// MAIN
	//
	public static void main(String[] args) {
		System.out.println("Testing IntegerGeneMutator");

		C_GeneMutatorInteger intMut = new C_GeneMutatorInteger(10,10);
		Integer val = new Integer(2);
		Double mutRate = new Double(0.5);

		System.out.println("Allele value = " + val);
		System.out.println("Mutating at this rate: " + mutRate);
		for (int i = 1; i <= 10; i++) {
			System.out.println("mutated val = " + intMut.mutate(mutRate, val));
		}

	}
}
