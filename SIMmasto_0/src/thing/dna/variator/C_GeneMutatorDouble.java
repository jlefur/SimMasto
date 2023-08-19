package thing.dna.variator;
import data.constants.I_ConstantNumeric;
import simmasto0.C_ContextCreator;
/** @author kyle wagner, elyk@acm.org / modif. J.LeFur 2011
 * Version 1.0, Wed Dec 13 20:33:52 2000 */
public class C_GeneMutatorDouble implements I_GeneMutator, I_ConstantNumeric {
	//
	// FIELDS
	//
	private double mutMin, mutMax;
	private double mutRange;
	//
	// CONSTRUCTORS
	//
	/** @param mutMin Usually a negative number - how low the mutation can go
	 * @param mutMax Usually a positive number - how high the mutation can go */
	public C_GeneMutatorDouble(double mutMin, double mutMax) {
		if (mutMin >= mutMax) throw new RuntimeException("Mutation min/max values overlap!");
		this.mutMin = mutMin;
		this.mutMax = mutMax;
		this.mutRange = mutMax - mutMin;
	}
	/** Creates a new Random object to use for mutation. */
	public C_GeneMutatorDouble() {
		this(DEFAULT_MUT_RATE * -1, DEFAULT_MUT_RATE);
	}
	//
	// METHODS
	//
	/** Determine a new value for the Double, value, and return it. A number between mutMin and MutMax is generated, added to value,
	 * and that is returned. Does no checking on info's or value's type, for efficiency's sake.
	 * @param info the mutation rate, expressed as a Double in interval [0,1]
	 * @param value the value that is being mutated. A Double. */
	public Object mutate(Object info, Object value) {
		Double mutRate = (Double) info;
		// Double d = (Double)value;
		Number d = (Number) value;
		double random = C_ContextCreator.randomGeneratorForDNA.nextDouble();
		// If randomly chosen number is below mutation rate, mutate the gene, otherwise leave it
		// alone (return its original value).
		if (random < mutRate.doubleValue()) {
			// Get a number in the mutation range, then bump it up to the minimum amount. So its
			// range is now [mutMin, mutMax].
			double mutAmount = mutRange * (C_ContextCreator.randomGeneratorForDNA.nextDouble());
			mutAmount = mutAmount + mutMin;
			return new Double(mutAmount + d.doubleValue());
		}
		else return value;
	}
	public String toString() {
		return "<mutator range:" + Double.toString(mutRange) + " min:" + Double.toString(mutMin) + " max:"
				+ Double.toString(mutMax) + ">";
	}
	//
	// MAIN et al.
	//
	public static void main(String[] args) {
		System.out.println("Testing DoubleGeneMutator");
		C_GeneMutatorDouble dblMut = new C_GeneMutatorDouble(0.5, 0.9);
		Double val = new Double(1.5);
		Double mutRate = new Double(0.5);
		System.out.println("Mutating at this rate: " + mutRate);
		for (int i = 1; i <= 10; i++) {
			System.out.println("mutated val = " + dblMut.mutate(mutRate, val));
		}
	}
}
